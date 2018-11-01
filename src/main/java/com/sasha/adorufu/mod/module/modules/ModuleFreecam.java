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
import com.sasha.adorufu.mod.events.client.ClientPacketRecieveEvent;
import com.sasha.adorufu.mod.events.client.ClientPacketSendEvent;
import com.sasha.adorufu.mod.events.client.ClientPushOutOfBlocksEvent;
import com.sasha.adorufu.mod.events.client.EntityMoveEvent;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.world.GameType;

import static com.sasha.adorufu.mod.AdorufuMod.minecraft;

/**
 * Created by Sasha on 12/08/2018 at 9:12 AM
 **/
@ModuleInfo(description = "Client-sided spectator mode.") //todo fix the fact that you cant fly thru stuff
public class ModuleFreecam extends AdorufuModule implements SimpleListener {

    public static double oldX, oldY, oldZ, oldYaw, oldPitch;
    public static GameType oldGameType;

    public ModuleFreecam() {
        super("Freecam", AdorufuCategory.MOVEMENT, false);
    }

    @Override
    public void onEnable() {
        oldX = AdorufuMod.minecraft.player.posX;
        oldY = AdorufuMod.minecraft.player.posY;
        oldZ = AdorufuMod.minecraft.player.posZ;
        oldYaw = AdorufuMod.minecraft.player.rotationYaw;
        oldPitch = AdorufuMod.minecraft.player.rotationPitch;
        oldGameType = AdorufuMod.minecraft.playerController.currentGameType;
        AdorufuMod.minecraft.playerController.setGameType(GameType.SPECTATOR);
        AdorufuMod.minecraft.player.setGameType(GameType.SPECTATOR);
        AdorufuMod.minecraft.player.noClip = true;
    }

    @Override
    public void onDisable() {
        AdorufuMod.minecraft.player.motionX = 0.0;
        AdorufuMod.minecraft.player.motionY = 0.0;
        AdorufuMod.minecraft.player.motionZ = 0.0;
        AdorufuMod.minecraft.player.setLocationAndAngles(oldX, oldY, oldZ, (float) oldYaw, (float) oldPitch);
        AdorufuMod.minecraft.playerController.setGameType(oldGameType);
        AdorufuMod.minecraft.player.setGameType(oldGameType);
        AdorufuMod.minecraft.player.noClip = false;
    }

    @Override
    public void onTick() {
        if (minecraft.world == null) this.toggle();
        AdorufuMod.minecraft.player.noClip = true;
    }

    @SimpleEventHandler
    public void onMove(EntityMoveEvent e) {
        if (this.isEnabled()) {
            e.getEntity().setEntityBoundingBox(e.getEntity().getEntityBoundingBox()
                    .offset(e.getX(), e.getY(), e.getZ()));
            e.getEntity().resetPositionToBB();
        }
    }

    @SimpleEventHandler
    public void onPacketRx(ClientPacketRecieveEvent e) {
        if (this.isEnabled()) {
            if (e.getRecievedPacket() instanceof CPacketPlayer) {
                this.toggle();
            }
        }
    }

    @SimpleEventHandler
    public void onPacketTx(ClientPacketSendEvent e) {
        if (this.isEnabled()) {
            if (minecraft.world == null) this.toggle();
            if (e.getSendPacket() instanceof CPacketPlayer || e.getSendPacket() instanceof CPacketInput) {
                e.setCancelled(true);
            }
        }
    }

    @SimpleEventHandler
    public void onPushoutofblocks(ClientPushOutOfBlocksEvent e) {
        if (minecraft.world == null && this.isEnabled()) this.toggle();
        if (this.isEnabled()) e.setCancelled(true);
    }
}
