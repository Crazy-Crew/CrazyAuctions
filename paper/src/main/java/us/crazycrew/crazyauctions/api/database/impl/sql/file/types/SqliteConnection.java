package us.crazycrew.crazyauctions.api.database.impl.sql.file.types;

import us.crazycrew.crazyauctions.api.database.impl.sql.file.ConnectionImpl;
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
    public void init() {
        try {
            getFile().createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected Connection create() throws SQLException {
        try {
            Class.forName("org.sqlite.jdbc4.JDBC4Connection").getDeclaredConstructor().newInstance();

            return DriverManager.getConnection("jdbc:sqlite:" + getFile());
        } catch (ReflectiveOperationException exception) {
            if (exception.getCause() instanceof SQLException) {
                throw (SQLException) exception.getCause();
            }

            throw new RuntimeException(exception);
        }
    }
}