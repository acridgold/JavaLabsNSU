package ru.nsu.calculator;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для класса Calculator")
class CalculatorTest {

    private Calculator calculator;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        outputStream.reset();
    }

    @Test
    @DisplayName("Конструктор создает калькулятор с пустым контекстом")
    void testConstructor() {
        assertNotNull(calculator.getContext());
        assertTrue(calculator.getContext().stackIsEmpty());
    }

    @Nested
    @DisplayName("Тесты выполнения файлов")
    class FileExecutionTests {

        @Test
        @DisplayName("Успешное выполнение простой программы")
        void testExecuteSimpleProgram(@TempDir Path tempDir) throws IOException {
            // Создаем временный файл с командами
            Path programFile = tempDir.resolve("simple.txt");
            Files.writeString(programFile, """
                    PUSH 5
                    PUSH 3
                    +
                    PRINT
                    """);

            calculator.executeFile(programFile.toString());

            // Проверяем вывод
            String output = outputStream.toString();
            assertTrue(output.contains("8.0"));
            assertTrue(output.contains("Calculator: Done"));
        }

        @Test
        @DisplayName("Выполнение программы с переменными")
        void testProgramWithVariables(@TempDir Path tempDir) throws IOException {
            Path programFile = tempDir.resolve("vars.txt");
            Files.writeString(programFile, """
                    DEFINE a 4
                    DEFINE b 5
                    PUSH a
                    PUSH b
                    *
                    PRINT
                    """);

            calculator.executeFile(programFile.toString());

            // Проверяем результат
            String output = outputStream.toString();
            assertTrue(output.contains("20.0"));

            // Проверяем, что переменные сохранились
            assertEquals(4.0, calculator.getContext().getParameter("a"));
            assertEquals(5.0, calculator.getContext().getParameter("b"));
        }

        @Test
        @DisplayName("Программа с комментариями выполняется корректно")
        void testProgramWithComments(@TempDir Path tempDir) throws IOException {
            Path programFile = tempDir.resolve("comments.txt");
            Files.writeString(programFile, """
                    # Это комментарий
                    PUSH 10  # число 10
                    PUSH 20  # еще число
                    +        # складываем
                    PRINT    # печатаем 30
                    """);

            calculator.executeFile(programFile.toString());

            String output = outputStream.toString();
            assertTrue(output.contains("30.0"));
        }

        @Test
        @DisplayName("Пустые строки игнорируются")
        void testEmptyLines(@TempDir Path tempDir) throws IOException {
            Path programFile = tempDir.resolve("empty.txt");
            Files.writeString(programFile, """
                    
                    PUSH 5
                    
                    PUSH 3
                    
                    +
                    
                    PRINT
                    
                    """);

            assertDoesNotThrow(() -> calculator.executeFile(programFile.toString()));

            String output = outputStream.toString();
            assertTrue(output.contains("8.0"));
        }

        @Test
        @DisplayName("Файл только с комментариями не вызывает ошибок")
        void testOnlyComments(@TempDir Path tempDir) throws IOException {
            Path programFile = tempDir.resolve("only_comments.txt");
            Files.writeString(programFile, """
                    # Это комментарий
                    # Еще комментарий
                    # И еще
                    """);

            assertDoesNotThrow(() -> calculator.executeFile(programFile.toString()));

            // Проверяем, что вывод все равно есть (Done)
            String output = outputStream.toString();
            assertTrue(output.contains("Calculator: Done"));
        }
    }

    @Nested
    @DisplayName("Тесты обработки ошибок")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Несуществующий файл кидает IOException")
        void testNonExistentFile() {
            Calculator calculator = new Calculator();

            assertThrows(IOException.class, () -> calculator.executeFile("non_existent_file.txt"));
        }

        @Test
        @DisplayName("Ошибка в команде кидает RuntimeException с номером строки")
        void testCommandError(@TempDir Path tempDir) throws IOException {
            Path programFile = tempDir.resolve("error.txt");
            Files.writeString(programFile, """
                    PUSH 5
                    POP
                    POP  # здесь будет ошибка (стек пуст)
                    """);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> calculator.executeFile(programFile.toString()));

            assertTrue(exception.getMessage().contains("Ошибка в строке 3"));
            assertTrue(exception.getMessage().contains("Стек пуст"));
        }

        @Test
        @DisplayName("Неизвестная команда кидает исключение")
        void testUnknownCommand(@TempDir Path tempDir) throws IOException {
            Path programFile = tempDir.resolve("unknown.txt");
            Files.writeString(programFile, """
                    PUSH 5
                    UNKNOWN_COMMAND  # такой команды нет
                    """);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> calculator.executeFile(programFile.toString()));

            assertTrue(exception.getMessage().contains("Неизвестная команда"));
        }

        @Test
        @DisplayName("Ошибка в DEFINE с неправильным числом")
        void testInvalidDefine(@TempDir Path tempDir) throws IOException {
            Path programFile = tempDir.resolve("bad_define.txt");
            Files.writeString(programFile, """
                    DEFINE x not_a_number
                    """);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> calculator.executeFile(programFile.toString()));

            assertTrue(exception.getMessage().contains("должен быть числом"));
        }
    }
}