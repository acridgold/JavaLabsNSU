package ru.nsu.calculator.mods;

import ru.nsu.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        Mods.PUSH push = new Mods.PUSH(5.0);
        push.execute(context);

        assertEquals(1, context.stackSize());
        assertEquals(5.0, context.pop());
    }

    @Test
    @DisplayName("Несколько PUSH кладут числа в правильном порядке")
    void testMultiplePushes() {
        Mods.PUSH push1 = new Mods.PUSH(1.0);
        Mods.PUSH push2 = new Mods.PUSH(2.0);
        Mods.PUSH push3 = new Mods.PUSH(3.0);

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

        Mods.POP pop = new Mods.POP();
        pop.execute(context);

        assertEquals(1, context.stackSize());
        assertEquals(5.0, context.pop());
    }

    @Test
    @DisplayName("POP на пустом стеке кидает исключение")
    void testPopOnEmptyStack() {
        Mods.POP pop = new Mods.POP();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> pop.execute(context)
        );
        assertEquals("Стек пуст", exception.getMessage());
    }
}