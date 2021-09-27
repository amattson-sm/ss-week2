package com.ss.utopia.dao;

import com.ss.utopia.entity.Booking;
import com.ss.utopia.entity.Passenger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Booking objects - a massive union of booking, flight booking, booking payment, booking user, and passengers
 */
public class BookingDAO extends BaseDAO<Booking> {

    public BookingDAO(Connection conn) {
        super(conn);
    }


    /**
     * Read all booking objects with relevant data
     * @return list of objects found
     */
    public List<Booking> readBookings() throws SQLException, ClassNotFoundException {
        return read("SELECT booking.id, booking_user.user_id, booking.is_active, booking_payment.stripe_id, flight_bookings.flight_id, booking.confirmation_code FROM " +
            "booking INNER JOIN booking_payment ON booking_payment.booking_id = booking.id " +
            "INNER JOIN flight_bookings ON flight_bookings.booking_id = booking.id " +
            "INNER JOIN booking_user ON booking_user.booking_id = booking.id",
            null);
    }


    /**
     * Read all booking objects that have been refunded
     * @return list of objects found
     */
    public List<Booking> readRefundedBookings() throws SQLException, ClassNotFoundException {
        return read("SELECT booking.id, booking_user.user_id, booking.is_active, booking_payment.stripe_id, flight_bookings.flight_id, booking.confirmation_code FROM " +
                "booking INNER JOIN booking_payment ON booking_payment.booking_id = booking.id " +
                "INNER JOIN flight_bookings ON flight_bookings.booking_id = booking.id " +
                "INNER JOIN booking_user ON booking_user.booking_id = booking.id WHERE booking.is_active = 0",
            null);
    }


    /**
     * Read booking object by ID
     * @return booking found, or null if none
     */
    public Booking readBookingById(Integer id) throws SQLException, ClassNotFoundException {
        List<Booking> out = read("SELECT booking.id, booking_user.user_id, booking.is_active, booking_payment.stripe_id, flight_bookings.flight_id, booking.confirmation_code FROM " +
                "booking INNER JOIN booking_payment ON booking_payment.booking_id = booking.id " +
                "INNER JOIN flight_bookings ON flight_bookings.booking_id = booking.id " +
                "INNER JOIN booking_user ON booking_user.booking_id = booking.id WHERE id = ?",
            new Object[] { id });
        if (out.size() == 0) return null;
        return out.get(0);
    }


    /**
     * remove a booking
     * @param id booking to remove
     */
    public void deleteBooking(Integer id) throws SQLException {
        save("DELETE FROM booking WHERE id = ?", new Object[] {id});
    }


    /**
     * Read an inactive and refunded booking by its ID
     * @return object found if any, null if not
     */
    public Booking readInactiveBookingById(Integer id) throws SQLException, ClassNotFoundException {
        List<Booking> out = read("SELECT * FROM booking WHERE id = ? AND is_active = 0",
            new Object[] { id });
        if (out.size() == 0) return null;
        return out.get(0);
    }


    /**
     * flips is_active and refunded triggers to signify a cancelled or un-cancelled ticket
     */
    public void overrideRefund(Integer id, Boolean active) throws SQLException {
        save("UPDATE booking SET is_active = ? WHERE id = ?",
            new Object[] { active?1:0, id });
        save("UPDATE booking_payment SET refunded = ? WHERE booking_id = ?",
            new Object[] { active?0:1, id });
    }


    /**
     * Adds a booking object, declassifying into relevant tables
     * @return the primary key of the booking after inserting
     */
    public Integer addBooking(Booking booking) throws SQLException {
        PassengerDAO passengers = new PassengerDAO(conn);
        Integer pk = saveWithPK("INSERT INTO booking (is_active, confirmation_code) VALUES (?, ?)",
            new Object[] { booking.getActive(), booking.getConfirmation() });
        save("INSERT INTO booking_payment (booking_id, stripe_id, refunded) VALUES (?, ?, ?)",
            new Object[] { pk, booking.getCardStripe(), booking.getActive() ? 0 : 1 });
        save("INSERT INTO flight_bookings (flight_id, booking_id) VALUES (?, ?)",
            new Object[] { booking.getFlightId(), pk });
        save("INSERT INTO booking_user (booking_id, user_id) VALUES (?, ?)",
            new Object[] { pk, booking.getBookingUser() });
        List<Passenger> passengerList = booking.getPassengers();
        if (passengerList != null) {
            for (Passenger p : booking.getPassengers()) {
                passengers.addPassenger(p);
            }
        }
        return pk;
    }


    /**
     * Interpret the conglomerate table into a single booking object
     * @param rs the SQL results to parse
     * @return bookings after parsing
     */
    @Override
    protected List<Booking> extractData(ResultSet rs) throws ClassNotFoundException, SQLException {
        List<Booking> bookings = new ArrayList<>();
        PassengerDAO passengers = new PassengerDAO(conn);
        while (rs.next()) {
            Booking booking = new Booking();
            booking.setId(rs.getInt("id"));
            booking.setActive(rs.getInt("is_active") == 1 ? Boolean.FALSE : Boolean.TRUE);
            booking.setBookingUser(rs.getInt("user_id"));
            booking.setCardStripe(rs.getString("stripe_id"));
            booking.setFlightId(rs.getInt("flight_id"));
            booking.setPassengers(passengers.getPassengersByBooking(booking.getId()));
            booking.setConfirmation(rs.getInt("confirmation_code"));
            bookings.add(booking);
        }
        return bookings;
    }
}
