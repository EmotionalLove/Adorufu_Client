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
import com.sasha.adorufu.mod.feature.impl.deprecated.ModuleYawLock;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import com.sasha.adorufu.mod.gui.hud.Direction;
import com.sasha.adorufu.mod.misc.AdorufuMath;
import com.sasha.adorufu.mod.misc.Manager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Sasha at 3:55 PM on 9/23/2018
 */
public class AutoWalkFeature extends AbstractAdorufuTogglableFeature implements IAdorufuTickableFeature {
    private static int timer = 0;
    public AutoWalkFeature() {
        super("AutoWalk", AdorufuCategory.MOVEMENT,
                new AdorufuFeatureOption<>("Normal", true),
                new AdorufuFeatureOption<>("Pathfinder", false));
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
        AdorufuMod.setPressed(AdorufuMod.minecraft.gameSettings.keyBindForward, true);
        if (this.getFormattableOptionsMap().get("Pathfinder")) {
            timer++;
            if (timer >= 20) {
                if (isObstacleThere()) {
                    if (!isObstacleThereManual(AdorufuMod.minecraft.player.rotationYaw + 90)) {
                        boolean wasToggled = false;
                        if (Manager.Module.getModule(ModuleYawLock.class).isEnabled()) {
                            Manager.Module.getModule(ModuleYawLock.class).toggle();
                            wasToggled = true;
                        }
                        float oldRot = AdorufuMod.minecraft.player.rotationYaw;
                        AdorufuMod.minecraft.player.rotationYaw += 90;
                        WalkThread wt = new WalkThread("AutoWalker", oldRot, wasToggled);
                        wt.start();
                        timer = 0;
                    }
                    else if (!isObstacleThereManual(AdorufuMod.minecraft.player.rotationYaw - 90)) {
                        boolean wasToggled = false;
                        if (Manager.Module.getModule(ModuleYawLock.class).isEnabled()) {
                            Manager.Module.getModule(ModuleYawLock.class).toggle();
                            wasToggled = true;
                        }
                        float oldRot = AdorufuMod.minecraft.player.rotationYaw;
                        AdorufuMod.minecraft.player.rotationYaw -= 90;
                        WalkThread wt = new WalkThread("AutoWalker", oldRot, wasToggled);
                        wt.start();
                        timer = 0;
                    }
                    else {
                        AdorufuMod.logWarn(false, "Can't evade obstacle, manual input needed!");
                    }
                }
                if (isBlockBlockingInFront()) {
                    AdorufuMod.minecraft.player.jump();
                    timer = 0;
                }
                if (isHoleInFront()) {
                    AdorufuMod.minecraft.player.jump();
                    timer = 0;
                }
            }
        }
    }
    private static boolean isBlockBlockingInFront() {
        Direction dir = AdorufuMath.getCardinalDirection(AdorufuMod.minecraft.player.rotationYaw);
        if (dir == Direction.posX) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 1.2, AdorufuMod.minecraft.player.posY, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b.fullBlock;
        }
        if (dir == Direction.posZ) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY, AdorufuMod.minecraft.player.posZ + 1.2)).getBlock();
            return b.fullBlock;
        }
        if (dir == Direction.negX) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX - 1.2, AdorufuMod.minecraft.player.posY, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b.fullBlock;
        }
        if (dir == Direction.negZ) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY, AdorufuMod.minecraft.player.posZ - 1.2)).getBlock();
            return b.fullBlock;
        }
        return false;
    }
    private static boolean isHoleInFront() {
        Direction dir = AdorufuMath.getCardinalDirection(AdorufuMod.minecraft.player.rotationYaw);
        if (dir == Direction.posX) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 1.2, AdorufuMod.minecraft.player.posY - 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY - 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b == Blocks.AIR && bb.getDefaultState().isNormalCube();
        }
        if (dir == Direction.posZ) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY - 1, AdorufuMod.minecraft.player.posZ + 1.2)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY - 1, AdorufuMod.minecraft.player.posZ + 2.2)).getBlock();
            return b == Blocks.AIR && bb.getDefaultState().isNormalCube();
        }
        if (dir == Direction.negX) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX - 1.2, AdorufuMod.minecraft.player.posY - 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX - 2.2, AdorufuMod.minecraft.player.posY - 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b == Blocks.AIR && bb.getDefaultState().isNormalCube();
        }
        if (dir == Direction.negZ) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY - 1, AdorufuMod.minecraft.player.posZ - 1.2)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY - 1, AdorufuMod.minecraft.player.posZ - 2.2)).getBlock();
            return b == Blocks.AIR && bb.getDefaultState().isNormalCube();
        }
        return false;
    }
    private static boolean isObstacleThere() {
        Direction dir = AdorufuMath.getCardinalDirection(AdorufuMod.minecraft.player.rotationYaw);
        if (dir == Direction.posX) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 1.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b != Blocks.AIR || bb != Blocks.AIR;
        }
        if (dir == Direction.posZ) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ + 1.2)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b != Blocks.AIR || bb != Blocks.AIR;
        }
        if (dir == Direction.negX) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX - 1.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b != Blocks.AIR || bb != Blocks.AIR;
        }
        if (dir == Direction.negZ) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ - 1.2)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b != Blocks.AIR || bb != Blocks.AIR;
        }
        return false;
    }
    private static boolean isObstacleThereManual(float yaw) {
        Direction dir = AdorufuMath.getCardinalDirection(yaw);
        if (dir == Direction.posX) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 1.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b != Blocks.AIR || bb != Blocks.AIR;
        }
        if (dir == Direction.posZ) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ + 1.2)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b != Blocks.AIR || bb != Blocks.AIR;
        }
        if (dir == Direction.negX) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX - 1.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b != Blocks.AIR || bb != Blocks.AIR;
        }
        if (dir == Direction.negZ) {
            Block b = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ - 1.2)).getBlock();
            Block bb = AdorufuMod.minecraft.world.getBlockState(new BlockPos(AdorufuMod.minecraft.player.posX + 2.2, AdorufuMod.minecraft.player.posY + 1, AdorufuMod.minecraft.player.posZ)).getBlock();
            return b != Blocks.AIR || bb != Blocks.AIR;
        }
        return false;
    }
}
class WalkThread implements Runnable {
    private Thread t;
    private String threadName;
    private float oldYaw;
    private boolean needsYaw;

    public WalkThread(String name, float oldYaw, boolean needsYaw) {
        threadName = name;
        System.out.println("Creating " +  threadName);
        //this.args = args;
        this.oldYaw = oldYaw;
        this.needsYaw = needsYaw;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Minecraft.getMinecraft().player.rotationYaw = this.oldYaw;
        if (this.needsYaw) {
            Manager.Module.getModule(ModuleYawLock.class).toggle();
        }
    }
    public void start () {
        //System.out.println("Starting " +  threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
