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

package com.sasha.adorufu.mod.gui.hud;


import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.adorufu.AdorufuModuleTogglePostEvent;
import com.sasha.adorufu.mod.events.client.ClientOverlayRenderEvent;
import com.sasha.adorufu.mod.misc.AdorufuMath;
import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static com.sasha.adorufu.mod.misc.Manager.Renderable.renderableRegistry;


public class AdorufuHUD extends GuiScreen implements SimpleListener {

    public static int sHeight;
    public static int sWidth;

    // starts at x = 12
    private static ArrayList<RenderableObject> leftTop = new ArrayList<>();

    // starts at x = (sHeight - 15)
    // possible blockades: chatbox
    private static ArrayList<RenderableObject> leftBottom = new ArrayList<>();

    // starts at x = 2
    // possible blockades: potion effects
    private static ArrayList<RenderableObject> rightTop = new ArrayList<>();

    // starts at x = (sHeight - 15)
    // possible blockades: hack arraylist
    private static ArrayList<RenderableObject> rightBottom = new ArrayList<>();

    @SimpleEventHandler
    public void onOverlayRender(ClientOverlayRenderEvent e){
        renderScreen();
    }
    @SimpleEventHandler
    public void onPostToggle(AdorufuModuleTogglePostEvent e){
        if (e.getToggledModule().isRenderable()) {
            resetHUD();
        }
    }

    /* todo optimise this */
    public static void setupHUD(){
        AdorufuMod.logWarn(true, "The HUD is (re)setting up!");
        for (RenderableObject element : renderableRegistry) {
            for (AdorufuModule moduleElement : Manager.Module.moduleRegistry) {
                if (element.getName().equalsIgnoreCase(moduleElement.getModuleName())){
                    if (moduleElement.isEnabled() && moduleElement.isRenderable()){
                        ScreenCornerPos thePos=element.getPos();
                        if (element.getPos() == null){
                            thePos=element.getDefaultPos();
                        }
                        switch(thePos){
                            case LEFTTOP:
                                leftTop.add(element);
                                break;
                            case LEFTBOTTOM:
                                leftBottom.add(element);
                                break;
                            case RIGHTBOTTOM:
                                rightBottom.add(element);
                                break;
                            case RIGHTTOP:
                                rightTop.add(element);
                                break;
                        }
                        break;
                    }
                }
            }
        }
    }


    public static void renderScreen() {
        sHeight = new ScaledResolution(AdorufuMod.minecraft).getScaledHeight();
        sWidth = new ScaledResolution(AdorufuMod.minecraft).getScaledWidth();
        if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && AdorufuMod.minecraft.world.isRemote && AdorufuMod.minecraft.currentScreen == null) {
            return;
        }

        int lt_count = 0;
        for (RenderableObject o : leftTop) {
            o.renderTheObject((2) + (10 * lt_count));
            lt_count++;
        }
        int lb_count = 0;
        for (RenderableObject o : leftBottom) {
            if (AdorufuMod.minecraft.currentScreen instanceof GuiChat) {
                o.renderTheObject(((sHeight - 15) - (10 * lb_count)) - 14);
                lb_count++;
                continue;
            }
            o.renderTheObject((sHeight - 15) - (10 * lb_count));
            lb_count++;
        }
        int rt_count = 0;
        for (RenderableObject o : rightTop) {
            o.renderTheObject(((2) + (10 * rt_count)) + AdorufuMath.calculateFutureSyndromeFix());
            rt_count++;
        }
        int rb_count = 0;
        for (RenderableObject o : rightBottom) {
            if (AdorufuMod.minecraft.currentScreen instanceof GuiChat) {
                o.renderTheObject(((sHeight - 15) - (10 * rb_count)) - 14);
                rb_count++;
                continue;
            }
            o.renderTheObject((sHeight - 15) - (10 * rb_count));
            rb_count++;
        }


        if (!AdorufuModule.displayList.isEmpty()) {
            AdorufuModule.displayList.sort((m, m1) -> AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(m1.getModuleName() + m1.getSuffix()) - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(m.getModuleName() + m.getSuffix()));
        }
    }

    /*
    private static void renderServerResponding() {
        if (!AdorufuMod.minecraft.world.isRemote) {
            return;
        }
        Fonts.segoe_36.drawCenteredString("\247" + "4The server is not responding! (" + ServerResponding.responding/20 + " sec)", sWidth / 2, (sHeight / 2) - 15, 0xffffff, true);
    }*/

    public static void resetHUD() {
        leftBottom.clear();
        leftTop.clear();
        rightBottom.clear();
        rightTop.clear();
        setupHUD();
    }
}
