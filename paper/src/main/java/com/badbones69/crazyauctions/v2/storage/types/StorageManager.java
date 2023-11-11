package com.badbones69.crazyauctions.storage.types;

import com.badbones69.crazyauctions.storage.interfaces.UserManager;
import com.badbones69.crazyauctions.storage.types.file.yaml.YamlUserManager;

public class StorageManager {

    private UserManager userManager;

    public void init() {
        this.userManager = new YamlUserManager();
    }

    public UserManager getUserManager() {
        return this.userManager;
    }
}