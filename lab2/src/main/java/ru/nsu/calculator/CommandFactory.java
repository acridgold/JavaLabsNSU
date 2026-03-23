package ru.nsu.calculator;

import static ru.nsu.calculator.CalculatorExceptions.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandFactory {
    private final Map<String, Function<String[], Command>> commands = new HashMap<>();
    private final Context context;

    public CommandFactory(Context context) {
        this.context = context;
        loadCommands();
    }

    /**
     * Загрузка команд из конфига
     */
    private void loadCommands() {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("ru/nsu/calculator/mods.conf");
            if (inputStream == null) {
                throw new RuntimeException("Config file 'ru/nsu/calculator/mods.conf' not found");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    String[] parts = line.split("=");
                    if (parts.length != 2) {
                        System.err.println("Invalid config line: " + line);
                        continue;
                    }

                    String commandName = parts[0].trim().toUpperCase();
                    String className = parts[1].trim();

                    try {
                        Class<?> clazz = Class.forName(className);
                        Method createMethod = clazz.getMethod("create", String[].class);
                        commands.put(commandName, args -> {
                            try {
                                return (Command) createMethod.invoke(null, (Object) args);
                            } catch (Exception e) {
                                Throwable cause = e.getCause();
                                if (cause instanceof RuntimeException) {
                                    throw (RuntimeException) cause;
                                } else {
                                    throw new RuntimeException(cause);
                                }
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("Failed to load command '" + commandName + "' from class: " + className);
                        System.err.println("  Error: " + e.getMessage());
                        if (e.getCause() != null) {
                            System.err.println("  Caused by: " + e.getCause());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load commands", e);
        }
    }

    public Command createCommand(String inputLine) {
        String[] parts = inputLine.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            throw new UnknownCommandException("EMPTY_LINE");
        }

        String commandName = parts[0].toUpperCase();
        Function<String[], Command> creator = commands.get(commandName);

        if (creator == null) {
            throw new UnknownCommandException(commandName);
        }

        String[] resolvedParts = new String[parts.length];
        resolvedParts[0] = parts[0];
        for (int i = 1; i < parts.length; i++) {
            resolvedParts[i] = resolveValue(parts[i]);
        }

        return creator.apply(resolvedParts);
    }

    private String resolveValue(String token) {
        try {
            Double.parseDouble(token);
            return token;
        } catch (NumberFormatException e) {
            Double value = context.getParameter(token);
            if (value != null) {
                return value.toString();
            }
            return token;
        }
    }
}