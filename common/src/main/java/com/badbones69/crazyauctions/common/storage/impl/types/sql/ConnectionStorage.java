package com.badbones69.crazyauctions.common.storage.impl.types.sql;

import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.storage.IStorageHolder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import us.crazycrew.api.enums.ShopType;

public class ConnectionStorage extends IStorageHolder {

    private final ConnectionFactory factory;
    private final FusionKyori fusion;

    public ConnectionStorage(@NonNull final ConnectionFactory factory, @NonNull final FusionKyori fusion) {
        this.factory = factory;
        this.fusion = fusion;
    }

    @Override
    public @NonNull final ConnectionStorage init() {
        this.factory.init();

        return this;
    }

    @Override
    public void reload() {

    }

    @Override
    public void insertUser(@NonNull final UUID uuid, @NonNull final String name) {
        CompletableFuture.runAsync(() -> {
            try (final Connection connection = this.factory.getConnection(); final PreparedStatement statement =
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
    public void addItem(@NonNull final UUID uuid, @NonNull final String name, @NonNull final String base64, final double price, final int amount, @NonNull final ShopType shopType) {}

    @Override
    public boolean hasUser(@NonNull final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            boolean hasUser = false;

            try (final Connection connection = this.factory.getConnection(); final PreparedStatement statement =
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
    }

    @Override
    public void stop() {
        this.factory.stop();
    }
}