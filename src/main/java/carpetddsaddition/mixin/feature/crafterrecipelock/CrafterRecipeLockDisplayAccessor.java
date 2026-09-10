package carpetddsaddition.mixin.feature.crafterrecipelock;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12004
//$$ import com.mojang.math.Transformation;
//$$ import net.minecraft.world.entity.Display;
//$$ import org.spongepowered.asm.mixin.gen.Invoker;
//#else
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12004
//$$ @Mixin(Display.class)
//$$ public interface CrafterRecipeLockDisplayAccessor {
//$$     @Invoker("setBillboardConstraints")
//$$     void dds$setBillboard(Display.BillboardConstraints billboard);
//$$
//$$     @Invoker("setTransformation")
//$$     void dds$setTransformation(Transformation transformation);
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CrafterRecipeLockDisplayTarget")
public interface CrafterRecipeLockDisplayAccessor {}
//#endif