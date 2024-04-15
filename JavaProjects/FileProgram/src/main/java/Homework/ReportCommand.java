package Homework;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportCommand implements Command {
    private Configuration cfg;

    /**
     * Initializes a new instance of the ReportCommand class with a FreeMarker configuration
     * Sets up the class loader for template loading, default encoding, and exception handlers
     */
    public ReportCommand() {
        cfg = new Configuration(Configuration.VERSION_2_3_30);

        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates");

        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }

    /**
     * Generates an HTML report of all files in the directory.
     *
     * @param arguments An array of arguments where the first element should be the directory path.
     * @throws CommandException If there is an incorrect number of arguments
     *                          if the path does not exist, is not a directory
     *                          or if an error occurs during report generation
     */
    @Override
    public void execute(String[] arguments) throws CommandException {
        if (arguments.length != 1) {
            throw new CommandException("Usage: report <directory_path>");
        }

        String directoryPath = arguments[0];
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new CommandException("The path does not exist or is not a directory: " + directoryPath);
        }

        List<Map<String, Object>> filesList = new ArrayList<>();
        for (File file : directory.listFiles()) {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("name", file.getName());
            fileInfo.put("directory", file.isDirectory());
            filesList.add(fileInfo);
        }

        try {
            Template template = cfg.getTemplate("directory_report.ftl");
            Map<String, Object> root = new HashMap<>();
            root.put("files", filesList);

            File reportFile = new File(directory, "report.html");
            try (Writer fileWriter = new FileWriter(reportFile)) {
                template.process(root, fileWriter);
            }
            System.out.println("Report generated at: " + reportFile.getAbsolutePath());
        } catch (IOException | TemplateException e) {
            throw new CommandException("An error occurred while creating the report: " + e.getMessage());
        }
    }
}
