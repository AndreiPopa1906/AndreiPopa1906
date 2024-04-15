package Compulsory;

//cauta lombok

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repository {
    private Path masterDirectory;
    private static final Logger logger = Logger.getLogger(Repository.class.getName());

    /**
     * Initialises the repo object and checks if it is a directory
     * @param masterDirectory the name of the directory
     * @throws RepositoryException If it is not a directory/ it does not exist
     */
    public Repository(String masterDirectory) throws RepositoryException {
        this.masterDirectory = Paths.get(masterDirectory);
        if (!Files.isDirectory(this.masterDirectory)) {
            logger.log(Level.SEVERE, "[LOGGER] OOPSIE...  Master directory does not exist: ", masterDirectory);
            throw new RepositoryException("Master directory does not exist: " + masterDirectory);
        }
    }

    /**
     * The function displays the contents of the repository
     * @throws RepositoryException
     */
    public void displayContents() throws RepositoryException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(masterDirectory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    logger.info("[LOGGER] Directory: " + entry.getFileName());
                    BasicFileAttributes attr = Files.readAttributes(entry, BasicFileAttributes.class);
                    logger.info("[LOGGER] Last Modified Time: " + attr.lastModifiedTime());
                    try (DirectoryStream<Path> subStream = Files.newDirectoryStream(entry)) {
                        for (Path subEntry : subStream) {
                            logger.info("[LOGGER] File: " + subEntry.getFileName());
                        }
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "[LOGGER] Error accessing sub-directory: ", entry.getFileName());
                    }
                }
            }
        } catch (IOException e) {
            throw new RepositoryException("[LOGGER] OOPSIE... Error accessing directory: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Repository repo = new Repository("D:\\Andrei");
            repo.displayContents();
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, "[LOGGER] OOPSIE... An error occurred: ", e);
        }
    }

    public Path getMasterDirectory() {
        return masterDirectory;
    }
}




//package Compulsory;
//
//import Compulsory.RepositoryException;
//
//import java.io.IOException;
//import java.nio.file.DirectoryStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//public class Repository {
//    private Path masterDirectory;
//    private static final Logger logger = Logger.getLogger(Repository.class.getName());
//    public Repository(String masterDirectory) throws RepositoryException {
//        this.masterDirectory = Paths.get(masterDirectory);
//        if (!Files.isDirectory(this.masterDirectory)) {
//            logger.log(Level.SEVERE, "[LOGGER] (BIG) OOPSIE... Master directory does not exist: {0}", masterDirectory);
//            throw new RepositoryException("OOPSIE... Master directory does not exist: " + masterDirectory);
//        }
//    }
//
//
//    public void displayContents() throws RepositoryException {
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(masterDirectory)) {
//            for (Path entry : stream) {
//                if (Files.isDirectory(entry)) {
//                    logger.info("[LOGGER] OOPSIE... Directory: " + entry.getFileName());
//                    try (DirectoryStream<Path> subStream = Files.newDirectoryStream(entry)) {
//                        for (Path subEntry : subStream) {
//                            logger.info("  File: " + subEntry.getFileName());
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new RepositoryException("[LOGGER] OOPSIE... Error accessing directory: " + e.getMessage());
//        }
//    }
//
//    // Main method for demonstration purposes
//    public static void main(String[] args) {
//        try {
//            Repository repo = new Repository("C:\\Users\\andre");
//            repo.displayContents();
//        } catch (RepositoryException e) {
//            logger.info("[LOGGER] OOPSIE... An error occurred: " + e.getMessage());
//        }
//    }
//}
