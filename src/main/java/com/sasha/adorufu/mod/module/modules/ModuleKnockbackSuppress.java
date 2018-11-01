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

import com.sasha.adorufu.mod.events.playerclient.PlayerKnockbackEvent;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;

/**
 * Created by Sasha at 7:59 PM on 9/2/2018
 */
@ModuleInfo(description = "Reduce or completely ignore knockback")
public class ModuleKnockbackSuppress extends AdorufuModule implements SimpleListener {

    public ModuleKnockbackSuppress() {
        super("KnockbackSuppress", AdorufuCategory.COMBAT, false, true, true);
        this.addOption("Ignore", true);
        this.addOption("Reduce", false);
    }

    @Override
    public void onTick() {
        this.setSuffix(this.getModuleOptionsMap());
    }

    @SimpleEventHandler
    public void onPlayerKnockBack(PlayerKnockbackEvent e) {
        if (!this.isEnabled()) return;
        if (this.getOption("Ignore")) {
            e.setCancelled(true);
            return;
        }
        e.setMotionX(e.getMotionX() / 3.0);
        e.setMotionY(e.getMotionY() / 3.0);
        e.setMotionZ(e.getMotionZ() / 3.0);
    }
}
