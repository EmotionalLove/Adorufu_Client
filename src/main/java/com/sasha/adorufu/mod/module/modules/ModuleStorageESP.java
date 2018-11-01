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
import com.sasha.adorufu.mod.misc.AdorufuRender;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import net.minecraft.block.BlockChest;
import net.minecraft.tileentity.*;

/**
 * Created by Sasha on 09/08/2018 at 7:32 PM
 **/
@ModuleInfo(description = "Draws an outline around storage containers")
public class ModuleStorageESP extends AdorufuModule {
    public ModuleStorageESP() {
        super("StorageESP", AdorufuCategory.RENDER, false);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onRender() {
        if (!this.isEnabled())
            return;
        int i = 0;
        for (TileEntity TE : AdorufuMod.minecraft.world.loadedTileEntityList) {
            if (TE instanceof TileEntityChest) {
                i++;
                if (((TileEntityChest) TE).getChestType() == BlockChest.Type.TRAP /* OwO What's this? */) {
                    AdorufuRender.storageESP(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 1.0f, 0.0f, 0.0f);
                } else {
                    AdorufuRender.storageESP(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 1.0f, 0.3f, 0.0f);
                }
            }
            if (TE instanceof TileEntityFurnace) {
                i++;
                AdorufuRender.storageESP(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 0.0f, 0.3f, 1.0f);
            }
            if (TE instanceof TileEntityDropper) {
                i++;
                AdorufuRender.storageESP(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 0.0f, 0.3f, 1.0f);
            }
            if (TE instanceof TileEntityDispenser) {
                i++;
                AdorufuRender.storageESP(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 0.0f, 0.3f, 1.0f);
            }
            if (TE instanceof TileEntityShulkerBox) {
                i++;
                AdorufuRender.storageESP(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 1.0f, 0.3f, 0.3f);
            }
            if (TE instanceof TileEntityEnderChest) {
                i++;
                AdorufuRender.storageESP(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 1.0f, 0.0f, 1.0f);
            }
        }
        //todo int x = AdorufuMod.minecraft.world.loadedTileEntityList.stream().filter(tile -> tile. )
        this.setSuffix(i+"");
    }

    @Override
    public void onTick() {

    }

}
