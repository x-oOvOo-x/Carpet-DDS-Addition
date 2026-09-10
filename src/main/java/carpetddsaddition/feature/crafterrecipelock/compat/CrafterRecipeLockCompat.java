/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.crafterrecipelock.compat;

//#if MC >= 12004
//$$ import carpetddsaddition.feature.crafterrecipelock.CrafterRecipeLock;
//$$ import carpetddsaddition.feature.crafterrecipelock.CrafterRecipeLockAccess;
//$$ import carpetddsaddition.mixin.feature.crafterrecipelock.CrafterBlockOrientationAccessor;
//$$ import carpetddsaddition.mixin.feature.crafterrecipelock.CrafterRecipeLockDisplayAccessor;
//$$ import carpetddsaddition.mixin.feature.crafterrecipelock.CrafterRecipeLockTextDisplayAccessor;
//$$ import com.mojang.math.Transformation;
//$$ import net.minecraft.core.BlockPos;
//$$ import net.minecraft.core.Direction;
//$$ import net.minecraft.core.FrontAndTop;
//$$ import net.minecraft.core.particles.ParticleTypes;
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.sounds.SoundEvents;
//$$ import net.minecraft.sounds.SoundSource;
//$$ import net.minecraft.world.InteractionResult;
//$$ import net.minecraft.world.entity.Display;
//#if MC >= 260200
//$$ import net.minecraft.world.entity.EntityTypes;
//#else
//$$ import net.minecraft.world.entity.EntityType;
//#endif
//$$ import net.minecraft.world.entity.LivingEntity;
//$$ import net.minecraft.world.entity.player.Player;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import net.minecraft.world.item.context.UseOnContext;
//$$ import net.minecraft.world.item.crafting.RecipeType;
//$$ import net.minecraft.world.level.Level;
//$$ import net.minecraft.world.level.block.Blocks;
//$$ import net.minecraft.world.level.block.CrafterBlock;
//$$ import net.minecraft.world.level.block.entity.BlockEntity;
//$$ import net.minecraft.world.level.block.entity.CrafterBlockEntity;
//$$ import net.minecraft.world.level.block.state.BlockState;
//$$ import net.minecraft.world.phys.AABB;
//$$ import org.joml.Quaternionf;
//$$ import org.joml.Vector3f;
//#endif

public final class CrafterRecipeLockCompat {
    private CrafterRecipeLockCompat() {}

