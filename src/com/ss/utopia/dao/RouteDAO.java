package com.ss.utopia.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ss.utopia.entity.Route;

public class RouteDAO extends BaseDAO<Route> {

    public RouteDAO(Connection conn) {
        super(conn);
    }

    // add a new route
    public void addRoute(Route route) throws SQLException {
        save("INSERT INTO route (origin_id, destination_id) VALUES (?, ?)", new Object[] {
            route.getOriginAirport(), route.getDestinationAirport() });
    }

    // find a specific route, and return it if it exists; otherwise null
    public Route readRouteByAirportCodes(String origin, String destination) throws SQLException, ClassNotFoundException {
        List<Route> out = read("SELECT * FROM route WHERE origin_id = ? AND destination_id = ?",
            new Object[] { origin, destination });
        if (out.size() == 0) return null;
        return out.get(0);
    }

    // route-specific method for extracting data
    @Override
    protected List<Route> extractData(ResultSet rs) throws SQLException {
        List<Route> routes = new ArrayList<>();
        while (rs.next()) {
            Route route = new Route();
            route.setRouteId(rs.getInt("id"));
            route.setOriginAirport(rs.getString("origin_id"));
            route.setDestinationAirport(rs.getString("destination_id"));
            routes.add(route);
        }
        return routes;
    }
}
