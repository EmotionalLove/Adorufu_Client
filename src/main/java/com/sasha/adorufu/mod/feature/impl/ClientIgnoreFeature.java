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
import com.sasha.adorufu.mod.feature.AbstractAdorufuTogglableFeature;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.IAdorufuTickableFeature;
import com.sasha.adorufu.mod.feature.annotation.FeatureInfo;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.server.SPacketChat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@FeatureInfo(description = "Ignore players on the client side.")
public class ClientIgnoreFeature extends AbstractAdorufuTogglableFeature implements SimpleListener,
        IAdorufuTickableFeature {
    public static List<String> ignorelist = new ArrayList<>();
    public static List<String> filterList = new ArrayList<>();
    private static List<String> dms = new ArrayList<>();

    public ClientIgnoreFeature() {
        super("ClientIgnore", AdorufuCategory.CHAT,
                new AdorufuFeatureOption<>("Players", true));
        AdorufuMod.scheduler.schedule(() -> ignorelist = AdorufuMod.DATA_MANAGER.loadIgnorelist(), 0, TimeUnit.MILLISECONDS);
        AdorufuMod.scheduler.schedule(() -> filterList = AdorufuMod.DATA_MANAGER.loadFilterList(), 0, TimeUnit.MILLISECONDS);
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
        this.setSuffix(ignorelist.size() + ":" + filterList.size());
    }

    @SimpleEventHandler
    public void onPckRx(ClientPacketRecieveEvent e) {
        if (this.isEnabled() && e.getRecievedPacket() instanceof SPacketChat) {
            String msg = AutoIgnoreFeature.stripColours(((SPacketChat) e.getRecievedPacket()).getChatComponent().getUnformattedText());
            if (this.getFormattableOptionsMap().get("Players")) {
                for (String s : ignorelist) {
                    if (msg.toLowerCase().startsWith("<" + s.toLowerCase() + ">")) {
                        e.setCancelled(true);
                    }
                    if (msg.matches(s + " whispers: .*$")) {
                        e.setCancelled(true);
                        if (!dms.contains(s)) {
                            AdorufuMod.minecraft.player.sendChatMessage("/msg " + s + " [Adorufu] Sorry, your message could not be delivered to " + AdorufuMod.minecraft.player.getName() + ", because they have you ignored.");
                            dms.add(s);
                        }
                    }
                }
            }
            if (this.getFormattableOptionsMap().get("Words")) {
                String[] msgs = msg.split(" ");
                for (String s : filterList) {
                    for (String msg1 : msgs) {
                        msg1 = msg1.replace(".", "");
                        msg1 = msg1.replace(",", "");
                        msg1 = msg1.replace("\"", "");
                        msg1 = msg1.replace("'", "");
                        if (msg1.toLowerCase().startsWith(s.toLowerCase()) ||
                        msg1.toLowerCase().endsWith(s.toLowerCase())) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}
