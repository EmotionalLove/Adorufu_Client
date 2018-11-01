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

package com.sasha.adorufu.mod.feature;

import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOptionBehaviour;
import com.sasha.simplesettings.annotation.SerialiseSuper;

@SerialiseSuper
public abstract class AbstractAdorufuTogglableFeature
        extends AbstractAdorufuFeature implements IAdorufuTogglableFeature {

    private boolean enabled;
    private int keycode;

    public AbstractAdorufuTogglableFeature(String name, AdorufuCategory category) {
        super(name, category);
    }

    @SafeVarargs public AbstractAdorufuTogglableFeature(String name, AdorufuCategory category, AdorufuFeatureOption<Boolean>... featureOption) {
        super(name, category, featureOption);
    }

    @SafeVarargs public AbstractAdorufuTogglableFeature(String name, AdorufuCategory category, AdorufuFeatureOptionBehaviour behaviour, AdorufuFeatureOption<Boolean>... featureOption) {
        super(name, category, behaviour, featureOption);
    }

    @Override
    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    @Override
    public int getKeycode() {
        return keycode;
    }

    @Override
    public final boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void toggleState() {
        this.enabled = !enabled;
    }

    @Override
    public void setState(boolean state, boolean execute) {
        this.enabled = state;
        if (execute) {
            if (state) this.onEnable();
            else this.onDisable();
        }
    }
}
