package com.example.exam.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "user_groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "Java 101 - Section A"

    @Column
    private String description;

    // Admin who created/manages this group
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    // Students in this group
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> students;

    // Exams assigned to this group
    @ManyToMany(mappedBy = "targetGroups", fetch = FetchType.LAZY)
    private List<Exam> exams;

    public Group() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }
}
