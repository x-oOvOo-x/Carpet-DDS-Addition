/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.mixin.integration.org;

//#if MC >= 11904 && MC <= 260300
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerServerNetwork;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessions;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ @Pseudo
//$$ @Mixin(targets = {
//$$         "org.carpetorgaddition.wheel.screen.PlayerInventoryScreenHandler",
//$$         "org.carpetorgaddition.wheel.screen.WithButtonPlayerInventoryScreenHandler",
//$$         "org.carpetorgaddition.wheel.screen.PlayerEnderChestScreenHandler",
//$$         "boat.carpetorgaddition.wheel.screen.PlayerInventoryScreenHandler",
//$$         "boat.carpetorgaddition.wheel.screen.WithButtonPlayerInventoryScreenHandler",
//$$         "boat.carpetorgaddition.wheel.screen.PlayerEnderChestScreenHandler"
//$$ }, remap = false)
//$$ public abstract class OrgPlayerMenuContextMixin {
//$$     @Inject(method = "<init>", at = @At("RETURN"))
//$$     private void dds$sendPlayerMenuContext(CallbackInfo ci) {
//$$         DdsFakePlayerServerNetwork.sendContextFromMenu(DdsPlayerInventorySessions.openingViewer(), (AbstractContainerMenu) (Object) this);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.OrgPlayerMenuContextTarget")
public abstract class OrgPlayerMenuContextMixin {}
//#endif
