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
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.deprecated.AdorufuModule;
import com.sasha.adorufu.mod.feature.impl.FeaturelistRenderableFeature;
import com.sasha.adorufu.mod.gui.hud.RenderableObject;
import com.sasha.adorufu.mod.gui.hud.ScreenCornerPos;
import com.sasha.adorufu.mod.misc.Manager;

import java.util.List;
import java.util.stream.Collectors;

import static com.sasha.adorufu.mod.gui.hud.AdorufuHUD.sWidth;

public class RenderableFeatureList extends RenderableObject {
    public RenderableFeatureList() {
        super("Featurelist", ScreenCornerPos.RIGHTBOTTOM);

    }

    @Override
    public void renderObjectLT(int yyy) {
        if (Manager.Feature.isFeatureEnabled(FeaturelistRenderableFeature.class)) {
            int count = 0;
            for (AdorufuModule module : getValidList()) {
                if (module.getSuffix().equals("")) {
                    AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("" + module.getModuleNameColoured(), 4, (yyy) + (10 * count), 0xffffff);
                    count++;
                }
                else {
                    AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("" + module.getModuleNameColoured() + module.getSuffix(), 4, (yyy) - (10 * count), 0xffffff);
                    count++;
                }
            }
        }
    }
    @Override
    public void renderObjectLB(int yyy) {
        if (Manager.Feature.isFeatureEnabled(FeaturelistRenderableFeature.class)) {
            // TODO
        }
    }
    @Override
    public void renderObjectRT(int yyy) {
        if (Manager.Feature.isFeatureEnabled(FeaturelistRenderableFeature.class)) {
            int count = 0;
            for (AdorufuModule module : getValidList()) {
                if (module.isEnabled() && module.getSuffix().equals("")) {
                    AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("" + module.getModuleNameColoured(), sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(module.getModuleName()) - 2, (yyy) + (10 * count), 0xffffff);
                    count++;
                }
                else if (module.isEnabled()) {
                    AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("" + module.getModuleNameColoured() + module.getSuffix(), sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(module.getModuleName() + module.getSuffix()) - 2, (yyy) + (10 * count), 0xffffff);
                    count++;
                }
            }
        }
    }
    @Override
    public void renderObjectRB(int yyy) {
        if (Manager.Feature.isFeatureEnabled(FeaturelistRenderableFeature.class)) {
            int count = 0;
            for (AdorufuModule module : getValidList()) {
                if (module.isEnabled() && module.getSuffix().equals("")) {
                    AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("" + module.getModuleNameColoured(), sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(module.getModuleName()) - 2, (yyy) - (10 * count), 0xffffff);
                    count++;
                }
                else if (module.isEnabled()) {
                    AdorufuMod.FONT_MANAGER.segoe_36.drawStringWithShadow("" + module.getModuleNameColoured() + module.getSuffix(), sWidth - AdorufuMod.FONT_MANAGER.segoe_36.getStringWidth(module.getModuleName() + module.getSuffix()) - 2, (yyy) - (10 * count), 0xffffff);
                    count++;
                }
            }
        }
    }
    private List<AdorufuModule> getValidList() {
        return Manager.Module.moduleRegistry
                .stream()
                .filter(mod -> mod.isEnabled() && mod.getModuleCategory() != AdorufuCategory.GUI)
                .collect(Collectors.toList());
    }
}
