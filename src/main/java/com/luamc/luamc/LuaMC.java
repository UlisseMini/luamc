package com.luamc.luamc;

import java.io.File;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
// import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
// import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
// import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;


@Mod(modid = LuaMC.MODID, name = LuaMC.NAME, version = LuaMC.VERSION)
public class LuaMC
{
    public static final String MODID = "mclua";
    public static final String NAME = "Minecraft Lua Mod";
    public static final String VERSION = "0.078";

    LuaThread thread;
    KeyBinding cancel;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        cancel = new KeyBinding("Cancel", Keyboard.KEY_X, "LuaMC");
        ClientRegistry.registerKeyBinding(cancel);
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());

        // Start up the Lua Thread.
        thread = new LuaThread();
        thread.start();
        thread.setName("LuaThread");

        // Create the lua folder if it doesn't already exist
        try {
            new File("lua").mkdir();
        }

        catch(NullPointerException e) {
            // do nothing cause we don't care, screw you
        }

    }

    final class KeyInputHandler
    {
        @SubscribeEvent
        public void onKeyInput(KeyInputEvent event)
        {
            if (cancel.isPressed()) {
                System.out.println("OH BOY CANCEL PRESSED!@!!");
                thread.poopsie();
            }
        }
    }
 
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new LuaCommand(thread));
        event.registerServerCommand(new AbortCommand(thread));
    }
}