    //#if MC >= 12004
    //$$ private static final String VISUAL_TAG = "dds_crafter_recipe_lock";
    //$$
    //$$ public static InteractionResult tryLock(UseOnContext c) {
    //$$     Level l = c.getLevel(); BlockPos p = c.getClickedPos(); BlockEntity be = l.getBlockEntity(p);
    //$$     if (!CrafterRecipeLock.enabled() || !(be instanceof CrafterBlockEntity crafter)) return null;
    //$$
    //#if MC >= 12109
    //$$     if (l.isClientSide()) return InteractionResult.SUCCESS;
    //#else
    //$$     if (l.isClientSide) return InteractionResult.SUCCESS;
    //#endif
    //$$
    //$$     Player player = c.getPlayer(); CrafterRecipeLockAccess a = (CrafterRecipeLockAccess)crafter;
    //$$     String recipeId = recipeId((ServerLevel)l, crafter);
    //$$     if (player == null || a.dds$isRecipeLocked() || recipeId == null) return null;
    //$$     int mask = templateMask(crafter); if (mask == 0) return null;
    //$$     a.dds$setRecipeLock(true, mask, recipeId);
    //$$     if (!player.getAbilities().instabuild) c.getItemInHand().shrink(1);
    //$$     showLock((ServerLevel)l, p); lockFeedback((ServerLevel)l, p); refreshComparator(l, p);
    //$$     return InteractionResult.SUCCESS;
    //$$ }
    //$$
    //$$ public static InteractionResult tryUnlock(UseOnContext c) {
    //$$     Level l = c.getLevel(); BlockPos p = c.getClickedPos(); BlockEntity be = l.getBlockEntity(p);
    //$$     if (!(be instanceof CrafterBlockEntity crafter)) return null;
    //$$     CrafterRecipeLockAccess a = (CrafterRecipeLockAccess)crafter;
    //$$
    //#if MC >= 12109
    //$$     if (l.isClientSide()) return InteractionResult.SUCCESS;
    //#else
    //$$     if (l.isClientSide) return InteractionResult.SUCCESS;
    //#endif
    //$$
    //$$     Player player = c.getPlayer(); if (player == null || !a.dds$isRecipeLocked()) return null;
    //$$     a.dds$setRecipeLock(false, 0, ""); ItemStack s = c.getItemInHand();
    //$$     if (!player.getAbilities().instabuild) {
    //#if MC >= 12109
    //$$         s.hurtAndBreak(1, player, c.getHand());
    //#elseif MC >= 12005
    //$$         s.hurtAndBreak(1, player, LivingEntity.getSlotForHand(c.getHand()));
    //#else
    //$$         s.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(c.getHand()));
    //#endif
    //$$     }
    //$$     hideLock((ServerLevel)l, p); unlockFeedback((ServerLevel)l, p); refreshComparator(l, p);
    //$$     return InteractionResult.SUCCESS;
    //$$ }
    //$$
    //$$ public static boolean shouldBlockCraft(ServerLevel l, BlockPos p) {
    //$$     BlockEntity be = l.getBlockEntity(p);
    //$$     if (!(be instanceof CrafterBlockEntity c)) return false;
    //$$     CrafterRecipeLockAccess a = (CrafterRecipeLockAccess)c; if (!a.dds$isRecipeLocked()) return false;
    //$$     int m = a.dds$getRecipeTemplateMask();
    //$$     for (int i = 0; i < 9; i++) if ((m & 1 << i) != 0 && c.getItem(i).getCount() <= 1) return true;
    //$$     String locked = a.dds$getRecipeId();
    //$$     return !locked.isEmpty() && !locked.equals(recipeId(l, c));
    //$$ }
    //$$
    //$$ public static int comparatorSignal(CrafterBlockEntity c, CrafterRecipeLockAccess a) {
    //$$     int r = 0, m = a.dds$getRecipeTemplateMask();
    //$$     for (int i = 0; i < 9; i++) {
    //$$         if (c.isSlotDisabled(i)) { r++; continue; }
    //$$         ItemStack s = c.getItem(i);
    //$$         if (!s.isEmpty() && ((m & 1 << i) == 0 || s.getCount() > 1)) r++;
    //$$     }
    //$$     return r;
    //$$ }
    //$$
    //$$ public static void showLock(ServerLevel l, BlockPos p) {
    //$$     hideLock(l, p);
    //$$     String tag = visualTag(p); BlockState s = l.getBlockState(p);
    //$$     FrontAndTop o = s.getValue(CrafterBlockOrientationAccessor.dds$getOrientation());
    //$$     Direction f = o.front(), t = o.top();
    //$$     Direction a = side(f, t, false), b = side(f, t, true);
    //$$     spawnLock(l, p, a, tag); spawnLock(l, p, b, tag);
    //$$ }
    //$$
    //$$ public static void hideLock(ServerLevel l, BlockPos p) {
    //$$     String tag = visualTag(p);
    //#if MC >= 260102
    //$$     l.getEntitiesOfClass(Display.TextDisplay.class, visualBox(p), e -> e.entityTags().contains(tag)).forEach(Display.TextDisplay::discard);
    //#else
    //$$     l.getEntitiesOfClass(Display.TextDisplay.class, visualBox(p), e -> e.getTags().contains(tag)).forEach(Display.TextDisplay::discard);
    //#endif
    //$$ }
    //$$
    //$$ private static void spawnLock(ServerLevel l, BlockPos p, Direction face, String tag) {
    //$$     double x = p.getX()+.5+face.getStepX()*.506, y = p.getY()+.5+face.getStepY()*.506, z = p.getZ()+.5+face.getStepZ()*.506;
    //#if MC >= 260200
    //$$     Display.TextDisplay d = new Display.TextDisplay(EntityTypes.TEXT_DISPLAY, l);
    //#else
    //$$     Display.TextDisplay d = new Display.TextDisplay(EntityType.TEXT_DISPLAY, l);
    //#endif
    //$$     CrafterRecipeLockTextDisplayAccessor ta = (CrafterRecipeLockTextDisplayAccessor)d;
    //$$     CrafterRecipeLockDisplayAccessor da = (CrafterRecipeLockDisplayAccessor)d;
    //$$     ta.dds$setText(Component.literal("🔒"));
    //$$     ta.dds$setBackgroundColor(0);
    //$$     ta.dds$setFlags(Display.TextDisplay.FLAG_SHADOW);
    //$$     ta.dds$setTextOpacity((byte)200);
    //$$     da.dds$setBillboard(Display.BillboardConstraints.FIXED);
    //$$     da.dds$setTransformation(new Transformation(new Vector3f(-.02F,-.1875F,0F), new Quaternionf(), new Vector3f(1.5F), new Quaternionf()));
    //$$     d.setPos(x, y, z); d.setYRot(yaw(face)); d.setXRot(pitch(face));
    //$$     d.setNoGravity(true); d.setInvulnerable(true); d.setSilent(true);
    //$$     d.addTag(VISUAL_TAG); d.addTag(tag); l.addFreshEntity(d);
    //$$ }
    //$$
    //$$ private static Direction side(Direction f, Direction t, boolean opposite) {
    //$$     int x = f.getStepY()*t.getStepZ()-f.getStepZ()*t.getStepY();
    //$$     int y = f.getStepZ()*t.getStepX()-f.getStepX()*t.getStepZ();
    //$$     int z = f.getStepX()*t.getStepY()-f.getStepY()*t.getStepX();
    //$$     if (opposite) { x=-x; y=-y; z=-z; }
    //$$     return x>0?Direction.EAST:x<0?Direction.WEST:y>0?Direction.UP:y<0?Direction.DOWN:z>0?Direction.SOUTH:Direction.NORTH;
    //$$ }
    //$$
    //$$ private static float yaw(Direction f) {
    //$$     return f==Direction.NORTH?180F:f==Direction.WEST?90F:f==Direction.EAST?-90F:0F;
    //$$ }
    //$$
    //$$ private static float pitch(Direction f) {
    //$$     return f==Direction.UP?-90F:f==Direction.DOWN?90F:0F;
    //$$ }
    //$$
    //$$ private static int templateMask(CrafterBlockEntity c) {
    //$$     int m = 0; for (int i=0;i<9;i++) if (!c.isSlotDisabled(i) && !c.getItem(i).isEmpty()) m |= 1<<i;
    //$$     return m;
    //$$ }
    //$$
    //$$ private static String recipeId(ServerLevel l, CrafterBlockEntity c) {
    //#if MC >= 12100
    //$$     var r = CrafterBlock.getPotentialResults(l, c.asCraftInput());
    //#else
    //$$     var r = l.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, c, l);
    //#endif
    //#if MC >= 12102
    //$$     return r.map(h -> h.id().location().toString()).orElse(null);
    //#else
    //$$     return r.map(h -> h.id().toString()).orElse(null);
    //#endif
    //$$ }
    //$$
    //$$ private static void lockFeedback(ServerLevel l, BlockPos p) {
    //$$     l.sendParticles(ParticleTypes.WAX_ON, p.getX()+.5, p.getY()+.5, p.getZ()+.5, 10, .3, .3, .3, 0);
    //#if MC >= 12100
    //$$     l.playSound(null, p, SoundEvents.VAULT_REJECT_REWARDED_PLAYER, SoundSource.BLOCKS, 1F, 1F);
    //#else
    //$$     l.playSound(null, p, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1F, 1F);
    //#endif
    //$$ }
    //$$
    //$$ private static void unlockFeedback(ServerLevel l, BlockPos p) {
    //$$     l.playSound(null, p, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1F, 1F);
    //$$ }
    //$$
    //$$ private static String visualTag(BlockPos p) { return VISUAL_TAG+"_"+p.asLong(); }
    //$$ private static AABB visualBox(BlockPos p) {
    //$$     return new AABB(p.getX()-.75,p.getY()-.75,p.getZ()-.75,p.getX()+1.75,p.getY()+1.75,p.getZ()+1.75);
    //$$ }
    //$$ private static void refreshComparator(Level l, BlockPos p) { l.updateNeighbourForOutputSignal(p, Blocks.CRAFTER); }
    //#endif
}