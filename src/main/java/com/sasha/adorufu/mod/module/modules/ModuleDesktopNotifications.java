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
import com.sasha.adorufu.mod.events.client.ClientScreenChangedEvent;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.network.play.server.SPacketChat;
import org.lwjgl.opengl.Display;

import java.awt.*;

/**
 * Created by Sasha at 3:30 PM on 9/9/2018
 */

@ModuleInfo(description = "Uses balloon notifications to notify you of events when the game is minimised")
public class ModuleDesktopNotifications extends AdorufuModule implements SimpleListener {

    public ModuleDesktopNotifications() {
        super("DesktopNotifications", AdorufuCategory.MISC, false, true);
        this.addOption("Chat mentions", true);
        this.addOption("Server kick", false);
    }

    @Override
    public void onTick() {
        this.setSuffix(this.getModuleOptionsMap());
    }

    @SimpleEventHandler
    public void onScreenChanged(ClientScreenChangedEvent e) {
        if (e.getScreen() instanceof GuiDisconnected) {
            if (Display.isActive() || !this.getOption("Server kick")) return;
            AdorufuMod.TRAY_MANAGER.trayIcon.displayMessage("Disconnected", ((GuiDisconnected) e.getScreen()).message.getUnformattedText().replaceAll("§.", ""), TrayIcon.MessageType.WARNING);
        }
    }

    @SimpleEventHandler
    public void onChatRx(ClientPacketRecieveEvent e) {
        if (!this.isEnabled() || this.getOption("Chat mentions")) return;
        if (e.getRecievedPacket() instanceof SPacketChat) {
            SPacketChat pck = e.getRecievedPacket();
            if (!Display.isActive() && pck.getChatComponent().getUnformattedText().contains(AdorufuMod.minecraft.player.getName())) {
                AdorufuMod.TRAY_MANAGER.trayIcon.displayMessage("Mentioned in Chat",
                        pck.getChatComponent().getUnformattedText(),
                        TrayIcon.MessageType.WARNING);
            }
        }
    }
}
