/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.core.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Low-frequency control-plane registry for generated DDS rule identities. */
public final class DdsRuleRegistry {
    private final Map<String, DdsRule<?>> rules = new LinkedHashMap<String, DdsRule<?>>();
    private boolean frozen;

    public synchronized <T> DdsRule<T> register(DdsRule<T> rule) {
        if (frozen) throw new IllegalStateException("DDS rule registry is frozen");
        if (rules.containsKey(rule.name()))
            throw new IllegalStateException("Duplicate DDS rule: " + rule.name());
        rules.put(rule.name(), rule);
        return rule;
    }

    public synchronized void freeze() { frozen = true; }
    public synchronized boolean frozen() { return frozen; }

    public synchronized DdsRule<?> get(String name) { return rules.get(name); }

    public synchronized Collection<DdsRule<?>> all() {
        return Collections.unmodifiableList(new ArrayList<DdsRule<?>>(rules.values()));
    }

    public synchronized List<DdsRule<?>> available() {
        List<DdsRule<?>> result = new ArrayList<DdsRule<?>>();
        for (DdsRule<?> rule : rules.values()) if (rule.available()) result.add(rule);
        return Collections.unmodifiableList(result);
    }
}