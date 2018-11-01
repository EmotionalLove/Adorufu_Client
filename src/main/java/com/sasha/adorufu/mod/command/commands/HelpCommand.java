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
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.util.HashMap;

import static com.sasha.adorufu.mod.AdorufuMod.*;
import static com.sasha.adorufu.mod.misc.Manager.Module.moduleRegistry;

/**
 * Created by Sasha on 10/08/2018 at 9:28 AM
 **/
@SimpleCommandInfo(description = "Provide info about the other commands.", syntax = {"['command'/'module'] [module name]"})
public class HelpCommand extends SimpleCommand {

    public HelpCommand() {
        super("help");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null) {
            logMsg(false, "Listing commands...");
            AdorufuMod.COMMAND_PROCESSOR.getCommandRegistry().forEach((clazz, ins) -> {
                String b = COMMAND_PROCESSOR.getCommandPrefix() + ((SimpleCommand) ins).getCommandName() + " | " + COMMAND_PROCESSOR.getDescription(((SimpleCommand) ins).getClass());
                logMsg(b);
            });
            return;
        }
        if (this.getArguments().length != 2 || !this.getArguments()[0].toLowerCase().matches("command|module")) {
            logErr(false, "Incorrect syntax! Listing valid args:");
            for (String s : COMMAND_PROCESSOR.getSyntax(this.getClass())) {
                logMsg(COMMAND_PROCESSOR.getCommandPrefix() + this.getCommandName() + " " + s);
            }
            return;
        }
        if (this.getArguments()[0].equalsIgnoreCase("command")) {
            for (HashMap.Entry<Class<? extends SimpleCommand>, Object> simpleEntry : COMMAND_PROCESSOR.getCommandRegistry().entrySet()) {
                SimpleCommand simpleCommand = (SimpleCommand) simpleEntry.getValue();
                if (simpleCommand.getCommandName().equalsIgnoreCase(this.getArguments()[1])) {
                    logMsg(false, "\247c" + COMMAND_PROCESSOR.getDescription(simpleCommand.getClass()));
                    for (String sy : COMMAND_PROCESSOR.getSyntax(simpleCommand.getClass()))
                        logMsg(COMMAND_PROCESSOR.getCommandPrefix() + simpleCommand.getCommandName() + " " + sy);
                }
            }
            return;
        }
        if (this.getArguments()[0].equalsIgnoreCase("module")) {
            for (AdorufuModule module : moduleRegistry) {
                if (module.getModuleName().equalsIgnoreCase(this.getArguments()[1])) {
                    logMsg(module.getDescription());
                }
            }
        }
    }
}
