package com.luamc.luamc;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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
    }
}

