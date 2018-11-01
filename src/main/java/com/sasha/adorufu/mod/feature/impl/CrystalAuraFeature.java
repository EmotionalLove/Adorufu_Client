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
import com.sasha.adorufu.mod.feature.annotation.FeatureInfo;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

import static com.sasha.adorufu.mod.feature.impl.deprecated.ModuleKillaura.rotateTowardsEntity;

/**
 * Created by Sasha on 12/08/2018 at 8:53 AM
 **/
@FeatureInfo(description = "Automatically hit nearby crystals")
public class CrystalAuraFeature extends AbstractAdorufuTogglableFeature implements IAdorufuTickableFeature {
    public CrystalAuraFeature() {
        super("CrystalAura", AdorufuCategory.COMBAT,
                new AdorufuFeatureOption<>("aura", true),
                new AdorufuFeatureOption<>("auto", false),
                new AdorufuFeatureOption<>("auto all", false));
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onTick() {
        if (this.isEnabled()) {
            this.setSuffix(this.getFormattableOptionsMap());
            if (this.getFormattableOptionsMap().get("auto")) { // use radius of 3
                boolean hasCrystals = false; // make sure the player even has crystal in their hotbar.
                for (int s = 36; s <= 44; s++) {
                    ItemStack stack = AdorufuMod.minecraft.player.inventory.getStackInSlot(s);
                    AdorufuMod.logMsg(stack.getTranslationKey());
                    if (stack.getItem() == Items.END_CRYSTAL) {
                        AdorufuMod.minecraft.player.inventory.currentItem = s;
                        AdorufuMod.logMsg("ender crystal @ " + s);
                        hasCrystals = true;
                        break;
                    }
                }
                if (!hasCrystals) {
                    return; // no use in continuing without crystals.
                }
                World theCurrentWorld = AdorufuMod.minecraft.world;
                for (int x = ((int) AdorufuMod.minecraft.player.posX - 3); x < AdorufuMod.minecraft.player.posX + 3; x++) {
                    for (int y = ((int) AdorufuMod.minecraft.player.posY - 3); y < AdorufuMod.minecraft.player.posY + 3; y++) {
                        for (int z = ((int) AdorufuMod.minecraft.player.posZ - 3); z < AdorufuMod.minecraft.player.posZ + 3; z++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            Block currentBlock = theCurrentWorld.getBlockState(pos).getBlock();
                            if (currentBlock == Blocks.OBSIDIAN || currentBlock == Blocks.BEDROCK) {
                                Block aboveBlock = theCurrentWorld.getBlockState(pos.up()).getBlock();
                                if (aboveBlock == Blocks.AIR) {
                                    AdorufuMod.minecraft.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.1f, 0.1f, 0.1f));
                                }
                            }
                        }
                    }
                }
            }


            if (this.getFormattableOptionsMap().get("aura")) {
                for (Entity e : AdorufuMod.minecraft.world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal) {
                        if (AdorufuMod.minecraft.player.getDistance(e) <= 3.8f) {
                            if (Mouse.isButtonDown(1) && AdorufuMod.minecraft.player.inventory.getCurrentItem().getItem() != Items.END_CRYSTAL) {
                                return; // dont do anything if the player is eating or anything
                            }
                            if (e.isEntityAlive()) {
                                float yaw = AdorufuMod.minecraft.player.rotationYaw;
                                float pitch = AdorufuMod.minecraft.player.rotationPitch;
                                float yawHead = AdorufuMod.minecraft.player.rotationYawHead;
                                boolean wasSprinting = AdorufuMod.minecraft.player.isSprinting();
                                rotateTowardsEntity(e);
                                AdorufuMod.minecraft.player.setSprinting(false);
                                AdorufuMod.minecraft.playerController.attackEntity(AdorufuMod.minecraft.player, e);
                                AdorufuMod.minecraft.player.swingArm(EnumHand.MAIN_HAND);
                                AdorufuMod.minecraft.player.rotationYaw = yaw;
                                AdorufuMod.minecraft.player.rotationPitch = pitch;
                                AdorufuMod.minecraft.player.rotationYawHead = yawHead;
                                if (wasSprinting) {
                                    AdorufuMod.minecraft.player.setSprinting(true);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
