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
import com.sasha.adorufu.mod.feature.impl.deprecated.ClientIgnoreFeature;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.io.IOException;

/**
 * Created by Sasha on 09/08/2018 at 3:28 PM
 **/
@SimpleCommandInfo(description = "Filter messages containing a word or phrase from chat", syntax = {"<word or phrase>"})
public class FilterCommand extends SimpleCommand {
    public FilterCommand() {
        super("filter");
    }

    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 1) {
            AdorufuMod.logErr(false, "Too few or too many arguments. Try -help command ignore");
            return;
        }
        if (!ClientIgnoreFeature.filterList.contains(this.getArguments()[0])) {
            ClientIgnoreFeature.filterList.add(this.getArguments()[0]);
            AdorufuMod.logMsg(false, "\"" + this.getArguments()[0] + "\" will now be filtered.");
            try {
                AdorufuMod.DATA_MANAGER.saveFilterList(ClientIgnoreFeature.filterList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        ClientIgnoreFeature.filterList.remove(this.getArguments()[0]);
        AdorufuMod.logMsg(false, "\"" + this.getArguments()[0] + "\" will no longer be filtered.");
        try {
            AdorufuMod.DATA_MANAGER.saveFilterList(ClientIgnoreFeature.filterList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
