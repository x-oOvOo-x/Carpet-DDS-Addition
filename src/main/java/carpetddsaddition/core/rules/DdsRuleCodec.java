/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.core.rules;

/** Converts the stable external Carpet string representation to a typed DDS value. */
public interface DdsRuleCodec<T> {
    T parse(String value);
    String serialize(T value);
}
