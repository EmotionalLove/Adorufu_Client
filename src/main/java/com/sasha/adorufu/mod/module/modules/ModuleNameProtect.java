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
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;

@ModuleInfo(description = "Hides your name on-screen")
public class ModuleNameProtect extends AdorufuModule implements SimpleListener {

    public ModuleNameProtect() {
        super("NameRedact", AdorufuCategory.RENDER, false);
    }

    @SimpleEventHandler
    public void onChatRx(ClientPacketRecieveEvent e) {
        if (!this.isEnabled()) return;
        if (e.getRecievedPacket() instanceof SPacketChat) {
            SPacketChat chat = e.getRecievedPacket();
            if (!chat.getChatComponent().getUnformattedComponentText().contains(AdorufuMod.minecraft.player.getName()))
                return;
            String str = chat.chatComponent.getFormattedText().replace(AdorufuMod.minecraft.player.getName(), "[redacted]");
            chat.chatComponent = new TextComponentString(str);
        }
    }
}
