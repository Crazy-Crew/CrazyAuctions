package com.badbones69.crazyauctions.frame.storage.types.file.yaml;

import com.badbones69.crazyauctions.frame.storage.FileExtension;
import com.badbones69.crazyauctions.frame.storage.enums.StorageType;
import com.badbones69.crazyauctions.frame.storage.types.file.FileLoader;
import com.badbones69.crazyauctions.frame.storage.types.file.yaml.keys.Comment;
import com.badbones69.crazyauctions.frame.storage.types.file.yaml.keys.Header;
import com.badbones69.crazyauctions.frame.storage.types.file.yaml.keys.Path;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.comments.CommentType;
import org.simpleyaml.configuration.file.YamlFile;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class YamlLoader implements FileLoader {

    private final FileExtension fileExtension;

    private final File file;

    private YamlFile config;

    public @NotNull YamlFile getConfig() {
        return this.config;
    }

    public YamlLoader(FileExtension fileExtension) {
        this.fileExtension = fileExtension;

        this.file = this.fileExtension.getFile();
    }

    protected void setComments(@NotNull String path, @NotNull String comment) {
        getConfig().setComment(path, comment, CommentType.BLOCK);
    }

    protected Object getValue(@NotNull String path, @NotNull Object def) {
        if (this.config.get(path) == null) this.config.set(path, def);

        return this.config.get(path);
    }

    @Override
    public void load() {
        this.config = new YamlFile(this.file);

        try {
            this.config.createOrLoadWithComments();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            Path path = field.getDeclaredAnnotation(Path.class);
            Comment comment = field.getDeclaredAnnotation(Comment.class);

            if (path == null) return;

            Object pathValue = getValue(path.value(), comment.value());

            try {
                field.set(this.fileExtension, pathValue instanceof String stringValue ? stringValue.translateEscapes() : pathValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            setComments(path.value(), comment.value());
        }

        Header header = this.fileExtension.getClass().getDeclaredAnnotation(Header.class);

        if (header != null) this.config.setHeader(header.value());

        save();
    }

    @Override
    public void save() {
        try {
            getConfig().save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getImplName() {
        return StorageType.YAML.getName();
    }
}