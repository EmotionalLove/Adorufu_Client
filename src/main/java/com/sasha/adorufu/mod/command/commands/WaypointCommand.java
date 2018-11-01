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
import com.sasha.adorufu.mod.waypoint.Waypoint;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

@SimpleCommandInfo(description = "Add or remove waypoints", syntax = {"<'add'/'del'> <name>", "<'add'/'del'> <name> <x> <y> <z>"})
public class WaypointCommand extends SimpleCommand {

    public WaypointCommand() {
        super("waypoint");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length < 2) {
            AdorufuMod.logErr(false, "Arguments required! Try \"-help command waypoint\"");
            return;
        }
        boolean manualMode = false;
        if (this.getArguments().length == 5) {
            manualMode = true;
        }
        switch (this.getArguments()[0].toLowerCase()) {
            case "add":
                if (AdorufuMod.WAYPOINT_MANAGER.sameName(this.getArguments()[1])) {
                    AdorufuMod.logErr(false, "There is already a waypoint with that name!");
                    return;
                }
                Waypoint daWaypoint = new Waypoint(manualMode ? Integer.parseInt(this.getArguments()[2]) : AdorufuMod.minecraft.player.getPosition().x,
                        manualMode ? Integer.parseInt(this.getArguments()[3]) : AdorufuMod.minecraft.player.getPosition().y,
                        manualMode ? Integer.parseInt(this.getArguments()[4]) : AdorufuMod.minecraft.player.getPosition().z,
                        true,
                        (AdorufuMod.minecraft.getCurrentServerData() == null) ? null : AdorufuMod.minecraft.getCurrentServerData().serverIP,
                        this.getArguments()[1]);
                AdorufuMod.WAYPOINT_MANAGER.addWaypoint(daWaypoint, true);
                AdorufuMod.logMsg(false, this.getArguments()[1] + " successfully added");
                break;
            case "del":
                if (!AdorufuMod.WAYPOINT_MANAGER.sameName(this.getArguments()[1])) {
                    AdorufuMod.logErr(false, "There isn't a waypoint with that name!");
                    return;
                }
                AdorufuMod.WAYPOINT_MANAGER.delWaypoint(this.getArguments()[1]);
                AdorufuMod.logMsg(false, this.getArguments()[1] + " successfully removed");
                break;
        }
    }
}
