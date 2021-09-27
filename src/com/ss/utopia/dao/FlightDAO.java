package com.ss.utopia.dao;

import com.ss.utopia.entity.Airplane;
import com.ss.utopia.entity.Flight;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO extends BaseDAO<Flight> {

    public FlightDAO(Connection conn) {
        super(conn);
    }

    // insert a new flight
    public void addFlight(Flight flight) throws SQLException {
        save("INSERT INTO flight (route_id, airplane_id, departure_time, reserved_seats, seat_price) VALUES (?, ?, ?, ?, ?)", new Object[] {
            flight.getRouteId(), flight.getAirplaneId(), flight.getDateTime(), flight.getReservedSeats(), flight.getSeatPrice() });
    }

    // read all flights
    public List<Flight> readFlights() throws SQLException, ClassNotFoundException {
        return read("SELECT * FROM flight", null);
    }

    // read flight by id
    public List<Flight> readFlightsById(Integer id) throws SQLException, ClassNotFoundException {
        return read("SELECT * FROM flight WHERE id = ?", new Object[] { id });
    }

    // update a flight
    public void updateFlight(Flight flight) throws SQLException {
        save("UPDATE flight SET departure_time = ?, reserved_seats = ?, seat_price = ? WHERE id = ?",
            new Object[] { flight.getDateTime(), flight.getReservedSeats(), flight.getSeatPrice(), flight.getFlightId() });
    }

    // get flights with an airplane id
    public List<Flight> getFlightsByAirplane(Integer id) throws SQLException, ClassNotFoundException {
        return read("SELECT * FROM flight WHERE airplane_id = ?",
            new Object[] { id } );
    }

    // delete a flight
    public void deleteFlight(Flight flight) throws SQLException {
        save("DELETE FROM flight WHERE id = ?", new Object[] {flight.getFlightId()});
    }

    // parse result information from a query
    @Override
    protected List<Flight> extractData(ResultSet rs) throws SQLException, ClassNotFoundException {
        List<Flight> flights = new ArrayList<>();
        while (rs.next()) {
            Flight flight = new Flight();
            flight.setFlightId(rs.getInt("id"));
            flight.setRouteId(rs.getInt("route_id"));
            flight.setAirplaneId(rs.getInt("airplane_id"));
            flight.setDateTime(rs.getTimestamp("departure_time"));
            flight.setReservedSeats(rs.getInt("reserved_seats"));
            flight.setSeatPrice(rs.getFloat("seat_price"));
            // get airplane info
            Airplane plane = new AirplaneDAO(conn).readAirplanesById(flight.getAirplaneId());
            flight.setMaxSeats(plane.getCapacity());
            flights.add(flight);
        }
        return flights;
    }
}
