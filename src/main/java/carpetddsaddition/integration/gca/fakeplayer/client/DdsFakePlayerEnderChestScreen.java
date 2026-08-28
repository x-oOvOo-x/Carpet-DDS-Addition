/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.client;

//#if MC >= 11902 && MC <= 260200
//$$ import carpetddsaddition.CarpetDDSAddition;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerAction;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerEnderChestMenu;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerClientNetwork;
//$$ import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.world.entity.player.Inventory;
//#if MC < 12000
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.gui.GuiComponent;
//$$ import net.minecraft.client.renderer.GameRenderer;
//#elseif MC < 260000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import net.minecraft.client.gui.GuiGraphicsExtractor;
//#endif
//#if MC >= 12103 && MC < 12108
//$$ import net.minecraft.client.renderer.RenderType;
//#endif
//#if MC >= 12108
//$$ import net.minecraft.client.renderer.RenderPipelines;
//#endif
//#if MC >= 12111
//$$ import net.minecraft.resources.Identifier;
//#else
//$$ import net.minecraft.resources.ResourceLocation;
//#endif
//#if MC >= 12109
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif
//$$ import java.util.ArrayList;
//$$ import java.util.List;
//$$ import java.util.function.BooleanSupplier;
//$$
//$$ public final class DdsFakePlayerEnderChestScreen extends AbstractContainerScreen<DdsFakePlayerEnderChestMenu> {
//$$     private static final int IMAGE_WIDTH = 176, TOOLBAR_HEIGHT = 8, BUTTON_Y = -3;
//$$     private static final int DROP_X = 7, BUTTON_STEP = 22, STORE_X = DROP_X + BUTTON_STEP, TAKE_X = STORE_X + BUTTON_STEP, SNEAK_X = TAKE_X + BUTTON_STEP, DISCONNECT_X = 151;
//#if MC >= 12111
//$$     private static final Identifier BACKGROUND_3 = ddsId("textures/gui/container/gca_fake_player_ender_chest_3.png"), BACKGROUND_6 = ddsId("textures/gui/container/gca_fake_player_ender_chest_6.png");
//$$     private static final Identifier BUTTON_NORMAL = ddsId("textures/gui/gca/action_button_normal.png"), BUTTON_HOVER = ddsId("textures/gui/gca/action_button_hover.png"), BUTTON_ACTIVE = ddsId("textures/gui/gca/action_button_active.png"), ACTION_ICONS = ddsId("textures/gui/gca/action_icons.png");
//#else
//$$     private static final ResourceLocation BACKGROUND_3 = ddsId("textures/gui/container/gca_fake_player_ender_chest_3.png"), BACKGROUND_6 = ddsId("textures/gui/container/gca_fake_player_ender_chest_6.png");
//$$     private static final ResourceLocation BUTTON_NORMAL = ddsId("textures/gui/gca/action_button_normal.png"), BUTTON_HOVER = ddsId("textures/gui/gca/action_button_hover.png"), BUTTON_ACTIVE = ddsId("textures/gui/gca/action_button_active.png"), ACTION_ICONS = ddsId("textures/gui/gca/action_icons.png");
//#endif
//$$     private final List<DdsFakePlayerActionButton> buttons = new ArrayList<>();
//$$
//$$     public DdsFakePlayerEnderChestScreen(DdsFakePlayerEnderChestMenu menu, Inventory inventory, Component title) {
//#if MC >= 260000
//$$         super(menu, inventory, title, IMAGE_WIDTH, height(menu.rows()));
//#else
//$$         super(menu, inventory, title); imageWidth = IMAGE_WIDTH; imageHeight = height(menu.rows());
//$$         titleLabelX = inventoryLabelX = -10000; titleLabelY = inventoryLabelY = -10000;
//#endif
//$$     }
//$$     @Override protected void init() {
//$$         super.init(); topPos += TOOLBAR_HEIGHT; buttons.clear();
//$$         addButton(DROP_X, DdsFakePlayerActionButton.DROP, () -> false, () -> send(DdsFakePlayerAction.ENDER_DROP_ALL), "全部扔出：按假人当前视线方向抛出末影箱内全部物品");
//$$         addButton(STORE_X, DdsFakePlayerActionButton.STORE, () -> false, () -> send(DdsFakePlayerAction.ENDER_STORE_ALL), "全部存入：玩家背包 → 假人末影箱");
//$$         addButton(TAKE_X, DdsFakePlayerActionButton.TAKE, () -> false, () -> send(DdsFakePlayerAction.ENDER_TAKE_ALL), "全部取出：假人末影箱 → 玩家背包");
//$$         addButton(SNEAK_X, DdsFakePlayerActionButton.SNEAK, menu::isSneakingActive, this::toggleSneak, "潜行：点击切换", "潜行中：点击取消");
//$$         addButton(DISCONNECT_X, DdsFakePlayerActionButton.DISCONNECT, () -> false, () -> send(DdsFakePlayerAction.DISCONNECT), "退出假人");
//$$     }
//$$     private void addButton(int x, int icon, BooleanSupplier selected, Runnable action, String tooltip) { addButton(x, icon, selected, action, tooltip, tooltip); }
//$$     private void addButton(int x, int icon, BooleanSupplier selected, Runnable action, String off, String on) { buttons.add(new DdsFakePlayerActionButton(leftPos + x, topPos + BUTTON_Y, icon, selected, action, Component.literal(off), Component.literal(on))); }
//$$
//#if MC < 260000
//$$     @Override public void render(
//#if MC < 12000
//$$             PoseStack graphics,
//#else
//$$             GuiGraphics graphics,
//#endif
//$$             int mouseX, int mouseY, float partialTick) { super.render(graphics, mouseX, mouseY, partialTick); renderTooltip(graphics, mouseX, mouseY); renderControlTooltip(graphics, mouseX, mouseY); }
//$$     @Override protected void renderBg(
//#if MC < 12000
//$$             PoseStack graphics,
//#else
//$$             GuiGraphics graphics,
//#endif
//$$             float partialTick, int mouseX, int mouseY) { renderContent(graphics, mouseX, mouseY); }
//#else
//$$     @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractRenderState(graphics, mouseX, mouseY, partialTick); renderControlTooltip(graphics, mouseX, mouseY); }
//$$     @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) { super.extractBackground(graphics, mouseX, mouseY, partialTick); renderContent(graphics, mouseX, mouseY); }
//$$     @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}
//#endif
//$$     private void renderContent(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int mouseX, int mouseY) {
//$$         int panelTop = topPos - TOOLBAR_HEIGHT;
//$$         blit(graphics, menu.rows() == 6 ? BACKGROUND_6 : BACKGROUND_3, leftPos, panelTop, 0, 0, IMAGE_WIDTH, imageHeight, IMAGE_WIDTH, imageHeight);
//$$         for (DdsFakePlayerActionButton button : buttons) {
//$$             boolean active = button.isSelected(), hovered = !active && button.contains(mouseX, mouseY); int o = DdsFakePlayerActionButton.RENDER_OFFSET;
//$$             blit(graphics, active ? BUTTON_ACTIVE : hovered ? BUTTON_HOVER : BUTTON_NORMAL, button.x - o, button.y - o, 0, 0, DdsFakePlayerActionButton.RENDER_SIZE, DdsFakePlayerActionButton.RENDER_SIZE, DdsFakePlayerActionButton.RENDER_SIZE, DdsFakePlayerActionButton.RENDER_SIZE);
//$$             renderActionIcon(graphics, button.icon, button.x + 1, button.y + 1);
//$$         }
//$$     }
//$$     private void renderControlTooltip(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int mouseX, int mouseY) {
//$$         Component tooltip = null; for (DdsFakePlayerActionButton button : buttons) if (button.contains(mouseX, mouseY)) { tooltip = button.tooltip(); break; } if (tooltip == null) return;
//#if MC < 12000
//$$         renderTooltip(graphics, tooltip, mouseX, mouseY);
//#elseif MC < 12108
//$$         graphics.renderTooltip(font, tooltip, mouseX, mouseY);
//#else
//$$         graphics.setTooltipForNextFrame(font, tooltip, mouseX, mouseY);
//#endif
//$$     }
//$$     @Override public boolean mouseClicked(
//#if MC >= 12109
//$$             MouseButtonEvent event, boolean doubleClick
//#else
//$$             double mouseX, double mouseY, int button
//#endif
//$$     ) {
//#if MC >= 12109
//$$         if (event.button() == 0 && press(event.x(), event.y())) return true; return super.mouseClicked(event, doubleClick);
//#else
//$$         if (button == 0 && press(mouseX, mouseY)) return true; return super.mouseClicked(mouseX, mouseY, button);
//#endif
//$$     }
//$$     private boolean press(double mouseX, double mouseY) { for (DdsFakePlayerActionButton button : buttons) if (button.contains(mouseX, mouseY)) { button.press(); return true; } return false; }
//$$     private void toggleSneak() { boolean next = !menu.isSneakingActive(); menu.predictSneaking(next); send(DdsFakePlayerAction.TOGGLE_SNEAK); }
//$$     private void send(DdsFakePlayerAction action) { DdsFakePlayerClientNetwork.sendAction(action, 0); }
//$$     private static int height(int rows) { return 114 + rows * 18 + TOOLBAR_HEIGHT; }
//$$     private static void blit(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//#if MC >= 12111
//$$             Identifier texture,
//#else
//$$             ResourceLocation texture,
//#endif
//$$             int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
//#if MC < 12000
//$$         RenderSystem.setShader(GameRenderer::getPositionTexShader); RenderSystem.setShaderTexture(0, texture); GuiComponent.blit(graphics, x, y, (float) u, (float) v, width, height, textureWidth, textureHeight);
//#elseif MC < 12103
//$$         graphics.blit(texture, x, y, (float) u, (float) v, width, height, textureWidth, textureHeight);
//#elseif MC < 12108
//$$         graphics.blit(RenderType::guiTextured, texture, x, y, (float) u, (float) v, width, height, textureWidth, textureHeight);
//#else
//$$         graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, (float) u, (float) v, width, height, textureWidth, textureHeight);
//#endif
//$$     }
//$$     private static void renderActionIcon(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int icon, int x, int y) {
//$$         if (icon < 0 || icon * DdsFakePlayerActionButton.ICON_SIZE >= DdsFakePlayerActionButton.ICON_ATLAS_WIDTH) return;
//$$         blit(graphics, ACTION_ICONS, x, y, icon * DdsFakePlayerActionButton.ICON_SIZE, 0, DdsFakePlayerActionButton.ICON_SIZE, DdsFakePlayerActionButton.ICON_SIZE, DdsFakePlayerActionButton.ICON_ATLAS_WIDTH, DdsFakePlayerActionButton.ICON_SIZE);
//$$     }
//#if MC >= 12111
//$$     private static Identifier ddsId(String path) { return Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path); }
//#elseif MC >= 12101
//$$     private static ResourceLocation ddsId(String path) { return ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path); }
//#else
//$$     private static ResourceLocation ddsId(String path) { return new ResourceLocation(CarpetDDSAddition.MOD_ID, path); }
//#endif
//$$ }
//#else
public final class DdsFakePlayerEnderChestScreen { private DdsFakePlayerEnderChestScreen() {} }
//#endif
