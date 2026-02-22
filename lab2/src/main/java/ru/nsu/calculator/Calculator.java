package ru.nsu.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    private final CommandFactory factory;
    private final Context context;

    public Calculator() {
        this.context = new Context();
        this.factory = new CommandFactory(this.context);
    }

    public void executeFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                int commentIndex = line.indexOf('#');
                if (commentIndex >= 0) {
                    line = line.substring(0, commentIndex).trim();
                }

                if (line.isEmpty()) {
                    continue;
                }

                try {
                    Command command = factory.createCommand(line);
                    command.execute(context);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка в строке " + lineNumber + ": " + e.getMessage(), e);
                }
            }

            System.out.println("\n\u001B[32mCalculator: Done\u001B[0m");
        }
    }

    // todo: а нах написал аххахаха
    public Context getContext() {
        return context;
    }
}