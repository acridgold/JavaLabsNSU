package ru.nsu.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты CommandFactory")
class CommandFactoryTest {

    private Context context;
    private CommandFactory factory;

    @BeforeEach
    void setUp() {
        context = new Context();
        factory = new CommandFactory(context);
    }

    @Test
    @DisplayName("Создание команд без аргументов")
    void testNoArgCommands() {
        assertAll(
                () -> assertNotNull(factory.createCommand("POP")),
                () -> assertNotNull(factory.createCommand("SQRT")),
                () -> assertNotNull(factory.createCommand("PRINT")),
                () -> assertNotNull(factory.createCommand("+")),
                () -> assertNotNull(factory.createCommand("-")),
                () -> assertNotNull(factory.createCommand("*")),
                () -> assertNotNull(factory.createCommand("/"))
        );
    }

    @Test
    @DisplayName("PUSH с числом и переменной")
    void testPush() {
        context.define("x", 42.0);

        Command cmd1 = factory.createCommand("PUSH 5");
        cmd1.execute(context);
        assertEquals(5.0, context.pop());

        Command cmd2 = factory.createCommand("PUSH x");
        cmd2.execute(context);
        assertEquals(42.0, context.pop());
    }

    @Test
    @DisplayName("DEFINE работает")
    void testDefine() {
        context.define("TEN", 10.0);

        factory.createCommand("DEFINE a 7").execute(context);
        assertEquals(7.0, context.getParameter("a"));

        factory.createCommand("DEFINE b TEN").execute(context);
        assertEquals(10.0, context.getParameter("b"));
    }

    @Test
    @DisplayName("Ошибки при создании команд")
    void testErrors() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> factory.createCommand("")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> factory.createCommand("UNKNOWN")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> factory.createCommand("PUSH")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> factory.createCommand("POP 5")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> factory.createCommand("DEFINE x"))
        );
    }

    @Test
    @DisplayName("Подстановка переменных")
    void testVariableSubstitution() {
        context.define("a", 1.0);
        context.define("b", 2.0);

        factory.createCommand("PUSH a").execute(context);
        factory.createCommand("PUSH b").execute(context);
        factory.createCommand("+").execute(context);

        assertEquals(3.0, context.pop());
    }

    @Test
    @DisplayName("Регистронезависимость")
    void testCaseInsensitive() {
        assertAll(
                () -> assertNotNull(factory.createCommand("push 5")),
                () -> assertNotNull(factory.createCommand("PoP")),
                () -> assertNotNull(factory.createCommand("sQrT")),
                () -> assertNotNull(factory.createCommand("DeFiNe x 10"))
        );
    }

    @Test
    @DisplayName("createSqrt() кидает исключение при наличии аргументов")
    void testCreateSqrtIllArgExc() {
        CommandFactory factory = new CommandFactory(new Context());

        assertThrows(IllegalArgumentException.class, () -> factory.createCommand("SQRT 42"));
    }
}