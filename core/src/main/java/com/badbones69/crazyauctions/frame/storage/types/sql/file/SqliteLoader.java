package com.badbones69.crazyauctions.frame.storage.types.sql.file;

import com.badbones69.crazyauctions.frame.storage.enums.StorageType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;

public class SqliteLoader extends FlatFileLoader {

    private Connection connection;

    public SqliteLoader(String name, Path path) {
        super(name, path);
    }

    @Override
    protected Connection createConnection(String name, Path path) {
        File file = new File(path.toFile(), name);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String url = "jdbc:sqlite:" + file.getPath();

        return null;
    }

    @Override
    public String getImplName() {
        return StorageType.SQLITE.getName();
    }
}