package com.ss.utopia.entity;

import java.sql.Timestamp;

public class Flight {

    // personal vars
    private Integer flightId; // id INT
    private Integer routeId; // route_id INT
    private Integer airplaneId; // airplane_id INT
    private Timestamp dateTime; // departure_time DATETIME
    private Integer reservedSeats; // reserved_seats INT
    private Float seatPrice; // seat_price FLOAT
    private Integer maxSeats; // airplane_type.max_capacity INT

    // GET
    public Integer getFlightId() {
        return flightId;
    }
    public Integer getRouteId() {
        return routeId;
    }
    public Integer getAirplaneId() {
        return airplaneId;
    }
    public Timestamp getDateTime() {
        return dateTime;
    }
    public Integer getReservedSeats() {
        return reservedSeats;
    }
    public Float getSeatPrice() {
        return seatPrice;
    }
    public Integer getMaxSeats() { return maxSeats; }

    // SET
    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }
    public void setRouteId(Integer route) {
        this.routeId = route;
    }
    public void setAirplaneId(Integer airplane) {
        this.airplaneId = airplane;
    }
    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }
    public void setReservedSeats(Integer reservedSeats) {
        this.reservedSeats = reservedSeats;
    }
    public void setSeatPrice(Float seatPrice) {
        this.seatPrice = seatPrice;
    }
    public void setMaxSeats(Integer maxSeats) { this.maxSeats = maxSeats; }
}
