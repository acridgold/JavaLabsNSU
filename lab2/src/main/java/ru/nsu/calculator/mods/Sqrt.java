package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import static ru.nsu.calculator.CalculatorExceptions.*;

public class Sqrt implements Command {
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

    public static String getCommandName() {
        return "SQRT";
    }

    public static Command create(String[] args) {
        if (args.length != 1) {
            throw new WrongArgumentsCountException("SQRT", 0, args.length - 1);
        }
        return new Sqrt();
    }
}
