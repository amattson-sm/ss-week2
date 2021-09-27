package com.ss.utopia.dao;

import com.ss.utopia.entity.Airplane;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AirplaneDAO extends BaseDAO<Airplane> {

    // basic constructor
    public AirplaneDAO(Connection conn) {
        super(conn);
    }


    /**
     * Read all airplanes joined to airplane type for a conglomerate object
     * @return list of airplanes
     */
    public List<Airplane> readAirplanes() throws SQLException, ClassNotFoundException {
        return read("SELECT airplane.id, type_id, max_capacity FROM airplane INNER JOIN airplane_type " +
            "ON airplane_type.id = airplane.type_id;", null);
    }


    /**
     * Gets a single airplane by ID
     * @param id the ID to search for
     * @return the airplane, or null if not found
     */
    public Airplane readAirplanesById(Integer id) throws SQLException, ClassNotFoundException {
        List<Airplane> out = read("SELECT airplane.id, type_id, max_capacity FROM airplane INNER JOIN airplane_type " +
            "ON airplane_type.id = airplane.type_id WHERE airplane.id = ?",
            new Object[] { id });
        if (out.size() == 0) return null;
        return out.get(0);
    }

    public void addAirplaneWithType(Integer capacity) throws SQLException {
        Integer save = saveWithPK("INSERT INTO airplane_type (max_capacity) VALUES (?)",
            new Object[] { capacity });
        save("INSERT INTO airplane (type_id) VALUES (?)",
            new Object[] { save });
    }


    public Integer addType(Integer capacity) throws SQLException {
        return saveWithPK("INSERT INTO airplane_type (max_capacity) VALUES (?)",
            new Object[] { capacity });
    }


    public void addAirplane(Integer type) throws SQLException {
        save("INSERT INTO airplane (type_id) VALUES (?)",
            new Object[] { type });
    }


    public void removeAirplane(Integer id) throws SQLException {
        save("DELETE FROM airplane WHERE id = ?",
            new Object[] { id });
    }

    public void updateAirplane(Integer type, Integer id) throws SQLException {
        save("UPDATE airplane SET type_id = ? WHERE id = ?",
            new Object[] { type, id });
    }


    /**
     * get a plane by its capacity
     */
    public Integer getIdByCapacity(Integer capacity) throws SQLException, ClassNotFoundException {
        List<Airplane> out = read("SELECT airplane.id, type_id, max_capacity FROM airplane INNER JOIN airplane_type " +
            "ON airplane_type.id = airplane.type_id WHERE airplane_type.max_capacity = ?",
            new Object[] { capacity });
        if (out.size() == 0) return null;
        return out.get(0).getType();
    }


    /**
     * Read a result set and construct an Airplane object
     * @param rs the SQL results to parse
     * @return a list of objects created
     */
    @Override
    protected List<Airplane> extractData(ResultSet rs) throws SQLException {
        List<Airplane> airplanes = new ArrayList<>();
        while (rs.next()) {
            Airplane airplane = new Airplane();
            airplane.setId(rs.getInt("id"));
            airplane.setType(rs.getInt("type_id"));
            airplane.setCapacity(rs.getInt("max_capacity"));
            airplanes.add(airplane);
        }
        return airplanes;
    }
}
