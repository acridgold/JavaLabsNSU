package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;

import static ru.nsu.calculator.CalculatorExceptions.*;

public class Push implements Command {
    private final double value;

    public Push(double value) {
        this.value = value;
    }

    @Override
    public void execute(Context context) {
        context.push(value);
    }

    public static String getCommandName() {
        return "PUSH";
    }

    public static Command create(String[] args) {
        if (args.length != 2) {
            throw new WrongArgumentsCountException("PUSH", 1, args.length - 1);
        }
        try {
            double value = Double.parseDouble(args[1]);
            return new Push(value);
        } catch (NumberFormatException e) {
            throw new CalcNumberFormatException(args[1], "PUSH", e);
        }
    }
}
