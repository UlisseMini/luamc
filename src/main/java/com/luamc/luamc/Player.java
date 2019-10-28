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
		player.set("getFood", new getFood());
		player.set("getHealth", new getHealth());
		player.set("slot", new slot());
		player.set("getPos", new getPos());
		env.set("player", player);

		return player;
	}

	final class turnYaw extends OneArgFunction {
		public LuaValue call(LuaValue dir) {
			Minecraft MC = Minecraft.getMinecraft();
			if (dir.isnumber()) {
				MC.player.rotationYaw = dir.tofloat();
			} else if (dir.isstring()) {
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

	final class getFood extends ZeroArgFunction {
		public LuaValue call() {
			Minecraft MC = Minecraft.getMinecraft();
			return LuaValue.valueOf(MC.player.getFoodStats().getFoodLevel());
		}
	}

	final class getHealth extends ZeroArgFunction {
		public LuaValue call() {
			Minecraft MC = Minecraft.getMinecraft();
			return LuaValue.valueOf((int) MC.player.getHealth());
		}
	}

	final class slot extends OneArgFunction {
		public LuaValue call(LuaValue slot) {
			Minecraft MC = Minecraft.getMinecraft();
			MC.player.inventory.pickItem(slot.toint());
			return LuaValue.NIL;
		}
	}

	final class getPos extends ZeroArgFunction {
		public LuaValue call() { //, MC.player.posY, MC.player.posZ
			LuaValue pos = new LuaTable();
			Minecraft MC = Minecraft.getMinecraft();
			pos.set("X", MC.player.posX);
			pos.set("Y", MC.player.posY);
			pos.set("Z", MC.player.posZ);
			
			return pos;
		}
	}
}
