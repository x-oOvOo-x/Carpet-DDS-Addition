/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.mixin.integration.carpet.gca;

//#if MC >= 11904 && MC <= 260300
//$$ import carpetddsaddition.core.inventory.DdsCarriedScratchSwap;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import net.minecraft.world.inventory.Slot;
//$$ import net.minecraft.world.item.ItemStack;
//#if MC >= 260102
//$$ import net.minecraft.world.inventory.ContainerInput;
//#else
//$$ import net.minecraft.world.inventory.ClickType;
//#endif
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ /**
//$$  * Implements DDS reserved SWAP button 9 for GCA's native PlayerInventoryMenu.
//$$  * GCA inherits clicked(...) from AbstractContainerMenu, so injection must target
//$$  * the declaring class. The exact runtime-class guard keeps this GCA-only.
//$$  */
//$$ @Mixin(AbstractContainerMenu.class)
//$$ public abstract class GcaPlayerInventoryScratchMixin {
//$$     private static final String GCA_PLAYER_INVENTORY_MENU =
//$$             "dev.dubhe.gugle.carpet.tools.player.PlayerInventoryMenu";
//$$     private static final int DDS_SORT_SCRATCH_BUTTON = 9;
//$$     private static final int GCA_STORAGE_START = 18;
//$$     private static final int GCA_STORAGE_END = 54;
//$$
//#if MC >= 260102
//$$     @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
//$$     private void dds$carriedScratch(int slotId, int button, ContainerInput type, Player player, CallbackInfo ci) {
//$$         if (type != ContainerInput.SWAP || !dds$handles(slotId, button)) return;
//$$         dds$swapWithCarried(slotId, player);
//$$         ci.cancel();
//$$     }
//#else
//$$     @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
//$$     private void dds$carriedScratch(int slotId, int button, ClickType type, Player player, CallbackInfo ci) {
//$$         if (type != ClickType.SWAP || !dds$handles(slotId, button)) return;
//$$         dds$swapWithCarried(slotId, player);
//$$         ci.cancel();
//$$     }
//#endif
//$$
//$$     private boolean dds$handles(int slotId, int button) {
//$$         Object self = this;
//$$         return button == DDS_SORT_SCRATCH_BUTTON
//$$                 && self instanceof DdsCarriedScratchSwap
//$$                 && GCA_PLAYER_INVENTORY_MENU.equals(self.getClass().getName())
//$$                 && slotId >= GCA_STORAGE_START && slotId < GCA_STORAGE_END;
//$$     }
//$$
//$$     private void dds$swapWithCarried(int slotId, Player player) {
//$$         AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
//$$         if (slotId < 0 || slotId >= menu.slots.size()) return;
//$$         Slot slot = menu.getSlot(slotId);
//$$         ItemStack slotStack = slot.getItem(), carried = menu.getCarried();
//$$         if (!slot.mayPickup(player) || !carried.isEmpty() && !slot.mayPlace(carried)) return;
//$$         slot.set(carried);
//$$         menu.setCarried(slotStack);
//$$     }
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
@Pseudo
@Mixin(targets = "carpetddsaddition.disabled.GcaPlayerInventoryScratchTarget")
public abstract class GcaPlayerInventoryScratchMixin {}
//#endif
