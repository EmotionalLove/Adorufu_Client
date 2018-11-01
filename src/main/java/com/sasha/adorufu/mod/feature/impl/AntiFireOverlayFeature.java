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

package com.sasha.adorufu.mod.feature.impl;

import com.sasha.adorufu.mod.events.client.ClientRenderFireOverlayEvent;
import com.sasha.adorufu.mod.feature.AbstractAdorufuTogglableFeature;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.annotation.FeatureInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;

@FeatureInfo(description = "Hide the annoying 1st person fire overlay")
public class AntiFireOverlayFeature extends AbstractAdorufuTogglableFeature implements SimpleListener {

    public AntiFireOverlayFeature() {
        super("AntiFireOverlay", AdorufuCategory.RENDER);
    }

    @SimpleEventHandler
    public void onFireRender(ClientRenderFireOverlayEvent e) {
        if (this.isEnabled()) e.setCancelled(true);
    }
}
