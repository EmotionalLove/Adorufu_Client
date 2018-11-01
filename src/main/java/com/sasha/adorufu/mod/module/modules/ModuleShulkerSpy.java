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
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

@ModuleInfo(description = "View the contents of shulker boxes being held by other nearby players")
public class ModuleShulkerSpy extends AdorufuModule {

    public ModuleShulkerSpy() {
        super("ShulkerSpy", AdorufuCategory.RENDER, false);
    }

    /**
     * todo made a gui overlay thingy that shows the items in the grid
     */
    @Override
    public void onTick() {
        if (!this.isEnabled()) return;
        for (Entity entity : AdorufuMod.minecraft.world.getLoadedEntityList()) {
            if (entity instanceof EntityOtherPlayerMP
                    && ((EntityOtherPlayerMP) entity).getHeldItemMainhand().getItem() instanceof ItemShulkerBox) {
                NBTTagCompound tag = ((EntityOtherPlayerMP) entity).getHeldItemMainhand().getTagCompound();
                if (tag != null && tag.hasKey("BlockEntityTag", 10)) {
                    NBTTagCompound realTag = tag.getCompoundTag("BlockEntityTag");
                    if (realTag.hasKey("Items", 9)) {
                        NonNullList<ItemStack> shulkerContentsList = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
                        ItemStackHelper.loadAllItems(realTag, shulkerContentsList);
                        for (ItemStack itemStack : shulkerContentsList) {
                            AdorufuMod.logMsg(itemStack.getDisplayName());
                        }
                    }
                }
            }
        }
    }
}
