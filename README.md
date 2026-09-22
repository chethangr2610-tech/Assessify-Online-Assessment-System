# Assessify — Secure Online Assessment Platform

> A secure, role-based web application for creating, managing, assigning, and conducting online examinations.

## 📌 Overview

**Assessify** is a web-based online assessment platform developed using **Java and Spring Boot** to simplify and secure the process of conducting online examinations.

The platform provides separate workflows for **Administrators, Sub-Administrators, and Students**. Administrators can manage students, groups, examinations, questions, and exam assignments, while students can securely attempt assigned examinations and view their results.

The system also incorporates examination-control mechanisms such as **timed assessments, duplicate-attempt prevention, tab-switch monitoring, and automatic submission after repeated violations**.

The application uses **MySQL** for persistent data storage and **Spring Data JPA with Hibernate** for database interaction.

---

## ✨ Key Features

### 👨‍💼 Administrator

- Secure administrator authentication
- Administrator dashboard
- View examination and student statistics
- Create and manage examinations
- Add and manage examination questions
- Create and manage student groups
- Assign examinations to specific groups
- Manage students
- Manage sub-administrators
- Monitor examination activity
- View student results and performance
- Visualize assessment statistics using charts

### 👨‍💼 Sub-Administrator

- Controlled access to administrative functionalities
- Manage assigned examination-related activities
- Work with students, groups, and assessments according to assigned permissions

### 👨‍🎓 Student

- Secure student authentication
- View assigned examinations
- Attempt time-bound examinations
- Automatic examination countdown
- Submit examinations manually
- Automatic submission when examination time expires
- View examination results
- Track assessment performance

---

## 🔐 Examination Security Features

Assessify includes several mechanisms designed to improve examination integrity.

### ⏱️ Timed Examination

Each examination is conducted within a predefined time limit.

