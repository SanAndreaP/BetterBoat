/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.betterboat.network;

import de.sanandrew.core.manpack.network.NetworkManager;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.betterboat.BetterBoat;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketManager
{
    public static final short BOAT_SYNC = 0;

    public static void initialize() {
        NetworkManager.registerModHandler(BetterBoat.MOD_ID, BetterBoat.MOD_CHANNEL);

        NetworkManager.registerModPacketCls(BetterBoat.MOD_ID, BOAT_SYNC, PacketSendBoatPos.class);
    }

    public static void sendToServer(short packet, Tuple data) {
        NetworkManager.sendToServer(BetterBoat.MOD_ID, packet, data);
    }

    public static void sendToAll(short packed, Tuple data) {
        NetworkManager.sendToAll(BetterBoat.MOD_ID, packed, data);
    }

    public static void sendToPlayer(short packed, EntityPlayerMP player, Tuple data) {
        NetworkManager.sendToPlayer(BetterBoat.MOD_ID, packed, player, data);
    }

    public static void sendToAllInDimension(short packed, int dimensionId, Tuple data) {
        NetworkManager.sendToAllInDimension(BetterBoat.MOD_ID, packed, dimensionId, data);
    }

    public static void sendToAllAround(short packed, int dimensionId, double x, double y, double z, double range, Tuple data) {
        NetworkManager.sendToAllAround(BetterBoat.MOD_ID, packed, dimensionId, x, y, z, range, data);
    }
}
