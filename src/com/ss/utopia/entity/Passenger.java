package com.ss.utopia.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

public class Passenger {

    // vars
    private Integer id; // id (INT)
    private String givenName, familyName; // given_name (CHAR(255)), familyName(CHAR(255))
    private LocalDate dob; // dob (DATE)
    private String gender; // gender (CHAR(45))
    private String address; // address (CHAR(45))
    private Integer bookingId; // booking_id (INT)

    // GET
    public Integer getId() {
        return id;
    }
    public String getGivenName() {
        return givenName;
    }
    public String getFamilyName() {
        return familyName;
    }
    public LocalDate getDob() {
        return dob;
    }
    public String getGender() {
        return gender;
    }
    public String getAddress() {
        return address;
    }
    public Integer getBookingId() { return bookingId; }

    // SET
    public void setId(Integer id) {
        this.id = id;
    }
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setBookingId(Integer bookingId) {this.bookingId = bookingId; }
}
