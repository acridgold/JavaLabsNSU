package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;
import static ru.nsu.calculator.CalculatorExceptions.*;

public class Print implements Command {
    @Override
    public void execute(Context context) {
        if (context.stackIsEmpty()) {
            throw new InsufficientStackElementsException("PRINT");
        }
        System.out.println(context.peek());
    }

    public static String getCommandName() {
        return "PRINT";
    }

    public static Command create(String[] args) {
        if (args.length != 1) {
            throw new WrongArgumentsCountException("PRINT", 0, args.length - 1);
        }
        return new Print();
    }
}
