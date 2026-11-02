package carpetddsaddition.core.rules;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

final class DdsRuleCoreTest {
    @Test
    void ownedRuleCommitsTypedValueAndEmitsOneChange() {
        DdsRule<Boolean> rule = DdsRule.owned(
                "testRule", Boolean.class, Boolean.FALSE,
                new String[]{"DDS", "feature"}, new String[]{"true", "false"},
                true, DdsRuleCodecs.BOOLEAN
        );
        AtomicInteger calls = new AtomicInteger();
        AtomicReference<DdsRuleChange<Boolean>> last = new AtomicReference<DdsRuleChange<Boolean>>();
        rule.onChanged(change -> {
            calls.incrementAndGet();
            last.set(change);
        });

        rule.captureCurrentState();
        assertTrue(rule.set(Boolean.TRUE, DdsRuleChange.Origin.COMMAND, null));
        assertFalse(rule.set(Boolean.TRUE, DdsRuleChange.Origin.COMMAND, null));

        assertTrue(rule.value());
        assertEquals(1, calls.get());
        assertFalse(last.get().oldValue());
        assertTrue(last.get().newValue());
        assertEquals(DdsRuleChange.Origin.COMMAND, last.get().origin());
    }

    @Test
    void bridgedRuleUsesOnlyExternalStore() {
        AtomicReference<Integer> backend = new AtomicReference<Integer>(4);
        DdsRule<Integer> rule = DdsRule.bridged(
                "testInt", Integer.class, 4,
                new String[]{"DDS"}, new String[]{}, true, DdsRuleCodecs.INTEGER,
                backend::get, backend::set
        );
        AtomicInteger calls = new AtomicInteger();
        rule.onChanged(change -> calls.incrementAndGet());
        rule.captureCurrentState();

        backend.set(7);
        rule.captureBackendChange(DdsRuleChange.Origin.LEGACY_BACKEND, null);

        assertEquals(7, rule.value());
        assertEquals(1, calls.get());

        backend.set(7);
        rule.captureBackendChange(DdsRuleChange.Origin.LEGACY_BACKEND, null);
        assertEquals(1, calls.get());
    }

    @Test
    void freeFormStringRemainsNonStrictWithoutSuggestions() {
        DdsRule<String> rule = DdsRule.owned(
                "mode", String.class, "off",
                new String[]{"DDS"}, new String[]{}, true, DdsRuleCodecs.STRING
        );

        assertFalse(rule.strict());
        assertEquals("custom-value", rule.parse("custom-value"));
    }

    @Test
    void silentConfigCommitRebaselinesWithoutBusinessCallback() {
        DdsRule<Boolean> rule = DdsRule.owned(
                "configRule", Boolean.class, Boolean.FALSE,
                new String[]{"DDS"}, new String[]{"true", "false"},
                true, DdsRuleCodecs.BOOLEAN
        );
        AtomicInteger calls = new AtomicInteger();
        AtomicReference<DdsRuleChange<Boolean>> last = new AtomicReference<DdsRuleChange<Boolean>>();
        rule.onChanged(change -> {
            calls.incrementAndGet();
            last.set(change);
        });

        assertTrue(rule.setSilently(Boolean.TRUE));
        assertTrue(rule.value());
        assertEquals(0, calls.get());

        assertTrue(rule.set(Boolean.FALSE, DdsRuleChange.Origin.COMMAND, null));
        assertEquals(1, calls.get());
        assertTrue(last.get().oldValue());
        assertFalse(last.get().newValue());
    }

    @Test
    void duplicateRegistrationKeepsOriginalIdentity() {
        DdsRuleRegistry registry = new DdsRuleRegistry();
        DdsRule<Boolean> original = DdsRule.owned(
                "sameName", Boolean.class, Boolean.FALSE,
                new String[]{"DDS"}, new String[]{"true", "false"}, true, DdsRuleCodecs.BOOLEAN
        );
        DdsRule<Boolean> duplicate = DdsRule.owned(
                "sameName", Boolean.class, Boolean.TRUE,
                new String[]{"DDS"}, new String[]{"true", "false"}, true, DdsRuleCodecs.BOOLEAN
        );

        registry.register(original);
        assertThrows(IllegalStateException.class, () -> registry.register(duplicate));
        assertSame(original, registry.get("sameName"));
        assertEquals(1, registry.all().size());
        registry.freeze();
        assertThrows(IllegalStateException.class, () -> registry.register(
                DdsRule.owned("other", Boolean.class, Boolean.FALSE,
                        new String[]{"DDS"}, new String[]{"true", "false"}, true, DdsRuleCodecs.BOOLEAN)));
    }

    @Test
    void booleanCodecRejectsUnknownValues() {
        assertEquals(Boolean.TRUE, DdsRuleCodecs.BOOLEAN.parse("true"));
        assertEquals(Boolean.FALSE, DdsRuleCodecs.BOOLEAN.parse("false"));
        assertThrows(IllegalArgumentException.class, () -> DdsRuleCodecs.BOOLEAN.parse("yes"));
    }
}