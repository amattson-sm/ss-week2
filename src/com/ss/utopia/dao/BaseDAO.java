package com.ss.utopia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Base class for reading/saving to SQL database
 * @param <T> extends into object types for reading and saving
 */
public abstract class BaseDAO<T> {

    // vars
    protected static Connection conn = null;
    public BaseDAO(Connection conn) {
        BaseDAO.conn = conn;
    }

    // abstract class for getting table data on a per-type basis
    abstract protected List<T> extractData(ResultSet rs) throws ClassNotFoundException, SQLException;


    /**
     * Write a query to the database with no return
     * @param sql string with SQL query
     * @param vals inserted values for the SQL query
     */
    protected void save(String sql, Object[] vals) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        if(vals!=null) {
            int ct = 1;
            for(Object o: vals) {
                pstmt.setObject(ct, o);
                ct++;
            }
        }
        pstmt.execute();
    }


    /**
     * Write a query to the database and save the key saved under
     * @param sql string with SQL query
     * @param vals inserted values for the SQL query
     */
    protected Integer saveWithPK(String sql, Object[] vals) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        if(vals!=null) {
            int ct = 1;
            for(Object o: vals) {
                pstmt.setObject(ct, o);
                ct++;
            }
        }
        pstmt.execute();
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next())
            return rs.getInt(1);
        return null;
    }


    /**
     * Write a query to the database and save all resulting table values
     * @param sql string with SQL query
     * @param vals inserted values for the SQL query
     */
    protected List<T> read(String sql, Object[] vals) throws ClassNotFoundException, SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        if(vals!=null) {
            int ct = 1;
            for(Object o: vals) {
                pstmt.setObject(ct, o);
                ct++;
            }
        }
        ResultSet rs = pstmt.executeQuery();
        return extractData(rs);
    }
}
