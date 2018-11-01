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

package com.sasha.adorufu.mod.feature.impl.deprecated;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.client.ClientPacketRecieveEvent;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.annotation.FeatureInfo;
import com.sasha.adorufu.mod.feature.deprecated.AdorufuModule;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.server.SPacketChat;

import java.util.concurrent.TimeUnit;

import static com.sasha.adorufu.mod.feature.impl.AutoIgnoreFeature.stripColours;

@FeatureInfo(description = "Show the estimated time left in queue in chat")
public class ModuleQueueTime extends AdorufuModule implements SimpleListener {
    public static int milestone = 5;
    public String tu = "Calculating...";
    private static long estTimePerSpot = 10000;
    private static int lastQueuePos = -1;
    private static int queueMeasurementMilestone = 0;
    private static long preMeasurementMilestoneTime = 0;

    public ModuleQueueTime() {
        super("QueueTime", AdorufuCategory.CHAT, false);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onTick() {

    }

    @SimpleEventHandler
    public void onChatRecieved(ClientPacketRecieveEvent ev) {
        if (!this.isEnabled()) return;
        if (ev.getRecievedPacket() instanceof SPacketChat) {
            SPacketChat e = (SPacketChat) ev.getRecievedPacket();
            //AdorufuMod.logMsg(false, stripColours(e.chatComponent.getUnformattedText()));
            if (stripColours(e.getChatComponent().getUnformattedText()).startsWith("Position in queue: ")) {
                int queuepos = Integer.parseInt(stripColours(e.chatComponent.getUnformattedText()).replace("Position in queue: ", ""));
                if (lastQueuePos == -1) {
                    lastQueuePos = queuepos;
                    AdorufuMod.logMsg("\247" + "6Estimated Time: " + "\247" + "r" + "\247" + "6" + "\247" + "lCalculating...");
                    return;
                }
                if (queuepos == lastQueuePos) {
                    for (int i = 0; i < queuepos; i++) {
                        if (i != 0) {
                            estTime += (10000 / i) / sameCount;
                            continue;
                        }
                        estTime += 10000 / sameCount;
                    }
                    sameCount++;
                    long estTimeWhole = estTime * queuepos;
                    String tu = convert(estTimeWhole);
                    if (estTimeWhole > 0) {
                        AdorufuMod.logMsg("\247" + "6Estimated Time: " + "\247" + "r" + "\247" + "6" + "\247" + "l" + tu);
                    } else {
                        AdorufuMod.logMsg("\247" + "6Estimated Time: " + "\247" + "r" + "\247" + "6" + "\247" + "lCalculating...");
                        estTime = 10000;
                    }
                    return;
                }
                if (sameCount >= 2) {
                    sameCount--;
                }
                for (int i = 0; i < queuepos; i++) {
                    if (i != 0) {
                        estTime -= (10000 / i) / sameCount;
                        continue;
                    }
                    estTime -= 10000;
                }
                long estTimeWhole = estTime * queuepos;
                String tu = convert(estTimeWhole);
                if (estTimeWhole > 0) {
                    AdorufuMod.logMsg("\247" + "6Estimated Time: " + "\247" + "r" + "\247" + "6" + "\247" + "l" + tu);
                } else {
                    AdorufuMod.logMsg("\247" + "6Estimated Time: " + "\247" + "r" + "\247" + "6" + "\247" + "lCalculating...");
                    estTime = 10000;
                }
                lastQueuePos = queuepos;
            }
        }
    }

    private static String convert(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02dh %02dm %02ds", hrs, min, sec);
    }

}
