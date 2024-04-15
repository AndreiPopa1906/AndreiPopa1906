package Homework;

/**
 * Command interface defining the structure for all commands
 */
public interface Command {
    void execute(String[] arguments) throws CommandException;
}
