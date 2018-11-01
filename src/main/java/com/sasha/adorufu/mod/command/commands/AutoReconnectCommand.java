/*
 * Copyright (c) Sasha Stevens 2018.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.sasha.adorufu.mod.command.commands;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.module.modules.ModuleAutoReconnect;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

@SimpleCommandInfo(description = "Adjust the speed of auto-reconnection", syntax = {"<speed (decimals allowed)>"})
public class AutoReconnectCommand extends SimpleCommand {

    public AutoReconnectCommand() {
        super("autorec");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length > 1) {
            AdorufuMod.logErr(false, "Incorrect Arguments. Try \"-help command " + this.getCommandName() + "\"");
            return;
        }
        double newSpeed = Double.parseDouble(this.getArguments()[0]);
        if (newSpeed < 0.1f) {
            AdorufuMod.logErr(false, "Speed values smaller than 0.1 are not allowed.");
            return;
        }
        ModuleAutoReconnect.delay = (long) newSpeed / 1000;
        AdorufuMod.logMsg(false, "AutoReconnect's speed changed to " + newSpeed);
    }
}
