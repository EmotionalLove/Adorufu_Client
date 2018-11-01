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

import com.sasha.adorufu.api.AdorufuPluginLoader;
import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

/**
 * Created by Sasha at 3:54 PM on 9/17/2018
 */
@SimpleCommandInfo(description = "List loaded plugins")
public class PluginsCommand extends SimpleCommand {
    public PluginsCommand() {
        super("plugins");
    }

    @Override
    public void onCommand() {
        if (AdorufuPluginLoader.getLoadedPlugins().size() == 0) {
            AdorufuMod.logMsg(false, "There aren't any plugins loaded...");
        }
        StringBuilder builder = new StringBuilder("\2476");
        for (int i = 0; i < AdorufuPluginLoader.getLoadedPlugins().size(); i++) {
            if (i == 0) {
                builder.append(AdorufuPluginLoader.getLoadedPlugins().get(i));
                continue;
            }
            builder.append(", ").append(AdorufuPluginLoader.getLoadedPlugins().get(i));
        }
        AdorufuMod.logMsg(builder.toString());
        AdorufuMod.logMsg(false, "There are \247l" + AdorufuPluginLoader.getLoadedPlugins().size() + " \247r\2477plugins loaded");
    }
}
