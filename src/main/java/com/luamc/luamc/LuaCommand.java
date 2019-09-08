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
import org.luaj.vm2.LuaValue;


import java.nio.file.Paths;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.lib.*;
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

    // mocked print that prints to chat
	static class print extends VarArgFunction {
        String prefix;
        print(String path) {
            this.prefix = Paths.get(path)
                .getFileName()
                .toString();
            
            if (this.prefix.endsWith(".lua")) {
                this.prefix = this.prefix.substring(0, this.prefix.length()-4);
            }
        }
        
		public LuaValue call(LuaValue x) {
            Minecraft.getMinecraft().ingameGUI.addChatMessage(
                ChatType.SYSTEM,
                new TextComponentString(TextFormatting.BLUE + "" + TextFormatting.ITALIC + "[" + this.prefix + "] " + TextFormatting.RESET + "" + TextFormatting.WHITE + x.toString())
            );
            return null;
		}
    }

    // TODO: running script freezes all minecraft shit
    private LuaValue loadFile(String path) {
        Globals globals = JsePlatform.standardGlobals();
        globals.set("print", new print(path));
        globals.set("player", player.table());

        // Use the convenience function on Globals to load a chunk.
        LuaValue chunk = globals.loadfile(path);

        // Use any of the "call()" or "invoke()" functions directly on the chunk.
        return chunk.call( LuaValue.valueOf(path) );
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        GuiIngame gui = Minecraft.getMinecraft().ingameGUI;

        if (args.length < 1) {
            gui.addChatMessage(
                ChatType.SYSTEM,
                new TextComponentString(TextFormatting.RED + getUsage(null))
            );
        } else {
            String path = "lua/" + args[0];
            try {
                loadFile(path);
            } catch(LuaError e) { // TODO: Only catch lua exceptions
                gui.addChatMessage(
                    ChatType.SYSTEM,
                    new TextComponentString(TextFormatting.RED + e.getMessage())
                );
            }
        }
    }
}
