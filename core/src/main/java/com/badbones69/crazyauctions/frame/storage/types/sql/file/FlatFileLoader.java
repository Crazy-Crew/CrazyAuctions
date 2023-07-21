package com.badbones69.crazyauctions.frame.storage.types.sql.file;

import com.badbones69.crazyauctions.frame.storage.types.sql.ConnectionManager;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

abstract class FlatFileLoader implements ConnectionManager {

    protected abstract Connection createConnection(String name, Path path) throws SQLException;

    private Connection connection;
    private final String name;
    private final Path path;

    FlatFileLoader(String name, Path path) {
        this.name = name;

        this.path = path;
    }

    @Override
    public synchronized Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed()) this.connection = createConnection(this.name, this.path);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.connection;
    }

    @Override
    public void shutdown() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected File getFile() {
        return new File(this.path.toFile(), this.name);
    }

    protected String getName() {
        return this.name;
    }

    protected Path getPath() {
        return this.path;
    }
}