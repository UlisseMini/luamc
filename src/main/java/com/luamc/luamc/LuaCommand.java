package com.luamc.luamc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;

import java.io.*;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaCommand extends CommandBase {

    @Override
    public String getName() {
        return "lua";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/lua <file.lua>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        GuiIngame gui = Minecraft.getMinecraft().ingameGUI;

        if (args.length < 1) {
            gui.addChatMessage(
                ChatType.SYSTEM,
                new TextComponentString(TextFormatting.RED + getUsage(null))
            );
            return;
        }
        // TODO: Prepend .minecraft dir
        String path = "lua/" + args[0];

        // TODO: Don't recreate enviorment for every script.
        // create an environment to run in
        Globals globals = JsePlatform.standardGlobals();

        // Use the convenience function on Globals to load a chunk.
        LuaValue chunk = globals.loadfile(path);

        // Use any of the "call()" or "invoke()" functions directly on the chunk.
        chunk.call( LuaValue.valueOf(path) );
        
        // BufferedReader br;
 
        // try {
        //     br = new BufferedReader(new FileReader(path));
        // } catch (FileNotFoundException e) {
        //     gui.addChatMessage(
        //         ChatType.SYSTEM,
        //         new TextComponentString(TextFormatting.RED + e.toString())
        //     );
        //     return;
        // }

        // try {
        //     String line;
        //     while ((line = br.readLine()) != null) {
        //         System.out.println(line);
        //         gui.addChatMessage(ChatType.SYSTEM, new TextComponentString(line));

        //     }
        //     br.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
