package com.ryderbelserion.crazyauctions.api.database.impl.sql.file.types;

import com.ryderbelserion.crazyauctions.api.database.impl.sql.file.ConnectionImpl;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnection extends ConnectionImpl {

    public SqliteConnection(File file) {
        super(file);
    }

    @Override
    public String getImplName() {
        return "SQLite";
    }

    @Override
    public void start() {
        try {
            getFile().createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected Connection create() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + getFile());
    }
}