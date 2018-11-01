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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;

/**
 * Created by Sasha at 3:48 PM on 9/24/2018
 */
public class ModuleBoatFly extends AdorufuModule {

    public ModuleBoatFly() {
        super("BoatFly", AdorufuCategory.MOVEMENT, false, true);
        this.addOption("yawlock", false);
        this.addOption("gravity", true);
        this.addOption("all entities", false);
    }

    @Override
    public void onTick() {
        if (!this.isEnabled()) return;
        this.setSuffix(this.getModuleOptionsMap());
        if (AdorufuMod.minecraft.player.isRiding()) {
            if (AdorufuMod.minecraft.player.getRidingEntity() instanceof EntityBoat || this.getOption("all entities")) {
                Entity e = AdorufuMod.minecraft.player.getRidingEntity();
                if (e == null) return;
                if (!this.getOption("gravity")) {
                    e.setNoGravity(true);
                } else {
                    e.setNoGravity(false);
                }
                if (this.getOption("yawlock")) e.rotationYaw = (AdorufuMod.minecraft.player.rotationYaw);
                // actual boatfly
                if (AdorufuMod.minecraft.gameSettings.keyBindJump.isPressed()) {
                    e.motionY = 0.5f;
                    return;
                }
                if (AdorufuMod.minecraft.gameSettings.keyBindPlayerList.isPressed()) {
                    e.motionY = -0.5f;
                    return;
                }
                e.motionY = 0f;
            }
        }
    }
}
