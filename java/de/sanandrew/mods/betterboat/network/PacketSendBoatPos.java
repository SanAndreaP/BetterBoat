/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.betterboat.network;

import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.betterboat.BetterBoat;
import de.sanandrew.mods.betterboat.entity.EntityBetterBoat;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;

import java.io.IOException;

public class PacketSendBoatPos
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream byteBufInputStream, ByteBuf byteBuf, INetHandler iNetHandler) throws IOException {
        BetterBoat.proxy.setBoatPosAndRot(byteBufInputStream.readInt(), byteBufInputStream.readDouble(), byteBufInputStream.readDouble(), byteBufInputStream.readDouble(),
                                          byteBufInputStream.readFloat(), byteBufInputStream.readFloat());
    }

    @Override
    public void writeData(ByteBufOutputStream byteBufOutputStream, Tuple tuple) throws IOException {
        EntityBetterBoat boat = (EntityBetterBoat)tuple.getValue(0);

        byteBufOutputStream.writeInt(boat.getEntityId());
        byteBufOutputStream.writeDouble(boat.posX);
        byteBufOutputStream.writeDouble(boat.posY);
        byteBufOutputStream.writeDouble(boat.posZ);
        byteBufOutputStream.writeFloat(boat.rotationYaw);
        byteBufOutputStream.writeFloat(boat.rotationPitch);
    }
}
