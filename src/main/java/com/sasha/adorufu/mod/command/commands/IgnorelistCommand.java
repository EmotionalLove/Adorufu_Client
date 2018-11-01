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

import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import static com.sasha.adorufu.mod.AdorufuMod.logMsg;
import static com.sasha.adorufu.mod.module.modules.ModuleClientIgnore.ignorelist;
/**
 * Created by Sasha on 09/08/2018 at 3:28 PM
 **/
@SimpleCommandInfo(description = "View your ignorelist", syntax = {"", "[player]"})
public class IgnorelistCommand extends SimpleCommand {
    public IgnorelistCommand() {
        super("ignorelist");
    }

    public void onCommand(){
        if (this.getArguments() == null) {
            StringBuilder builder = new StringBuilder();
            logMsg(false, "Listing \247l" + ignorelist.size() + " \247r\2477ignored players:");
            for (int i = 0; i < ignorelist.size(); i++) {
                if (i == 0) {
                    builder.append(ignorelist.get(i));
                    continue;
                }
                builder.append(", ").append(ignorelist.get(i));
            }
            logMsg(builder.toString());
            return;
        }
        String player = this.getArguments()[0];
        logMsg(false, player + " is" + (ignorelist.contains(player) ? "" : " not") + " ignored.");
    }
}