```text
Start Examination
       ↓
Timer Starts
       ↓
Student Attempts Questions
       ↓
Time Remaining?
    /       \
  Yes        No
   ↓          ↓
Continue   Auto Submit

When the examination time expires, the system automatically submits the student's attempt.

🚫 Duplicate Attempt Prevention

The system prevents a student from repeatedly attempting the same examination when an attempt has already been recorded.

🖥️ Tab-Switch Monitoring

The examination interface monitors tab-switch events while the student is attempting an examination.

Student Starts Examination
          ↓
    Tab Switch Detected
          ↓
   Violation Counter +1
          ↓
      Warning Shown
          ↓
  3 Violations Reached?
       /          \
     No            Yes
      ↓             ↓
  Continue      Auto Submit

After 3 tab-switch violations, the examination is automatically submitted.

🔒 Role-Based Access Control

Different users have access to different parts of the system based on their roles.

👥 User Roles
Administrator

The Administrator has access to the major management features of the platform.

Administrator
     │
     ├── Dashboard
     ├── Student Management
     ├── Sub-Administrator Management
     ├── Group Management
     ├── Examination Management
     ├── Question Management
     ├── Exam Assignment
     └── Performance Monitoring
Sub-Administrator

The Sub-Administrator provides controlled administrative support based on the permissions and responsibilities assigned by the main Administrator.

Student

Students can access examinations assigned to them.

Student
   │
   ├── Dashboard
   ├── Assigned Examinations
   ├── Attempt Examination
   ├── Submit Examination
   └── View Results
🛠️ Technology Stack
Technology	Purpose
Java 17	Core programming language
Spring Boot 3.2	Backend application framework
Spring Security	Authentication and authorization
Spring Data JPA	Data persistence
Hibernate	Object-Relational Mapping
MySQL	Relational database
Thymeleaf	Server-side HTML rendering
HTML5	Web page structure
CSS3	Styling
Bootstrap 5.3	Responsive user interface
JavaScript	Client-side functionality
Chart.js	Dashboard and performance visualization
Maven	Build and dependency management
Git	Version control
GitHub	Source code hosting
🏗️ System Architecture

Assessify follows a layered application architecture.

                     ┌──────────────────────────┐
                     │       Web Browser        │
                     │ HTML / CSS / JavaScript  │
                     │ Bootstrap / Chart.js     │
                     └────────────┬─────────────┘
                                  │
                                  ▼
                     ┌──────────────────────────┐
                     │       Controller Layer   │
                     │       Spring MVC         │
                     └────────────┬─────────────┘
                                  │
                                  ▼
                     ┌──────────────────────────┐
                     │        Service Layer     │
                     │     Business Logic       │
                     └────────────┬─────────────┘
                                  │
                                  ▼
                     ┌──────────────────────────┐
                     │      Repository Layer    │
                     │     Spring Data JPA      │
                     └────────────┬─────────────┘
                                  │
                                  ▼
                     ┌──────────────────────────┐
                     │        MySQL Database    │
                     │    Persistent Storage    │
                     └──────────────────────────┘
📚 Main Modules
1. Authentication & Authorization

Provides secure login and role-based access using Spring Security.

The application separates access according to user roles and restricts unauthorized access to protected resources.

2. User Management

Provides functionality for managing students and administrative users.

3. Group Management

Students can be organized into groups, allowing administrators to assign examinations efficiently.

4. Examination Management

Administrators can create and manage examinations and their associated questions.

5. Question Management

Questions can be created and associated with examinations for use during online assessments.

6. Examination Assignment

Administrators can assign examinations to specific student groups.

7. Online Examination

Students can attempt assigned examinations through an interactive, time-controlled examination interface.

8. Result Management

The system records examination attempts and provides students and administrators with relevant result information.

9. Performance Monitoring

Assessment information is presented through dashboards and charts to help visualize examination performance.

📊 Dashboard

The administrator dashboard provides an overview of important application statistics.

Depending on the configured features, the dashboard can display information such as:

Total students
Total examinations
Assigned examinations
Examination activity
Student performance
Assessment statistics

Chart.js is used to provide graphical representations of relevant assessment data.

🗄️ Database

Assessify uses MySQL as its relational database.

Database interaction is handled using:

Spring Data JPA
       ↓
Hibernate ORM
       ↓
MySQL

The application uses JPA entities and repositories to perform database operations while Hibernate manages object-relational mapping.

Database Configuration

The database connection is configured through the Spring Boot application properties.

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/assessify
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

Important: Do not commit actual database passwords, API keys, tokens, or other sensitive credentials to GitHub.

For local development, configure your own MySQL credentials in the application configuration.

⚙️ Getting Started
Prerequisites

Make sure the following software is installed:

Java 17
MySQL 8.0 or later
Git
Maven (optional because Maven Wrapper is included)
An IDE such as:
IntelliJ IDEA
Eclipse
Visual Studio Code
🗄️ MySQL Setup
1. Start MySQL

Make sure your MySQL server is running.

2. Create the Database

Open MySQL Workbench or MySQL Command Line Client and create the database.

CREATE DATABASE assessify;
3. Configure Database Credentials

Update the database configuration in:

src/main/resources/application.properties

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/assessify
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

Use the username and password configured on your local MySQL installation.

4. Start the Application

Spring Boot will use Hibernate/JPA to create and update the required database tables according to the application's entity configuration.

🚀 Running the Application
Clone the Repository
git clone https://github.com/chethangr2610-tech/Assessify-Online-Assessment-System.git
Navigate to the Project
cd Assessify-Online-Assessment-System
Run Using Maven Wrapper
Windows
mvnw.cmd spring-boot:run
Linux / macOS
./mvnw spring-boot:run

Alternatively, open the project in an IDE and run the main Spring Boot application class.

🌐 Access the Application

After successfully starting the application, open:

http://localhost:8080

The application port can be changed through the Spring Boot configuration.

📁 Project Structure
Assessify-Online-Assessment-System/
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ...
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       ├── static/
│   │       └── application.properties
│   │
│   └── test/
│
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md

Runtime and generated directories such as target/, data/, and uploads/ are excluded from version control.

🔒 Security Considerations

Assessify implements several mechanisms to improve application and examination security:

Spring Security authentication
Role-based authorization
Protected application resources
Duplicate examination attempt prevention
Examination time limits
Tab-switch monitoring
Automatic submission after repeated tab-switch violations
Restricted examination access
Server-side persistence of examination data

This project is intended primarily for educational and development purposes. Additional security hardening, infrastructure protection, monitoring, and testing would be required before using the system for high-stakes production examinations.

🔄 Application Workflow
                    ┌───────────────────┐
                    │      Login        │
                    └─────────┬─────────┘
                              │
                              ▼
                    ┌───────────────────┐
                    │ Authentication &  │
                    │ Authorization     │
                    └─────────┬─────────┘
                              │
                  ┌───────────┴───────────┐
                  │                       │
                  ▼                       ▼
          ┌───────────────┐       ┌───────────────┐
          │ Administrator │       │    Student    │
          └───────┬───────┘       └───────┬───────┘
                  │                       │
                  ▼                       ▼
          Manage Exams              View Assigned
          Manage Questions          Examinations
          Manage Students                │
          Manage Groups                  ▼
          Assign Exams              Start Exam
                  │                       │
                  │                       ▼
                  │                 Timed Attempt
                  │                       │
                  │                 Tab Monitoring
                  │                       │
                  │                       ▼
                  │                  Submit Exam
                  │                       │
                  └───────────┐           ▼
                              │      View Result
                              ▼
                       Monitor Results
                       & Performance
                       
                       
                       👨‍💻 Developer
Chethan G R

Computer Science Engineering

Areas of Interest
Java Development
Spring Boot
Full-Stack Development
Artificial Intelligence
Computer Vision
IoT Applications
Connect With Me
LinkedIn: Chethan G R
GitHub: chethangr2610-tech
LeetCode: Chethan_AtMan
Portfolio: chethangr-portfolio