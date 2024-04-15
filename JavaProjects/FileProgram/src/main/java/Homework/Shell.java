package Homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Andrei Popa
 */
public class Shell {

    private final Map<String, Command> commands = new HashMap<>();

    public Shell() {
        commands.put("view", new ViewCommand());
        commands.put("report", new ReportCommand());
        commands.put("export", new ExportCommand());
    }

    /**
     * Reads commands from the standard input and executes them until the user types 'exit'
     */
    public void readAndExecuteCommand() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Shell. Type 'exit' to quit.");

        String input;
        while (true) {
            System.out.print("> ");
            input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input.trim())) {
                break;
            }

            String[] parts = input.split("\\s+");
            if (parts.length == 0) {
                continue;
            }

            String commandName = parts[0];
            String[] arguments = new String[parts.length - 1];
            System.arraycopy(parts, 1, arguments, 0, arguments.length);

            executeCommand(commandName, arguments);
        }

        scanner.close();
        System.out.println("Shell exited.");
    }
    /**
     * Executes a command based on the provided command name and arguments
     *
     * @param commandName The name of the command to execute
     * @param arguments   The arguments to pass to the command
     */

    private void executeCommand(String commandName, String[] arguments) {
        Command command = commands.get(commandName);
        if (command == null) {
            System.out.println("Error: Unknown command '" + commandName + "'");
            return;
        }

        try {
            command.execute(arguments);
        } catch (CommandException e) {
            System.out.println("Error executing command '" + commandName + "': " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.readAndExecuteCommand();
    }

}

