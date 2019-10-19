package com.luamc.luamc;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import net.minecraft.client.Minecraft;

final class player {
	static public LuaValue table() {
		return CoerceJavaToLua.coerce(Minecraft.getMinecraft().player);
	}
}