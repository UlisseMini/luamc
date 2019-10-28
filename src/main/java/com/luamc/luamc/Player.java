package com.luamc.luamc;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class Player extends TwoArgFunction {
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaTable player = new LuaTable();
		player.set("key", new key());
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
			return null;
		}
	}

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
					name = name.substring(idx+1, name.length());
					
					if (keyname.equals(name)) {
						keycode = kb.getKeyCode();
					}

				} 
				if (keycode == -1337) {
					throw new LuaError(
						"Could not find key '" + keyname + "'");
				}
			} else {
				throw new LuaError(
					"Invalid argument #1 to 'key' (expected string or int got " + key.typename() + ")");
			}

			return make_keyobj(keycode);
		}
	}
}

