package com.luamc.luamc;

import net.minecraft.client.Minecraft;
// import net.minecraftforge.common.MinecraftForge;
// import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
// import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import org.apache.logging.log4j.Logger;


@Mod(modid = LuaMC.MODID, name = LuaMC.NAME, version = LuaMC.VERSION)
public class LuaMC
{
    public static final String MODID = "mclua";
    public static final String NAME = "Minecraft Lua Mod";
    public static final String VERSION = "0.01";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

        // some example code
        System.out.println("WE HAVE LIFTOFF");

        // TODO: Player object cannot be obtained, since they are not in a world yet.
        // EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (Minecraft.IS_RUNNING_ON_MAC) {
            for (int i = 0; i < 100; i++) {
                System.out.println("Linux masterrace <o/");
            }
            System.exit(1);
        }
    }
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new LuaCommand());
    }
}
