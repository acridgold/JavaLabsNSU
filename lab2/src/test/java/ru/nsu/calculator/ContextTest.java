package ru.nsu.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для класса Context")
class ContextTest {

    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Test
    @DisplayName("Push и Pop работают корректно")
    void testPushAndPop() {
        context.push(5.0);
        context.push(3.0);

        assertEquals(3.0, context.pop());
        assertEquals(5.0, context.pop());
        assertTrue(context.stackIsEmpty());
    }

    @Test
    @DisplayName("Peek смотрит верхний элемент без удаления")
    void testPeek() {
        context.push(42.0);
        assertEquals(42.0, context.peek());
        assertFalse(context.stackIsEmpty());
        assertEquals(42.0, context.pop());
    }

    @Test
    @DisplayName("Define и GetParameter работают с переменными")
    void testDefineAndGetParameter() {
        context.define("pi", 3.14);
        context.define("e", 2.71);

        assertEquals(3.14, context.getParameter("pi"));
        assertEquals(2.71, context.getParameter("e"));
        assertNull(context.getParameter("unknown"));
    }
}