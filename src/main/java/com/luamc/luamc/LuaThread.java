package com.luamc.luamc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.luamc.luamc.LuaCommand.print;
import com.luamc.luamc.LuaCommand.sleep;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;


// LuaThread represents a thread that can have closures thrown at it and executed.
public class LuaThread extends Thread {
    private AtomicBoolean run = new AtomicBoolean(true);
    private CustomDebugLib debugLib = new CustomDebugLib();

    // Passed A String path to the lua file.
    private ArrayBlockingQueue<String> tasks = new ArrayBlockingQueue<String>(1);

    // Run a lua file on the LuaThread.
    public void spawn_run(String path) throws InterruptedException {
        tasks.put(path);
        
    }


    private LuaValue loadFile(String path) {
        Globals globals = JsePlatform.standardGlobals();
        globals.set("print", new print(path));
        globals.set("sleep", new sleep());
        globals.set("player", player.table());
        globals.load(debugLib);

        // Use the convenience function on Globals to load a chunk.
        LuaValue chunk = globals.loadfile(path);

        // Use any of the "call()" or "invoke()" functions directly on the chunk.
        return chunk.call(LuaValue.valueOf(path));
    }

    
    public void run() {
        System.out.println("thread started");

        String path = null;
        while (run.get()) {
            System.out.println("Waiting for task");


            // Get a task and execute it
            try {
                path = tasks.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            System.out.println("Got a path: " + path);

            if (path == null) {
                System.out.println("path is null");
                continue;
            }

            try {
                loadFile(path);
            } catch (LuaError e) {
                GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
                gui.addChatMessage(ChatType.SYSTEM, new TextComponentString(TextFormatting.RED + e.getMessage()));
            }
            debugLib.interrupted = false;
        }
    }

    // Abort the current task, This sends an interrupt to the lua interpreter.
    void poopsie() {
        debugLib.interrupted = true;
    }
}
