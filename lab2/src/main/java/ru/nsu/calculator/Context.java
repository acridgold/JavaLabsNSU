package ru.nsu.calculator;

import ru.nsu.calculator.CalculatorExceptions.CalcNumberFormatException;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Context {
    private final Stack<Double> stack = new Stack<>();
    private final Map<String, Double> parameters = new HashMap<>();

    /**
     * Положить аргумент на стек
     */
    public void push(double value) {
        stack.push(value);
    }

    /**
     * Снять аргумент со стека
     */
    public double pop() {
        return stack.pop();
    }

    /**
     * Взять корень из верхнего значения
     */
    public double peek() {
        return stack.peek();
    }

    public boolean stackIsEmpty() {
        return stack.isEmpty();
    }

    /**
     * Задать значение параметра
     */
    public void define(String name, double value) {
        parameters.put(name, value);
    }

    public Double getParameter(String name) {
        return parameters.get(name);
    }

    public double resolveNumericToken(String token, String commandName) {
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException e) {
            Double value = parameters.get(token);
            if (value != null) {
                return value;
            }
            throw new CalcNumberFormatException(token, commandName, e);
        }
    }

    public int stackSize() {
        return stack.size();
    }
}