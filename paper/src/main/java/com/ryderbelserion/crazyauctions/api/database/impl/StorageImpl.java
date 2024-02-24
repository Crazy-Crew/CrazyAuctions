package com.ryderbelserion.crazyauctions.api.database.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public interface StorageImpl {

    String getImplName();

    void start();

    void shutdown() throws SQLException;

    File getFile();

    Connection getConnection() throws SQLException;

}