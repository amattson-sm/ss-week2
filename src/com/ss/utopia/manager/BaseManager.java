package com.ss.utopia.manager;

import java.sql.Connection;

public abstract class BaseManager {

    protected static Connection conn = null;

    public BaseManager(Connection conn) {
        BaseManager.conn = conn;
    }

    // base class for holding interactive options
    abstract protected void printOptions();

    // base class for running option collection and db modification
    abstract public Boolean execute();

    // methods to interface with the DAO
    abstract protected Boolean addObject();
    abstract protected Boolean removeObject();
    abstract protected Boolean updateObject();
}
