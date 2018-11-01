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
import com.sasha.adorufu.mod.module.modules.ModuleClientIgnore;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.io.IOException;

/**
 * Created by Sasha on 09/08/2018 at 3:28 PM
 **/
@SimpleCommandInfo(description = "Ignore or unignore a player. The player's name is CaSe SeNsiTve", syntax = {"<player>"})
public class IgnoreCommand extends SimpleCommand {

    public IgnoreCommand() {
        super("ignore");
    }

    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 1) {
            AdorufuMod.logErr(false, "Too few or too many arguments. Try -help command ignore");
            return;
        }
        if (!ModuleClientIgnore.ignorelist.contains(this.getArguments()[0])) {
            ModuleClientIgnore.ignorelist.add(this.getArguments()[0]);
            AdorufuMod.logMsg(false, this.getArguments()[0] + " ignored.");
            try {
                AdorufuMod.DATA_MANAGER.saveIgnorelist(ModuleClientIgnore.ignorelist);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        ModuleClientIgnore.ignorelist.remove(this.getArguments()[0]);
        AdorufuMod.logMsg(false, this.getArguments()[0] + " unignored.");
        try {
            AdorufuMod.DATA_MANAGER.saveIgnorelist(ModuleClientIgnore.ignorelist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
