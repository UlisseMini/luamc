package com.luamc.luamc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

import net.minecraft.client.settings.KeyBinding;

public class Player extends TwoArgFunction {
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaTable player = new LuaTable();
		player.set("key", new key());
		player.set("startWalking", new startWalking());
		player.set("stopWalking", new stopWalking());
		player.set("turnYaw", new turnYaw());
		player.set("turnPitch", new turnPitch());
		player.set("getFood", new getFood());
		player.set("getHealth", new getHealth());
		player.set("moveSlot", new slot());
		player.set("getPos", new getPos());
		player.set("rclick", new rclick());
		player.set("lclick", new lclick());
		player.set("getMouseEvent", new getMouseEvent());
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
		public LuaValue call() { // , MC.player.posY, MC.player.posZ
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
			tryRunMethods(MC, new String[] { "rightClickMouse", "func_147121_ag" });
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

// WHAT DOES THIS DO
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
// ALSO HERE
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