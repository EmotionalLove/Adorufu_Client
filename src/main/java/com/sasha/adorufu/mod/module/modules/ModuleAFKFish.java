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
import com.sasha.adorufu.mod.events.client.ClientPacketRecieveEvent;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.server.SPacketSoundEffect;

/**
 * Created by Sasha on 11/08/2018 at 6:27 PM
 **/
@ModuleInfo(description = "Automated fishing, for when you're not at your computer.")
public class ModuleAFKFish extends AdorufuModule implements SimpleListener {

    public ModuleAFKFish() {
        super("AFKFish", AdorufuCategory.MISC, false);
    }

    @SimpleEventHandler
    public void onSplash(ClientPacketRecieveEvent e) {
        if (!this.isEnabled()) return;
        if (e.getRecievedPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect pck = (SPacketSoundEffect) e.getRecievedPacket();
            if (pck.getSound().getSoundName().toString().equalsIgnoreCase("minecraft:entity.bobber.splash")) {
                new Thread(() -> {
                    AdorufuMod.minecraft.rightClickMouse();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    AdorufuMod.minecraft.rightClickMouse();
                }).start();
            }
        }
    }
}
