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
import com.sasha.adorufu.mod.gui.hud.RenderableObject;
import com.sasha.adorufu.mod.gui.hud.ScreenCornerPos;
import com.sasha.adorufu.mod.misc.AdorufuMath;
import com.sasha.adorufu.mod.misc.Manager;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

import static com.sasha.adorufu.mod.AdorufuMod.minecraft;
import static com.sasha.adorufu.mod.misc.AdorufuMath.dround;

public class RenderableCoordinates extends RenderableObject {
    public RenderableCoordinates() {
        super("Coordinates", ScreenCornerPos.LEFTBOTTOM);
        try {
            this.setPos(AdorufuMod.DATA_MANAGER.getHudPositionState(this));
        } catch (IOException e) {
            e.printStackTrace();
            this.setPos(this.getDefaultPos());
        }
    }

    @Override
    public void renderObjectLT(int yyy) {
        if (Manager.Module.getModule("Coordinates").isEnabled()) {
            double xx = dround(minecraft.player.posX, 3);
            double y = dround(minecraft.player.posY, 3);
            double z = dround(minecraft.player.posZ, 3);
            if (minecraft.player.dimension == 0 || minecraft.player.dimension == 1) {
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fX " + "\247" + "7" + xx + " (" + dround(xx/8, 3) + ") " + "\247" + "fY " + "\247" + "7" + y + " " + "\247" + "fZ " + "\247" + "7" + z + " (" + dround(z/8, 3) + ")" + attachDirection(), 4, yyy, 0xffffff);
            }
            if (minecraft.player.dimension == -1) {
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fX " + "\247" + "7" + xx + " (" + dround(xx*8, 3) + ") " + "\247" + "fY " + "\247" + "7" + y + " " + "\247" + "fZ " + "\247" + "7" + z + " (" + dround(z*8, 3) + ")" + attachDirection(), 4, yyy, 0xffffff);
            }
        }
    }
    @Override
    public void renderObjectLB(int yyy) {
        if (Manager.Module.getModule("Coordinates").isEnabled()) {
            double xx = dround(minecraft.player.posX, 3);
            double y = dround(minecraft.player.posY, 3);
            double z = dround(minecraft.player.posZ, 3);
            if (minecraft.player.dimension == 0 || minecraft.player.dimension == 1) {
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fX " + "\247" + "7" + xx + " (" + dround(xx/8, 3) + ") " + "\247" + "fY " + "\247" + "7" + y + " " + "\247" + "fZ " + "\247" + "7" + z + " (" + dround(z/8, 3) + ")" + attachDirection(), 4, yyy, 0xffffff);
            }
            if (minecraft.player.dimension == -1) {
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fX " + "\247" + "7" + xx + " (" + dround(xx*8, 3) + ") " + "\247" + "fY " + "\247" + "7" + y + " " + "\247" + "fZ " + "\247" + "7" + z + " (" + dround(z*8, 3) + ")" + attachDirection(), 4, yyy, 0xffffff);
            }
        }
    }
    @Override
    public void renderObjectRT(int yyy) {
        if (Manager.Module.getModule("Coordinates").isEnabled()) {
            double xx = dround(minecraft.player.posX, 3);
            double y = dround(minecraft.player.posY, 3);
            double z = dround(minecraft.player.posZ, 3);
            ScaledResolution sr = new ScaledResolution(minecraft);
            int width = sr.getScaledWidth();
            if (minecraft.player.dimension == 0 || minecraft.player.dimension == 1) {
                String ss = "\247" + "fX " + "\247" + "7" + xx + " (" + dround(xx/8, 3) + ") " + "\247" + "fY " + "\247" + "7" + y + " " + "\247" + "fZ " + "\247" + "7" + z + " (" + dround(z/8, 3) + ")" + attachDirection();
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(ss, width - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(ss) - 2, yyy, 0xffffff);
            }
            if (minecraft.player.dimension == -1) {
                String s = "\247" + "fX " + "\247" + "7" + xx + " (" + dround(xx*8, 3) + ") " + "\247" + "fY " + "\247" + "7" + y + " " + "\247" + "fZ " + "\247" + "7" + z + " (" + dround(z*8, 3) + ")" + attachDirection();
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s, width - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(s) - 2, yyy, 0xffffff);
            }
        }
    }
    @Override
    public void renderObjectRB(int yyy) {
        if (Manager.Module.getModule("Coordinates").isEnabled()) {
            double xx = dround(minecraft.player.posX, 3);
            double y = dround(minecraft.player.posY, 3);
            double z = dround(minecraft.player.posZ, 3);
            ScaledResolution sr = new ScaledResolution(minecraft);
            int width = sr.getScaledWidth();
            if (minecraft.player.dimension == 0 || minecraft.player.dimension == 1) {
                String ss = "\247" + "fX " + "\247" + "7" + xx + " (" + dround(xx/8, 3) + ") " + "\247" + "fY " + "\247" + "7" + y + " " + "\247" + "fZ " + "\247" + "7" + z + " (" + dround(z/8, 3) + ")" + attachDirection();
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(ss, width - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(ss) - 2, yyy, 0xffffff);
            }
            if (minecraft.player.dimension == -1) {
                String s = "\247" + "fX " + "\247" + "7" + xx + " (" + dround(xx*8, 3) + ") " + "\247" + "fY " + "\247" + "7" + y + " " + "\247" + "fZ " + "\247" + "7" + z + " (" + dround(z*8, 3) + ")" + attachDirection();
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s, width - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(s) - 2, yyy, 0xffffff);
            }
        }
    }
    private static String attachDirection() {
        return " " + "\247" + "f[" + AdorufuMath.getStringDirection(AdorufuMath.getCardinalDirection(minecraft.player.rotationYaw)) + "]";

    }
}
