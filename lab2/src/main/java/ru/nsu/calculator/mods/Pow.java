package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import static ru.nsu.calculator.CalculatorExceptions.*;

public class Pow implements Command {
    @Override
    public void execute(Context context) {
        if (context.stackSize() < 2) {
            throw new InsufficientStackElementsException("POW");
        }
        double exponent = context.pop();
        double base = context.pop();
        double result = Math.pow(base, exponent);
        context.push(result);
    }

    public static String getCommandName() {
        return "POW";
    }

    public static Command create(String[] args) {
        if (args.length != 1) {
            throw new WrongArgumentsCountException("POW", 0, args.length - 1);
        }
        return new Pow();
    }
}
