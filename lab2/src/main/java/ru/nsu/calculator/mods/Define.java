package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import static ru.nsu.calculator.CalculatorExceptions.*;

public class Define implements Command {
    private final String name;
    private final String valueToken;

    public Define(String name, String valueToken) {
        this.name = name;
        this.valueToken = valueToken;
    }

    @Override
    public void execute(Context context) {
        context.define(name, context.resolveNumericToken(valueToken, getCommandName()));
    }

    public static String getCommandName() {
        return "DEFINE";
    }

    public static Command create(String[] args) {
        if (args.length != 3) {
            throw new WrongArgumentsCountException("DEFINE", 2, args.length - 1);
        }
        return new Define(args[1], args[2]);
    }
}
