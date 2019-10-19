package com.luamc.luamc;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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
        // TODO: have this actually abort the lua code
        thread.poopsie();
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.SYSTEM,
            new TextComponentString(TextFormatting.RED + "Program Exited."));
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
