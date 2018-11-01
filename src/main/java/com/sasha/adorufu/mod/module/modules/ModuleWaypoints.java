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
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.adorufu.mod.waypoint.Waypoint;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

@ModuleInfo(description = "Display tracers to enabled waypoints.")
public class ModuleWaypoints extends AdorufuModule {

    public static int i = 0;

    public ModuleWaypoints() {
        super("Waypoints", AdorufuCategory.RENDER, false);
    }

    @Override
    public void onDisable() {
        i = 0;
    }

    @Override
    public void onRender() {
        if (this.isEnabled()) {
            i = 0;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glLineWidth(1);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glPushMatrix();
            GL11.glTranslated(-AdorufuMod.minecraft.getRenderManager().renderPosX, -AdorufuMod.minecraft.getRenderManager().renderPosY, -AdorufuMod.minecraft.getRenderManager().renderPosZ);

            // set start position
            Vec3d start = getClientLookVec().add(0, AdorufuMod.minecraft.player.getEyeHeight(), 0).add(AdorufuMod.minecraft.getRenderManager().renderPosX, AdorufuMod.minecraft.getRenderManager().renderPosY, AdorufuMod.minecraft.getRenderManager().renderPosZ);

            GL11.glBegin(GL11.GL_LINES);
            for (Waypoint waypoint : AdorufuMod.WAYPOINT_MANAGER.getWaypoints()) {
                if (!waypoint.isDoesRender()) {
                    continue;
                }
                i++;
                Vec3d end = (new Vec3d(waypoint.getCoords()[0], waypoint.getCoords()[1], waypoint.getCoords()[2]).scale(1));

                // set color
                GL11.glColor3f(0.0f, 1.5f, 1.5f);

                // draw line
                GL11.glVertex3d(start.x, start.y, start.z);
                GL11.glVertex3d(end.x, end.y, end.z);
            }
            GL11.glEnd();

            GL11.glPopMatrix();

            // GL resets
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }
    }

    private static Vec3d getClientLookVec() {
        double f = Math.cos(-AdorufuMod.minecraft.player.rotationYaw * 0.017453292F - (float) Math.PI);
        double f1 = Math.sin(-AdorufuMod.minecraft.player.rotationYaw * 0.017453292F - (float) Math.PI);
        double f2 = -Math.cos(-AdorufuMod.minecraft.player.rotationPitch * 0.017453292F);
        double f3 = Math.sin(-AdorufuMod.minecraft.player.rotationPitch * 0.017453292F);
        return new Vec3d(f1 * f2, f3, f * f2);
    }
}
