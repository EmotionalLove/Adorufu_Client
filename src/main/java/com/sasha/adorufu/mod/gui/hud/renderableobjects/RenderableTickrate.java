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

package com.sasha.adorufu.mod.gui.hud.renderableobjects;


import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.gui.hud.AdorufuHUD;
import com.sasha.adorufu.mod.gui.hud.RenderableObject;
import com.sasha.adorufu.mod.gui.hud.ScreenCornerPos;
import com.sasha.adorufu.mod.misc.TPS;

import java.io.IOException;

public class RenderableTickrate extends RenderableObject {
    public RenderableTickrate() {
        super("TPS", ScreenCornerPos.RIGHTTOP);
        try {
            this.setPos(AdorufuMod.DATA_MANAGER.getHudPositionState(this));
        } catch (IOException e) {
            e.printStackTrace();
            this.setPos(this.getDefaultPos());
        }
    }

    @Override
    public void renderObjectLT(int yyy) {
        AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fTPS" + "\247" + "7: " + formatTickrate(TPS.getTickRate()), 4, yyy, 0xffffff);
    }
    @Override
    public void renderObjectLB(int yyy) {
        AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fTPS" + "\247" + "7: " + formatTickrate(TPS.getTickRate()), 4, yyy, 0xffffff);
    }
    @Override
    public void renderObjectRT(int yyy) {
        String s = "\247" + "fTPS" + "\247" + "7: " + formatTickrate(TPS.getTickRate());
        AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s, (AdorufuHUD.sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(s) - 2), yyy, 0xffffff);
    }
    @Override
    public void renderObjectRB(int yyy) {
        String s = "\247" + "fTPS" + "\247" + "7: " + formatTickrate(TPS.getTickRate());
        AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s, (AdorufuHUD.sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(s) - 2), yyy, 0xffffff);
    }
    private static String formatTickrate(float tps) {
        if (tps > 15) {
            return "\247" + "a" + tps;
        }
        else if (tps > 10) {
            return "\247" + "e" + tps;
        }
        else if (tps > 5) {
            return "\247" + "c" + tps;
        }
        else if (tps >= 0) {
            return "\247" + "4" + tps;
        }
        return "\247" + "b" + tps;
    }
}
