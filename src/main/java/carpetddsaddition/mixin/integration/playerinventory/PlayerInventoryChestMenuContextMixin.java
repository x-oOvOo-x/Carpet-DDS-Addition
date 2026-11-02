/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.mixin.integration.playerinventory;

//#if MC >= 11904 && MC <= 260300
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessions;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.inventory.ChestMenu;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ /**
//$$  * Provider-neutral ChestMenu hook. Provider resolution stays in
//$$  * DdsPlayerInventorySessions; Org offline menus remain under native ownership.
//$$  */
//$$ @Mixin(ChestMenu.class)
//$$ public abstract class PlayerInventoryChestMenuContextMixin {
//$$     @Inject(method = "<init>", at = @At("RETURN"))
//$$     private void dds$sendPlayerInventoryContext(CallbackInfo ci) {
//$$         DdsFakePlayerServerNetwork.sendContextFromMenu(
//$$                 DdsPlayerInventorySessions.openingViewer(),
//$$                 (AbstractContainerMenu) (Object) this);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.PlayerInventoryChestMenuContextTarget")
public abstract class PlayerInventoryChestMenuContextMixin {}
//#endif
