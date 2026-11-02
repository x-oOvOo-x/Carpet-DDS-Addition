/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.mixin.integration.playerinventory;

//#if MC == 12111 || MC >= 260102 && MC <= 260300
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessions;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.MenuProvider;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$ import java.util.OptionalInt;
//$$
//$$ /** Captures the explicit viewer while a Provider creates a menu. */
//$$ @Mixin(ServerPlayer.class)
//$$ public abstract class ServerPlayerMenuOpenContextMixin {
//$$     @Inject(method = "openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;", at = @At("HEAD"))
//$$     private void dds$beginPlayerInventoryMenuOpen(MenuProvider provider, CallbackInfoReturnable<OptionalInt> cir) {
//$$         DdsPlayerInventorySessions.beginMenuOpen((ServerPlayer) (Object) this);
//$$     }
//$$
//$$     @Inject(method = "openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;", at = @At("RETURN"))
//$$     private void dds$endPlayerInventoryMenuOpen(MenuProvider provider, CallbackInfoReturnable<OptionalInt> cir) {
//$$         DdsPlayerInventorySessions.endMenuOpen((ServerPlayer) (Object) this);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.ServerPlayerMenuOpenContextTarget")
public abstract class ServerPlayerMenuOpenContextMixin {}
//#endif
