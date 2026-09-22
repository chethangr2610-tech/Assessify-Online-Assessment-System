package com.example.exam.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects; // Import for equals/hashCode

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;



    // We can use the 'username' field for email, or add a separate one.


    @Column(nullable = true) // Mobile number can be optional
    private String mobileNumber;

    @Column(nullable = true)
    private String fullName;

    @Column(nullable = true) // Profile pic URL is optional
    private String profilePicUrl;


    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExamResult> results = new ArrayList<>();

    // For students: which group/section they belong to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    // For admins: groups they manage/created
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Group> managedGroups = new ArrayList<>();

    // For admins: track who created this admin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = true)
    private User createdBy;

    // For admins: track admins they created
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<User> createdAdmins = new ArrayList<>();

    // --- Manual Getters and Setters ---


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<ExamResult> getResults() {
        return results;
    }

    public void setResults(List<ExamResult> results) {
        this.results = results;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Group> getManagedGroups() {
        return managedGroups;
    }

    public void setManagedGroups(List<Group> managedGroups) {
        this.managedGroups = managedGroups;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<User> getCreatedAdmins() {
        return createdAdmins;
    }

    public void setCreatedAdmins(List<User> createdAdmins) {
        this.createdAdmins = createdAdmins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

