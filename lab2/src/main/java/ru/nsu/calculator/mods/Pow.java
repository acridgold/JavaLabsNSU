package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import static ru.nsu.calculator.CalculatorExceptions.*;

public class Pow implements Command {
    private final String baseToken;
    private final String exponentToken;

    public Pow(String baseToken, String exponentToken) {
        this.baseToken = baseToken;
        this.exponentToken = exponentToken;
    }

    @Override
    public void execute(Context context) {
        double base = context.resolveNumericToken(baseToken, getCommandName());
        double exponent = context.resolveNumericToken(exponentToken, getCommandName());
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
        return new Pow(args[1], args[2]);
    }
}
