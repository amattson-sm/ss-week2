package com.ss.utopia.entity;

public class Airplane {

    // vars
    private Integer id;
    private Integer type;
    private Integer capacity;

    // GET
    public Integer getId() {
        return id;
    }
    public Integer getType() {
        return type;
    }
    public Integer getCapacity() {
        return capacity;
    }

    // SET
    public void setId(Integer id) {
        this.id = id;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
