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

import java.util.concurrent.TimeUnit;

import static com.sasha.adorufu.mod.module.modules.ModuleAutoIgnore.stripColours;


@ModuleInfo(description = "Show the estimated time left in queue in chat")
public class ModuleQueueTime extends AdorufuModule implements SimpleListener {

    public static int milestone = 5;
    public String tu = "";
    private static long estTimePerSpot = 10000;
    private static int lastQueuePos = -1;
    private static int queueMeasurementMilestone = 0;
    private static long preMeasurementMilestoneTime = 0;

    public ModuleQueueTime() {
        super("QueueTime", AdorufuCategory.CHAT, false);
    }

    @SimpleEventHandler
    public void onChatRecieved(ClientPacketRecieveEvent ev) {
        if (!this.isEnabled()) return;
        if (ev.getRecievedPacket() instanceof SPacketChat) {
            SPacketChat e = ev.getRecievedPacket();
            if (stripColours(e.getChatComponent().getUnformattedText()).startsWith("Position in queue: ")) {
                int queuepos = Integer.parseInt(stripColours(e.chatComponent.getUnformattedText()).replace("Position in queue: ", ""));
                //This runs when the module is initiated
                if (lastQueuePos == -1) {
                    lastQueuePos = queuepos;
                    AdorufuMod.logMsg("Wait patiently for " + milestone + " spots in the queue to pass to ensure (semi)accurate measurement.");
                    queueMeasurementMilestone = queuepos - milestone;
                    preMeasurementMilestoneTime = System.nanoTime();
                    return;
                }
                //this should run recursively
                if (queueMeasurementMilestone >= queuepos) {
                    AdorufuMod.logMsg("Queue measurement milestone reached. Re-Calculating...");
                    estTimePerSpot = (System.nanoTime() - preMeasurementMilestoneTime) / milestone;
                    //Resetting...
                    preMeasurementMilestoneTime = System.nanoTime();
                    queueMeasurementMilestone = queuepos - milestone;

                    long estTimeWhole = estTimePerSpot * queuepos;
                    tu = convert(estTimeWhole);
                    AdorufuMod.logMsg("\247" + "6Estimated Time: " + "\247" + "r" + "\247" + "6" + "\247" + "l" + tu);
                    return;
                }
            }
        }
    }

    private String convert(long nanoSeconds) {
        int hrs = (int) TimeUnit.NANOSECONDS.toHours(nanoSeconds) % 24;
        int min = (int) TimeUnit.NANOSECONDS.toMinutes(nanoSeconds) % 60;
        int sec = (int) TimeUnit.NANOSECONDS.toSeconds(nanoSeconds) % 60;
        return String.format("%02dh %02dm %02ds", hrs, min, sec);
    }
}


