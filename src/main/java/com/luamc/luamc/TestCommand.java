package com.luamc.luamc;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class TestCommand extends CommandBase {

    @Override
    public String getName() {
        return "eat pant";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "i have no fucking idea";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer)sender;
        EntityLivingBase.moveForward();
        if (player instanceof EntityPlayer)
            System.out.println(player.move());
            
    }
}