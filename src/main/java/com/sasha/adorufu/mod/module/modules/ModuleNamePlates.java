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

import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;

import static com.sasha.adorufu.mod.misc.AdorufuMath.dround;

@ModuleInfo(description = "Display an enchanced nametag above a player's head")
public class ModuleNamePlates extends AdorufuModule {
    public ModuleNamePlates() {
        super("NamePlates", AdorufuCategory.RENDER, false);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onTick() {

    }
    public static String formatHealthTag(double health) {
        if (health > 15.0) {
            return "\247" + "a" + (dround(health / 2, 3)) + " " + "\247" + "c<3";
        }
        else if (health > 10.0) {
            return "\247" + "e" + (dround(health / 2, 3)) + " " + "\247" + "c<3";
        }
        else if (health > 5.0) {
            return "\247" + "c" + (dround(health / 2, 3)) + " " + "\247" + "c<3";
        }
        else if (health > 0.0) {
            return "\247" + "4" + (dround(health / 2, 3)) + " " + "\247" + "c</3";
        }
        else {
            return "\247" + "4</3";
        }
    }
}
