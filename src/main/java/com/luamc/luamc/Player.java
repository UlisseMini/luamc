package com.luamc.luamc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.lwjgl.input.Mouse;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class Player extends TwoArgFunction {
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaTable player = new LuaTable();
		player.set("key",				new key());
		player.set("setYaw",			new setYaw());
		player.set("setPitch",			new setPitch());
		player.set("getYaw",			new getYaw());
		player.set("getPitch",			new getPitch());
		player.set("getFood",			new getFood());
		player.set("getHealth", 		new getHealth());
		player.set("moveSlot",			new slot());
		player.set("getPos",			new getPos());
		player.set("rclick",			new rclick());
		player.set("lclick",			new lclick());
		player.set("inspect",			new inspect());
		player.set("getMouseEvent",		new getMouseEvent());
		player.set("getItemName",		new inspectItem());
		player.set("getItemCount",		new itemCount());
		player.set("getLookedAtBlock",  new lookedatblock());
		player.set("chat",				new chat());
		env.set("player", player);
		env.set("settings", CoerceJavaToLua.coerce(Minecraft.getMinecraft().gameSettings));

		return player;
	}

	public LuaValue make_keyobj(int code) {
		// KeyBinding.setKeyBindState(code, bool);
		LuaTable obj = new LuaTable();
		obj.set("up", new press(code, false));
		obj.set("down", new press(code, true));
		return obj;
	}

	final class press extends ZeroArgFunction {
		int code;
		boolean bool;

		press(int code, boolean bool) {
			this.code = code;
			this.bool = bool;
		}

		public LuaValue call() {
			KeyBinding.setKeyBindState(code, bool);
			return LuaValue.NIL;
		}
	}

	final class getMouseEvent extends ZeroArgFunction {
		public LuaValue call() {
			return LuaValue.valueOf(Mouse.getEventButton());
		}
	}

	// screw you java
	final class key extends OneArgFunction {
		public LuaValue call(LuaValue key) {
			int keycode = -1337;
			if (key.isint()) {
				keycode = key.toint();
			} else if (key.isstring()) {
				GameSettings settings = Minecraft.getMinecraft().gameSettings;
				String keyname = key.toString();

				for (KeyBinding kb : settings.keyBindings) {
					String name = kb.getKeyDescription();
					int idx = name.lastIndexOf('.');
					name = name.substring(idx + 1, name.length());

					if (keyname.equals(name)) {
						keycode = kb.getKeyCode();
					}

				}
				if (keycode == -1337) {
					throw new LuaError("Could not find key '" + keyname + "'");
				}
			} else {
				throw new LuaError("Invalid argument #1 to 'key' (expected string or int got " + key.typename() + ")");
			}

			return make_keyobj(keycode);
		}
	}

	final class setYaw extends OneArgFunction {
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

	final class setPitch extends OneArgFunction {
		public LuaValue call(LuaValue dir) {
			if (dir.isnumber()) {
				Minecraft MC = Minecraft.getMinecraft();
				MC.player.rotationPitch = dir.tofloat();
			}
			return LuaValue.NIL;
		}
	}

	final class getYaw extends ZeroArgFunction {
		public LuaValue call() {
			Minecraft MC = Minecraft.getMinecraft();
			return LuaValue.valueOf(MC.player.rotationYaw);
		}
	}

	final class getPitch extends ZeroArgFunction {
		public LuaValue call() {
			Minecraft MC = Minecraft.getMinecraft();
			return LuaValue.valueOf(MC.player.rotationPitch);
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
		public LuaValue call() { 
			LuaValue pos = new LuaTable();
			Minecraft MC = Minecraft.getMinecraft();
			pos.set("X", MC.player.posX);
			pos.set("Y", MC.player.posY);
			pos.set("Z", MC.player.posZ);

			return pos;
		}
	}

	final class rclick extends ZeroArgFunction {
		public LuaValue call() {
			Minecraft MC = Minecraft.getMinecraft();
			tryRunMethods(MC, new String[] { "rightClickMouse", "func_147121_ag" }); //thank fuck for the internet
			return LuaValue.NIL;
		}
	}
	
	final class lclick extends ZeroArgFunction {
		public LuaValue call() {
			Minecraft MC = Minecraft.getMinecraft();
			tryRunMethods(MC, new String[] { "clickMouse", "func_147116_af" });
			return LuaValue.NIL;
		}
	}

	final class inspect extends ThreeArgFunction {
		public LuaValue call(LuaValue LuaX, LuaValue LuaY, LuaValue LuaZ) {
			if (!LuaX.isint() || !LuaY.isint() || !LuaZ.isint()) {
				throw new LuaError("Expected int, got... Not int"); // shut up, okay?
			}
			BlockPos pos = new BlockPos(LuaX.toint(), LuaY.toint(), LuaZ.toint());
			Block block = Minecraft.getMinecraft().world.getBlockState(pos).getBlock();
			return LuaValue.valueOf(block.toString().substring(6, block.toString().length() -1)); // :)
			
		}
	}

	final class inspectItem extends ZeroArgFunction {
		public LuaValue call () {
			String id = Minecraft.getMinecraft().player.inventory.getCurrentItem().getItem().getRegistryName().toString();
			return LuaValue.valueOf(id);
		}
	}

	final class itemCount extends ZeroArgFunction {
		public LuaValue call () {
			int count = Minecraft.getMinecraft().player.inventory.getCurrentItem().getCount();
			return LuaValue.valueOf(count);
		}
	}

	final class chat extends OneArgFunction { // easiest shit in the world
		public LuaValue call (LuaValue message) {
			Minecraft.getMinecraft().player.sendChatMessage(message.toString());

			return LuaValue.NIL;
		}
	}

	final class lookedatblock extends ZeroArgFunction {
		public LuaValue call () {
			RayTraceResult lookingAt = Minecraft.getMinecraft().objectMouseOver;
			if (lookingAt != null && lookingAt.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos blockpos = lookingAt.getBlockPos();
				LuaValue pos = new LuaTable();
				pos.set("X", blockpos.getX());
				pos.set("Y", blockpos.getY());
				pos.set("Z", blockpos.getZ());
    			return pos;
			}
			return LuaValue.NIL;
		}
	}

// the wonders of copy/paste
	public static Object tryRunMethods(Object o, String[] methodNames) {
		Method method = null;
		for (String name : methodNames) {
			if ((method = tryGetMethod(o, name)) != null)
				break;
		}
		if (method != null) {
			try {
				method.setAccessible(true);
				return method.invoke(o);
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Method tryGetMethod(Object o, String methodName) {
		try {
			return o.getClass().getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}