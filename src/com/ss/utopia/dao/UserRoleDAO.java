package com.ss.utopia.dao;

import com.ss.utopia.entity.UserRole;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRoleDAO extends BaseDAO<UserRole> {

    // permanent identifiers for user types
    public static final int ADMIN = 1;
    public static final int TRAVELLER = 2;
    public static final int AGENT = 3;

    public UserRoleDAO(Connection conn) {
        super(conn);
    }


    /**
     * Create user roles as needed
     * @param rs SQL results to parse
     * @return all user roles
     */
    @Override
    protected List<UserRole> extractData(ResultSet rs) throws SQLException {
        List<UserRole> roles = new ArrayList<>();
        while (rs.next()) {
            UserRole role = new UserRole();
            role.setId(rs.getInt("id"));
            role.setName(rs.getString("name"));
            roles.add(role);
        }
        return roles;
    }
}
