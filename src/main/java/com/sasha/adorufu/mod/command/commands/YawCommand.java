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
import com.sasha.adorufu.mod.module.modules.ModuleYawLock;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

/**
 * Created by Sasha on 08/08/2018 at 9:26 PM
 **/
@SimpleCommandInfo(description = "Adjust the yawlock value", syntax = {"<angle>"})
public class YawCommand extends SimpleCommand {

    public YawCommand() {
        super("yaw");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null) {
            AdorufuMod.logErr(false, "Arguments required! Try \"-help command yaw\"");
            return;
        }
        try {
            ModuleYawLock.yawDegrees = Integer.parseInt(this.getArguments()[0]);
        } catch (Exception e) {
            AdorufuMod.logErr(false, "Your argument must be an integer.");
        }
    }
}
