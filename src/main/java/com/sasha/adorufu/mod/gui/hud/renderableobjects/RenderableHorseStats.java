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
import com.sasha.adorufu.mod.misc.Manager;
import net.minecraft.entity.passive.EntityHorse;

import java.io.IOException;

import static com.sasha.adorufu.mod.AdorufuMod.minecraft;
import static com.sasha.adorufu.mod.misc.AdorufuMath.dround;

public class RenderableHorseStats extends RenderableObject {
    public RenderableHorseStats() {
        super("HorseStats", ScreenCornerPos.RIGHTTOP);
        try {
            this.setPos(AdorufuMod.DATA_MANAGER.getHudPositionState(this));
        } catch (IOException e) {
            e.printStackTrace();
            this.setPos(this.getDefaultPos());
        }
    }

    @Override
    public void renderObjectLT(int yyy) {
        if (Manager.Module.getModule("HorseStats").isEnabled()) {
            String s = "\247" + "fHorse Stats: " + "\247" + "7Jump Height: " + "\247" + "f@JH" + "\247" + "7 Health: " + "\247" + "f@<3" + "\247" + "7 Max Speed: " + "\247" + "f@S";
            if (minecraft.player.isRidingHorse() && minecraft.player.getRidingEntity() instanceof EntityHorse) {
                EntityHorse e = ((EntityHorse) minecraft.player.getRidingEntity());
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s.replace("@JH", "" + dround(e.getHorseJumpStrength(), 2)).replace("@<3", "" + e.getMaxHealth() / 2).replace("@S", "" + dround((e.getAIMoveSpeed() * 20) * 3, 3)), 4, yyy, 0xffffff);
                return;
            }
            AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fHorse stats: " + "\247" + "4None.", 4, yyy, 0xffffff);
        }
    }
    @Override
    public void renderObjectLB(int yyy) {
        if (Manager.Module.getModule("HorseStats").isEnabled()) {
            String s = "\247" + "fHorse Stats: " + "\247" + "7Jump Height: " + "\247" + "f@JH" + "\247" + "7 Health: " + "\247" + "f@<3" + "\247" + "7 Max Speed: " + "\247" + "f@S";
            if (minecraft.player.isRidingHorse() && minecraft.player.getRidingEntity() instanceof EntityHorse) {
                EntityHorse e = ((EntityHorse) minecraft.player.getRidingEntity());
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s.replace("@JH", "" + dround(e.getHorseJumpStrength(), 2)).replace("@<3", "" + e.getMaxHealth() / 2).replace("@S", "" + dround((e.getAIMoveSpeed() * 20) * 3, 3)), 4, yyy, 0xffffff);
                return;
            }
            AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fHorse stats: " + "\247" + "4None.", 4, yyy, 0xffffff);
        }
    }
    @Override
    public void renderObjectRT(int yyy) {
        if (Manager.Module.getModule("HorseStats").isEnabled()) {
            String s = "\247" + "fHorse Stats: " + "\247" + "7Jump Height: " + "\247" + "f@JH" + "\247" + "7 Health: " + "\247" + "f@<3" + "\247" + "7 Max Speed: " + "\247" + "f@S";
            if (minecraft.player.isRidingHorse() && minecraft.player.getRidingEntity() instanceof EntityHorse) {
                EntityHorse e = ((EntityHorse) minecraft.player.getRidingEntity());
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s.replace("@JH", "" + dround(e.getHorseJumpStrength(), 2)).replace("@<3", "" + e.getMaxHealth() / 2).replace("@S", "" + dround((e.getAIMoveSpeed() * 20) * 3, 3)), (AdorufuHUD.sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(s.replace("@JH", "" + dround(e.getHorseJumpStrength(), 2)).replace("@<3", "" + e.getMaxHealth() / 2).replace("@S", "" + dround((e.getAIMoveSpeed() * 20) * 3, 3))) - 2), yyy, 0xffffff);
                return;
            }
            AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fHorse stats: " + "\247" + "4None.", (AdorufuHUD.sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth("Horse stats: None.")) - 2, yyy, 0xffffff);
        }
    }
    @Override
    public void renderObjectRB(int yyy) {
        if (Manager.Module.getModule("HorseStats").isEnabled()) {
            String s = "\247" + "fHorse Stats: " + "\247" + "7Jump Height: " + "\247" + "f@JH" + "\247" + "7 Health: " + "\247" + "f@<3" + "\247" + "7 Max Speed: " + "\247" + "f@S";
            if (minecraft.player.isRidingHorse() && minecraft.player.getRidingEntity() instanceof EntityHorse) {
                EntityHorse e = ((EntityHorse) minecraft.player.getRidingEntity());
                AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow(s.replace("@JH", "" + dround(e.getHorseJumpStrength(), 2)).replace("@<3", "" + e.getMaxHealth() / 2).replace("@S", "" + dround((e.getAIMoveSpeed() * 20) * 3, 3)), (AdorufuHUD.sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(s.replace("@JH", "" + dround(e.getHorseJumpStrength(), 2)).replace("@<3", "" + e.getMaxHealth() / 2).replace("@S", "" + dround((e.getAIMoveSpeed() * 20) * 3, 3))) - 2), yyy, 0xffffff);
                return;
            }
            AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("\247" + "fHorse stats: " + "\247" + "4None.", (AdorufuHUD.sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth("Horse stats: None.")) - 2, yyy, 0xffffff);
        }
    }
}
