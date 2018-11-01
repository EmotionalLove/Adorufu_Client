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
import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

/**
 * Created by Sasha on 09/08/2018 at 3:28 PM
 **/
@SimpleCommandInfo(description = "Used for debugging. To be removed", syntax = {""})
public class ModulesCommand extends SimpleCommand {

    public ModulesCommand() {
        super("modules");
    }

    public void onCommand() {
        Manager.Module.moduleRegistry.forEach(module -> AdorufuMod.logMsg(false, module.getModuleName() + (module.isEnabled() ? "\247aenabled" : "\247cdisabled")
                + " " + (module.isRenderable() ? "\247arenderable" : "\247crenderable")));
    }
}
