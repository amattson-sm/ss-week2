package com.ss.utopia.dao;

import com.ss.utopia.entity.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO<User> {

    public UserDAO(Connection conn) {
        super(conn);
    }

    // read user with a specific login
    public User readUserByLogin(String username, String password) throws SQLException, ClassNotFoundException {
        List<User> out = read("SELECT * FROM user WHERE username = ? AND password = ?",
            new Object[] { username, password });
        User login = null;
        if (out.size() > 0)
            login = out.get(0);
        return login;
    }

    // get user with a given ID in a role
    public User readUserByRoleAndId(Integer id, Integer role) throws SQLException, ClassNotFoundException {
        List<User> out = read("SELECT * FROM user WHERE id = ? AND role_id = ?",
            new Object[] { id, role });
        User find = null;
        if (out.size() > 0)
            find = out.get(0);
        return find;
    }

    // get users that fit a role (admin, traveller, agent)
    public List<User> readUserByRole(Integer role) throws SQLException, ClassNotFoundException {
        return read("SELECT * FROM user WHERE role_id = ?",
            new Object[] { role } );
    }

    // add a new user from object
    public void addUser(User insert) throws SQLException {
        save("INSERT INTO user (role_id, given_name, family_name, username, email, password, phone) VALUES (?, ?, ?, ?, ?, ?, ?)",
            new Object[] { insert.getRole(), insert.getFirstName(), insert.getLastName(), insert.getUsername(),
                insert.getEmail(), insert.getPassword(), insert.getPhone() });
    }

    // remove a specified user
    public void deleteUser(User user) throws SQLException {
        save("DELETE FROM user WHERE id = ?", new Object[] { user.getId() });
    }

    // update a specified user
    public void updateUser(User user) throws SQLException {
        save("UPDATE user SET role_id = ?, given_name = ?, family_name = ?, username = ?, email = ?, password = ?, phone = ? WHERE id = ?",
            new Object[] { user.getRole(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPhone(), user.getId() });
    }

    @Override
    protected List<User> extractData(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setRole(rs.getInt("role_id"));
            user.setFirstName(rs.getString("given_name"));
            user.setLastName(rs.getString("family_name"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setPhone(rs.getString("phone"));
            users.add(user);
        }
        return users;
    }
}
