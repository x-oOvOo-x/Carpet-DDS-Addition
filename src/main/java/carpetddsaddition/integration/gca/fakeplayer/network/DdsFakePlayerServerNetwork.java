/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.integration.gca.fakeplayer.network;

//#if MC >= 11904 && MC <= 260300
//$$ import carpet.helpers.EntityPlayerActionPack.ActionType;
//$$ import carpet.patches.EntityPlayerMPFake;
//$$ import carpetddsaddition.feature.playerinventoryextension.PlayerInventoryExtension;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerAction;
//$$ import carpetddsaddition.integration.gca.fakeplayer.DdsFakePlayerActions;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySession;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventorySessions;
//$$ import carpetddsaddition.integration.playerinventory.DdsPlayerInventoryEnderNetwork;
//$$ import carpetddsaddition.network.DdsPacketRegistry;
//$$ import carpetddsaddition.network.DdsServerNetwork;
//$$ import net.minecraft.nbt.CompoundTag;
//$$ import net.minecraft.server.MinecraftServer;
//$$ import net.minecraft.server.level.ServerPlayer;
//$$ import net.minecraft.world.inventory.AbstractContainerMenu;
//$$ import java.util.ArrayList;
//$$ import java.util.Map;
//$$ import java.util.UUID;
//$$ import java.util.WeakHashMap;
//$$
//$$ public final class DdsFakePlayerServerNetwork {
//$$     private static final boolean TRACE = Boolean.getBoolean("dds.playerinventory.trace");
//$$     private static final org.apache.logging.log4j.Logger TRACE_LOG =
//$$             org.apache.logging.log4j.LogManager.getLogger("dds.playerinventory.cda");
//$$     private static final int FULL_STATE_CORRECTION_TICKS = 20;
//$$     private static final String INVENTORY_ACTIONS=actions(DdsFakePlayerAction.ATTACK_INTERVAL,DdsFakePlayerAction.ATTACK_CONTINUOUS,DdsFakePlayerAction.STOP_ATTACK,DdsFakePlayerAction.USE_INTERVAL,DdsFakePlayerAction.USE_CONTINUOUS,DdsFakePlayerAction.STOP_USE,DdsFakePlayerAction.STOP_ALL,DdsFakePlayerAction.DROP_ALL,DdsFakePlayerAction.DISCONNECT,DdsFakePlayerAction.SELECT_HOTBAR);
//$$     private static final String STRICT_ENDER_ACTIONS=actions(DdsFakePlayerAction.TOGGLE_SNEAK,DdsFakePlayerAction.DISCONNECT);
//$$     private static final Map<ServerPlayer,Tracker> TRACKED=new WeakHashMap<>();
//$$     private DdsFakePlayerServerNetwork() {}
//$$
//$$     public static void registerPackets() {
//$$         DdsPacketRegistry.registerC2S(DdsFakePlayerPackets.ACTION_C2S, DdsFakePlayerActions::handleRequest, true, false, false);
//$$         DdsPacketRegistry.registerC2S(DdsFakePlayerPackets.PROVIDER_ACTION_C2S, DdsFakePlayerActions::handleProviderRequest, true, false, false);
//$$         DdsPacketRegistry.registerS2C(DdsFakePlayerPackets.CONTEXT_S2C, false);
//$$         DdsPacketRegistry.registerS2C(DdsFakePlayerPackets.STATE_S2C, false);
//$$         DdsPacketRegistry.registerS2C(DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C, false);
//$$     }
//$$
//$$     /** Called from Provider menu constructors while ServerPlayer.openMenu has an explicit viewer scope. */
//$$     public static void sendContextFromMenu(ServerPlayer viewer, AbstractContainerMenu menu) {
//$$         boolean gcaEnder = TRACE && menu != null && menu.slots.stream()
//$$                 .anyMatch(slot -> slot.container != null
//$$                         && slot.container.getClass().getName().endsWith(".PlayerEnderChestContainer"));
//$$         if (gcaEnder) TRACE_LOG.info("provider_ender_open id=" + menu.containerId + " menu=" + menu.getClass().getSimpleName()
//$$                 + " scoped_viewer=" + (viewer != null) + " extension=" + PlayerInventoryExtension.enabled());
//$$         if(!PlayerInventoryExtension.enabled()||viewer==null||menu==null)return;
//$$         DdsPlayerInventorySession session=DdsPlayerInventorySessions.prepare(viewer,menu);
//$$         if (gcaEnder) TRACE_LOG.info("gca_context_prepare id=" + menu.containerId
//$$                 + " session=" + (session == null ? "missing" : session.inventoryKind().wireName())
//$$                 + " canonical_size=" + (session == null ? -1 : session.canonicalSize())
//$$                 + " generation=" + (session == null ? -1L : session.generation())
//$$                 + " advertised=" + DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.CONTEXT_S2C));
//$$         if (TRACE && session != null
//$$                 && !DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.CONTEXT_S2C))
//$$             TRACE_LOG.info("provider_context_unadvertised id=" + menu.containerId
//$$                     + " menu=" + menu.getClass().getSimpleName()
//$$                     + " provider=" + session.providerId()
//$$                     + " canonical_size=" + session.canonicalSize()
//$$                     + " context=false session_v3="
//$$                     + DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C)
//$$                     + " structure_result="
//$$                     + DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.STRUCTURE_RESULT_S2C));
//$$         if(session==null||!DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.CONTEXT_S2C))return;
//$$         sendContext(viewer,session);
//$$     }
//$$
//$$     private static boolean sendContext(ServerPlayer viewer,DdsPlayerInventorySession session) {
//$$         if(viewer==null||session==null)return false;
//$$         boolean fake=session.targetType()==DdsPlayerInventorySession.TargetType.LIVE_FAKE&&session.liveTarget() instanceof EntityPlayerMPFake;
//$$         boolean strict=DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C);
//$$         CompoundTag data=new CompoundTag();
//$$         data.putInt("context_schema",strict?3:(fake?1:2));
//$$         if(strict){data.putString("event","open");data.putLong("session_generation",session.generation());}
//$$         data.putInt("container_id",session.containerId());
//$$         data.putString("source",session.providerId());
//$$         data.putString("provider",session.providerId());
//$$         data.putString("target_uuid",session.targetUuid().toString());
//$$         data.putString("target_name",session.targetName());
//$$         data.putString("target_type",session.targetType().wireName());
//$$         data.putString("inventory_kind",session.inventoryKind().wireName());
//$$         data.putString("layout_id",session.layoutId());
//$$         data.putInt("layout_schema",session.layoutSchema());
//$$         data.putString("behavior_id",session.behaviorId());
//$$         data.putString("interaction_capabilities",interactionCapabilities(viewer,session));
//$$         data.putString("supported_actions",strictSupportedActions(viewer,session));
//$$         data.putInt("canonical_size",session.canonicalSize());
//$$         State state=null;
//$$         long revision=0L;
//$$         if(fake){
//$$             state=State.capture(session.containerId(),(EntityPlayerMPFake)session.liveTarget());
//$$             revision=strict?1L:0L;
//$$             if(strict)data.putLong("state_revision",revision);
//$$             state.write(data);
//$$         }
//$$         if(!DdsServerNetwork.send(viewer,DdsFakePlayerPackets.CONTEXT_S2C,data))return false;
//$$         if (TRACE && "gca".equals(session.providerId())
//$$                 && session.inventoryKind() == DdsPlayerInventorySession.InventoryKind.ENDER_CHEST)
//$$             TRACE_LOG.info("gca_context_sent id=" + session.containerId() + " size=" + session.canonicalSize()
//$$                     + " generation=" + session.generation() + " strict=" + strict);
//$$         TRACKED.put(viewer,new Tracker(ContextKey.of(session),session.generation(),strict,revision,state));
//$$         return true;
//$$     }
//$$
//$$     public static void tick(MinecraftServer server) {
//$$         if(server==null)return;
//$$         if(!PlayerInventoryExtension.enabled()){clearTracked(true);return;}
//$$         for(ServerPlayer stale:new ArrayList<>(TRACKED.keySet()))
//$$             if(stale==null||server.getPlayerList().getPlayer(stale.getUUID())!=stale)TRACKED.remove(stale);
//$$         for(ServerPlayer viewer:server.getPlayerList().getPlayers()){
//$$             if(viewer==null||!DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.CONTEXT_S2C)){TRACKED.remove(viewer);continue;}
//$$             AbstractContainerMenu menu=viewer.containerMenu;
//$$             DdsPlayerInventorySession session=menu==null?null:DdsPlayerInventorySessions.current(viewer,menu.containerId,null);
//$$             Tracker tracker=TRACKED.get(viewer);
//$$             if(session==null){
//$$                 if(tracker!=null)sendClear(viewer,tracker);
//$$                 TRACKED.remove(viewer);
//$$                 continue;
//$$             }
//$$             ContextKey key=ContextKey.of(session);
//$$             boolean strict=DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.SESSION_V3_CAPABILITY_S2C);
//$$             if(tracker==null||tracker.generation!=session.generation()||tracker.strict!=strict||!key.equals(tracker.key)){
//$$                 sendContext(viewer,session);
//$$                 continue;
//$$             }
//$$             if(session.targetType()!=DdsPlayerInventorySession.TargetType.LIVE_FAKE||!(session.liveTarget() instanceof EntityPlayerMPFake)){
//$$                 tracker.lastState=null;tracker.stateRevision=0L;tracker.ticksSinceState=0;continue;
//$$             }
//$$             sendState(viewer,session,(EntityPlayerMPFake)session.liveTarget(),tracker);
//$$         }
//$$     }
//$$
//$$     private static void sendState(ServerPlayer viewer,DdsPlayerInventorySession session,EntityPlayerMPFake target,Tracker tracker) {
//$$         if(!DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.STATE_S2C))return;
//$$         State state=State.capture(session.containerId(),target);
//$$         boolean changed=!state.equals(tracker.lastState);
//$$         if(!changed&&(!tracker.strict||++tracker.ticksSinceState<FULL_STATE_CORRECTION_TICKS))return;
//$$         CompoundTag data=new CompoundTag();
//$$         data.putInt("state_schema",tracker.strict?2:1);
//$$         data.putString("target_uuid",target.getUUID().toString());
//$$         long nextRevision=tracker.strict?tracker.stateRevision+1L:0L;
//$$         if(tracker.strict){
//$$             data.putLong("session_generation",tracker.generation);
//$$             data.putLong("state_revision",nextRevision);
//$$         }
//$$         state.write(data);
//$$         if(DdsServerNetwork.send(viewer,DdsFakePlayerPackets.STATE_S2C,data)){
//$$             tracker.lastState=state;tracker.ticksSinceState=0;if(tracker.strict)tracker.stateRevision=nextRevision;
//$$         }
//$$     }
//$$
//$$     private static void sendClear(ServerPlayer viewer,Tracker tracker){
//$$         if(viewer==null||tracker==null||!tracker.strict||!DdsServerNetwork.doesClientSupportS2C(viewer,DdsFakePlayerPackets.CONTEXT_S2C))return;
//$$         CompoundTag data=new CompoundTag();
//$$         data.putInt("context_schema",3);data.putString("event","clear");data.putInt("container_id",tracker.key.containerId);
//$$         data.putLong("session_generation",tracker.generation);
//$$         DdsServerNetwork.send(viewer,DdsFakePlayerPackets.CONTEXT_S2C,data);
//$$     }
//$$
//$$     private static void clearTracked(boolean sendClear){
//$$         if(sendClear)for(Map.Entry<ServerPlayer,Tracker> entry:new ArrayList<>(TRACKED.entrySet()))sendClear(entry.getKey(),entry.getValue());
//$$         TRACKED.clear();
//$$         DdsPlayerInventorySessions.clearActiveSessions();
//$$     }
//$$
//$$     public static void onExtensionRuleChanged(MinecraftServer server){
//$$         if(!PlayerInventoryExtension.enabled()){clearTracked(true);DdsPlayerInventoryEnderNetwork.clearRuntimeState();}
//$$     }
//$$     public static void clearRuntimeState(){TRACKED.clear();DdsPlayerInventorySessions.clearRuntimeState();DdsPlayerInventoryEnderNetwork.clearRuntimeState();}
//$$
//$$     private static String interactionCapabilities(ServerPlayer viewer,DdsPlayerInventorySession session){
//$$         StringBuilder b=new StringBuilder();
//$$         if(session.carriedScratch())b.append("carried_scratch_swap");
//$$         if(session.rootReturn()!=null&&session.rootReturn().ddsCanReopen(viewer)){
//$$             if(b.length()>0)b.append(',');
//$$             b.append("return_parent");
//$$         }
//$$         return b.toString();
//$$     }
//$$
//$$     private static String strictSupportedActions(ServerPlayer viewer,DdsPlayerInventorySession session){
//$$         if(viewer==null||session==null
//$$                 ||!DdsServerNetwork.doesClientSupport(viewer,DdsFakePlayerPackets.ACTION_C2S)
//$$                 ||session.targetType()!=DdsPlayerInventorySession.TargetType.LIVE_FAKE)return "";
//$$         return session.inventoryKind()==DdsPlayerInventorySession.InventoryKind.ENDER_CHEST
//$$                 ?STRICT_ENDER_ACTIONS:INVENTORY_ACTIONS;
//$$     }
//$$     private static String actions(DdsFakePlayerAction... values){StringBuilder b=new StringBuilder();for(DdsFakePlayerAction action:values){if(b.length()>0)b.append(',');b.append(action.wireName());}return b.toString();}
//$$
//$$     private static final class ContextKey{
//$$         private final int containerId,layoutSchema,canonicalSize;
//$$         private final UUID targetUuid;
//$$         private final DdsPlayerInventorySession.InventoryKind kind;
//$$         private final DdsPlayerInventorySession.TargetType targetType;
//$$         private final String provider,layout,behavior;
//$$         private ContextKey(int containerId,UUID targetUuid,DdsPlayerInventorySession.InventoryKind kind,DdsPlayerInventorySession.TargetType targetType,String provider,String layout,int layoutSchema,String behavior,int canonicalSize){this.containerId=containerId;this.targetUuid=targetUuid;this.kind=kind;this.targetType=targetType;this.provider=provider;this.layout=layout;this.layoutSchema=layoutSchema;this.behavior=behavior;this.canonicalSize=canonicalSize;}
//$$         static ContextKey of(DdsPlayerInventorySession s){return new ContextKey(s.containerId(),s.targetUuid(),s.inventoryKind(),s.targetType(),s.providerId(),s.layoutId(),s.layoutSchema(),s.behaviorId(),s.canonicalSize());}
//$$         @Override public boolean equals(Object value){if(this==value)return true;if(!(value instanceof ContextKey))return false;ContextKey other=(ContextKey)value;return containerId==other.containerId&&layoutSchema==other.layoutSchema&&canonicalSize==other.canonicalSize&&targetUuid.equals(other.targetUuid)&&kind==other.kind&&targetType==other.targetType&&provider.equals(other.provider)&&layout.equals(other.layout)&&behavior.equals(other.behavior);}
//$$         @Override public int hashCode(){int r=containerId;r=31*r+targetUuid.hashCode();r=31*r+kind.hashCode();r=31*r+targetType.hashCode();r=31*r+provider.hashCode();r=31*r+layout.hashCode();r=31*r+layoutSchema;r=31*r+behavior.hashCode();return 31*r+canonicalSize;}
//$$     }
//$$
//$$     private static final class Tracker{
//$$         private final ContextKey key;private final long generation;private final boolean strict;
//$$         private long stateRevision;private State lastState;private int ticksSinceState;
//$$         private Tracker(ContextKey key,long generation,boolean strict,long stateRevision,State lastState){this.key=key;this.generation=generation;this.strict=strict;this.stateRevision=stateRevision;this.lastState=lastState;}
//$$     }
//$$
//$$     private static final class State{
//$$         private final int containerId,selectedHotbar,attackMode,attackInterval,useMode,useInterval;private final boolean sneaking;
//$$         private State(int containerId,int selectedHotbar,int attackMode,int attackInterval,int useMode,int useInterval,boolean sneaking){this.containerId=containerId;this.selectedHotbar=selectedHotbar;this.attackMode=attackMode;this.attackInterval=attackInterval;this.useMode=useMode;this.useInterval=useInterval;this.sneaking=sneaking;}
//$$         static State capture(int containerId,EntityPlayerMPFake target){return new State(containerId,DdsFakePlayerActions.selectedHotbarSlot(target),DdsFakePlayerActions.mode(target,ActionType.ATTACK),DdsFakePlayerActions.interval(target,ActionType.ATTACK),DdsFakePlayerActions.mode(target,ActionType.USE),DdsFakePlayerActions.interval(target,ActionType.USE),target.isShiftKeyDown());}
//$$         void write(CompoundTag data){data.putInt("container_id",containerId);data.putInt("selected_hotbar",selectedHotbar);data.putInt("attack_mode",attackMode);data.putInt("attack_interval",attackInterval);data.putInt("use_mode",useMode);data.putInt("use_interval",useInterval);data.putInt("sneaking",sneaking?1:0);}
//$$         @Override public boolean equals(Object value){if(this==value)return true;if(!(value instanceof State))return false;State other=(State)value;return containerId==other.containerId&&selectedHotbar==other.selectedHotbar&&attackMode==other.attackMode&&attackInterval==other.attackInterval&&useMode==other.useMode&&useInterval==other.useInterval&&sneaking==other.sneaking;}
//$$         @Override public int hashCode(){int r=containerId;r=31*r+selectedHotbar;r=31*r+attackMode;r=31*r+attackInterval;r=31*r+useMode;r=31*r+useInterval;return 31*r+(sneaking?1:0);}
//$$     }
//$$ }
//#else
public final class DdsFakePlayerServerNetwork {
    private DdsFakePlayerServerNetwork() {}
    public static void registerPackets() {}
    public static void sendContextFromMenu(net.minecraft.server.level.ServerPlayer viewer,net.minecraft.world.inventory.AbstractContainerMenu menu) {}
    public static void tick(net.minecraft.server.MinecraftServer server) {}
    public static void onExtensionRuleChanged(net.minecraft.server.MinecraftServer server) {}
    public static void clearRuntimeState() {}
}
//#endif
