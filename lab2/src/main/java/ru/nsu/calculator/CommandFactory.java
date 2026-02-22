package ru.nsu.calculator;

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

    /**
     * Регистрация новых команд
     */
    public void registerCommand(String name, Function<String[], Command> creator) {
        commands.put(name.toUpperCase(), creator);
    }

    /**
     * Парсит строку и вызывает нужную инструкцию
     */
    public Command createCommand(String inputLine) {
        String[] parts = inputLine.trim().split("\\s+");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Пустая строка команды");
        }

        String commandName = parts[0].toUpperCase();

        String[] resolvedParts = new String[parts.length];
        resolvedParts[0] = parts[0];

        for (int i = 1; i < parts.length; i++) {
            resolvedParts[i] = resolveValue(parts[i]);
        }

        Function<String[], Command> creator = commands.get(commandName);

        if (creator == null) {
            throw new IllegalArgumentException("Неизвестная команда: " + commandName);
        }

        return creator.apply(resolvedParts);
    }

    /**
     * Преобразует токен в строковое представление значения |
     * Если токен - число, возвращает его |
     * Если токен - переменная, возвращает ее значение как строку |
     * Иначе возвращает исходный токен
     */
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
            throw new IllegalArgumentException("PUSH требует один аргумент");
        }
        try {
            double value = Double.parseDouble(parts[1]);
            return new Mods.PUSH(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("PUSH: аргумент должен быть числом, получено: " + parts[1]);
        }
    }

    private Command createPop(String[] parts) {
        if (parts.length != 1) {
            throw new IllegalArgumentException("POP не требует аргументов");
        }
        return new Mods.POP();
    }

    private Command createSqrt(String[] parts) {
        if (parts.length != 1) {
            throw new IllegalArgumentException("SQRT не требует аргументов");
        }
        return new Mods.SQRT();
    }

    private Command createDefine(String[] parts) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("DEFINE требует два аргумента: имя и значение");
        }
        try {
            String name = parts[1];
            double value = Double.parseDouble(parts[2]);
            return new Mods.DEFINE(name, value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("DEFINE: второй аргумент должен быть числом, получено: " + parts[2]);
        }
    }

    private Command createPrint(String[] parts) {
        if (parts.length != 1) {
            throw new IllegalArgumentException("PRINT не требует аргументов");
        }
        return new Mods.PRINT();
    }

    private Command createSimpleMath(String[] parts) {
        if (parts.length != 1) {
            throw new IllegalArgumentException(parts[0] + " не требует аргументов");
        }
        return new Mods.Simple_Math(parts[0].charAt(0));
    }
}