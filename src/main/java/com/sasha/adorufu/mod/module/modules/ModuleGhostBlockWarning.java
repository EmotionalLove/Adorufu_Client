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

package com.sasha.adorufu.mod.module.modules;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.client.ClientItemSpawnEvent;
import com.sasha.adorufu.mod.events.playerclient.PlayerBlockBreakEvent;
import com.sasha.adorufu.mod.misc.AdorufuRender;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;

import java.util.HashMap;
import java.util.LinkedHashMap;

@ModuleInfo(description = "Highlight blocks that might've not been mined on the server's side.")
public class ModuleGhostBlockWarning extends AdorufuModule implements SimpleListener {

    /**
     * dont ask why this variable is named this it was like this already HUSH PLS
     */
    private static LinkedHashMap<Coordinate, Boolean> BlakeIsMyBoyfriendMap = new LinkedHashMap<>();

    public ModuleGhostBlockWarning() {
        super("GhostBlockWarning", AdorufuCategory.RENDER, false);
    }

    @Override
    public void onRender() {
        if (!this.isEnabled()) {
            return;
        }
        for (HashMap.Entry<Coordinate, Boolean> wow : BlakeIsMyBoyfriendMap.entrySet()) {
            if (!wow.getValue()) {
                AdorufuRender.ghostBlock(wow.getKey().getX(), wow.getKey().getY(), wow.getKey().getZ(), 1.0f, 0.0f, 0.0f, 0.5f);
            }
        }
    }

    @SimpleEventHandler
    public void onBlockBreak(PlayerBlockBreakEvent e) {
        if (e.getBlock().canHarvestBlock(AdorufuMod.minecraft.world, e.getBlockPos(), AdorufuMod.minecraft.player)) {
            BlakeIsMyBoyfriendMap.put(new Coordinate(e.getBlockPos().x, e.getBlockPos().y, e.getBlockPos().z, AdorufuMod.minecraft.player.dimension), false);
        }
    }

    @SimpleEventHandler
    public void onItemDrop(ClientItemSpawnEvent e) {
        int[] coords = e.getCoordinate();
        for (HashMap.Entry<Coordinate, Boolean> fugg : BlakeIsMyBoyfriendMap.entrySet()) {
            if (!fugg.getValue() && between(fugg.getKey().getX(), coords[0], 0.25d, 0.25d) && between(fugg.getKey().getY(), coords[1], 0.25d, 0.25d) && between(fugg.getKey().getZ(), coords[2], 0.25d, 0.25d)) {
                BlakeIsMyBoyfriendMap.put(fugg.getKey(), true);
            }
        }
    }

    private static boolean between(int i, int coord, double min, double max) {
        if (coord < 0) {
            coord--;
        }
        return i >= (coord - min) && i <= (coord + max);
    }
}

class Coordinate {
    private int x, y, z, dim;

    public Coordinate(int x, int y, int z, int dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDim() {
        return dim;
    }
}
