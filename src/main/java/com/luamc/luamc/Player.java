package com.luamc.luamc;

import org.luaj.vm2.LuaTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;

final class player {
    public player() {}

	static public LuaValue table() {
		LuaValue library = new LuaTable();
		library.set( "test", new test() );
		library.set( "teleport", new teleport() );
		// env.set("player", library);
		return library;
	}
	static class test extends OneArgFunction {
		public LuaValue call(LuaValue x) {
			System.out.println(x);
			return x;
		}
	}
	static class teleport extends ThreeArgFunction {
		public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
 			EntityPlayerSP player = Minecraft.getMinecraft().player;
			player.attemptTeleport(x.todouble(), y.todouble(), z.todouble());
			// player.moveForward(x.tofloat());


			return null;
		}
	}
}