/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.betterboat.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityBoat;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class EntitySpawnHandler
{
    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if( event.entity.getClass().equals(EntityBoat.class) ) {
            event.setCanceled(true);

            if( !event.world.isRemote ) {
                EntityBetterBoat betterBoat = new EntityBetterBoat(event.world, event.entity.prevPosX, event.entity.prevPosY, event.entity.prevPosZ);
                betterBoat.rotationYaw = event.entity.rotationYaw;

                event.world.spawnEntityInWorld(betterBoat);
            }
        }
    }
}
