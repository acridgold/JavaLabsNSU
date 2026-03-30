package ru.nsu.calculator.mods;

import ru.nsu.calculator.Command;
import ru.nsu.calculator.Context;

import static ru.nsu.calculator.CalculatorExceptions.*;

public class Push implements Command {
    private final String token;

    public Push(String token) {
        this.token = token;
    }

    @Override
    public void execute(Context context) {
        context.push(context.resolveNumericToken(token, getCommandName()));
    }

    public static String getCommandName() {
        return "PUSH";
    }

    public static Command create(String[] args) {
        if (args.length != 2) {
            throw new WrongArgumentsCountException("PUSH", 1, args.length - 1);
        }
        return new Push(args[1]);
    }
}
