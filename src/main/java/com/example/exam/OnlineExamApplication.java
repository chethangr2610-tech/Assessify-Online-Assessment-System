package com.example.exam;

import com.example.exam.model.User;
import com.example.exam.model.Group;
import com.example.exam.model.Exam;
import com.example.exam.model.Question;
import com.example.exam.repository.UserRepository;
import com.example.exam.repository.GroupRepository;
import com.example.exam.repository.ExamRepository;
import com.example.exam.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class OnlineExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineExamApplication.class, args);
	}


	//  will run once when the application starts
	@Bean
	public CommandLineRunner createDemoData(UserRepository userRepository, GroupRepository groupRepository,
									ExamRepository examRepository, QuestionRepository questionRepository,
									PasswordEncoder passwordEncoder) {
		return args -> {
			String adminUsername = "admin";
			String demoStudentUsername = "student@example.com";

			// Check if the admin user already exists
			User adminUser = userRepository.findByUsername(adminUsername).orElse(null);
			if (adminUser == null) {
				adminUser = new User();
				adminUser.setUsername(adminUsername);
				adminUser.setPassword(passwordEncoder.encode("adminpass"));
				adminUser.setRole("ROLE_ADMIN");
				adminUser.setFullName("Admin");

				userRepository.save(adminUser);
				System.out.println(">>> Admin user created: admin / adminpass <<<");
			} else {
				System.out.println(">>> Admin user 'admin' already exists. <<<");
			}

			// Create demo groups if they don't exist
			Group javaSectionA = groupRepository.findByNameAndAdmin("Java 101 - Section A", adminUser);
			if (javaSectionA == null) {
				javaSectionA = new Group();
				javaSectionA.setName("Java 101 - Section A");
				javaSectionA.setDescription("First semester Java programming students");
				javaSectionA.setAdmin(adminUser);
				groupRepository.save(javaSectionA);
				System.out.println(">>> Demo group created: Java 101 - Section A <<<");
			}

			Group javaSectionB = groupRepository.findByNameAndAdmin("Java 101 - Section B", adminUser);
			if (javaSectionB == null) {
				javaSectionB = new Group();
				javaSectionB.setName("Java 101 - Section B");
				javaSectionB.setDescription("Second semester Java programming students");
				javaSectionB.setAdmin(adminUser);
				groupRepository.save(javaSectionB);
				System.out.println(">>> Demo group created: Java 101 - Section B <<<");
			}

			// Create a demo student account for localhost presentation
			User demoStudent = userRepository.findByUsername(demoStudentUsername).orElse(null);
			if (demoStudent == null) {
				demoStudent = new User();
				demoStudent.setUsername(demoStudentUsername);
				demoStudent.setPassword(passwordEncoder.encode("studentpass"));
				demoStudent.setRole("ROLE_STUDENT");
				demoStudent.setFullName("Demo Student");
				demoStudent.setGroup(javaSectionA); // Assign to Section A

				userRepository.save(demoStudent);
				System.out.println(">>> Demo student created: student@example.com / studentpass (assigned to Java 101 - Section A) <<<");
			} else {
				System.out.println(">>> Demo student already exists. <<<");
			}

			// Create demo exam if it doesn't exist
			if (examRepository.findAll().isEmpty()) {
				Exam demoExam = new Exam();
				demoExam.setTitle("Java Fundamentals Quiz");
				demoExam.setDurationInMinutes(30);
				demoExam.setOwner(adminUser);
				demoExam.setTargetGroups(Arrays.asList(javaSectionA, javaSectionB)); // Assign to both sections

				examRepository.save(demoExam);

				// Create sample questions
				Question q1 = new Question();
				q1.setText("What is the correct way to declare a variable in Java?");
				q1.setOption1("var x = 5;");
				q1.setOption2("int x = 5;");
				q1.setOption3("variable x = 5;");
				q1.setOption4("declare x = 5;");
				q1.setCorrectAnswer(2);
				q1.setMarks(5);
				q1.setExam(demoExam);
				questionRepository.save(q1);

				Question q2 = new Question();
				q2.setText("Which of the following is a valid Java identifier?");
				q2.setOption1("123variable");
				q2.setOption2("_variable");
				q2.setOption3("variable-name");
				q2.setOption4("variable name");
				q2.setCorrectAnswer(2);
				q2.setMarks(5);
				q2.setExam(demoExam);
				questionRepository.save(q2);

				Question q3 = new Question();
				q3.setText("What does JVM stand for?");
				q3.setOption1("Java Virtual Machine");
				q3.setOption2("Java Variable Method");
				q3.setOption3("Java Virtual Memory");
				q3.setOption4("Java Variable Machine");
				q3.setCorrectAnswer(1);
				q3.setMarks(5);
				q3.setExam(demoExam);
				questionRepository.save(q3);

				System.out.println(">>> Demo exam created: Java Fundamentals Quiz (assigned to both sections) <<<");
			} else {
				System.out.println(">>> Demo exam already exists. <<<");
			}
		};
	}
}
