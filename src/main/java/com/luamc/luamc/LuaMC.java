package com.luamc.luamc;

import java.io.File;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
// import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
// import org.apache.logging.log4j.Logger;


@Mod(modid = LuaMC.MODID, name = LuaMC.NAME, version = LuaMC.VERSION)
public class LuaMC
{
    public static final String MODID = "mclua";
    public static final String NAME = "Minecraft Lua Mod";
    public static final String VERSION = "0.078";

    LuaThread thread;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Start up the Lua Thread.
        thread = new LuaThread();
        thread.start();
        // Create the lua folder if it doesn't already exist
        try {
            new File("lua").mkdir();
        }

        catch(NullPointerException e) {
            // do nothing cause we don't care, screw you
        }
        
    }
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new LuaCommand(thread));
        event.registerServerCommand(new AbortCommand(thread));
    }
}
