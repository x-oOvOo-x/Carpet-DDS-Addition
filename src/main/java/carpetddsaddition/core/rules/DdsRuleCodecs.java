/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.core.rules;

/** Built-in codecs for the cross-version scalar rule types supported by rules.json. */
public final class DdsRuleCodecs {
    private DdsRuleCodecs() {}

    public static final DdsRuleCodec<Boolean> BOOLEAN = new DdsRuleCodec<Boolean>() {
        @Override public Boolean parse(String value) {
            if ("true".equals(value)) return Boolean.TRUE;
            if ("false".equals(value)) return Boolean.FALSE;
            throw new IllegalArgumentException("Invalid boolean value");
        }
        @Override public String serialize(Boolean value) { return value.toString(); }
    };

    public static final DdsRuleCodec<Integer> INTEGER = new DdsRuleCodec<Integer>() {
        @Override public Integer parse(String value) {
            try { return Integer.valueOf(value); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid integer value", e); }
        }
        @Override public String serialize(Integer value) { return value.toString(); }
    };

    public static final DdsRuleCodec<Double> DOUBLE = new DdsRuleCodec<Double>() {
        @Override public Double parse(String value) {
            try { return Double.valueOf(value); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid double value", e); }
        }
        @Override public String serialize(Double value) { return value.toString(); }
    };

    public static final DdsRuleCodec<String> STRING = new DdsRuleCodec<String>() {
        @Override public String parse(String value) { return value; }
        @Override public String serialize(String value) { return value; }
    };
}
