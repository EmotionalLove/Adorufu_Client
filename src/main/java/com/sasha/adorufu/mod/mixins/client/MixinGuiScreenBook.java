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

package com.sasha.adorufu.mod.mixins.client;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.misc.AdorufuBookNBTBuilder;
import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.adorufu.mod.feature.impl.BookForgerFeature;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Created by Sasha at 3:51 PM on 9/16/2018
 */
@Mixin(value = GuiScreenBook.class, priority = 999)
public class MixinGuiScreenBook extends MixinGuiScreen {

    @Shadow
    @Final
    private boolean bookIsUnsigned;

    @Shadow
    private boolean bookIsModified;

    @Shadow
    private NBTTagList bookPages;

    @Shadow
    @Final
    private ItemStack book;

    @Shadow
    @Final
    private EntityPlayer editingPlayer;

    @Shadow
    private String bookTitle;

    /**
     * @author Sasha Stevens
     * @reason book forge
     */
    @Overwrite
    private void sendBookToServer(boolean publish) {
        if (this.bookIsUnsigned && this.bookIsModified) {
            if (this.bookPages != null) {
                while (this.bookPages.tagCount() > 1) {
                    String s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);
                    if (!s.isEmpty()) {
                        break;
                    }
                    this.bookPages.removeTag(this.bookPages.tagCount() - 1);
                }
                if (this.book.hasTagCompound()) {
                    NBTTagCompound nbttagcompound = this.book.getTagCompound();
                    nbttagcompound.setTag("pages", this.bookPages);
                } else {
                    this.book.setTagInfo("pages", this.bookPages);
                }
                AdorufuBookNBTBuilder builder = new AdorufuBookNBTBuilder(this.book);
                boolean flag = Manager.Feature.isFeatureEnabled(BookForgerFeature.class);
                builder.setAuthor(flag ? BookForgerFeature.author : this.editingPlayer.getName());
                if (flag) AdorufuMod.logMsg(false, BookForgerFeature.author);
                builder.setTitle(this.bookTitle);
                builder.setSign(publish);
                builder.push();
            }
        }
    }
}
