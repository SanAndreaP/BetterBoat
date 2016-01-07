/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.betterboat.network;

import net.darkhax.bookshelf.common.network.AbstractMessage;
import de.sanandrew.mods.betterboat.BetterBoat;
import de.sanandrew.mods.betterboat.entity.EntityBetterBoat;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSendBoatPos
        extends AbstractMessage<PacketSendBoatPos>
{
    private int boatId;
    private double posX;
    private double posY;
    private double posZ;
    private float rotationYaw;
    private float rotationPitch;

    public PacketSendBoatPos() {}

    public PacketSendBoatPos(EntityBetterBoat boat) {
        this.boatId = boat.getEntityId();
        this.posX = boat.posX;
        this.posY = boat.posY;
        this.posZ = boat.posZ;
        this.rotationYaw = boat.rotationYaw;
        this.rotationPitch = boat.rotationPitch;
    }

    @Override
    public void handleClientMessage(PacketSendBoatPos pkt, EntityPlayer player) {
        BetterBoat.proxy.setBoatPosAndRot(pkt.boatId, pkt.posX, pkt.posY, pkt.posZ, pkt.rotationYaw, pkt.rotationPitch);
    }

    @Override
    public void handleServerMessage(PacketSendBoatPos packetSendBoatPos, EntityPlayer entityPlayer) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.boatId = buf.readInt();
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
        this.rotationYaw = buf.readFloat();
        this.rotationPitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.boatId);
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
        buf.writeFloat(this.rotationYaw);
        buf.writeFloat(this.rotationPitch);
    }
}
