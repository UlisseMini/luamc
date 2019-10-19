package com.luamc.luamc;

import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;

// Credit: https://stackoverflow.com/a/29438498
public class CustomDebugLib extends DebugLib {
    public boolean interrupted = false;

    @Override
    public void onInstruction(int pc, Varargs v, int top) {
        if (interrupted) {
            throw new ScriptInterruptException();
        }
        super.onInstruction(pc, v, top);
    }

    public static class ScriptInterruptException extends RuntimeException {}
}