package com.example.exam.controller;

import com.example.exam.dto.UserRegistrationDto;
import com.example.exam.model.User;
import com.example.exam.model.Group;
import com.example.exam.repository.GroupRepository;
import com.example.exam.service.FileStorageService;
import com.example.exam.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping("/")
    public String home() {
        // Get the current user's authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {


            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch("ROLE_ADMIN"::equals);

            if (isAdmin) {
                // If ADMIN, redirect to admin dashboard
                return "redirect:/admin/dashboard";
            } else {
                // If STUDENT, redirect to student dashboard
                return "redirect:/student/dashboard";
            }
        }

        return "index";
    }


    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        // Get all groups available for students to select
        List<Group> allGroups = groupRepository.findAll();
        model.addAttribute("groups", allGroups);
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(
            @Valid @ModelAttribute("user") UserRegistrationDto userDto,
            BindingResult bindingResult,
            Model model) {

        if (userDto.getUsername() != null) {
            String normalizedUsername = userDto.getUsername().trim().toLowerCase();
            userDto.setUsername(normalizedUsername);
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Passwords do not match.");
        }

        if (userService.findByUsername(userDto.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.user", "An account already exists with that email.");
        }

        if (userDto.getGroupId() == null) {
            bindingResult.rejectValue("groupId", "error.user", "Please select your section/class.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            List<Group> allGroups = groupRepository.findAll();
            model.addAttribute("groups", allGroups);
            return "register";
        }

        // Fetch the group
        Optional<Group> groupOptional = groupRepository.findById(userDto.getGroupId());
        if (!groupOptional.isPresent()) {
            bindingResult.rejectValue("groupId", "error.user", "Selected section/class is not valid.");
            model.addAttribute("user", userDto);
            List<Group> allGroups = groupRepository.findAll();
            model.addAttribute("groups", allGroups);
            return "register";
        }

        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setMobileNumber(userDto.getMobileNumber());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setGroup(groupOptional.get()); // Assign the group

        if (userDto.getProfilePicFile() != null && !userDto.getProfilePicFile().isEmpty()) {
            try {
                String filePath = fileStorageService.saveFile(userDto.getProfilePicFile());
                user.setProfilePicUrl(filePath);
            } catch (Exception e) {
                model.addAttribute("user", userDto);
                model.addAttribute("fileError", "Could not save profile picture. Please try again.");
                List<Group> allGroups = groupRepository.findAll();
                model.addAttribute("groups", allGroups);
                return "register";
            }
        }

        userService.saveStudent(user);
        return "redirect:/login?registered=true";
    }
}
