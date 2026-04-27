package ru.nsu.factory;

import org.junit.jupiter.api.Test;
import ru.nsu.factory.model.Accessory;
import ru.nsu.factory.model.Body;
import ru.nsu.factory.model.Motor;
import ru.nsu.factory.model.Part;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PartTest {

    @Test
    void allPartIdsAreGloballyUnique() {
        // Создаём детали разных типов — ID не должны пересекаться
        Set<Integer> ids = Stream.of(
                new Body(), new Body(),
                new Motor(), new Motor(),
                new Accessory(), new Accessory()
        ).map(Part::getId).collect(Collectors.toSet());

        assertEquals(6, ids.size());
    }
}
