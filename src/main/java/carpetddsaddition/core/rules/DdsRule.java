/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.core.rules;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Stable DDS rule identity and business access API.
 *
 * <p>The physical value store is backend-specific: modern Carpet may use a DDS-owned
 * store while legacy Carpet bridges directly to the annotated static field.</p>
 */
public final class DdsRule<T> {
    private static final Logger LOGGER = LogManager.getLogger("dds.rules");

    @FunctionalInterface
    public interface Listener<T> {
        void onChanged(DdsRuleChange<T> change);
    }

    private interface ValueStore<T> {
        T get();
        void set(T value);
    }

    private static final class OwnedValueStore<T> implements ValueStore<T> {
        private volatile T value;
        private OwnedValueStore(T value) { this.value = value; }
        @Override public T get() { return value; }
        @Override public void set(T value) { this.value = value; }
    }

    private static final class BridgedValueStore<T> implements ValueStore<T> {
        private final Supplier<T> reader;
        private final Consumer<T> writer;
        private BridgedValueStore(Supplier<T> reader, Consumer<T> writer) {
            this.reader = reader;
            this.writer = writer;
        }
        @Override public T get() { return reader.get(); }
        @Override public void set(T value) { writer.accept(value); }
    }

    private final String name;
    private final Class<T> type;
    private final T defaultValue;
    private final List<String> categories;
    private final List<String> suggestions;
    private final boolean strict;
    private final boolean available;
    private final DdsRuleCodec<T> codec;
    private final ValueStore<T> store;
    private final List<Listener<T>> listeners = new ArrayList<Listener<T>>();

    private boolean captured;
    private T observedValue;

    private DdsRule(String name, Class<T> type, T defaultValue, String[] categories,
                    String[] suggestions, boolean strict, boolean available,
                    DdsRuleCodec<T> codec, ValueStore<T> store) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.defaultValue = Objects.requireNonNull(defaultValue, "defaultValue");
        this.categories = immutableStrings(categories);
        this.suggestions = immutableStrings(suggestions);
        this.strict = strict && !this.suggestions.isEmpty();
        this.available = available;
        this.codec = Objects.requireNonNull(codec, "codec");
        this.store = Objects.requireNonNull(store, "store");
    }

    public static <T> DdsRule<T> owned(String name, Class<T> type, T defaultValue,
                                       String[] categories, String[] suggestions,
                                       boolean strict, DdsRuleCodec<T> codec) {
        return new DdsRule<T>(name, type, defaultValue, categories, suggestions, strict, true,
                codec, new OwnedValueStore<T>(defaultValue));
    }

    public static <T> DdsRule<T> bridged(String name, Class<T> type, T defaultValue,
                                         String[] categories, String[] suggestions,
                                         boolean strict, DdsRuleCodec<T> codec,
                                         Supplier<T> reader, Consumer<T> writer) {
        return new DdsRule<T>(name, type, defaultValue, categories, suggestions, strict, true,
                codec, new BridgedValueStore<T>(reader, writer));
    }

    public static <T> DdsRule<T> unavailable(String name, Class<T> type, T defaultValue,
                                             String[] categories, String[] suggestions,
                                             boolean strict, DdsRuleCodec<T> codec) {
        return new DdsRule<T>(name, type, defaultValue, categories, suggestions, strict, false,
                codec, new OwnedValueStore<T>(defaultValue));
    }

    private static List<String> immutableStrings(String[] values) {
        return Collections.unmodifiableList(Arrays.asList(values.clone()));
    }

    public String name() { return name; }
    public Class<T> type() { return type; }
    public T defaultValue() { return defaultValue; }
    public T value() { return store.get(); }
    public List<String> categories() { return categories; }
    public List<String> suggestions() { return suggestions; }
    public boolean strict() { return strict; }
    public boolean available() { return available; }
    public DdsRuleCodec<T> codec() { return codec; }

    public T parse(String value) {
        T parsed = codec.parse(value);
        validate(parsed);
        return parsed;
    }

    public String serialize(T value) { return codec.serialize(value); }

    public synchronized DdsRule<T> onChanged(Listener<T> listener) {
        listeners.add(Objects.requireNonNull(listener, "listener"));
        return this;
    }

    /** Commits a DDS-owned value. Legacy Carpet should mutate its field then call captureBackendChange. */
    public boolean set(T value, DdsRuleChange.Origin origin, MinecraftServer server) {
        return setInternal(value, origin, server, true);
    }

    /** Mirrors Carpet's source-null config/reset path without emitting business callbacks. */
    public boolean setSilently(T value) {
        return setInternal(value, DdsRuleChange.Origin.INTERNAL, null, false);
    }

    private boolean setInternal(T value, DdsRuleChange.Origin origin,
                                MinecraftServer server, boolean dispatchChange) {
        if (!available) throw new IllegalStateException("Rule is unavailable on this target: " + name);
        validate(value);
        T oldValue = store.get();
        if (Objects.equals(oldValue, value)) {
            captureValue(value);
            return false;
        }
        store.set(value);
        captureValue(value);
        if (dispatchChange) dispatch(oldValue, value, origin, server);
        return true;
    }

    /** Establishes a baseline without emitting a change. */
    public synchronized void captureCurrentState() {
        observedValue = store.get();
        captured = true;
    }

    /**
     * Detects a mutation performed by a legacy backend whose field is the physical store.
     * This method never writes a second copy of the value.
     */
    public void captureBackendChange(DdsRuleChange.Origin origin, MinecraftServer server) {
        T current = store.get();
        T oldValue;
        synchronized (this) {
            if (!captured) {
                observedValue = current;
                captured = true;
                return;
            }
            oldValue = observedValue;
            if (Objects.equals(oldValue, current)) return;
            observedValue = current;
        }
        dispatch(oldValue, current, origin, server);
    }

    private void validate(T value) {
        Objects.requireNonNull(value, "value");
        if (strict && !suggestions.contains(codec.serialize(value))) {
            throw new IllegalArgumentException("Valid values for " + name + " are: " + suggestions);
        }
    }

    private synchronized void captureValue(T value) {
        observedValue = value;
        captured = true;
    }

    private void dispatch(T oldValue, T newValue, DdsRuleChange.Origin origin, MinecraftServer server) {
        DdsRuleChange<T> change = new DdsRuleChange<T>(
                this, oldValue, newValue,
                origin == null ? DdsRuleChange.Origin.UNKNOWN : origin,
                server
        );
        Listener<T>[] snapshot;
        synchronized (this) {
            @SuppressWarnings("unchecked")
            Listener<T>[] values = listeners.toArray(new Listener[listeners.size()]);
            snapshot = values;
        }
        for (Listener<T> listener : snapshot) {
            try {
                listener.onChanged(change);
            } catch (RuntimeException e) {
                LOGGER.error("DDS rule listener failed for {}", name, e);
            }
        }
    }
}