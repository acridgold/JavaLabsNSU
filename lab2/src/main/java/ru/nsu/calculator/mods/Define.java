package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import static ru.nsu.calculator.CalculatorExceptions.*;

public class Define implements Command {
    private final String name;
    private final double value;

    public Define(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(Context context) {
        context.define(name, value);
    }

    public static String getCommandName() {
        return "DEFINE";
    }

    public static Command create(String[] args) {
        if (args.length != 3) {
            throw new WrongArgumentsCountException("DEFINE", 2, args.length - 1);
        }
        try {
            String name = args[1];
            double value = Double.parseDouble(args[2]);
            return new Define(name, value);
        } catch (NumberFormatException e) {
            throw new CalcNumberFormatException(args[2], "DEFINE", e);
        }
    }
}
