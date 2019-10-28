package com.luamc.luamc;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;


public class Player extends TwoArgFunction {
	public LuaValue call(LuaValue modname, LuaValue env) {
		LuaTable player = new LuaTable();
		player.set("func", new func());
		env.set("player", player);

		return player;
	}

	final class func extends OneArgFunction {
		public LuaValue call(LuaValue x) {
			return x;
		}
	}
}

