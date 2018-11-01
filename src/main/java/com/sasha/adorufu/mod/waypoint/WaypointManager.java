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

package com.sasha.adorufu.mod.waypoint;

import com.sasha.adorufu.mod.AdorufuMod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WaypointManager {

    private List<Waypoint> waypoints = new ArrayList<>();

    public WaypointManager() {
        try {
            waypoints = AdorufuMod.DATA_MANAGER.loadWaypoints();
        } catch (IOException e) {
            e.printStackTrace();
            AdorufuMod.logErr(true, "Failed to load waypoints from files.");
        }
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void addWaypoint(Waypoint waypoint, boolean save) {
        waypoints.add(waypoint);
        if (!save) return;
        try {
            AdorufuMod.DATA_MANAGER.saveWaypoint(waypoint, false);
        } catch (IOException e) {
            e.printStackTrace();
            AdorufuMod.logErr(false, "Couldn't save waypoint to file!");
        }
    }

    public void delWaypoint(String waypoint) {
        Waypoint toDelete = null;
        for (Waypoint waypoint1 : waypoints) {
            if (waypoint1.getName().equalsIgnoreCase(waypoint)) {
                toDelete = waypoint1;
                break;
            }
        }
        if (toDelete == null) return;
        waypoints.remove(toDelete);
        try {
            AdorufuMod.DATA_MANAGER.saveWaypoint(toDelete, true);
        } catch (IOException e) {
            e.printStackTrace();
            AdorufuMod.logErr(false, "Couldn't delete waypoint file on disk!");
        }
    }

    public boolean sameName(String name) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
