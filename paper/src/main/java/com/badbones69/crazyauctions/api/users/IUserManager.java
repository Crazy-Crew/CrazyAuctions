package com.badbones69.crazyauctions.api.users;

import java.util.UUID;

public interface IUserManager {

    void migrate();

    void save(UUID uuid);

}