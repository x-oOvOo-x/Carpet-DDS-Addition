package carpetddsaddition.mixin.feature.crafterrecipelock;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12004
//$$ import carpetddsaddition.feature.crafterrecipelock.compat.CrafterRecipeLockCompat;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.CrafterBlock;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
import org.spongepowered.asm.mixin.Pseudo;
//#endif

//#if MC >= 12004
//$$ @Mixin(CrafterBlock.class)
//$$ public abstract class CrafterBlockMixin {
//$$     @Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
//$$     private void dds$preserveRecipe(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
//$$         if (CrafterRecipeLockCompat.shouldBlockCraft(level, pos)) ci.cancel();
//$$     }
//$$
//#if MC >= 12105
//$$ @Inject(method = "affectNeighborsAfterRemoval", at = @At("HEAD"))
//$$ private void dds$removeLockDisplay(BlockState state, ServerLevel level, BlockPos pos, boolean moved, CallbackInfo ci) {
//$$     CrafterRecipeLockCompat.hideLock(level, pos);
//$$ }
//#else
//$$ @Inject(method = "onRemove", at = @At("HEAD"))
//$$ private void dds$removeLockDisplay(BlockState state, Level level, BlockPos pos, BlockState next, boolean moved, CallbackInfo ci) {
//$$     if (state.getBlock() != next.getBlock() && level instanceof ServerLevel server) CrafterRecipeLockCompat.hideLock(server, pos);
//$$ }
//#endif
//$$ }
//#else
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.CrafterRecipeLockBlockTarget")
public abstract class CrafterBlockMixin {}
//#endif