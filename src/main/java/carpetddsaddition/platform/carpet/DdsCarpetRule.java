//#if MC >= 11902
//$$ /*
//$$  * This file is part of the Carpet DDS Addition project, licensed under the
//$$  * GNU Lesser General Public License v3.0
//$$  */
//$$ package carpetddsaddition.platform.carpet;
//$$
//$$ import carpet.api.settings.CarpetRule;
//$$ import carpet.api.settings.InvalidRuleValueException;
//$$ import carpet.api.settings.SettingsManager;
//$$ import carpetddsaddition.core.rules.DdsRule;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.network.chat.Component;
//$$
//$$ import java.util.Collection;
//$$ import java.util.Collections;
//$$ import java.util.List;
//$$
//$$ /** Public-API Carpet view of one DDS-owned modern rule. */
//$$ final class DdsCarpetRule<T> implements CarpetRule<T> {
//$$     private final DdsSettingsManager manager;
//$$     private final DdsRule<T> rule;
//$$
//$$     DdsCarpetRule(DdsSettingsManager manager, DdsRule<T> rule) {
//$$         this.manager = manager;
//$$         this.rule = rule;
//$$     }
//$$
//$$     @Override public String name() { return rule.name(); }
//$$     @Override public List<Component> extraInfo() { return Collections.emptyList(); }
//$$     @Override public Collection<String> categories() { return rule.categories(); }
//$$     @Override public Collection<String> suggestions() { return rule.suggestions(); }
//$$     @Override public SettingsManager settingsManager() { return manager; }
//$$     @Override public T value() { return rule.value(); }
//$$     @Override public boolean canBeToggledClientSide() { return rule.categories().contains("client"); }
//$$     @Override public Class<T> type() { return rule.type(); }
//$$     @Override public T defaultValue() { return rule.defaultValue(); }
//$$     @Override public boolean strict() { return rule.strict(); }
//$$
//$$     @Override
//$$     public void set(CommandSourceStack source, String value) throws InvalidRuleValueException {
//$$         final T parsed;
//$$         try {
//$$             parsed = rule.parse(value);
//$$         } catch (IllegalArgumentException e) {
//$$             throw new InvalidRuleValueException(e.getMessage());
//$$         }
//$$         set(source, parsed, value);
//$$     }
//$$
//$$     @Override
//$$     public void set(CommandSourceStack source, T value) throws InvalidRuleValueException {
//$$         set(source, value, rule.serialize(value));
//$$     }
//$$
//$$     private void set(CommandSourceStack source, T value, String userInput) throws InvalidRuleValueException {
//$$         final boolean changed;
//$$         try {
//$$             changed = source == null
//$$                     ? rule.setSilently(value)
//$$                     : rule.set(value, manager.currentOrigin(), manager.currentServer());
//$$         } catch (IllegalArgumentException e) {
//$$             throw new InvalidRuleValueException(e.getMessage());
//$$         }
//$$         if (changed && source != null) manager.notifyRuleChanged(source, this, userInput);
//$$     }
//$$ }
//#endif