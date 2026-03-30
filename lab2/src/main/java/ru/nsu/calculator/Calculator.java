package ru.nsu.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.*;

public class Calculator {
    private static final Logger logger = Logger.getLogger(Calculator.class.getName());

    private final CommandFactory factory;
    private final Context context;

    public Calculator() {
        this.context = new Context();
        this.factory = new CommandFactory();
        setupLogging();
    }

    /**
     * Настройка формата логов
     */
    private void setupLogging() {
        try {
            logger.setUseParentHandlers(false);

            FileHandler fileHandler = new FileHandler("calculator.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.SEVERE);
            logger.addHandler(consoleHandler);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Не удалось создать файл лога", e);
        }
    }

    public void executeFile(String filename) throws IOException {
        logger.info("Начало выполнения файла: " + filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            long startTime = System.currentTimeMillis();

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String originalLine = line.trim();

                String cleanLine = originalLine.split("#")[0].trim();
                if (cleanLine.isEmpty()) continue;

                try {
                    Command command = factory.createCommand(cleanLine);
                    command.execute(context);
                } catch (CalculatorExceptions.CommandExecutionException e) {
                    CalculatorExceptions.CommandExecutionException enrichedException =
                            new CalculatorExceptions.CommandExecutionException(
                                    e.getRawMessage(),
                                    e.getCommandName(),
                                    lineNumber
                            );

                    logger.log(Level.SEVERE, enrichedException.getMessage());

                    throw enrichedException;

                } catch (CalculatorExceptions.CalculatorException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                    throw e;
                }
            }

            long endTime = System.currentTimeMillis();
            logger.info("Файл '" + filename + "' выполнен за " + (endTime - startTime) + " мс");
            System.out.println("\n\u001B[32mCalculator: Done\u001B[0m");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка доступа к файлу: " + filename, e);
            throw e;
        }
    }

    public Context getContext() {
        return context;
    }
}