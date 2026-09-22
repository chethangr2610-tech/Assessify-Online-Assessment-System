package com.example.exam.controller;

import com.example.exam.model.Exam;
import com.example.exam.model.ExamResult;
import com.example.exam.model.Question;
import com.example.exam.model.User;
import com.example.exam.model.Group;
import com.example.exam.repository.ExamRepository;
import com.example.exam.repository.ExamResultRepository;
import com.example.exam.repository.QuestionRepository;
import com.example.exam.repository.UserRepository;
import com.example.exam.repository.GroupRepository;
import com.example.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamResultRepository examResultRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    /**
     * Helper method to get the currently authenticated admin user
     */
    private User getAuthenticatedAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {

        // 1. Get KPI Card Stats
        long totalStudents = userRepository.countByRole("ROLE_STUDENT");
        long totalExams = examRepository.count();
        long totalQuestions = questionRepository.count();
        long totalSubmissions = examResultRepository.count();

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalExams", totalExams);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("totalSubmissions", totalSubmissions);

        // 2. Get Data for Submissions Chart
        List<Exam> allExams = examRepository.findAll();
        List<String> chartLabels = allExams.stream()
                .map(Exam::getTitle)
                .collect(Collectors.toList());
        List<Integer> chartData = allExams.stream()
                .map(exam -> exam.getResults().size())
                .collect(Collectors.toList());

        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);


        List<ExamResult> recentResults = examResultRepository.findTop5ByOrderBySubmissionTimeDesc();
        model.addAttribute("recentResults", recentResults);

        return "admin/dashboard";
    }

    // ========== ADMIN CREATION ==========

    @GetMapping("/create-admin")
    public String showCreateAdminForm(Model model) {
        model.addAttribute("newAdmin", new User());
        return "admin/create_admin";
    }

    @PostMapping("/create-admin")
    public String createAdmin(
            @RequestParam("email") String email,
            @RequestParam("fullName") String fullName,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        // Normalize email
        String normalizedEmail = email.trim().toLowerCase();

        // Validation
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/admin/create-admin";
        }

        if (userService.findByUsername(normalizedEmail) != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "An admin account already exists with that email.");
            return "redirect:/admin/create-admin";
        }

        try {
            User newAdmin = new User();
            newAdmin.setUsername(normalizedEmail);
            newAdmin.setFullName(fullName);
            newAdmin.setPassword(passwordEncoder.encode(password));
            newAdmin.setRole("ROLE_ADMIN");
            newAdmin.setCreatedBy(getAuthenticatedAdmin()); // Track the creator

            userRepository.save(newAdmin);

            redirectAttributes.addFlashAttribute("successMessage", "Admin account created successfully! Email: " + normalizedEmail);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while creating the admin account: " + e.getMessage());
        }

        return "redirect:/admin/create-admin";
    }

    @GetMapping("/team")
    public String getManageTeamPage(Model model) {
        User currentAdmin = getAuthenticatedAdmin();
        List<User> createdAdmins = userRepository.findByCreatedByAndRole(currentAdmin, "ROLE_ADMIN");
        model.addAttribute("admins", createdAdmins);
        return "admin/manage_team";
    }

    // ========== GROUP MANAGEMENT ==========

    @GetMapping("/groups")
    public String manageGroups(Model model) {
        User currentAdmin = getAuthenticatedAdmin();
        // Show only groups managed by this admin
        List<Group> adminGroups = groupRepository.findByAdmin(currentAdmin);
        model.addAttribute("groups", adminGroups);
        return "admin/manage_groups";
    }

    @GetMapping("/group/create")
    public String showCreateGroupForm(Model model) {
        model.addAttribute("group", new Group());
        return "admin/create_group";
    }

    @PostMapping("/group/create")
    public String createGroup(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttributes) {

        try {
            User currentAdmin = getAuthenticatedAdmin();

            Group group = new Group();
            group.setName(name);
            group.setDescription(description);
            group.setAdmin(currentAdmin);

            groupRepository.save(group);

            redirectAttributes.addFlashAttribute("successMessage", "Section/Class created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return "redirect:/admin/groups";
    }

    @GetMapping("/group/delete/{groupId}")
    public String deleteGroup(@PathVariable Long groupId, RedirectAttributes redirectAttributes) {
        try {
            Optional<Group> groupOpt = groupRepository.findById(groupId);
            if (!groupOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Group not found.");
                return "redirect:/admin/groups";
            }

            Group group = groupOpt.get();
            User currentAdmin = getAuthenticatedAdmin();

            // Only allow deletion if the current admin owns the group
            if (!group.getAdmin().getId().equals(currentAdmin.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only delete your own groups.");
                return "redirect:/admin/groups";
            }

            groupRepository.delete(group);
            redirectAttributes.addFlashAttribute("successMessage", "Group deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
        }

        return "redirect:/admin/groups";
    }

    @GetMapping("/manage-exams")
    public String getManageExamsPage(Model model) {
        User currentAdmin = getAuthenticatedAdmin();
        // Show only exams owned by this admin
        List<Exam> adminExams = examRepository.findAll().stream()
                .filter(exam -> exam.getOwner() != null && exam.getOwner().getId().equals(currentAdmin.getId()))
                .collect(Collectors.toList());
        model.addAttribute("exams", adminExams);
        return "admin/manage_exams";
    }

    @GetMapping("/exam/add")
    public String showAddExamForm(Model model) {
        User currentAdmin = getAuthenticatedAdmin();
        model.addAttribute("exam", new Exam());
        // Get groups managed by this admin
        List<Group> adminGroups = groupRepository.findByAdmin(currentAdmin);
        model.addAttribute("groups", adminGroups);
        return "admin/add_exam";
    }

    @PostMapping("/exam/add")
    public String addExam(
            @ModelAttribute Exam exam,
            @RequestParam(value = "groupIds", required = false) List<Long> groupIds,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        try {
            // Validation
            if (exam.getTitle() == null || exam.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Exam title is required.");
                return "redirect:/admin/exam/add";
            }

            if (exam.getDurationInMinutes() <= 0 || exam.getDurationInMinutes() > 480) {
                redirectAttributes.addFlashAttribute("errorMessage", "Duration must be between 1 and 480 minutes.");
                return "redirect:/admin/exam/add";
            }

            if (groupIds == null || groupIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one section/class for this exam.");
                return "redirect:/admin/exam/add";
            }

            User currentAdmin = getAuthenticatedAdmin();
            exam.setOwner(currentAdmin);

            // Set target groups
            List<Group> targetGroups = groupRepository.findAllById(groupIds);
            
            // Verify all groups belong to this admin
            for (Group group : targetGroups) {
                if (!group.getAdmin().getId().equals(currentAdmin.getId())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "You can only assign exams to your own sections.");
                    return "redirect:/admin/exam/add";
                }
            }

            exam.setTargetGroups(targetGroups);
            examRepository.save(exam);

            redirectAttributes.addFlashAttribute("successMessage", "Exam created successfully! Now add questions to the exam.");
            return "redirect:/admin/exam/" + exam.getId() + "/question/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/admin/exam/add";
        }
    }

    @GetMapping("/exam/{examId}/question/add")
    public String showAddQuestionForm(@PathVariable Long examId, Model model) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));
        Question question = new Question();
        question.setExam(exam);
        model.addAttribute("question", question);
        model.addAttribute("examId", examId);
        return "admin/add_question";
    }


    @PostMapping("/exam/question/add")
    public String addQuestion(@ModelAttribute Question question,
                              @RequestParam Long examId,
                              RedirectAttributes redirectAttributes) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));

        question.setExam(exam);
        questionRepository.save(question);

        redirectAttributes.addFlashAttribute("successMessage", "Question added successfully!");

        return "redirect:/admin/exam/" + examId + "/question/add";
    }

    @GetMapping("/results/{examId}")
    public String viewResults(@PathVariable Long examId, Model model) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));

        List<ExamResult> results = examResultRepository.findByExam(exam);

        model.addAttribute("exam", exam);
        model.addAttribute("results", results);

        return "admin/view_results";
    }

    @GetMapping("/exam/delete/{examId}")
    public String deleteExam(@PathVariable Long examId) {
        // CascadeType.ALL, deleting the exam will also delete
        // all associated questions and results.
        examRepository.deleteById(examId);
        return "redirect:/admin/manage-exams";
    }

    @GetMapping("/exam/edit/{examId}")
    public String showEditExamForm(@PathVariable Long examId, Model model) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));
        model.addAttribute("exam", exam);
        return "admin/edit_exam";
    }
    /***
     Subscribe Lazycoder - https://www.youtube.com/c/LazyCoderOnline?sub_confirmation=1
     whatsapp - https://wa.me/919572181024
     email - wapka1503@gmail.com
     ***/
    @PostMapping("/exam/update/{examId}")
    public String updateExam(@PathVariable Long examId, @ModelAttribute Exam updatedExam) {
        Exam existingExam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));

        existingExam.setTitle(updatedExam.getTitle());
        existingExam.setDurationInMinutes(updatedExam.getDurationInMinutes());

        examRepository.save(existingExam);
        return "redirect:/admin/manage-exams";
    }

    @GetMapping("/exam/manage-questions/{examId}")
    public String showManageQuestionsPage(@PathVariable Long examId, Model model) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid exam Id:" + examId));
        model.addAttribute("exam", exam);
        model.addAttribute("questions", exam.getQuestions());
        return "admin/manage_questions";
    }

    @GetMapping("/question/edit/{questionId}")
    public String showEditQuestionForm(@PathVariable Long questionId, Model model) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question Id:" + questionId));
        model.addAttribute("question", question);
        return "admin/edit_question.html";
    }

    @PostMapping("/question/update/{questionId}")
    public String updateQuestion(@PathVariable Long questionId, @ModelAttribute Question updatedQuestion) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question Id:" + questionId));

        existingQuestion.setText(updatedQuestion.getText());
        existingQuestion.setOption1(updatedQuestion.getOption1());
        existingQuestion.setOption2(updatedQuestion.getOption2());
        existingQuestion.setOption3(updatedQuestion.getOption3());
        existingQuestion.setOption4(updatedQuestion.getOption4());
        existingQuestion.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
        existingQuestion.setMarks(updatedQuestion.getMarks());

        questionRepository.save(existingQuestion);

        return "redirect:/admin/exam/manage-questions/" + existingQuestion.getExam().getId();
    }

    @GetMapping("/exam/{examId}/question/delete/{questionId}")
    public String deleteQuestion(@PathVariable Long examId,
                                 @PathVariable Long questionId,
                                 RedirectAttributes redirectAttributes) { // <-- Added RedirectAttributes
        try {
            questionRepository.deleteById(questionId);
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete this question. It has already been answered by one or more students and is part of their exam results.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while trying to delete the question.");
        }

        return "redirect:/admin/exam/manage-questions/" + examId;
    }


    @GetMapping("/students")
    public String getManageStudentsPage(Model model) {
        User currentAdmin = getAuthenticatedAdmin();
        // Get only groups managed by this admin
        List<Group> adminGroups = groupRepository.findByAdmin(currentAdmin);
        
        List<User> students;
        if (adminGroups.isEmpty()) {
            students = List.of(); // No groups means no students
        } else {
            // Find all students that belong to any of these groups
            students = userRepository.findByGroupIn(adminGroups);
        }
        
        model.addAttribute("students", students);
        return "admin/manage_students";
    }

    @GetMapping("/student/delete/{id}")
    @Transactional
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // 1. Find the student
            Optional<User> studentOpt = userRepository.findById(id);
            if (studentOpt.isEmpty()) {
                throw new IllegalArgumentException("Invalid student Id:" + id);
            }
            User student = studentOpt.get();

            // 2. MANUALLY delete the "children" (ExamResults)
            examResultRepository.deleteByStudent(student);

            // 3. Force Hibernate to execute the pending DELETE statements NOW.
            examResultRepository.flush();

            // 4. NOW it is safe to delete the "parent" (User)
            userRepository.delete(student);

            redirectAttributes.addFlashAttribute("successMessage", "Student account and all associated results have been deleted.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete student. A database integrity error occurred.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while trying to delete the student account: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/all-results")
    public String getAllResultsPage(Model model) {
        User currentAdmin = getAuthenticatedAdmin();
        List<Group> adminGroups = groupRepository.findByAdmin(currentAdmin);

        List<ExamResult> results;
        if (adminGroups.isEmpty()) {
            results = List.of();
        } else {
            List<User> students = userRepository.findByGroupIn(adminGroups);
            if (students.isEmpty()) {
                results = List.of();
            } else {
                results = examResultRepository.findByStudentInOrderBySubmissionTimeDesc(students);
            }
        }

        model.addAttribute("results", results);
        return "admin/all_results";
    }

    @PostMapping("/student/reset-password")
    public String resetStudentPassword(@RequestParam("userId") Long userId,
                                       @RequestParam("newPassword") String newPassword,
                                       RedirectAttributes redirectAttributes) {

        if (newPassword.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password cannot be empty.");
            return "redirect:/admin/students";
        }

        try {
            User student = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + userId));

            // Encode the new password before saving
            student.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(student);

            redirectAttributes.addFlashAttribute("successMessage", "Password for " + student.getUsername() + " has been reset.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while trying to reset the password.");
        }
        return "redirect:/admin/students";
    }
}

