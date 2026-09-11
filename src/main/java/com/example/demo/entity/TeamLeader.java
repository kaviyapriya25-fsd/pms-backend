package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "team_leaders")
public class TeamLeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable =false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String department;

    // Assigned automatically from Project Module
    private String project;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(nullable = false)
    private String role = "TEAM_LEADER";

    private LocalDate joiningDate;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="company_id")
    private Company company;

    @PrePersist
    public void onCreate(){

        createdAt = LocalDateTime.now();

        if(joiningDate==null)
            joiningDate=LocalDate.now();

        if(project==null)
            project="";

    }

    public TeamLeader(){}

    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id=id; }

    public String getName(){ return name; }
    public void setName(String name){ this.name=name; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email=email; }

    public String getPassword(){ return password; }
    public void setPassword(String password){ this.password=password; }

    public String getPhone(){ return phone; }
    public void setPhone(String phone){ this.phone=phone; }

    public String getDepartment(){ return department; }
    public void setDepartment(String department){ this.department=department; }

    public String getProject(){ return project; }
    public void setProject(String project){ this.project=project; }

    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status=status; }

    public String getRole(){ return role; }

    public LocalDate getJoiningDate(){ return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate){ this.joiningDate=joiningDate; }

    public LocalDateTime getCreatedAt(){ return createdAt; }

    public Company getCompany(){ return company; }
    public void setCompany(Company company){ this.company=company; }

}