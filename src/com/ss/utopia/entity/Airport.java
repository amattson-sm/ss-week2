package com.ss.utopia.entity;

public class Airport {

    // personal vars
    private String airportCode; // iata_id CHAR(3)
    private String cityName; // city VARCHAR(45)

    // GET
    public String getAirportCode() {
        return airportCode;
    }
    public String getCityName() {
        return cityName;
    }

    // SET
    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
