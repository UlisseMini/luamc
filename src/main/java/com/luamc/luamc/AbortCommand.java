package com.luamc.luamc;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class AbortCommand extends CommandBase {
    // Lua is ran on a seaperate thread.
    LuaThread thread;

    AbortCommand(LuaThread thread) {
        super();
        this.thread = thread;
    }

    @Override
    public String getName() {
        return "abort";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/abort";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        thread.poopsie();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
