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

package com.sasha.adorufu.mod.gui.clickgui.helper;

import com.sasha.adorufu.mod.feature.IAdorufuFeature;
import com.sasha.adorufu.mod.feature.deprecated.AdorufuModule;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;

import static com.sasha.adorufu.mod.AdorufuMod.minecraft;

public class OptionToggler implements Runnable, IToggler {

    private IAdorufuFeature m;
    private String optionName;

    public OptionToggler(IAdorufuFeature m, String optionName) {
        this.m = m;
        this.optionName = optionName;
    }

    @Override
    public void run() {
        minecraft.world.playSound(minecraft.player.posX, minecraft.player.posY, minecraft.player.posZ, SoundEvents.UI_BUTTON_CLICK , SoundCategory.AMBIENT, 1f, 1f, false);
        if (m.getOptionBehaviour().isUsingModeBehaviour()) {
            m.getOptions();
        }
        m.toggleOption(optionName);
    }

    @Override
    public AdorufuModule getMod() {
        return m;
    }

    public boolean isTrue() {
        return m.getOption(optionName);
    }



}
