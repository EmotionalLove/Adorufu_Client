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

import com.sasha.adorufu.mod.events.client.ClientPacketSendEvent;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.client.CPacketKeepAlive;

/**
 * Created by Sasha at 11:00 AM on 8/28/2018
 */
@ModuleInfo(description = "Suspend packets")
public class ModuleBlink extends AdorufuModule implements SimpleListener {

    public ModuleBlink() {
        super("Blink", AdorufuCategory.COMBAT, false);
    }


    @SimpleEventHandler
    public void onPacketTx(ClientPacketSendEvent e) {
        if (this.isEnabled() && !(e.getSendPacket() instanceof CPacketKeepAlive))
            e.setCancelled(true);
    }
}
