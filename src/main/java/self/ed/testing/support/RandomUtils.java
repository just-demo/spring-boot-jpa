package self.ed.testing.support;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.number.IntegerRandomizer;
import io.github.benas.randombeans.randomizers.number.LongRandomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;

import static io.github.benas.randombeans.randomizers.text.StringRandomizer.aNewStringRandomizer;

/**
 * @author Anatolii
 */
public class RandomUtils {
    private static final IntegerRandomizer INTEGER_RANDOMIZER = new PositiveIntegerRandomizer();
    private static final LongRandomizer LONG_RANDOMIZER = new PositiveLongRandomizer();
    private static final StringRandomizer STRING_RANDOMIZER = aNewStringRandomizer(10, 10, 0);
    private static final EnhancedRandom ENHANCED_RANDOM = enhancedRandom();

    public static <T> T random(Class<T> type, String... excludedFields) {
        return ENHANCED_RANDOM.nextObject(type, excludedFields);
    }

    private static EnhancedRandom enhancedRandom() {
        return new EnhancedRandomBuilder()
                .randomize(Integer.class, INTEGER_RANDOMIZER)
                .randomize(Long.class, LONG_RANDOMIZER)
                .randomize(String.class, STRING_RANDOMIZER)
                .collectionSizeRange(0, 0)
                .build();
    }

    private static class PositiveIntegerRandomizer extends IntegerRandomizer {
        @Override
        public Integer getRandomValue() {
            return Math.abs(super.getRandomValue());
        }
    }

    private static class PositiveLongRandomizer extends LongRandomizer {
        @Override
        public Long getRandomValue() {
            return Math.abs(super.getRandomValue());
        }
    }
}
