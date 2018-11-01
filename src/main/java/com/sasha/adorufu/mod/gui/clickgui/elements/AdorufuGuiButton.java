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

package com.sasha.adorufu.mod.gui.clickgui.elements;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.gui.clickgui.AdorufuClickGUI;
import com.sasha.adorufu.mod.gui.clickgui.helper.FeatureToggler;
import com.sasha.adorufu.mod.gui.clickgui.helper.IToggler;
import com.sasha.adorufu.mod.gui.clickgui.helper.OptionToggler;
import com.sasha.adorufu.mod.misc.AdorufuMath;

import java.util.ArrayList;
import java.util.List;

public class AdorufuGuiButton implements IAdorufuGuiElement {

    private String title;
    private float highlightColourR = 255f;
    private float highlightColourG = 255f;
    private float highlightColourB = 255f;
    private float highlightColourA = 75f;
    private int x;
    private int y;
    private int width;
    private int height;
    private IToggler buttonAction;

    public AdorufuGuiButton(String title, int x, int y, int width, int height, IToggler buttonAction) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonAction = buttonAction;
    }

    @Override
    public void drawElement(int x, int y) {
        AdorufuMod.FONT_MANAGER.segoe_36.drawCenteredString(this.title, (this.x + (this.width / 2)), this.y + (((this.y + this.height) - this.y) / 4), 0xffffff, true);
        if ((x >= this.x && x <= (this.x + this.width))
                &&
                y >= this.y && y <= (this.y + this.height)) {
            drawHighlight();
            return;
        }
        if (buttonAction.isTrue()) {
            drawEnabled();
        }
    }

    private void drawHighlight() {
        AdorufuMath.drawRect(this.x, this.y,
                this.x + this.width,
                this.y + this.height,
                highlightColourR, highlightColourG, highlightColourB, highlightColourA);
    }

    private void drawEnabled() {
        AdorufuMath.drawRect(this.x, this.y,
                this.x + this.width,
                this.y + this.height,
                145f, 255f, 158f, 50f);
    }

    @Override
    public boolean onMouseEngage(int x, int y, int b) {
        if (b == 0) {
            if ((x >= this.x && x <= (this.x + this.width))
                    &&
                    y >= this.y && y <= (this.y + this.height)) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseRelease(int x, int y, int b) {
        if (b == 0) {
            if ((x >= this.x && x <= (this.x + this.width))
                    &&
                    y >= this.y && y <= (this.y + this.height)) {
                this.buttonAction.run();
                return false;
            }
        }
        if (b == 1) {
            if ((x >= this.x && x <= (this.x + this.width))
                    &&
                    y >= this.y && y <= (this.y + this.height)) {
                if (buttonAction instanceof FeatureToggler && ((FeatureToggler) buttonAction).getFeature().hasOptions()) {
                    List<IAdorufuGuiElement> proposedOptionButtons = new ArrayList<>();
                    ((FeatureToggler) buttonAction).getFeature().getOptions().forEach((name, bool) -> {
                        proposedOptionButtons.add(new AdorufuGuiButton(name, 0, 0, 100, 12,
                                new OptionToggler(buttonAction.getMod(), name)));
                    });
                    new Thread(() -> {
                        try {
                            AdorufuClickGUI.elementList.add(
                                    new AdorufuGuiWindow(this.x + (this.width + 10),
                                            y, AdorufuClickGUI.calcListLength(proposedOptionButtons.size(), 12), 100,
                                            86f, 0f, 80f, 255f,
                                            buttonAction.getMod().getModuleName(),
                                            proposedOptionButtons)
                            );
                        } finally {
                            AdorufuClickGUI.lock.unlock();
                        }
                    }).start();
                }
            }
        }
        return false;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }
}
