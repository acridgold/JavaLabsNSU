package ru.nsu.calculator.mods;

import ru.nsu.calculator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для команды DEFINE")
class ModsDefineTest {

    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Test
    @DisplayName("DEFINE сохраняет переменную")
    void testDefine() {
        Mods.DEFINE define = new Mods.DEFINE("x", 10.0);
        define.execute(context);

        assertEquals(10.0, context.getParameter("x"));
    }

    @Test
    @DisplayName("DEFINE перезаписывает существующую переменную")
    void testDefineOverwrite() {
        context.define("x", 5.0);

        Mods.DEFINE define = new Mods.DEFINE("x", 20.0);
        define.execute(context);

        assertEquals(20.0, context.getParameter("x"));
    }

    @Test
    @DisplayName("Несколько DEFINE сохраняют разные переменные")
    void testMultipleDefines() {
        Mods.DEFINE define1 = new Mods.DEFINE("a", 1.0);
        Mods.DEFINE define2 = new Mods.DEFINE("b", 2.0);
        Mods.DEFINE define3 = new Mods.DEFINE("c", 3.0);

        define1.execute(context);
        define2.execute(context);
        define3.execute(context);

        assertEquals(1.0, context.getParameter("a"));
        assertEquals(2.0, context.getParameter("b"));
        assertEquals(3.0, context.getParameter("c"));
    }

    @Test
    @DisplayName("DEFINE с одинаковым именем обновляет значение")
    void testDefineUpdate() {
        Mods.DEFINE define1 = new Mods.DEFINE("x", 10.0);
        Mods.DEFINE define2 = new Mods.DEFINE("x", 99.0);

        define1.execute(context);
        assertEquals(10.0, context.getParameter("x"));

        define2.execute(context);
        assertEquals(99.0, context.getParameter("x"));
    }
}