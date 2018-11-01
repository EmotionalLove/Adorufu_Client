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
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * Created by Sasha on 11/08/2018 at 8:27 PM
 **/
@ModuleInfo(description = "Automatically moves a totem into your offhand if it's empty")
public class ModuleAutoTotem extends AdorufuModule {
    public ModuleAutoTotem() {
        super("AutoTotem", AdorufuCategory.COMBAT, false);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onTick() {
        if (!this.isEnabled())
            return;
        ItemStack offhand = AdorufuMod.minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        int i = 0;
        for (int x = 9; x <= 44; x++) {
            ItemStack stack = AdorufuMod.minecraft.player.inventory.getStackInSlot(x);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                i++;
            }
        }
        this.setSuffix(i +"");
        if (offhand.getItem() != Items.TOTEM_OF_UNDYING) {
            for (int x = 9; x <= 44; x++) {
                ItemStack stack = AdorufuMod.minecraft.player.inventory.getStackInSlot(x);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING && AdorufuMod.minecraft.currentScreen == null /* don't move totems if the inventory is open */) {
                    AdorufuMod.minecraft.playerController.windowClick(0, x, 0, ClickType.PICKUP, AdorufuMod.minecraft.player);
                    AdorufuMod.minecraft.playerController.windowClick(0, 45, 0, ClickType.PICKUP, AdorufuMod.minecraft.player);
                    break;
                }
            }
        }

    }
}
