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
import com.sasha.adorufu.mod.events.client.ClientPacketSendEvent;
import com.sasha.adorufu.mod.feature.AbstractAdorufuTogglableFeature;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.IAdorufuTickableFeature;
import com.sasha.adorufu.mod.feature.annotation.FeatureInfo;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * Created by Sasha on 10/08/2018 at 12:47 PM
 **/
@FeatureInfo(description = "Makes your hunger last longer while sprinting/jumping/etc")
public class AntiHungerFeature extends AbstractAdorufuTogglableFeature implements SimpleListener, IAdorufuTickableFeature {
    public AntiHungerFeature() {
        super("AntiHunger"
                , AdorufuCategory.MOVEMENT
                , new AdorufuFeatureOption<>("ncp", true)
                , new AdorufuFeatureOption<>("aac", false));
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
        if (!AdorufuMod.minecraft.gameSettings.keyBindAttack.isPressed() || !AdorufuMod.minecraft.gameSettings.keyBindAttack.isKeyDown()) {
            if (this.getOption("aac")) {
                return;
            }
            AdorufuMod.minecraft.getConnection().sendPacket(new CPacketPlayer(false));
        }
    }

    @SimpleEventHandler
    public void packetSent(ClientPacketSendEvent e) {
        if (!this.isEnabled()) {
            return;
        }
        if (AdorufuMod.minecraft.player.motionY > 0.1 && this.getOption("aac")) {
            return;
        }
        if (e.getSendPacket() instanceof CPacketPlayer) {
            if (!AdorufuMod.minecraft.gameSettings.keyBindAttack.isPressed() || !AdorufuMod.minecraft.gameSettings.keyBindAttack.isKeyDown()) {
                ((CPacketPlayer) e.getSendPacket()).onGround = false;
            }
        }
    }
}
