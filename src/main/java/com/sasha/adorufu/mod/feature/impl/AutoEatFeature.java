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
import com.sasha.adorufu.mod.feature.AbstractAdorufuTogglableFeature;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.IAdorufuTickableFeature;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;

public class AutoEatFeature extends AbstractAdorufuTogglableFeature implements IAdorufuTickableFeature {

    private static boolean eating = false;
    private static boolean checked = false;
    private static int prevSlot = 0;

    public AutoEatFeature() {
        super("AutoEat", AdorufuCategory.MISC,
                new AdorufuFeatureOption<>("priority gapple", false),
                new AdorufuFeatureOption<>("conserve  gapple", true));
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onTick() {
        this.setSuffix(this.getFormattableOptionsMap());
        if (AdorufuMod.minecraft.player.getFoodStats().getFoodLevel() <= 8) {
            // we need to eat
            for (int s = 0; s <= 8; s++) {
                if (AdorufuMod.minecraft.player.inventory.getStackInSlot(s).getItemUseAction() == EnumAction.EAT) {
                    // we can eat this item
                    if (this.getFormattableOptionsMap().get("conserve gapple")) {
                        if (AdorufuMod.minecraft.player.inventory.getStackInSlot(s).getItem() == Items.GOLDEN_APPLE) {
                            continue;
                        }
                    }
                    else if (this.getFormattableOptionsMap().get("priority gapple") && !checked) {
                        if (AdorufuMod.minecraft.player.inventory.getStackInSlot(s).getItem() != Items.GOLDEN_APPLE) {
                            continue;
                        }
                    }
                    eating = true;
                    checked = false;
                    prevSlot = AdorufuMod.minecraft.player.inventory.currentItem;
                    AdorufuMod.minecraft.player.inventory.currentItem = s;
                    AdorufuMod.setPressed(AdorufuMod.minecraft.gameSettings.keyBindUseItem, true);
                    return;
                }
            }
            if (this.getFormattableOptionsMap().get("priority gapple")) {
                checked = true;
            }
            return;
        }
        if (eating) {
            AdorufuMod.setPressed(AdorufuMod.minecraft.gameSettings.keyBindUseItem, false);
            AdorufuMod.minecraft.player.inventory.currentItem = prevSlot;
            eating = false;
        }
    }
}
