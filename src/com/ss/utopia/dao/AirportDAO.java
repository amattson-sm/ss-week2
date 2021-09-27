package com.ss.utopia.dao;

import com.ss.utopia.entity.Airport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AirportDAO extends BaseDAO<Airport> {

    // default constructor
    public AirportDAO(Connection conn) {
        super(conn);
    }


    /**
     * Insert a new airport
     * @param airport the airport to insert
     */
    public void addAirport(Airport airport) throws SQLException {
        save("INSERT INTO airport (iata_id, city) VALUES (?, ?)", new Object[] {
            airport.getAirportCode(), airport.getCityName() });
    }


    /**
     * Modify airport values (not ID)
     * @param airport object containing updated values
     */
    public void updateAirport(Airport airport) throws SQLException {
        save("UPDATE airport SET city = ? WHERE iata_id = ?",
            new Object[] { airport.getCityName(), airport.getAirportCode() });
    }


    /**
     * remove an airport by ID
     * @param airport object containing ID to remove
     */
    public void deleteAirport(Airport airport) throws SQLException {
        save("DELETE FROM airport WHERE iata_id = ?", new Object[] { airport.getAirportCode() });
    }


    /**
     * Read all airports in the database
     * @return list of found airports as objects
     */
    public List<Airport> readAirports() throws SQLException, ClassNotFoundException {
        return read("SELECT * FROM airport", null);
    }


    /**
     * find all airports given a single airport code
     * @param airportCode the key to search for
     * @return list of objects found
     */
    public List<Airport> readAirportsById(String airportCode) throws SQLException, ClassNotFoundException {
        return read("SELECT * FROM airport WHERE iata_id = ?",
            new Object[] { airportCode });
    }


    /**
     * parse the SQL data received
     * @param rs SQL result set
     * @return objects parsed
     */
    @Override
    protected List<Airport> extractData(ResultSet rs) throws SQLException {
        List<Airport> airports = new ArrayList<>();
        while (rs.next()) {
            Airport airport = new Airport();
            airport.setAirportCode(rs.getString("iata_id"));
            airport.setCityName(rs.getString("city"));
            airports.add(airport);
        }
        return airports;
    }
}
