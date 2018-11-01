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
import net.minecraft.network.play.client.CPacketPlayer;

@ModuleInfo(description = "NCP Flight bypass. Useful for 2b2t's nether roof.")
public class ModulePacketFly extends AdorufuModule {

    public ModulePacketFly() {
        super("PacketFly", AdorufuCategory.MOVEMENT, false);
    }

    @Override
    public void onTick() {
        if (!this.isEnabled())
            return;
        double[] dir = moveLooking(0);
        double xDir = dir[0];
        double zDir = dir[1];
        AdorufuMod.minecraft.player.motionX = 0;
        AdorufuMod.minecraft.player.motionY = 0;
        AdorufuMod.minecraft.player.motionZ = 0;
        AdorufuMod.minecraft.player.connection.sendPacket(
                new CPacketPlayer.PositionRotation
                        (AdorufuMod.minecraft.player.posX + AdorufuMod.minecraft.player.motionX + (AdorufuMod.minecraft.gameSettings.keyBindForward.isKeyDown() ? 0.0624 : 0) - (AdorufuMod.minecraft.gameSettings.keyBindBack.isKeyDown() ? 0.0624 : 0)
                                , AdorufuMod.minecraft.player.posY + (AdorufuMod.minecraft.gameSettings.keyBindJump.isKeyDown() ? 0.0624 : 0) - (AdorufuMod.minecraft.gameSettings.keyBindSneak.isKeyDown() ? 0.0624 : 0),
                                AdorufuMod.minecraft.player.posZ + AdorufuMod.minecraft.player.motionZ + (AdorufuMod.minecraft.gameSettings.keyBindRight.isKeyDown() ? 0.0624 : 0) - (AdorufuMod.minecraft.gameSettings.keyBindLeft.isKeyDown() ? 0.0624 : 0)
                                , AdorufuMod.minecraft.player.rotationYaw, AdorufuMod.minecraft.player.rotationPitch, false));
        AdorufuMod.minecraft.player.connection.sendPacket(new CPacketPlayer.PositionRotation(AdorufuMod.minecraft.player.posX + AdorufuMod.minecraft.player.motionX, AdorufuMod.minecraft.player.posY - 42069, AdorufuMod.minecraft.player.posZ + AdorufuMod.minecraft.player.motionZ, AdorufuMod.minecraft.player.rotationYaw, AdorufuMod.minecraft.player.rotationPitch, true));
    }

    private static double[] moveLooking(int ignored) {
        return new double[]{AdorufuMod.minecraft.player.rotationYaw * 360 / 360 * 180 / 180, ignored};
    }
}
