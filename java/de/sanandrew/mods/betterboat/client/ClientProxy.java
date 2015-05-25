/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.betterboat.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import de.sanandrew.mods.betterboat.CommonProxy;
import de.sanandrew.mods.betterboat.entity.EntityBetterBoat;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class ClientProxy
        extends CommonProxy
{
    @Override
    public void setBoatPosAndRot(int entityId, double posX, double posY, double posZ, float yaw, float pitch) {
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);

        if( entity instanceof EntityBetterBoat ) {
            entity.setPositionAndRotation2(posX, posY, posZ, yaw, pitch, 10);
        }
    }

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityBetterBoat.class, new RenderBetterBoat());
    }
}
