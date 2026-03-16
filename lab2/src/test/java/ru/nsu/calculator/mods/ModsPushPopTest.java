package ru.nsu.calculator.mods;

import ru.nsu.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static ru.nsu.calculator.CalculatorExceptions.*;

@DisplayName("Тесты для команд PUSH и POP")
class ModsPushPopTest {

    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Test
    @DisplayName("PUSH кладет число на стек")
    void testPush() {
        Push push = new Push(5.0);
        push.execute(context);

        assertEquals(1, context.stackSize());
        assertEquals(5.0, context.pop());
    }

    @Test
    @DisplayName("Несколько PUSH кладут числа в правильном порядке")
    void testMultiplePushes() {
        Push push1 = new Push(1.0);
        Push push2 = new Push(2.0);
        Push push3 = new Push(3.0);

        push1.execute(context);
        push2.execute(context);
        push3.execute(context);

        assertEquals(3.0, context.pop());
        assertEquals(2.0, context.pop());
        assertEquals(1.0, context.pop());
    }

    @Test
    @DisplayName("POP удаляет верхний элемент")
    void testPop() {
        context.push(5.0);
        context.push(3.0);

        Pop pop = new Pop();
        pop.execute(context);

        assertEquals(1, context.stackSize());
        assertEquals(5.0, context.pop());
    }

    @Test
    @DisplayName("POP на пустом стеке кидает исключение")
    void testPopOnEmptyStack() {
        Pop pop = new Pop();

        InsufficientStackElementsException exception = assertThrows(
                InsufficientStackElementsException.class,
                () -> pop.execute(context)
        );
        assertEquals("Стек пуст", exception.getMessage());
    }
}