/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.betterboat;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import de.sanandrew.mods.betterboat.entity.EntityBetterBoat;
import de.sanandrew.mods.betterboat.entity.EntitySpawnHandler;
import de.sanandrew.mods.betterboat.network.PacketManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = BetterBoat.MOD_ID, version = BetterBoat.VERSION, name = "Better Boat", dependencies = "required-after:sapmanpack@[2.4.1,)")
public class BetterBoat
{
    public static final String MOD_ID = "betterboat";
    public static final String VERSION = "1.0.0";
    public static final String MOD_CHANNEL = "BetterBoatNWCH";

    private static final String MOD_PROXY_CLIENT = "de.sanandrew.mods.betterboat.client.ClientProxy";
    private static final String MOD_PROXY_COMMON = "de.sanandrew.mods.betterboat.CommonProxy";

    @Mod.Instance(BetterBoat.MOD_ID)
    public static BetterBoat instance;

    @SidedProxy(modId = BetterBoat.MOD_ID, clientSide = BetterBoat.MOD_PROXY_CLIENT, serverSide = BetterBoat.MOD_PROXY_COMMON)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PacketManager.initialize();

        MinecraftForge.EVENT_BUS.register(new EntitySpawnHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(EntityBetterBoat.class, "betterBoat", 0, BetterBoat.instance, 80, 3, true);

        proxy.registerRenderers();
    }
}
