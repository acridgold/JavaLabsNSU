package ru.nsu.calculator;

import static ru.nsu.calculator.CalculatorExceptions.*;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CommandFactory {
    private final Map<String, Function<String[], Command>> commands = new HashMap<>();
    private final Context context;

    public CommandFactory(Context context) {
        this.context = context;
        loadCommands();
    }

    /**
    * Загрузка команд из пакета
     */
    private void loadCommands() {
        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackage("ru.nsu.calculator.mods")
                    .addScanners(Scanners.SubTypes));
            Set<Class<? extends Command>> subTypes = reflections.getSubTypesOf(Command.class);
            for (Class<? extends Command> clazz : subTypes) {
                try {
                    Method getNameMethod = clazz.getMethod("getCommandName");
                    String name = (String) getNameMethod.invoke(null);
                    Method createMethod = clazz.getMethod("create", String[].class);
                    commands.put(name.toUpperCase(), args -> {
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
                    // Без методов -> скип
                    System.err.println("Failed to load command from class: " + clazz.getName());
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