package com.badbones69.crazyenvoys.storage.impl.types.sql.file.types;

import com.badbones69.crazyenvoys.storage.impl.types.sql.file.FlatFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SqliteFactory extends FlatFactory {

    public SqliteFactory(@NotNull final Path path) {
        super(path);
    }

    @Override
    public void init() {
        if (!Files.exists(this.path)) {
            try {
                final Path parent = this.path.getParent();

                if (!Files.exists(parent)) {
                    Files.createDirectory(parent);
                }

                Files.createFile(this.path);
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }

        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url());
        config.setMaximumPoolSize(5); // 5 is enough for flat file.
        config.setConnectionInitSql("PRAGMA foreign_keys = ON;");

        this.source = new HikariDataSource(config);

        super.init();
    }

    @Override
    protected String url() {
        return "jdbc:sqlite:" + getPath().toFile().getAbsolutePath();
    }

    @Override
    public String getImplementation() {
        return "SQLite";
    }
}