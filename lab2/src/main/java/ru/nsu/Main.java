package ru.nsu;

import ru.nsu.calculator.Calculator;
import ru.nsu.calculator.CalculatorExceptions.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Использование: java ru.nsu.calculator.Main <файл_с_командами>");
            System.exit(1);
        }

        String filename = args[0];
        Calculator calculator = new Calculator();

        try {
            calculator.executeFile(filename);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла '" + filename + "': " + e.getMessage());
            System.exit(1);
        } catch (CommandExecutionException e) {
            System.err.println("Ошибка выполнения: " + e.getMessage());
            System.exit(2);
        } catch (CalculatorException e) {
            System.err.println("Ошибка калькулятора: " + e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }
}