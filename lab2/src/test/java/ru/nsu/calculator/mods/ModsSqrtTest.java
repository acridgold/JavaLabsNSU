package ru.nsu.calculator.mods;

import ru.nsu.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для команды SQRT")
class ModsSqrtTest {

    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Test
    @DisplayName("SQRT вычисляет корень из положительного числа")
    void testSqrtPositive() {
        context.push(16.0);

        Mods.SQRT sqrt = new Mods.SQRT();
        sqrt.execute(context);

        assertEquals(4.0, context.pop());
    }

    @Test
    @DisplayName("SQRT вычисляет корень из нуля")
    void testSqrtZero() {
        context.push(0.0);

        Mods.SQRT sqrt = new Mods.SQRT();
        sqrt.execute(context);

        assertEquals(0.0, context.pop());
    }

    @Test
    @DisplayName("SQRT из отрицательного числа кидает исключение")
    void testSqrtNegative() {
        context.push(-4.0);

        Mods.SQRT sqrt = new Mods.SQRT();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sqrt.execute(context)
        );
        assertEquals("Нельзя взять корень из отрицательного числа", exception.getMessage());
    }

    @Test
    @DisplayName("SQRT на пустом стеке кидает исключение")
    void testSqrtOnEmptyStack() {
        Mods.SQRT sqrt = new Mods.SQRT();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> sqrt.execute(context)
        );
        assertEquals("Стек пуст", exception.getMessage());
    }

    @Test
    @DisplayName("SQRT удаляет исходное число и кладет результат")
    void testSqrtRemovesAndPushes() {
        context.push(9.0);
        context.push(100.0);

        Mods.SQRT sqrt = new Mods.SQRT();
        sqrt.execute(context);

        assertEquals(10.0, context.pop());
        assertEquals(9.0, context.pop());
    }
}
