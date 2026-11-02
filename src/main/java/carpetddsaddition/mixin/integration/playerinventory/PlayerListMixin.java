/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.mixin.integration.playerinventory;

//#if MC == 12111 || MC >= 260102 && MC <= 260300
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventoryEnderNetwork;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.server.players.PlayerList;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ @Mixin(PlayerList.class)
//$$ public abstract class PlayerListMixin {
//$$     @Inject(method = "remove", at = @At("HEAD"))
//$$     private void dds$clearPlayerInventoryStructureOnDisconnect(ServerPlayer player, CallbackInfo ci) {
//$$         DdsPlayerInventoryEnderNetwork.onDisconnect(player);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.PlayerInventoryPlayerListTarget")
public abstract class PlayerListMixin {}
//#endif
