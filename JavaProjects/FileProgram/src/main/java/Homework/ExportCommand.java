package Homework;

import Compulsory.Repository;
import Compulsory.RepositoryException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportCommand implements Command {
    private final ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(ExportCommand.class.getName());

    /**
     * Constructs an ExportCommand object and sets up the ObjectMapper
     */
    public ExportCommand() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    /**
     * Executes the export command using the provided arguments to read file information from a repository and write it to a JSON file
     *
     * @param arguments A string array containing the repository path at index 0 and the output file path at index 1
     * @throws CommandException if an error occurs during the export process or if the incorrect number of arguments is provided
     */
    @Override
    public void execute(String[] arguments) throws CommandException {
        if (arguments.length != 2) {
            throw new CommandException("Usage: export <repository_path> <output_file_path>");
        }
        String repositoryPath = arguments[0];
        String outputPath = arguments[1];
        try {
            Repository repository = new Repository(repositoryPath);
            List<FileData> filesData = getFilesData(repository);
            if (filesData.isEmpty()) {
                logger.info("No files found in the repository to export.");
            } else {
                exportToJson(filesData, outputPath);
            }
            System.out.println("Repository data exported to: " + outputPath);
        } catch (RepositoryException | IOException e) {
            throw new CommandException("An error occurred during export: " + e.getMessage());
        }
    }
    /**
     * Retrieves the list of file data from the given repository
     *
     * @param repository The repository from which to retrieve file data
     * @return A list of FileData objects, each representing metadata for a file in the repository
     * @throws IOException if an I/O error is encountered when accessing the repository's directory
     */

    private List<FileData> getFilesData(Repository repository) throws IOException {
        List<FileData> filesData = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(repository.getMasterDirectory())) {
            for (Path entry : stream) {
                BasicFileAttributes attr = Files.readAttributes(entry, BasicFileAttributes.class);
                FileData fileData = new FileData(entry.getFileName().toString(),
                        Files.isDirectory(entry),
                        attr.lastModifiedTime().toString());
                filesData.add(fileData);
                logger.info("Added file data for: " + entry.getFileName());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error accessing directory: " + repository.getMasterDirectory(), e);
            throw e;
        }

        if (filesData.isEmpty()) {
            logger.info("The directory stream is empty for the path: " + repository.getMasterDirectory());
        }

        return filesData;
    }

    /**
     * Exports the provided data to a JSON file at the specified output path
     *
     * @param data       The data to export to JSON format
     * @param outputPath The file system path where the JSON file will be written
     * @throws JsonProcessingException if a processing error is encountered when mapping the data to JSON format
     * @throws IOException             if an I/O error occurs when writing the JSON file
     */
    private void exportToJson(Object data, String outputPath) throws JsonProcessingException, IOException {
        objectMapper.writeValue(new File(outputPath), data);
    }
}

