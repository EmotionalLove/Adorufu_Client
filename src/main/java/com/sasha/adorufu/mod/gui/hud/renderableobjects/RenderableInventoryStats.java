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

package com.sasha.adorufu.mod.gui.hud.renderableobjects;


import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.gui.hud.AdorufuHUD;
import com.sasha.adorufu.mod.gui.hud.RenderableObject;
import com.sasha.adorufu.mod.gui.hud.ScreenCornerPos;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import java.io.IOException;

import static com.sasha.adorufu.mod.AdorufuMod.minecraft;

public class RenderableInventoryStats extends RenderableObject {
    public RenderableInventoryStats() {
        super("InventoryStats", ScreenCornerPos.RIGHTTOP);
        try {
            this.setPos(AdorufuMod.DATA_MANAGER.getHudPositionState(this));
        } catch (IOException e) {
            e.printStackTrace();
            this.setPos(this.getDefaultPos());
        }
    }

    @Override
    public void renderObjectLT(int yyy) {
        AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fInventory Statistics" + "\247" + "7: " + "Food " + getFoodItemsInInventory(minecraft.player) + " " + "\247" + "7Building Blocks " + getBuildingBlocksInInventory(minecraft.player), 4, yyy, 0xffffff);
    }
    @Override
    public void renderObjectLB(int yyy) {
        AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fInventory Statistics" + "\247" + "7: " + "Food " + getFoodItemsInInventory(minecraft.player) + " " + "\247" + "7Building Blocks " + getBuildingBlocksInInventory(minecraft.player), 4, yyy, 0xffffff);
    }
    @Override
    public void renderObjectRT(int yyy) {
        String s = "\247" + "fInventory Statistics" + "\247" + "7: " + "Food " + getFoodItemsInInventory(minecraft.player) + " " + "\247" + "7Building Blocks " + getBuildingBlocksInInventory(minecraft.player);
        AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s, (AdorufuHUD.sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(s) - 2), yyy, 0xffffff);
    }
    @Override
    public void renderObjectRB(int yyy) {
        String s = "\247" + "fInventory Statistics" + "\247" + "7: " + "Food " + getFoodItemsInInventory(minecraft.player) + " " + "\247" + "7Building Blocks " + getBuildingBlocksInInventory(minecraft.player);
        AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s, (AdorufuHUD.sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(s) - 2), yyy, 0xffffff);
    }
    public static String getFoodItemsInInventory(EntityPlayerSP player) {
        int i = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem().getCreativeTab() == CreativeTabs.FOOD) {
                i += stack.getCount();
            }
        }
        if (i <= 5) {
            return "\247" + "4" + i;
        }
        if (i <= 10) {
            return "\247" + "c" + i;
        }
        if (i <= 32) {
            return "\247" + "e" + i;
        }
        else {
            return "\247" + "a" + i;
        }
    }
    public static String getBuildingBlocksInInventory(EntityPlayerSP player) {
        int i = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem().getCreativeTab() == CreativeTabs.BUILDING_BLOCKS) {
                i += stack.getCount();
            }
        }
        if (i <= 5) {
            return "\247" + "4" + i;
        }
        if (i <= 10) {
            return "\247" + "c" + i;
        }
        if (i <= 32) {
            return "\247" + "e" + i;
        }
        else {
            return "\247" + "a" + i;
        }
    }
}
