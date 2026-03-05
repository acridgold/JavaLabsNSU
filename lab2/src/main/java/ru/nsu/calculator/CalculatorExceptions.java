package ru.nsu.calculator;

public class CalculatorExceptions {

    public static class CalculatorException extends RuntimeException {
        public CalculatorException(String message) { super(message); }
    }

    public static class CommandExecutionException extends CalculatorException {
        private final String commandName;
        private final String rawMessage;

        // Конструктор для команд (Mods.java)
        public CommandExecutionException(String rawMessage, String commandName) {
            super(rawMessage);
            this.rawMessage = rawMessage;
            this.commandName = commandName;
        }

        // Конструктор для "пересборки" в Calculator.java с номером строки
        public CommandExecutionException(String rawMessage, String commandName, int lineNumber) {
            super(String.format("[Строка %d] '%s' - %s", lineNumber, commandName, rawMessage));
            this.rawMessage = rawMessage;
            this.commandName = commandName;
        }

        public String getCommandName() { return commandName; }
        public String getRawMessage() { return rawMessage; }
    }

    // --- Ошибки аргументов ---
    public static class UnknownCommandException extends CommandExecutionException {
        public UnknownCommandException(String cmdName) { super("Неизвестная команда", cmdName); }
    }

    public static class WrongArgumentsCountException extends CommandExecutionException {
        public WrongArgumentsCountException(String cmdName, int expected, int actual) {
            super(String.format("Ожидалось %d аргументов, получено %d", expected, actual), cmdName);
        }
    }

    public static class CalcNumberFormatException extends CommandExecutionException {
        public CalcNumberFormatException(String val, String cmdName, Throwable cause) {
            super("Неверный формат числа: " + val, cmdName);
            if (cause != null) initCause(cause);
        }
    }

    // --- Ошибки состояния ---
    public static class InsufficientStackElementsException extends CommandExecutionException {
        public InsufficientStackElementsException(String cmdName) { super("Недостаточно элементов на стеке", cmdName); }
    }

    // --- Арифметика ---
    public static class DivisionByZeroException extends CommandExecutionException {
        public DivisionByZeroException(String cmdName) { super("Деление на ноль", cmdName); }
    }

    public static class NegativeSqrtException extends CommandExecutionException {
        public NegativeSqrtException(String cmdName) { super("Корень из отрицательного числа", cmdName); }
    }
}