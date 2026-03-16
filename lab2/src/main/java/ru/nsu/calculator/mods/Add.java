package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import static ru.nsu.calculator.CalculatorExceptions.*;

public class Add implements Command {
    @Override
    public void execute(Context context) {
        if (context.stackSize() < 2) {
            throw new InsufficientStackElementsException("+");
        }
        double b = context.pop();
        double a = context.pop();
        double result = a + b;
        context.push(result);
    }

    public static String getCommandName() {
        return "+";
    }

    public static Command create(String[] args) {
        if (args.length != 1) {
            throw new WrongArgumentsCountException("+", 0, args.length - 1);
        }
        return new Add();
    }
}
