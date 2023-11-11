package com.badbones69.crazyauctions.api.enums.support;

import com.badbones69.crazyauctions.storage.interfaces.UserManager;
import com.badbones69.crazyauctions.storage.types.file.yaml.YamlUserManager;

public enum DataSupport {

    yaml(YamlUserManager.class);

    private final Class<? extends UserManager> classObject;

    DataSupport(Class<? extends UserManager> classObject) {
        this.classObject = classObject;
    }

    public Class<? extends UserManager> getClassObject() {
        return this.classObject;
    }
}