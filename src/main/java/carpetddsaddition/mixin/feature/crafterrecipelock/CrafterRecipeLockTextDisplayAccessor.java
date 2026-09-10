package carpetddsaddition.mixin.feature.crafterrecipelock;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12004
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.world.entity.Display;
//$$ import org.spongepowered.asm.mixin.gen.Invoker;
//#else
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12004
//$$ @Mixin(Display.TextDisplay.class)
//$$ public interface CrafterRecipeLockTextDisplayAccessor {
//$$     @Invoker("setText") void dds$setText(Component text);
//$$     @Invoker("setBackgroundColor") void dds$setBackgroundColor(int color);
//$$     @Invoker("setFlags") void dds$setFlags(byte flags);
//$$     @Invoker("setTextOpacity") void dds$setTextOpacity(byte opacity);
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CrafterRecipeLockTextDisplayTarget")
public interface CrafterRecipeLockTextDisplayAccessor {}
//#endif