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
import com.sasha.adorufu.mod.events.client.ClientGetMusicTypeEvent;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.client.audio.MusicTicker;

/**
 * Created by Sasha at 3:48 PM on 8/27/2018
 */
@ModuleInfo(description = "Plays the creative mode exclusive music in other gamemodes.")
public class ModuleMinecraftMusic extends AdorufuModule implements SimpleListener {

    public ModuleMinecraftMusic() {
        super("MinecraftMusic", AdorufuCategory.MISC, false, true, true);
        this.addOption("Creative", true);
        this.addOption("Survival", false);
        this.addOption("Nether", false);
        this.addOption("End", false);
        this.addOption("Main Menu", false);
    }

    @Override
    public void onEnable() {
        AdorufuMod.minecraft.getMusicTicker().update();
    }

    @Override
    public void onDisable() {
        AdorufuMod.minecraft.getMusicTicker().update();
    }

    @SimpleEventHandler
    public void onMusicSelect(ClientGetMusicTypeEvent e) {
        if (!this.isEnabled()) return;
        this.getModuleOptionsMap().forEach((option, bool) -> {
            if (bool) {
                switch (option.toLowerCase()) {
                    case "creative":
                        e.setMusicType(MusicTicker.MusicType.CREATIVE);
                        break;
                    case "survival":
                        e.setMusicType(MusicTicker.MusicType.GAME);
                        break;
                    case "nether":
                        e.setMusicType(MusicTicker.MusicType.NETHER);
                        break;
                    case "end":
                        e.setMusicType(MusicTicker.MusicType.END);
                        break;
                    case "main menu":
                        e.setMusicType(MusicTicker.MusicType.MENU);
                        break;
                    default:
                        e.setMusicType(MusicTicker.MusicType.CREATIVE);
                }
            }
        });
    }
}
