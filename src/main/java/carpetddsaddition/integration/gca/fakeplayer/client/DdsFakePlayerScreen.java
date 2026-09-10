/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.client;

//#if MC >= 11904 && MC <= 260200
//$$ import carpetddsaddition.CarpetDDSAddition;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerAction;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerActions;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerMenu;
//$$ import carpetddsaddition.integration.gca.fakeplayer.network.DdsFakePlayerClientNetwork;
//$$ import net.minecraft.client.gui.Font;
//$$ import net.minecraft.client.gui.components.EditBox;
//$$ import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//$$ import net.minecraft.client.gui.screens.inventory.InventoryScreen;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.entity.player.Inventory;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.inventory.Slot;
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
//$$ import net.minecraft.client.input.KeyEvent;
//$$ import net.minecraft.client.input.MouseButtonEvent;
//#endif
//$$ import java.util.ArrayList;
//$$ import java.util.List;
//$$ import java.util.function.BooleanSupplier;
//$$
//$$ @org.anti_ad.mc.ipn.api.IPNIgnore
//$$ public final class DdsFakePlayerScreen extends AbstractContainerScreen<DdsFakePlayerMenu> {
//$$     private static final int IMAGE_WIDTH = 176, IMAGE_HEIGHT = 267;
//$$     private static final int FIELD_X = 77, FIELD_WIDTH = 34, FIELD_HEIGHT = 18, FIELD_EDIT_HEIGHT = 14, FIELD_EDIT_Y = 5;
//$$     private static final int ARROW_X = 111, ARROW_WIDTH = 9, ARROW_HEIGHT = 9;
//$$     private static final int ACTION_RIGHT_X = 151, ACTION_STEP = 22, ATTACK_Y = 7, USE_Y = 29, INTERVAL_BUTTON_X = ACTION_RIGHT_X - ACTION_STEP, CONTINUOUS_BUTTON_X = ACTION_RIGHT_X;
//$$     private static final int PERSISTENT_Y = 61, STOP_X = ACTION_RIGHT_X - ACTION_STEP * 2, DROP_ALL_X = ACTION_RIGHT_X - ACTION_STEP, DISCONNECT_X = ACTION_RIGHT_X;
//$$     private static final int HOTBAR_NUMBER_X = 7, HOTBAR_NUMBER_Y = 161, HOTBAR_NUMBER_WIDTH = 18, HOTBAR_NUMBER_HEIGHT = 6;
//$$     private static final Component INTERVAL_FIELD_TOOLTIP = Component.literal("修改数值后自动切换为周期模式；单位：tick；范围 " + DdsFakePlayerActions.MIN_INTERVAL + "-" + DdsFakePlayerActions.MAX_INTERVAL);
//#if MC >= 12111
//$$     private static final Identifier BACKGROUND = id("textures/gui/container/gca_fake_player_inventory.png");
//$$     private static final Identifier BUTTON_NORMAL = id("textures/gui/gca/action_button_normal.png"), BUTTON_HOVER = id("textures/gui/gca/action_button_hover.png"), BUTTON_ACTIVE = id("textures/gui/gca/action_button_active.png"), ACTION_ICONS = id("textures/gui/gca/action_icons.png");
//$$     private static final Identifier INTERVAL_INPUT = id("textures/gui/gca/interval_input.png"), INTERVAL_INPUT_FOCUSED = id("textures/gui/gca/interval_input_focused.png"), INTERVAL_ARROWS = id("textures/gui/gca/interval_arrows.png");
//$$     private static final Identifier HOTBAR_NUMBERS = id("textures/gui/gca/hotbar_number_buttons.png"), HOTBAR_SELECTED = id("textures/gui/gca/hotbar_selected.png");
//#else
//$$     private static final ResourceLocation BACKGROUND = id("textures/gui/container/gca_fake_player_inventory.png");
//$$     private static final ResourceLocation BUTTON_NORMAL = id("textures/gui/gca/action_button_normal.png"), BUTTON_HOVER = id("textures/gui/gca/action_button_hover.png"), BUTTON_ACTIVE = id("textures/gui/gca/action_button_active.png"), ACTION_ICONS = id("textures/gui/gca/action_icons.png");
//$$     private static final ResourceLocation INTERVAL_INPUT = id("textures/gui/gca/interval_input.png"), INTERVAL_INPUT_FOCUSED = id("textures/gui/gca/interval_input_focused.png"), INTERVAL_ARROWS = id("textures/gui/gca/interval_arrows.png");
//$$     private static final ResourceLocation HOTBAR_NUMBERS = id("textures/gui/gca/hotbar_number_buttons.png"), HOTBAR_SELECTED = id("textures/gui/gca/hotbar_selected.png");
//#endif
//$$     private final List<DdsFakePlayerActionButton> actionButtons = new ArrayList<>();
//$$     private final String targetName;
//$$     private LivingEntity targetEntity;
//$$     private EditBox attackInterval, useInterval;
//$$     private int lastAttackInterval = -1, lastUseInterval = -1, pressedStepper = -1;
//$$     private boolean syncingIntervalFields;
//$$
//$$     public DdsFakePlayerScreen(DdsFakePlayerMenu menu, Inventory inventory, Component title) {
//#if MC >= 260000
//$$         super(menu, inventory, title, IMAGE_WIDTH, IMAGE_HEIGHT);
//#else
//$$         super(menu, inventory, title); imageWidth = IMAGE_WIDTH; imageHeight = IMAGE_HEIGHT;
//$$         titleLabelX = inventoryLabelX = -10000; titleLabelY = inventoryLabelY = -10000;
//#endif
//$$         targetName = title.getString();
//$$     }
//$$
//$$     @Override protected void init() {
//$$         super.init(); actionButtons.clear();
//$$         attackInterval = createIntervalBox(leftPos + FIELD_X, topPos + ATTACK_Y + FIELD_EDIT_Y, Component.literal("攻击周期"));
//$$         useInterval = createIntervalBox(leftPos + FIELD_X, topPos + USE_Y + FIELD_EDIT_Y, Component.literal("使用周期"));
//$$         attackInterval.setResponder(this::onAttackIntervalChanged); useInterval.setResponder(this::onUseIntervalChanged);
//$$         addRenderableWidget(attackInterval); addRenderableWidget(useInterval);
//$$         addActionButton(INTERVAL_BUTTON_X, ATTACK_Y, DdsFakePlayerActionButton.ATTACK_INTERVAL, menu::isAttackIntervalActive, this::toggleAttackInterval, "按输入的 tick 周期攻击", "再次点击关闭");
//$$         addActionButton(CONTINUOUS_BUTTON_X, ATTACK_Y, DdsFakePlayerActionButton.ATTACK_CONTINUOUS, menu::isAttackContinuousActive, this::toggleAttackContinuous, "持续攻击", "再次点击关闭");
//$$         addActionButton(INTERVAL_BUTTON_X, USE_Y, DdsFakePlayerActionButton.USE_INTERVAL, menu::isUseIntervalActive, this::toggleUseInterval, "按输入的 tick 周期使用", "再次点击关闭");
//$$         addActionButton(CONTINUOUS_BUTTON_X, USE_Y, DdsFakePlayerActionButton.USE_CONTINUOUS, menu::isUseContinuousActive, this::toggleUseContinuous, "持续使用", "再次点击关闭");
//$$         addActionButton(STOP_X, PERSISTENT_Y, DdsFakePlayerActionButton.STOP, () -> false, this::stopAll, "等同于 /player <name> stop");
//$$         addActionButton(DROP_ALL_X, PERSISTENT_Y, DdsFakePlayerActionButton.DROP, () -> false, () -> send(DdsFakePlayerAction.DROP_ALL, 0), "等同于 /player <name> dropStack all");
//$$         addActionButton(DISCONNECT_X, PERSISTENT_Y, DdsFakePlayerActionButton.DISCONNECT, () -> false, () -> send(DdsFakePlayerAction.DISCONNECT, 0), "等同于 /player <name> kill");
//$$         initializeIntervalFields();
//$$     }
//$$     @Override protected void containerTick() { super.containerTick(); syncIntervalFields(); }
//$$
//$$     private EditBox createIntervalBox(int x, int y, Component narration) {
//#if MC >= 260000
//$$         EditBox box = new IntervalEditBox(font, x, y, FIELD_WIDTH, FIELD_EDIT_HEIGHT, narration);
//#else
//$$         EditBox box = new EditBox(font, x, y, FIELD_WIDTH, FIELD_EDIT_HEIGHT, narration); box.setFilter(DdsFakePlayerScreen::isValidIntervalText);
//#endif
//$$         box.setMaxLength(5); box.setBordered(false);
//#if MC >= 12108
//$$         box.setCentered(true);
//#endif
//$$         return box;
//$$     }
//$$
//$$     private void addActionButton(int x, int y, int icon, BooleanSupplier selected, Runnable action, String tooltip) { addActionButton(x, y, icon, selected, action, tooltip, tooltip); }
//$$     private void addActionButton(int x, int y, int icon, BooleanSupplier selected, Runnable action, String inactiveTooltip, String activeTooltip) { actionButtons.add(new DdsFakePlayerActionButton(leftPos + x, topPos + y, icon, selected, action, Component.literal(inactiveTooltip), Component.literal(activeTooltip))); }
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
//$$
//$$     private void renderContent(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int mouseX, int mouseY) { blitTexture(graphics, BACKGROUND, leftPos, topPos, imageWidth, imageHeight); renderResourceControls(graphics, mouseX, mouseY); renderTargetPlayer(graphics, mouseX, mouseY); drawSelectedFakeHotbar(graphics); renderActionButtons(graphics, mouseX, mouseY); }
//$$
//$$     private void renderResourceControls(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int mouseX, int mouseY) {
//$$         blitTexture(graphics, attackInterval != null && attackInterval.isFocused() ? INTERVAL_INPUT_FOCUSED : INTERVAL_INPUT, leftPos + FIELD_X, topPos + ATTACK_Y, FIELD_WIDTH, FIELD_HEIGHT);
//$$         blitTexture(graphics, useInterval != null && useInterval.isFocused() ? INTERVAL_INPUT_FOCUSED : INTERVAL_INPUT, leftPos + FIELD_X, topPos + USE_Y, FIELD_WIDTH, FIELD_HEIGHT);
//$$         renderIntervalArrows(graphics, ATTACK_Y, 0); renderIntervalArrows(graphics, USE_Y, 2);
//$$         for (int i = 0; i < 9; i++) { int x = leftPos + HOTBAR_NUMBER_X + i * 18, y = topPos + HOTBAR_NUMBER_Y; int state = menu.selectedFakeHotbarSlot() == i ? 2 : inside(mouseX, mouseY, x, y, HOTBAR_NUMBER_WIDTH, HOTBAR_NUMBER_HEIGHT) ? 1 : 0; blitTextureRegion(graphics, HOTBAR_NUMBERS, x, y, i * HOTBAR_NUMBER_WIDTH, state * HOTBAR_NUMBER_HEIGHT, HOTBAR_NUMBER_WIDTH, HOTBAR_NUMBER_HEIGHT, 162, 18); }
//$$     }
//$$
//$$     private void renderIntervalArrows(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int rowY, int base) {
//$$         int x = leftPos + ARROW_X, y = topPos + rowY;
//$$         blitTextureRegion(graphics, INTERVAL_ARROWS, x, y, pressedStepper == base ? ARROW_WIDTH : 0, 0, ARROW_WIDTH, ARROW_HEIGHT, 18, 18);
//$$         blitTextureRegion(graphics, INTERVAL_ARROWS, x, y + ARROW_HEIGHT, pressedStepper == base + 1 ? ARROW_WIDTH : 0, ARROW_HEIGHT, ARROW_WIDTH, ARROW_HEIGHT, 18, 18);
//$$     }
//$$
//$$     private void renderTargetPlayer(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int mouseX, int mouseY) {
//$$         LivingEntity target = findTargetPlayer(); if (target == null) return;
//#if MC >= 260000
//$$         InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, leftPos + 26, topPos + 8, leftPos + 75, topPos + 78, 30, 0.0625F, mouseX, mouseY, target);
//#elseif MC >= 12002
//$$         InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, leftPos + 26, topPos + 8, leftPos + 75, topPos + 78, 30, 0.0625F, mouseX, mouseY, target);
//#else
//$$         InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, leftPos + 51, topPos + 75, 30, (float) (leftPos + 51 - mouseX), (float) (topPos + 30 - mouseY), target);
//#endif
//$$     }
//$$
//$$     private void renderActionButtons(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int mouseX, int mouseY) {
//$$         for (DdsFakePlayerActionButton button : actionButtons) {
//$$             boolean active = button.isSelected(), hovered = !active && button.contains(mouseX, mouseY); int o = DdsFakePlayerActionButton.RENDER_OFFSET;
//$$             blitTexture(graphics, active ? BUTTON_ACTIVE : hovered ? BUTTON_HOVER : BUTTON_NORMAL, button.x - o, button.y - o, DdsFakePlayerActionButton.RENDER_SIZE, DdsFakePlayerActionButton.RENDER_SIZE);
//$$             renderActionIcon(graphics, button.icon, button.x + 1, button.y + 1);
//$$         }
//$$     }
//$$
//$$     private void drawSelectedFakeHotbar(
//#if MC < 12000
//$$             PoseStack graphics
//#elseif MC < 260000
//$$             GuiGraphics graphics
//#else
//$$             GuiGraphicsExtractor graphics
//#endif
//$$     ) { Slot slot = selectedFakeHotbarSlot(); if (slot != null) blitTexture(graphics, HOTBAR_SELECTED, leftPos + slot.x - 2, topPos + slot.y - 2, 20, 20); }
//$$
//$$     private static void blitTexture(
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
//$$             int x, int y, int width, int height) { blitTextureRegion(graphics, texture, x, y, 0, 0, width, height, width, height); }
//$$     private static void blitTextureRegion(
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
//$$         blitTextureRegion(graphics, ACTION_ICONS, x, y, icon * DdsFakePlayerActionButton.ICON_SIZE, 0, DdsFakePlayerActionButton.ICON_SIZE, DdsFakePlayerActionButton.ICON_SIZE, DdsFakePlayerActionButton.ICON_ATLAS_WIDTH, DdsFakePlayerActionButton.ICON_SIZE);
//$$     }
//$$
//$$     private void renderControlTooltip(
//#if MC < 12000
//$$             PoseStack graphics,
//#elseif MC < 260000
//$$             GuiGraphics graphics,
//#else
//$$             GuiGraphicsExtractor graphics,
//#endif
//$$             int mouseX, int mouseY) {
//$$         Component tooltip = controlTooltip(mouseX, mouseY); if (tooltip == null) return;
//#if MC < 12000
//$$         renderTooltip(graphics, tooltip, mouseX, mouseY);
//#elseif MC < 12108
//$$         graphics.renderTooltip(font, tooltip, mouseX, mouseY);
//#else
//$$         graphics.setTooltipForNextFrame(font, tooltip, mouseX, mouseY);
//#endif
//$$     }
//$$
//$$     @Override public boolean mouseClicked(
//#if MC >= 12109
//$$             MouseButtonEvent event, boolean doubleClick
//#else
//$$             double mouseX, double mouseY, int button
//#endif
//$$     ) {
//#if MC >= 12109
//$$         if (event.button() == 0 && (pressIntervalStepper(event.x(), event.y()) || pressHotbarNumber(event.x(), event.y()) || pressActionButton(event.x(), event.y()))) return true;
//$$         return super.mouseClicked(event, doubleClick);
//#else
//$$         if (button == 0 && (pressIntervalStepper(mouseX, mouseY) || pressHotbarNumber(mouseX, mouseY) || pressActionButton(mouseX, mouseY))) return true;
//$$         return super.mouseClicked(mouseX, mouseY, button);
//#endif
//$$     }
//$$
//$$     @Override public boolean mouseReleased(
//#if MC >= 12109
//$$             MouseButtonEvent event
//#else
//$$             double mouseX, double mouseY, int button
//#endif
//$$     ) {
//#if MC >= 12109
//$$         if (event.button() == 0) pressedStepper = -1; return super.mouseReleased(event);
//#else
//$$         if (button == 0) pressedStepper = -1; return super.mouseReleased(mouseX, mouseY, button);
//#endif
//$$     }
//$$
//$$     @Override public boolean keyPressed(
//#if MC >= 12109
//$$             KeyEvent event
//#else
//$$             int keyCode, int scanCode, int modifiers
//#endif
//$$     ) {
//$$         if (isIntervalFieldEditing()) {
//#if MC >= 12109
//$$             return super.keyPressed(event);
//#else
//$$             return super.keyPressed(keyCode, scanCode, modifiers);
//#endif
//$$         }
//#if MC >= 12109
//$$         if (trySelectHotbar(event)) return true; return super.keyPressed(event);
//#else
//$$         if (trySelectHotbar(keyCode, scanCode)) return true; return super.keyPressed(keyCode, scanCode, modifiers);
//#endif
//$$     }
//$$     private boolean trySelectHotbar(
//#if MC >= 12109
//$$             KeyEvent event
//#else
//$$             int keyCode, int scanCode
//#endif
//$$     ) {
//$$         if (hoveredSlot != null) return false;
//$$         for (int i = 0; i < minecraft.options.keyHotbarSlots.length; i++) {
//#if MC >= 12109
//$$             boolean matches = minecraft.options.keyHotbarSlots[i].matches(event);
//#else
//$$             boolean matches = minecraft.options.keyHotbarSlots[i].matches(keyCode, scanCode);
//#endif
//$$             if (matches) { selectFakeHotbar(i); return true; }
//$$         }
//$$         return false;
//$$     }
//$$
//$$     private boolean pressIntervalStepper(double mouseX, double mouseY) { return pressStepper(mouseX, mouseY, ATTACK_Y, true, 0) || pressStepper(mouseX, mouseY, USE_Y, false, 2); }
//$$     private boolean pressStepper(double mouseX, double mouseY, int rowY, boolean attack, int base) { int x = leftPos + ARROW_X, y = topPos + rowY; if (inside(mouseX, mouseY, x, y, ARROW_WIDTH, ARROW_HEIGHT)) { pressedStepper = base; stepInterval(attack, 1); return true; } if (inside(mouseX, mouseY, x, y + ARROW_HEIGHT, ARROW_WIDTH, ARROW_HEIGHT)) { pressedStepper = base + 1; stepInterval(attack, -1); return true; } return false; }
//$$     private void stepInterval(boolean attack, int delta) { EditBox box = attack ? attackInterval : useInterval; int value = DdsFakePlayerActions.clampInterval(readInterval(box) + delta); setFieldValue(box, value); if (attack) lastAttackInterval = value; else lastUseInterval = value; }
//$$     private boolean pressHotbarNumber(double mouseX, double mouseY) { for (int i = 0; i < 9; i++) if (inside(mouseX, mouseY, leftPos + HOTBAR_NUMBER_X + i * 18, topPos + HOTBAR_NUMBER_Y, HOTBAR_NUMBER_WIDTH, HOTBAR_NUMBER_HEIGHT)) { selectFakeHotbar(i); return true; } return false; }
//$$     private boolean pressActionButton(double mouseX, double mouseY) { for (DdsFakePlayerActionButton button : actionButtons) if (button.contains(mouseX, mouseY)) { button.press(); return true; } return false; }
//$$     private boolean isIntervalFieldEditing() { return attackInterval != null && attackInterval.canConsumeInput() || useInterval != null && useInterval.canConsumeInput(); }
//$$     private void selectFakeHotbar(int slot) { menu.predictSelectedHotbar(slot); send(DdsFakePlayerAction.SELECT_HOTBAR, slot + 1); }
//$$
//$$     private Component controlTooltip(int mouseX, int mouseY) {
//$$         if (inside(mouseX, mouseY, leftPos + FIELD_X, topPos + ATTACK_Y, FIELD_WIDTH, FIELD_HEIGHT) || inside(mouseX, mouseY, leftPos + FIELD_X, topPos + USE_Y, FIELD_WIDTH, FIELD_HEIGHT)) return INTERVAL_FIELD_TOOLTIP;
//$$         for (int rowY : new int[]{ATTACK_Y, USE_Y}) { int x = leftPos + ARROW_X, y = topPos + rowY; if (inside(mouseX, mouseY, x, y, ARROW_WIDTH, ARROW_HEIGHT)) return Component.literal("增加 1 tick"); if (inside(mouseX, mouseY, x, y + ARROW_HEIGHT, ARROW_WIDTH, ARROW_HEIGHT)) return Component.literal("减少 1 tick"); }
//$$         for (int i = 0; i < 9; i++) if (inside(mouseX, mouseY, leftPos + HOTBAR_NUMBER_X + i * 18, topPos + HOTBAR_NUMBER_Y, HOTBAR_NUMBER_WIDTH, HOTBAR_NUMBER_HEIGHT)) return Component.literal("选择快捷栏 " + (i + 1));
//$$         for (DdsFakePlayerActionButton button : actionButtons) if (button.contains(mouseX, mouseY)) return button.tooltip(); return null;
//$$     }
//$$     private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) { return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height; }
//$$     private Slot selectedFakeHotbarSlot() { int selected = menu.selectedFakeHotbarSlot(); return selected >= 0 && selected <= 8 ? menu.getSlot(DdsFakePlayerMenu.FAKE_HOTBAR_START + selected) : null; }
//$$
//$$     private void onAttackIntervalChanged(String text) { onIntervalChanged(text, true); }
//$$     private void onUseIntervalChanged(String text) { onIntervalChanged(text, false); }
//$$     private void onIntervalChanged(String text, boolean attack) { if (syncingIntervalFields) return; Integer value = parseInterval(text); int mode = attack ? menu.attackMode() : menu.useMode(); if (value != null && mode != DdsFakePlayerActions.MODE_NONE) selectInterval(attack, value.intValue()); }
//$$     private void toggleAttackInterval() { toggleInterval(true); }
//$$     private void toggleUseInterval() { toggleInterval(false); }
//$$     private void toggleInterval(boolean attack) { boolean active = attack ? menu.isAttackIntervalActive() : menu.isUseIntervalActive(); if (active) { if (attack) menu.predictStopAttack(); else menu.predictStopUse(); send(attack ? DdsFakePlayerAction.STOP_ATTACK : DdsFakePlayerAction.STOP_USE, 0); return; } selectInterval(attack, readInterval(attack ? attackInterval : useInterval)); }
//$$     private void toggleAttackContinuous() { toggleContinuous(true); }
//$$     private void toggleUseContinuous() { toggleContinuous(false); }
//$$     private void toggleContinuous(boolean attack) { boolean active = attack ? menu.isAttackContinuousActive() : menu.isUseContinuousActive(); if (active) { if (attack) menu.predictStopAttack(); else menu.predictStopUse(); send(attack ? DdsFakePlayerAction.STOP_ATTACK : DdsFakePlayerAction.STOP_USE, 0); return; } if (attack) menu.predictAttackContinuous(); else menu.predictUseContinuous(); send(attack ? DdsFakePlayerAction.ATTACK_CONTINUOUS : DdsFakePlayerAction.USE_CONTINUOUS, 0); }
//$$     private void selectInterval(boolean attack, int interval) { int value = DdsFakePlayerActions.clampInterval(interval); if (attack) menu.predictAttackInterval(value); else menu.predictUseInterval(value); send(attack ? DdsFakePlayerAction.ATTACK_INTERVAL : DdsFakePlayerAction.USE_INTERVAL, value); }
//$$     private void stopAll() { menu.predictStopAll(); send(DdsFakePlayerAction.STOP_ALL, 0); }
//$$     private void initializeIntervalFields() { syncingIntervalFields = true; try { lastAttackInterval = initialInterval(true); lastUseInterval = initialInterval(false); setFieldValue(attackInterval, lastAttackInterval); setFieldValue(useInterval, lastUseInterval); } finally { syncingIntervalFields = false; } }
//$$     private int initialInterval(boolean attack) { boolean active = attack ? menu.isAttackIntervalActive() : menu.isUseIntervalActive(); int value = attack ? menu.attackInterval() : menu.useInterval(); return active ? DdsFakePlayerActions.clampInterval(value) : DdsFakePlayerActions.DEFAULT_INTERVAL; }
//$$     private void syncIntervalFields() { syncingIntervalFields = true; try { lastAttackInterval = syncIntervalField(attackInterval, true, lastAttackInterval); lastUseInterval = syncIntervalField(useInterval, false, lastUseInterval); } finally { syncingIntervalFields = false; } }
//$$     private int syncIntervalField(EditBox box, boolean attack, int previous) { boolean active = attack ? menu.isAttackIntervalActive() : menu.isUseIntervalActive(); if (box == null || box.isFocused() || !active) return previous; int value = DdsFakePlayerActions.clampInterval(attack ? menu.attackInterval() : menu.useInterval()); if (value != previous) setFieldValue(box, value); return value; }
//$$     private static void setFieldValue(EditBox box, int value) { if (box != null) box.setValue(Integer.toString(value)); }
//$$     private static int readInterval(EditBox box) { if (box == null) return DdsFakePlayerActions.DEFAULT_INTERVAL; Integer value = parseInterval(box.getValue()); return value == null ? DdsFakePlayerActions.DEFAULT_INTERVAL : value.intValue(); }
//$$     private static Integer parseInterval(String text) { if (!isValidIntervalText(text) || text.isEmpty()) return null; try { return Integer.valueOf(DdsFakePlayerActions.clampInterval(Integer.parseInt(text))); } catch (NumberFormatException ignored) { return null; } }
//$$     private static boolean isValidIntervalText(String text) { if (text == null || text.length() > 5) return false; for (int i = 0; i < text.length(); i++) if (!Character.isDigit(text.charAt(i))) return false; return true; }
//$$     private LivingEntity findTargetPlayer() { if (targetEntity != null) return targetEntity; if (minecraft == null || minecraft.level == null) return null; for (Player player : minecraft.level.players()) if (player.getName().getString().equals(targetName)) { targetEntity = player; return player; } return null; }
//$$     private void send(DdsFakePlayerAction action, int value) { DdsFakePlayerClientNetwork.sendAction(action, value); }
//$$
//#if MC >= 260000
//$$     private static final class IntervalEditBox extends EditBox { private IntervalEditBox(Font font, int x, int y, int width, int height, Component narration) { super(font, x, y, width, height, narration); } @Override public void insertText(String input) { if (isValidIntervalText(input)) super.insertText(input); } }
//#endif
//#if MC >= 12111
//$$     private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path); }
//#elseif MC >= 12101
//$$     private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(CarpetDDSAddition.MOD_ID, path); }
//#else
//$$     private static ResourceLocation id(String path) { return new ResourceLocation(CarpetDDSAddition.MOD_ID, path); }
//#endif
//$$ }
//#else
public final class DdsFakePlayerScreen { private DdsFakePlayerScreen() {} }
//#endif
