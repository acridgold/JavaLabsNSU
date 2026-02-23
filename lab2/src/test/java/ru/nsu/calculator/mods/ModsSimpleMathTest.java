package ru.nsu.calculator.mods;

import ru.nsu.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для математических операций (+, -, *, /)")
class ModsSimpleMathTest {

    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @ParameterizedTest
    @CsvSource({
            "+, 5, 3, 8",
            "-, 5, 3, 2",
            "*, 5, 3, 15",
            "/, 6, 3, 2"
    })
    @DisplayName("Базовые математические операции работают корректно")
    void testBasicOperations(char operation, double a, double b, double expected) {
        context.push(a);
        context.push(b);

        Mods.Simple_Math math = new Mods.Simple_Math(operation);
        math.execute(context);

        assertEquals(expected, context.pop());
    }

    @Test
    @DisplayName("Сложение с отрицательными числами")
    void testAddWithNegatives() {
        context.push(-5.0);
        context.push(3.0);

        Mods.Simple_Math add = new Mods.Simple_Math('+');
        add.execute(context);

        assertEquals(-2.0, context.pop());
    }

    @Test
    @DisplayName("Умножение на ноль")
    void testMultiplyByZero() {
        context.push(10.0);
        context.push(0.0);

        Mods.Simple_Math multiply = new Mods.Simple_Math('*');
        multiply.execute(context);

        assertEquals(0.0, context.pop());
    }

    @Test
    @DisplayName("Деление на ноль кидает исключение")
    void testDivideByZero() {
        context.push(10.0);
        context.push(0.0);

        Mods.Simple_Math divide = new Mods.Simple_Math('/');

        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> divide.execute(context)
        );
        assertEquals("Деление на ноль", exception.getMessage());

        // Проверяем, что стек восстановлен
        assertEquals(2, context.stackSize());
        assertEquals(0.0, context.pop());
        assertEquals(10.0, context.pop());
    }

    @Test
    @DisplayName("Операция с недостаточными аргументами кидает исключение")
    void testOperationWithOneArgument() {
        context.push(5.0); // только один аргумент

        Mods.Simple_Math add = new Mods.Simple_Math('+');

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> add.execute(context)
        );
        assertEquals("Недостаточно элементов на стеке", exception.getMessage());

        // Проверяем, что стек восстановлен
        assertEquals(1, context.stackSize());
        assertEquals(5.0, context.pop());
    }

    @Test
    @DisplayName("Операция на пустом стеке кидает исключение")
    void testOperationOnEmptyStack() {
        Mods.Simple_Math add = new Mods.Simple_Math('+');

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> add.execute(context)
        );
        assertEquals("Стек пуст", exception.getMessage());
    }

    @Test
    @DisplayName("Неизвестная операция кидает исключение")
    void testUnknownOperation() {
        context.push(5.0);
        context.push(3.0);

        Mods.Simple_Math unknown = new Mods.Simple_Math('%');

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> unknown.execute(context)
        );
        assertEquals("Неизвестная операция: %", exception.getMessage());

        // Проверяем, что стек восстановлен
        assertEquals(2, context.stackSize());
        assertEquals(3.0, context.pop());
        assertEquals(5.0, context.pop());
    }

    @Test
    @DisplayName("Цепочка операций")
    void testOperationChain() {
        // (5 + 3) * 2 = 16
        context.push(5.0);
        context.push(3.0);
        new Mods.Simple_Math('+').execute(context); // стек: [8]

        context.push(2.0);
        new Mods.Simple_Math('*').execute(context); // стек: [16]

        assertEquals(16.0, context.pop());
    }
}