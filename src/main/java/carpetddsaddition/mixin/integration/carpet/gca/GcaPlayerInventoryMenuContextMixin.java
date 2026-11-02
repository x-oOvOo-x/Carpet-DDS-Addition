/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.mixin.integration.carpet.gca;

//#if MC >= 11904 && MC <= 260300
//$$ import carpetddsaddition.core.inventory.DdsCarriedScratchSwap;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessions;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ /**
//$$  * Marks GCA's native player-inventory menu as using the DDS carried-scratch
//$$  * protocol and publishes its Provider Context. The click implementation lives
//$$  * on AbstractContainerMenu because PlayerInventoryMenu inherits clicked(...)
//$$  * rather than declaring an override.
//$$  */
//$$ @Pseudo
//$$ @Mixin(targets = "dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu", remap = false)
//$$ public abstract class GcaPlayerInventoryMenuContextMixin implements DdsCarriedScratchSwap {
//$$     @Inject(method = "<init>", at = @At("RETURN"))
//$$     private void dds$sendPlayerInventoryContext(CallbackInfo ci) {
//$$         DdsFakePlayerServerNetwork.sendContextFromMenu(DdsPlayerInventorySessions.openingViewer(), (AbstractContainerMenu) (Object) this);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.GcaPlayerInventoryMenuContextTarget")
public abstract class GcaPlayerInventoryMenuContextMixin {}
//#endif
