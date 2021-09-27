package com.ss.utopia.entity;

public class Route {

    // personal vars
    private Integer routeId; // id INT
    private String originAirport; // origin_id CHAR(3)
    private String destinationAirport; // destination_id CHAR(3)

    // GET
    public Integer getRouteId() {
        return routeId;
    }
    public String getOriginAirport() {
        return originAirport;
    }
    public String getDestinationAirport() {
        return destinationAirport;
    }

    // SET
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }
    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }
    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

}
