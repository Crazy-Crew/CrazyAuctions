package com.badbones69.crazyauctions.common.storage.impl.types.sql;

import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class ConnectionFactory {

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

    public abstract boolean tableExists(@NonNull final String table);

    public abstract Connection getConnection() throws SQLException;

    public abstract String getImplementation();

    public abstract boolean isRunning();

    public abstract void init();

    public abstract void stop();

    protected List<String> getTables(@NonNull final Connection connection) throws SQLException {
        final List<String> tables = new ArrayList<>();

        try (final ResultSet result = connection.getMetaData().getTables(connection.getCatalog(), null, "%", null)) {
            while (result.next()) {
                tables.add(result.getString(3).toLowerCase(Locale.ROOT));
            }
        }

        return tables;
    }
}