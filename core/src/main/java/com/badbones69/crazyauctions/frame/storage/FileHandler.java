package com.badbones69.crazyauctions.frame.storage;

import com.badbones69.crazyauctions.frame.storage.types.file.json.JsonLoader;
import com.badbones69.crazyauctions.frame.storage.types.file.yaml.YamlLoader;
import com.badbones69.crazyauctions.frame.storage.types.sql.file.SqliteLoader;
import com.badbones69.crazyauctions.frame.utils.FileUtils;
import java.io.File;
import java.nio.file.Path;

public class FileHandler implements FileManager {

    private JsonLoader jsonLoader;
    private YamlLoader yamlLoader;
    private SqliteLoader sqliteLoader;

    @Override
    public void addFile(FileExtension fileExtension) {
        switch (fileExtension.getType()) {

            case JSON -> {
                this.jsonLoader = new JsonLoader(fileExtension);
                this.jsonLoader.load();
            }

            case YAML -> {
                this.yamlLoader = new YamlLoader(fileExtension);
                this.yamlLoader.load();
            }

            case SQLITE -> {

            }

            default -> throw new IllegalStateException("Unexpected value: " + fileExtension.getType());
        }
    }

    @Override
    public void saveFile(FileExtension fileExtension) {
        switch (fileExtension.getType()) {
            case JSON -> {
                this.jsonLoader = new JsonLoader(fileExtension);
                this.jsonLoader.save();
            }

            case YAML -> {
                this.yamlLoader = new YamlLoader(fileExtension);
                this.yamlLoader.save();
            }

            case SQLITE -> {

            }

            default -> throw new IllegalStateException("Unexpected value: " + fileExtension.getType());
        }
    }

    @Override
    public void removeFile(FileExtension fileExtension) {
        File file = fileExtension.getPath().toFile();

        if (file.exists()) file.delete();
    }

    @Override
    public File getFile(FileExtension fileExtension) {
        return fileExtension.getFile();
    }

    public void extract(String value, Path directory) {
        File newDirectory = new File(directory + value);

        newDirectory.mkdir();

        FileUtils.extract(value, directory, false);
    }
}