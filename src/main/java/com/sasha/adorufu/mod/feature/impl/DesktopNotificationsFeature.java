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
import com.sasha.adorufu.mod.events.client.ClientPacketRecieveEvent;
import com.sasha.adorufu.mod.events.client.ClientScreenChangedEvent;
import com.sasha.adorufu.mod.feature.AbstractAdorufuTogglableFeature;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.IAdorufuTickableFeature;
import com.sasha.adorufu.mod.feature.annotation.FeatureInfo;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.network.play.server.SPacketChat;
import org.lwjgl.opengl.Display;

import java.awt.*;

/**
 * Created by Sasha at 3:30 PM on 9/9/2018
 */

@FeatureInfo(description = "Uses balloon notifications to notify you of events when the game is minimised")
public class DesktopNotificationsFeature extends AbstractAdorufuTogglableFeature implements SimpleListener,
        IAdorufuTickableFeature {

    public DesktopNotificationsFeature() {
        super("DesktopNotifications", AdorufuCategory.MISC,
                new AdorufuFeatureOption<>("Chat mentions", true),
                new AdorufuFeatureOption<>("Server kick", false));
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
    }

    @SimpleEventHandler
    public void onScreenChanged(ClientScreenChangedEvent e) {
        if (e.getScreen() instanceof GuiDisconnected) {
            if (Display.isActive() || !this.getFormattableOptionsMap().get("Server kick")) return;
            AdorufuMod.TRAY_MANAGER.trayIcon.displayMessage("Disconnected", ((GuiDisconnected) e.getScreen()).message.getUnformattedText().replaceAll("§.", ""), TrayIcon.MessageType.WARNING);
        }
    }
    @SimpleEventHandler
    public void onChatRx(ClientPacketRecieveEvent e) {
        if (!this.isEnabled() || this.getFormattableOptionsMap().get("Chat mentions")) return;
        if (e.getRecievedPacket() instanceof SPacketChat) {
            SPacketChat pck = (SPacketChat) e.getRecievedPacket();
            if (!Display.isActive() && pck.getChatComponent().getUnformattedText().contains(AdorufuMod.minecraft.player.getName())) {
                AdorufuMod.TRAY_MANAGER.trayIcon.displayMessage("Mentioned in Chat",
                        pck.getChatComponent().getUnformattedText(),
                        TrayIcon.MessageType.WARNING);

            }
        }

    }
}
