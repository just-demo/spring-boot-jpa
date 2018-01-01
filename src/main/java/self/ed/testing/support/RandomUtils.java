package self.ed.testing.support;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.api.Randomizer;

import java.util.Random;

import static io.github.benas.randombeans.randomizers.text.StringRandomizer.aNewStringRandomizer;

/**
 * @author Anatolii
 */
public class RandomUtils {
    private static final EnhancedRandom ENHANCED_RANDOM = enhancedRandom();

    public static <T> T random(Class<T> type, String... excludedFields) {
        return ENHANCED_RANDOM.nextObject(type, excludedFields);
    }

    private static EnhancedRandom enhancedRandom() {
        Random random = new Random();
        Randomizer<Integer> positiveIntegerRandomizer = () -> Math.abs(random.nextInt());
        Randomizer<Long> positiveLongRandomizer = () -> Math.abs(random.nextLong());
        Randomizer<String> fixedWidthStringRandomizer = aNewStringRandomizer(10, 10, 0);
        return new EnhancedRandomBuilder()
                .randomize(Integer.class, positiveIntegerRandomizer)
                .randomize(Long.class, positiveLongRandomizer)
                .randomize(String.class, fixedWidthStringRandomizer)
                .collectionSizeRange(0, 0)
                .build();
    }
}
