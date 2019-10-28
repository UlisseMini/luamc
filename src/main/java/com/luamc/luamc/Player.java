package com.luamc.luamc;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class Player extends TwoArgFunction {
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaTable player = new LuaTable();
		player.set("startWalking", new startWalking());
		player.set("stopWalking", new stopWalking());
		player.set("turnYaw", new turnYaw());
		player.set("turnPitch", new turnPitch());
		env.set("player", player);

		return player;
	}

	// Doing it like this was more convienyent for what i was trying to do, you can
	// change this if you want.
	final class turnYaw extends OneArgFunction {
		public LuaValue call(LuaValue dir) {
			Minecraft MC = Minecraft.getMinecraft();
			if (dir.isstring()) {
				switch (dir.toString()) {
				case "north":
					MC.player.rotationYaw = 180;
					break;
				case "east":
					MC.player.rotationYaw = -90;
					break;
				case "south":
					MC.player.rotationYaw = 0;
					break;
				case "west":
					MC.player.rotationYaw = 90;
					break;
				}
			} else if (dir.isnumber()) {
				MC.player.rotationYaw = dir.tofloat();
			}
			return LuaValue.NIL;
		}
	}

	final class turnPitch extends OneArgFunction {
		public LuaValue call(LuaValue dir) {
			if (dir.isnumber()) {
				Minecraft MC = Minecraft.getMinecraft();
				MC.player.rotationPitch = dir.tofloat();
			}
			return LuaValue.NIL;
		}
	}


	final class startWalking extends ZeroArgFunction {
		public LuaValue call() {
			Minecraft MC = Minecraft.getMinecraft();
			KeyBinding.setKeyBindState(MC.gameSettings.keyBindForward.getKeyCode(), true);
			return LuaValue.TRUE;
		}
	}

	final class stopWalking extends ZeroArgFunction {
		public LuaValue call() {
			Minecraft MC = Minecraft.getMinecraft();
			KeyBinding.setKeyBindState(MC.gameSettings.keyBindForward.getKeyCode(), false);
			return LuaValue.TRUE;
		}
	}
}
