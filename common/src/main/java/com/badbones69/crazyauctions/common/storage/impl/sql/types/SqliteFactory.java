package com.badbones69.crazyauctions.common.storage.impl.sql.types;

import com.badbones69.crazyauctions.common.CrazyAuctionsPlugin;
import com.badbones69.crazyauctions.common.storage.impl.sql.SqlFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.NullMarked;
import us.crazycrew.api.enums.ShopType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public final class SqliteFactory extends SqlFactory {

    private final Path path;

    public SqliteFactory(final CrazyAuctionsPlugin plugin) {
        super(plugin, "SQLite");

        this.path = plugin.getDataPath().resolve("crazycrates.db");
    }

    @Override
    public void reload() {

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
    public void addItem(final UUID uuid, final String name, final String base64, final long price, final ShopType shopType) {

    }

    @Override
    public boolean hasExpiredItem(final UUID uuid) {
        return false;
    }

    /*@Override
    public boolean hasUser(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            boolean hasUser = false;

            try (final Connection connection = getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("select 1 from auction_users where uuid=?")) {
                statement.setString(1, uuid.toString());

                final ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    hasUser = true;

                    return hasUser;
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }

            return hasUser;
        }).join();
    }*/

    @Override
    public void insert(final UUID uuid, final String name) {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = getConnection(); final PreparedStatement statement =
                    connection.prepareStatement("insert into auction_users(uuid, name) values(?, ?)")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, name);

                statement.executeUpdate();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public String url() {
        return "jdbc:sqlite:" + this.path.toFile().getAbsolutePath();
    }
}