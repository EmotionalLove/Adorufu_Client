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

package com.sasha.adorufu.mod.feature.impl;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.AdorufuModule;
import com.sasha.adorufu.mod.feature.ModuleInfo;
import com.sasha.simplesettings.SettingFlag;

/**
 * Created by Sasha on 12/08/2018 at 9:04 AM
 **/
@ModuleInfo(description = "Allows you to fly. Only works on servers that don't have an anticheat.")
public class ModuleFlight extends AdorufuModule implements SettingFlag {
    public ModuleFlight() {
        super("Flight", AdorufuCategory.MOVEMENT, false);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        AdorufuMod.minecraft.player.capabilities.isFlying = false;
    }

    @Override
    public void onTick() {
        if (!this.isEnabled()) return;
        AdorufuMod.minecraft.player.capabilities.isFlying = true;
    }
}
