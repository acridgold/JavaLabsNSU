package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import ru.nsu.calculator.CalculatorExceptions.*;

public class Pow implements Command {
    private final double base;
    private final double exponent;

    public Pow(double base, double exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    @Override
    public void execute(Context context) {
        double result = Math.pow(base, exponent);
        context.push(result);
    }

    public static String getCommandName() {
        return "POW";
    }

    public static Command create(String[] args) {
        if (args.length != 3) {
            throw new WrongArgumentsCountException("POW", 2, args.length - 1);
        }
        try {
            double base = Double.parseDouble(args[1]);
            double exponent = Double.parseDouble(args[2]);
            return new Pow(base, exponent);
        } catch (NumberFormatException e) {
            throw new CalcNumberFormatException(args[1].equals(args[1]) ? args[1] : args[2], "POW", e);
        }
    }
}
