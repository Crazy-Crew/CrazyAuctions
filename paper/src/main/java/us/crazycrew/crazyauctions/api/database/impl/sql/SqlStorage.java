package us.crazycrew.crazyauctions.api.database.impl.sql;

import us.crazycrew.crazyauctions.api.database.impl.StorageImpl;
import us.crazycrew.crazyauctions.api.database.impl.sql.file.ConnectionImpl;
import java.io.File;
import java.sql.SQLException;

public class SqlStorage implements StorageImpl {

    private final ConnectionImpl connection;

    public SqlStorage(ConnectionImpl connection) {
        this.connection = connection;
    }

    @Override
    public String getImplName() {
        return this.connection.getImplName();
    }

    @Override
    public void init() {
        this.connection.init();
    }

    @Override
    public void shutdown() throws SQLException {
        this.connection.shutdown();
    }

    @Override
    public File getFile() {
        return this.connection.getFile();
    }
}