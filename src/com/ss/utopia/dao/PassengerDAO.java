package com.ss.utopia.dao;

import com.ss.utopia.entity.Passenger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PassengerDAO extends BaseDAO<Passenger> {

    public PassengerDAO(Connection conn) {
        super(conn);
    }

    public List<Passenger> getPassengersByBooking(Integer id) throws SQLException, ClassNotFoundException {
        return read("SELECT * FROM passenger WHERE booking_id = ?",
            new Object[] { id });
    }

    public void addPassenger(Passenger p) throws SQLException {
        save("INSERT INTO passenger (booking_id, given_name, family_name, dob, gender, address)" +
            "VALUES (?, ?, ?, ?, ?, ?)",
            new Object[] { p.getBookingId(), p.getGivenName(), p.getFamilyName(), p.getDob(), p.getGender(), p.getAddress() });
    }

    public void deletePassenger(Integer id) throws SQLException {
        save("DELETE FROM passenger WHERE id = ?", new Object[] { id });
    }

    @Override
    protected List<Passenger> extractData(ResultSet rs) throws SQLException {
        List<Passenger> passengers = new ArrayList<>();
        while (rs.next()) {
            Passenger passenger = new Passenger();
            passenger.setId(rs.getInt("id"));
            passenger.setGivenName(rs.getString("given_name"));
            passenger.setFamilyName(rs.getString("family_name"));
            passenger.setDob(rs.getTimestamp("dob").toLocalDateTime().toLocalDate());
            passenger.setGender(rs.getString("gender"));
            passenger.setAddress(rs.getString("address"));
            passengers.add(passenger);
        }
        return passengers;
    }
}
