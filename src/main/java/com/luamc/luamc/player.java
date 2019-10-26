package com.luamc.luamc;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class player {
	static public LuaValue table() {
		LuaTable t = new LuaTable();
		t.set("pi", 3.1415);
		t.set("func", new func());
		return t;
	}
}


final class func extends OneArgFunction {
	public LuaValue call(LuaValue x) {
		return x;
	}
}