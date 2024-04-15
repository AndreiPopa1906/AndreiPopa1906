package Homework;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class ViewCommand implements Command {

    /**
     * Executes the view command which opens the specified file with the default application
     *
     * @param arguments An array of arguments where the first element should be the path of the file to be opened
     * @throws CommandException If the arguments are invalid, if the file does not exist
     *                          if the Desktop API is not supported on the platform
     *                          if the file cannot be read, or if an error occurs during opening the file
     */

    @Override
    public void execute(String[] arguments) throws CommandException {
        if (arguments.length != 1) {
            throw new CommandException("Usage: view <file_path>");
        }

        String filePath = arguments[0];
        File file = new File(filePath);

        if (!file.exists()) {
            throw new CommandException("File does not exist: " + filePath);
        }

        if (!Desktop.isDesktopSupported()) {
            throw new CommandException("Desktop is not supported on this platform");
        }

        Desktop desktop = Desktop.getDesktop();

        if (!file.canRead()) {
            throw new CommandException("File cannot be read: " + filePath);
        }

        try {
            desktop.open(file);
        } catch (IOException e) {
            throw new CommandException("An error occurred while opening the file: " + e.getMessage());
        }
    }
}
