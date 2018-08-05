package self.ed.testing.support;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.api.Randomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;

import java.util.Random;

import static java.lang.Math.abs;

public class RandomUtils {
    private static final Random RANDOM = new Random();
    private static final EnhancedRandom ENHANCED_RANDOM = new EnhancedRandomBuilder()
            .randomize(Integer.class, (Randomizer<Integer>) () -> abs(RANDOM.nextInt()))
            .randomize(Long.class, (Randomizer<Long>) () -> abs(RANDOM.nextLong()))
            .randomize(String.class, StringRandomizer.aNewStringRandomizer(10, 10, 0))
            .collectionSizeRange(0, 0)
            .build();

    public static <T> T random(Class<T> type, String... excludedFields) {
        return ENHANCED_RANDOM.nextObject(type, excludedFields);
    }
}
