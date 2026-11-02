/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.network;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/** DDS feature packet registry and routing metadata. */
public final class DdsPacketRegistry {
    private static final Map<String,C2SPacket>C2S=new LinkedHashMap<>();
    private static final Set<String>S2C=new LinkedHashSet<>(),CLIENT_C2S=new LinkedHashSet<>(),CLIENT_S2C=new LinkedHashSet<>();
    private static Set<String> localC2S=Collections.emptySet(),localS2C=Collections.emptySet(),clientC2S=Collections.emptySet(),clientS2C=Collections.emptySet();
    private static boolean frozen;
    private DdsPacketRegistry(){}

    public static synchronized void registerC2S(String id,C2SHandler handler,boolean requireClientAdvertisement,boolean allowDisabledGrace){registerC2S(id,handler,requireClientAdvertisement,allowDisabledGrace,true,null);}
    public static synchronized void registerC2S(String id,C2SHandler handler,boolean requireClientAdvertisement,boolean allowDisabledGrace,boolean clientImplemented){registerC2S(id,handler,requireClientAdvertisement,allowDisabledGrace,clientImplemented,null);}
    public static synchronized void registerC2S(String id,C2SHandler handler,boolean requireClientAdvertisement,boolean allowDisabledGrace,boolean clientImplemented,C2SAvailability availability){
        validatePacketId(id);if(handler==null)throw new IllegalArgumentException("Invalid DDS C2S handler for: "+id);ensureMutable();
        C2SPacket previous=C2S.putIfAbsent(id,new C2SPacket(id,handler,requireClientAdvertisement,allowDisabledGrace,availability));if(previous!=null)throw new IllegalStateException("Duplicate DDS C2S packet id: "+id);
        localC2S=immutable(C2S.keySet());if(clientImplemented){CLIENT_C2S.add(id);clientC2S=immutable(CLIENT_C2S);}
    }
    public static synchronized void registerS2C(String id){registerS2C(id,true);}
    public static synchronized void registerS2C(String id,boolean clientImplemented){
        validatePacketId(id);ensureMutable();if(!S2C.add(id))throw new IllegalStateException("Duplicate DDS S2C packet id: "+id);
        localS2C=immutable(S2C);if(clientImplemented){CLIENT_S2C.add(id);clientS2C=immutable(CLIENT_S2C);}
    }
    public static synchronized void freeze(){frozen=true;}
    private static void validatePacketId(String id){if(id==null||id.isEmpty()||!id.equals(id.trim())||id.indexOf(',')>=0)throw new IllegalArgumentException("Invalid DDS packet id: "+id);if(DdsProtocol.HELLO_C2S.equals(id)||DdsProtocol.HELLO_S2C.equals(id))throw new IllegalArgumentException("DDS HELLO ids are transport-reserved: "+id);}
    private static void ensureMutable(){if(frozen)throw new IllegalStateException("DDS packet registry is frozen");}
    private static Set<String> immutable(Set<String> values){return Collections.unmodifiableSet(new LinkedHashSet<>(values));}
    static synchronized C2SPacket getC2S(String id){return C2S.get(id);}static synchronized boolean hasS2C(String id){return S2C.contains(id);}
    public static synchronized Set<String> localC2SPackets(){return localC2S;}public static synchronized Set<String> localS2CPackets(){return localS2C;}
    public static synchronized Set<String> clientC2SPackets(){return clientC2S;}public static synchronized Set<String> clientS2CPackets(){return clientS2C;}
    public static synchronized Set<String> availableC2SPackets(ServerPlayer player){LinkedHashSet<String> out=new LinkedHashSet<>();for(C2SPacket packet:C2S.values())if(packet.available(player))out.add(packet.packetId());return Collections.unmodifiableSet(out);}

    @FunctionalInterface public interface C2SHandler{void handle(ServerPlayer player,CompoundTag data);}
    @FunctionalInterface public interface C2SAvailability{boolean available(ServerPlayer player);}
    static final class C2SPacket{
        private final String packetId;private final C2SHandler handler;private final boolean requireClientAdvertisement,allowDisabledGrace;private final C2SAvailability availability;
        private C2SPacket(String id,C2SHandler handler,boolean requireClientAdvertisement,boolean allowDisabledGrace,C2SAvailability availability){packetId=id;this.handler=handler;this.requireClientAdvertisement=requireClientAdvertisement;this.allowDisabledGrace=allowDisabledGrace;this.availability=availability;}
        String packetId(){return packetId;}boolean requireClientAdvertisement(){return requireClientAdvertisement;}boolean allowDisabledGrace(){return allowDisabledGrace;}boolean hasAvailabilityPredicate(){return availability!=null;}boolean available(ServerPlayer player){return availability==null||availability.available(player);}void handle(ServerPlayer player,CompoundTag data){handler.handle(player,data);}
    }
}
