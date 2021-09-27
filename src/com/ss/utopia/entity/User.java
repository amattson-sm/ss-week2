package com.ss.utopia.entity;

public class User {

    // personal vars
    private Integer id;
    private Integer role; // role_id INT
    private String firstName; // given_name VARCHAR(255)
    private String lastName; // family_name VARCHAR(255)
    private String username; // username VARCHAR(45)
    private String email; // email VARCHAR(255)
    private String password; // password VARCHAR(255)
    private String phone; // phone VARCHAR(45)

    // GET
    public Integer getId() { return id; }
    public Integer getRole() {
        return role;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getPhone() {
        return phone;
    }

    // SET
    public void setId(Integer id) { this.id = id; }
    public void setRole(Integer role) {
        this.role = role;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
