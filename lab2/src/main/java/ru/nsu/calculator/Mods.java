package ru.nsu.calculator;

/**
 * Описывает логику работы функций
 */
public class Mods {

    public static class PUSH implements Command {
        private final double value;

        public PUSH(double value) {
            this.value = value;
        }

        @Override
        public void execute(Context context) {
            context.push(value);
        }
    }

    public static class POP implements Command {
        @Override
        public void execute(Context context) {
            if (context.stackIsEmpty()) {
                throw new IllegalStateException("Стек пуст");
            }
            context.pop();
        }
    }

    public static class SQRT implements Command {
        @Override
        public void execute(Context context) {
            if (context.stackIsEmpty()) {
                throw new IllegalStateException("Стек пуст");
            }
            double value = context.pop();
            if (value < 0) {
                throw new IllegalArgumentException("Нельзя взять корень из отрицательного числа");
            }
            context.push(Math.sqrt(value));
        }
    }

    public static class DEFINE implements Command {
        private final String name;
        private final double value;

        public DEFINE(String name, double value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void execute(Context context) {
            context.define(name, value);
        }
    }

    public static class PRINT implements Command {
        @Override
        public void execute(Context context) {
            if (context.stackIsEmpty()) {
                throw new IllegalStateException("Стек пуст");
            }
            System.out.println(context.peek());
        }
    }

    public static class Simple_Math implements Command {
        private final char operation;

        public Simple_Math(char operation) {
            this.operation = operation;
        }

        @Override
        public void execute(Context context) {
            if (context.stackIsEmpty()) {
                throw new IllegalStateException("Стек пуст");
            }
            double b = context.pop();
            if (context.stackIsEmpty()) {
                // Возвращаем b обратно для сохранения консистентности
                context.push(b);
                throw new IllegalStateException("Недостаточно элементов на стеке");
            }
            double a = context.pop();

            double result;
            switch (operation) {
                case '+':
                    result = a + b;
                    break;
                case '-':
                    result = a - b;
                    break;
                case '*':
                    result = a * b;
                    break;
                case '/':
                    if (b == 0) {
                        context.push(a);
                        context.push(b);
                        throw new ArithmeticException("Деление на ноль");
                    }
                    result = a / b;
                    break;
                default:
                    context.push(a);
                    context.push(b);
                    throw new IllegalArgumentException("Неизвестная операция: " + operation);
            }
            context.push(result);
        }
    }
}