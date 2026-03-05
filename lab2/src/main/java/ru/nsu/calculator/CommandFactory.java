package ru.nsu.calculator;

import static ru.nsu.calculator.CalculatorExceptions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandFactory {
    private final Map<String, Function<String[], Command>> commands = new HashMap<>();
    private final Context context;

    public CommandFactory(Context context) {
        this.context = context;
        registerCommand("PUSH", this::createPush);
        registerCommand("POP", this::createPop);
        registerCommand("SQRT", this::createSqrt);
        registerCommand("DEFINE", this::createDefine);
        registerCommand("PRINT", this::createPrint);
        registerCommand("+", this::createSimpleMath);
        registerCommand("-", this::createSimpleMath);
        registerCommand("*", this::createSimpleMath);
        registerCommand("/", this::createSimpleMath);
    }

    public void registerCommand(String name, Function<String[], Command> creator) {
        commands.put(name.toUpperCase(), creator);
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

    private Command createPush(String[] parts) {
        if (parts.length != 2) {
            throw new WrongArgumentsCountException("PUSH", 1, parts.length - 1);
        }
        try {
            double value = Double.parseDouble(parts[1]);
            return new Mods.PUSH(value);
        } catch (NumberFormatException e) {
            throw new CalcNumberFormatException(parts[1], "PUSH", e);
        }
    }

    private Command createPop(String[] parts) {
        if (parts.length != 1) {
            throw new WrongArgumentsCountException("POP", 0, parts.length - 1);
        }
        return new Mods.POP();
    }

    private Command createSqrt(String[] parts) {
        if (parts.length != 1) {
            throw new WrongArgumentsCountException("SQRT", 0, parts.length - 1);
        }
        return new Mods.SQRT();
    }

    private Command createDefine(String[] parts) {
        if (parts.length != 3) {
            throw new WrongArgumentsCountException("DEFINE", 2, parts.length - 1);
        }
        try {
            String name = parts[1];
            double value = Double.parseDouble(parts[2]);
            return new Mods.DEFINE(name, value);
        } catch (NumberFormatException e) {
            throw new CalcNumberFormatException(parts[2], "DEFINE", e);
        }
    }

    private Command createPrint(String[] parts) {
        if (parts.length != 1) {
            throw new WrongArgumentsCountException("PRINT", 0, parts.length - 1);
        }
        return new Mods.PRINT();
    }

    private Command createSimpleMath(String[] parts) {
        if (parts.length != 1) {
            throw new WrongArgumentsCountException(parts[0], 0, parts.length - 1);
        }
        return new Mods.Simple_Math(parts[0].charAt(0));
    }
}