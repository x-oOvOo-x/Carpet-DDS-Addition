/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.platform.carpet;

import carpetddsaddition.core.rules.DdsRule;
import carpetddsaddition.generated.DDSRules;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

/** Adds /cda and literal-only legacy /carpet entry points without registering a second rule. */
final class DdsRuleCommandCompat {
    private static final Set<Object> REGISTERED =
            Collections.newSetFromMap(new WeakHashMap<Object, Boolean>());

    private DdsRuleCommandCompat() {}

    static void register(MinecraftServer server) {
        register(server.getCommands().getDispatcher());
    }

    static synchronized void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!REGISTERED.add(dispatcher)) return;

        registerCdaAlias(dispatcher);
        registerCarpetCompatibility(dispatcher);
    }

    private static void registerCdaAlias(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> cda =
                LiteralArgumentBuilder.<CommandSourceStack>literal("cda")
                        .executes(c -> forward(dispatcher, c.getSource(), ""));

        LiteralArgumentBuilder<CommandSourceStack> list =
                LiteralArgumentBuilder.<CommandSourceStack>literal("list")
                        .executes(c -> forward(dispatcher, c.getSource(), "list"));
        list.then(LiteralArgumentBuilder.<CommandSourceStack>literal("defaults")
                .executes(c -> forward(dispatcher, c.getSource(), "list defaults")));
        list.then(RequiredArgumentBuilder
                .<CommandSourceStack, String>argument("tag", StringArgumentType.word())
                .suggests((c, b) -> suggestCategories(b))
                .executes(c -> forward(dispatcher, c.getSource(),
                        "list " + StringArgumentType.getString(c, "tag"))));
        cda.then(list);

        LiteralArgumentBuilder<CommandSourceStack> setDefault =
                LiteralArgumentBuilder.literal("setDefault");
        LiteralArgumentBuilder<CommandSourceStack> removeDefault =
                LiteralArgumentBuilder.literal("removeDefault");

        for (DdsRule<?> rule : DDSRules.registry().available()) {
            cda.then(ruleCommand(dispatcher, rule));
            setDefault.then(defaultCommand(dispatcher, rule));
            removeDefault.then(removeDefaultCommand(dispatcher, rule));
        }

        cda.then(setDefault);
        cda.then(removeDefault);

        // Preserve forward compatibility with manager commands that are not mirrored above.
        cda.then(RequiredArgumentBuilder
                .<CommandSourceStack, String>argument("arguments", StringArgumentType.greedyString())
                .executes(c -> forward(dispatcher, c.getSource(),
                        StringArgumentType.getString(c, "arguments"))));
        dispatcher.register(cda);
    }

    private static void registerCarpetCompatibility(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> carpet =
                LiteralArgumentBuilder.literal("carpet");

        LiteralArgumentBuilder<CommandSourceStack> list =
                LiteralArgumentBuilder.literal("list");
        list.then(LiteralArgumentBuilder.<CommandSourceStack>literal("DDS")
                .executes(c -> forward(dispatcher, c.getSource(), "list")));
        list.then(LiteralArgumentBuilder.<CommandSourceStack>literal("dds")
                .executes(c -> forward(dispatcher, c.getSource(), "list")));

        LiteralArgumentBuilder<CommandSourceStack> setDefault =
                LiteralArgumentBuilder.literal("setDefault");
        LiteralArgumentBuilder<CommandSourceStack> removeDefault =
                LiteralArgumentBuilder.literal("removeDefault");

        for (DdsRule<?> rule : DDSRules.registry().available()) {
            carpet.then(ruleCommand(dispatcher, rule));
            setDefault.then(defaultCommand(dispatcher, rule));
            removeDefault.then(removeDefaultCommand(dispatcher, rule));
        }

        carpet.then(list);
        carpet.then(setDefault);
        carpet.then(removeDefault);
        dispatcher.register(carpet);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> ruleCommand(
            CommandDispatcher<CommandSourceStack> dispatcher, DdsRule<?> rule) {
        String name = rule.name();
        return LiteralArgumentBuilder.<CommandSourceStack>literal(name)
                .executes(c -> forward(dispatcher, c.getSource(), name))
                .then(RequiredArgumentBuilder
                        .<CommandSourceStack, String>argument("value_" + name, StringArgumentType.greedyString())
                        .suggests((c, b) -> suggest(rule, b))
                        .executes(c -> forward(dispatcher, c.getSource(),
                                name + " " + StringArgumentType.getString(c, "value_" + name))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> defaultCommand(
            CommandDispatcher<CommandSourceStack> dispatcher, DdsRule<?> rule) {
        String name = rule.name();
        return LiteralArgumentBuilder.<CommandSourceStack>literal(name)
                .then(RequiredArgumentBuilder
                        .<CommandSourceStack, String>argument("default_" + name, StringArgumentType.greedyString())
                        .suggests((c, b) -> suggest(rule, b))
                        .executes(c -> forward(dispatcher, c.getSource(),
                                "setDefault " + name + " "
                                        + StringArgumentType.getString(c, "default_" + name))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> removeDefaultCommand(
            CommandDispatcher<CommandSourceStack> dispatcher, DdsRule<?> rule) {
        String name = rule.name();
        return LiteralArgumentBuilder.<CommandSourceStack>literal(name)
                .executes(c -> forward(dispatcher, c.getSource(), "removeDefault " + name));
    }

    private static CompletableFuture<Suggestions> suggest(DdsRule<?> rule, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        for (String value : rule.suggestions())
            if (remaining.isEmpty() || value.toLowerCase().startsWith(remaining))
                builder.suggest(value);
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestCategories(SuggestionsBuilder builder) {
        Set<String> categories = new LinkedHashSet<String>();
        for (DdsRule<?> rule : DDSRules.registry().available())
            categories.addAll(rule.categories());

        String remaining = builder.getRemaining().toLowerCase();
        for (String category : categories)
            if (remaining.isEmpty() || category.toLowerCase().startsWith(remaining))
                builder.suggest(category);
        return builder.buildFuture();
    }

    private static int forward(CommandDispatcher<CommandSourceStack> dispatcher,
                               CommandSourceStack source, String arguments) throws CommandSyntaxException {
        String command = DdsSettingsManager.IDENTIFIER + (arguments.isEmpty() ? "" : " " + arguments);
        return dispatcher.execute(command, source);
    }
}
