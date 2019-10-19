package com.luamc.luamc;

import java.nio.file.Paths;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class LuaCommand extends CommandBase {
    // Lua is ran on a seaperate thread.
    LuaThread thread;

    LuaCommand(LuaThread thread) {
        super();
        this.thread = thread;
    }

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
            this.prefix = Paths.get(path).getFileName().toString();

            if (this.prefix.endsWith(".lua")) {
                this.prefix = this.prefix.substring(0, this.prefix.length() - 4);
            }
        }

        @Override
        public LuaValue call(LuaValue x) {
            Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.SYSTEM,
                    new TextComponentString(TextFormatting.BLUE + "" + TextFormatting.ITALIC + "[" + this.prefix + "] "
                            + TextFormatting.RESET + "" + TextFormatting.WHITE + x.toString()));
            return null;
        }
    }

    static class sleep extends OneArgFunction {
		public LuaValue call(LuaValue seconds) {
			try {
				Thread.sleep(seconds.tolong());
			} catch (InterruptedException e) {
				throw new LuaError("Thread Interrupted");
			}
			return null;
		}

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
            gui.addChatMessage(ChatType.SYSTEM, new TextComponentString(TextFormatting.RED + getUsage(null)));
        } else {
            String path = "lua/" + args[0];
            try {
                // Send a boi to the lua thread for running
                this.thread.spawn_run(path);
            } catch (InterruptedException e) {
                gui.addChatMessage(ChatType.SYSTEM, new TextComponentString(TextFormatting.RED + e.getMessage()));
            }
        }
    }
}
