package com.ss.utopia.entity;

import java.util.List;

/**
 * Contains: booking, booking_payment, booking_user, flight_booking
 */
public class Booking {

    // vars
    private Integer id; // booking.id (INT)
    private Boolean isActive; // booking.is_active, booking_payment.refunded (TINYINT)
    private List<Passenger> passengers; // passenger table
    private Integer bookingUser; // booking_user.user_id (INT)
    private String cardStripe; // booking_payment.stripe_id (CHAR(255))
    private Integer flightId; // flight_booking.flight_id (INT)
    private Integer confirmation; // booking.confirmation_code (CHAR(255))

    // GET
    public Integer getId() {
        return id;
    }
    public Boolean getActive() {
        return isActive;
    }
    public List<Passenger> getPassengers() {
        return passengers;
    }
    public Integer getBookingUser() {
        return bookingUser;
    }
    public String getCardStripe() { return cardStripe; }
    public Integer getFlightId() { return flightId; }
    public Integer getConfirmation() { return confirmation; }

    // SET
    public void setId(Integer id) {
        this.id = id;
    }
    public void setActive(Boolean active) {
        isActive = active;
    }
    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
    public void setBookingUser(Integer bookingUser) {
        this.bookingUser = bookingUser;
    }
    public void setCardStripe(String cardStripe) { this.cardStripe = cardStripe; }
    public void setFlightId(Integer flightId) { this.flightId = flightId; }
    public void setConfirmation(Integer confirmation) { this.confirmation = confirmation; }
}
