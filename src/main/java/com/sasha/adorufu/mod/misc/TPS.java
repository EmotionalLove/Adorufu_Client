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

package com.sasha.adorufu.mod.misc;

import com.sasha.adorufu.mod.events.client.ClientPacketRecieveEvent;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

/**
 * Created by Sasha on 08/08/2018 at 2:38 PM
 **/
public class TPS implements SimpleListener {

    private static final float[] tickRates = new float[3];
    private static int nextIndex = 0;
    private static long timeLastTimeUpdate;

    public static TPS INSTANCE;

    public static float getTickRate()
    {
        float numTicks = 0.0F;
        float sumTickRates = 0.0F;
        for (float tickRate : tickRates) {
            if (tickRate > 0.0F) {
                sumTickRates += tickRate;
                numTicks += 1.0F;
            }
        }
        try {
            return AdorufuMath.fround(sumTickRates / numTicks, 2);
        }catch (NumberFormatException e) {
            return 20f;
        }
    }

    @SimpleEventHandler
    public void onTimeUpdate(ClientPacketRecieveEvent e) {
        if (!(e.getRecievedPacket() instanceof SPacketTimeUpdate)){
            return;
        }
        if (timeLastTimeUpdate != -1L) {
            float timeElapsed = (float)(System.currentTimeMillis() - timeLastTimeUpdate) / 1000.0F;
            tickRates[(nextIndex % tickRates.length)] = MathHelper.clamp(20.0F / timeElapsed, 0.0F, 20.0F);
            nextIndex += 1;
        }
        timeLastTimeUpdate = System.currentTimeMillis();
    }

    public void reset() {
        nextIndex = 0;
        timeLastTimeUpdate = -1L;
        Arrays.fill(tickRates, 0.0F);
    }
}
