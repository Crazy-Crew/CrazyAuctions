package com.ryderbelserion.crazyauctions.api.database.impl.sql;

import com.ryderbelserion.crazyauctions.api.database.impl.StorageImpl;
import com.ryderbelserion.crazyauctions.api.database.impl.sql.file.ConnectionImpl;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlStorage implements StorageImpl {

    private final ConnectionImpl connection;

    public SqlStorage(ConnectionImpl connection) {
        this.connection = connection;
        this.connection.start();
    }

    @Override
    public String getImplName() {
        return this.connection.getImplName();
    }

    @Override
    public void start() {}

    @Override
    public void shutdown() throws SQLException {
        this.connection.shutdown();
    }

    @Override
    public File getFile() {
        return this.connection.getFile();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection.getConnection();
    }
}