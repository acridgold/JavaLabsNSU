package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import static ru.nsu.calculator.CalculatorExceptions.*;

public class Pop implements Command {
    @Override
    public void execute(Context context) {
        if (context.stackIsEmpty()) {
            throw new InsufficientStackElementsException("POP");
        }
        context.pop();
    }

    public static String getCommandName() {
        return "POP";
    }

    public static Command create(String[] args) {
        if (args.length != 1) {
            throw new WrongArgumentsCountException("POP", 0, args.length - 1);
        }
        return new Pop();
    }
}
