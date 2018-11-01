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
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;

@ModuleInfo(description = "Lock your yaw to a specified angle")
public class ModuleYawLock extends AdorufuModule {

    public static int yawDegrees = 90;

    public ModuleYawLock() {
        super("YawLock", AdorufuCategory.MOVEMENT, false);
    }

    @Override
    public void onTick() {
        if (this.isEnabled()) {
            AdorufuMod.minecraft.player.rotationYaw = yawDegrees;
            this.setSuffix(yawDegrees + "\u00b0");
        }
    }
}
