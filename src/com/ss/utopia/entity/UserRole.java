package com.ss.utopia.entity;

public class UserRole {

    // personal vars
    private Integer id; // id INT
    private String name; // name VARCHAR(45)

    // GET
    public Integer getId() { return id; }
    public String getName() { return name; }

    // SET
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
