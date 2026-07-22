package com.badbones69.crazyauctions.common.storage.impl.sql;

import com.badbones69.crazyauctions.common.CrazyAuctionsPlugin;
import com.badbones69.crazyauctions.common.storage.impl.file.FlatFactory;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class SqlFactory extends FlatFactory {

    protected final String create_users_table = "create table if not exists auction_users(" +
            "uuid varchar(36) primary key, " +
            "name varchar(16) not null)";

    protected final String create_expired_items_table = "create table if not exists expired_items(" +
            "uuid varchar(36) primary key, " +
            "name varchar(16) not null, " +

            "store_id bigint not null, " +

            "item varchar(255) not null, " +

            "time varchar(32) not null, " +

            "foreign key(uuid) references auction_users(uuid) on delete cascade)";

    protected final String create_bidder_table = "create table if not exists auction_users(" +
            "uuid varchar(36) primary key, " + // bidder uuid
            "name varchar(16) not null," + // bidder name

            "store_id bigint not null, " + // store id they bid on

            "foreign key(store_id) references active_items(store_id) on delete cascade)";

    protected final String create_active_items_table = "create table if not exists active_items(" +
            "uuid varchar(36) primary key, " +
            "name varchar(16) not null," +

            "store_id bigint not null, " +

            "item varchar(255) not null, " +

            "time varchar(32) not null, " +
            "expire_time varchar(32) not null, " +

            "foreign key(uuid) references auction_users(uuid) on delete cascade)";

    protected HikariDataSource source;

    public SqlFactory(final CrazyAuctionsPlugin plugin, final String impl) {
        super(plugin, impl);
    }

    public abstract void insert(final UUID uuid, final String name);

    public abstract String url();

    @Override
    public void init() {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = getConnection()) {
                if (connection == null) return;

                try (final Statement statement = connection.createStatement()) {
                    statement.addBatch(this.create_users_table);

                    statement.addBatch(this.create_active_items_table);
                    statement.addBatch(this.create_expired_items_table);

                    statement.addBatch(this.create_bidder_table);

                    statement.executeBatch();
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {
        try {
            final Connection connection = getConnection();

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void save() {

    }

    public Connection getConnection() throws SQLException {
        if (this.source.isClosed()) {
            throw new FusionException("Failed to get connection from pool. (Source returned closed)");
        }

        return this.source.getConnection();
    }

    public boolean tableExists(final String table) {
        try (final ResultSet resultSet = getConnection().getMetaData().getTables(
                null,
                null,
                table,
                null
        )) {
            return resultSet.next();
        } catch (SQLException exception) {
            exception.printStackTrace();

            return false;
        }
    }
}