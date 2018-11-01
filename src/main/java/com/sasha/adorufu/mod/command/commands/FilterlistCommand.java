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
import static com.sasha.adorufu.mod.module.modules.ModuleClientIgnore.filterList;

/**
 * Created by Sasha on 09/08/2018 at 3:28 PM
 **/
@SimpleCommandInfo(description = "View your filtered words and phrases", syntax = {"", "[word/phrase]"})
public class FilterlistCommand extends SimpleCommand {

    public FilterlistCommand() {
        super("filterlist");
    }

    public void onCommand() {
        if (this.getArguments() == null) {
            StringBuilder builder = new StringBuilder();
            logMsg(false, "Listing \247l" + filterList.size() + " \247r\2477ignored words and/or phrases:");
            for (int i = 0; i < filterList.size(); i++) {
                if (i == 0) {
                    builder.append("\"").append(filterList.get(i)).append("\"");
                    continue;
                }
                builder.append(", ").append("\"").append(filterList.get(i)).append("\"");
            }
            logMsg(builder.toString());
            return;
        }
        String player = this.getArguments()[0];
        logMsg(false, "\"" + player + "\" is" + (filterList.contains(player) ? "" : " not") + " filtered.");
    }
}
