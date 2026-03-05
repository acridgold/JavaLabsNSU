package ru.nsu.calculator;

import static ru.nsu.calculator.CalculatorExceptions.*;

public class Mods {

    public static class PUSH implements Command {
        private final double value;
        public PUSH(double value) { this.value = value; }

        @Override
        public void execute(Context context) {
            context.push(value);
        }
    }

    public static class POP implements Command {
        @Override
        public void execute(Context context) {
            if (context.stackIsEmpty()) {
                throw new InsufficientStackElementsException("POP");
            }
            context.pop();
        }
    }

    public static class SQRT implements Command {
        @Override
        public void execute(Context context) {
            if (context.stackIsEmpty()) {
                throw new InsufficientStackElementsException("SQRT");
            }
            double value = context.pop();
            if (value < 0) {
                context.push(value);
                throw new NegativeSqrtException("SQRT");
            }
            context.push(Math.sqrt(value));
        }
    }

    public static class DEFINE implements Command {
        private final String name;
        private final double value;
        public DEFINE(String name, double value) { this.name = name; this.value = value; }

        @Override
        public void execute(Context context) {
            context.define(name, value);
        }
    }

    public static class PRINT implements Command {
        @Override
        public void execute(Context context) {
            if (context.stackIsEmpty()) {
                throw new InsufficientStackElementsException("PRINT");
            }
            System.out.println(context.peek());
        }
    }

    public static class Simple_Math implements Command {
        private final char operation;
        public Simple_Math(char operation) { this.operation = operation; }

        @Override
        public void execute(Context context) {
            if (context.stackSize() < 2) {
                throw new InsufficientStackElementsException(String.valueOf(operation));
            }

            double b = context.pop();
            double a = context.pop();

            double result;
            switch (operation) {
                case '+': result = a + b; break;
                case '-': result = a - b; break;
                case '*': result = a * b; break;
                case '/':
                    if (b == 0) {
                        context.push(a);
                        context.push(b);
                        throw new DivisionByZeroException("/");
                    }
                    result = a / b;
                    break;
                default:
                    context.push(a);
                    context.push(b);
                    throw new UnknownCommandException(String.valueOf(operation));
            }
            context.push(result);
        }
    }
}