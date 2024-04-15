package Homework;

import Compulsory.Repository;


import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileData {
    private String name;
    private boolean isDirectory;
    private String lastModifiedTime;

    /**
     * Constructs a new FileData object with the specified metadata.
     * @param name             The name of the file or directory.
     * @param isDirectory      A boolean indicating if the path is a directory.
     * @param lastModifiedTime The last modified time of the file or directory.
     */
    public FileData(String name, boolean isDirectory, String lastModifiedTime) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }
}