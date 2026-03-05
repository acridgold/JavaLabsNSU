package ru.nsu.calculator;

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

    public int stackSize() {
        return stack.size();
    }
}