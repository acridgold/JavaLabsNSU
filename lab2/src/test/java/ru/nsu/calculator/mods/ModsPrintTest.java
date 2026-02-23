package ru.nsu.calculator.mods;

import ru.nsu.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для команды PRINT")
class ModsPrintTest {

    private Context context;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        context = new Context();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void restoreSystemOut() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("PRINT печатает верхний элемент стека")
    void testPrint() {
        context.push(42.0);

        Mods.PRINT print = new Mods.PRINT();
        print.execute(context);
        assertEquals("42.0" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    @DisplayName("PRINT не удаляет элемент со стека")
    void testPrintDoesNotPop() {
        context.push(3.14);

        Mods.PRINT print = new Mods.PRINT();
        print.execute(context);

        assertFalse(context.stackIsEmpty());
        assertEquals(3.14, context.pop());
    }

    @Test
    @DisplayName("PRINT на пустом стеке кидает исключение")
    void testPrintOnEmptyStack() {
        Mods.PRINT print = new Mods.PRINT();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> print.execute(context)
        );
        assertEquals("Стек пуст", exception.getMessage());
    }

    @Test
    @DisplayName("Несколько PRINT печатают правильные значения")
    void testMultiplePrints() {
        context.push(1.0);
        context.push(2.0);

        Mods.PRINT print = new Mods.PRINT();
        print.execute(context); // печатает 2.0
        print.execute(context); // печатает 2.0 (снова)

        String output = outputStream.toString();
        String[] lines = output.split(System.lineSeparator());
        assertEquals(2, lines.length);
        assertEquals("2.0", lines[0]);
        assertEquals("2.0", lines[1]);
    }
}