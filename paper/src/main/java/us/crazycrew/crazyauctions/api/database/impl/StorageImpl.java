package us.crazycrew.crazyauctions.api.database.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public interface StorageImpl {

    String getImplName();

    void init();

    void shutdown() throws SQLException;

    File getFile();

}