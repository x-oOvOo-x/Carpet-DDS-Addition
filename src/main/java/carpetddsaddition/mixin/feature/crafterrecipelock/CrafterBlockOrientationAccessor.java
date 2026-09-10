package carpetddsaddition.mixin.feature.crafterrecipelock;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12004
//$$ import net.minecraft.core.FrontAndTop;
//$$ import net.minecraft.world.level.block.CrafterBlock;
//$$ import net.minecraft.world.level.block.state.properties.EnumProperty;
//$$ import org.spongepowered.asm.mixin.gen.Accessor;
//#else
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12004
//$$ @Mixin(CrafterBlock.class)
//$$ public interface CrafterBlockOrientationAccessor {
//$$     @Accessor("ORIENTATION")
//$$     static EnumProperty<FrontAndTop> dds$getOrientation() { throw new AssertionError(); }
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CrafterBlockOrientationTarget")
public interface CrafterBlockOrientationAccessor {}
//#endif