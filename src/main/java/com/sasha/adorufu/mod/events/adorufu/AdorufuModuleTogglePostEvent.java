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

package com.sasha.adorufu.mod.events.adorufu;

import com.sasha.adorufu.mod.misc.ModuleState;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.eventsys.SimpleEvent;

/**
 * Created by Sasha on 08/08/2018 at 9:18 AM
 **/
public class AdorufuModuleTogglePostEvent extends SimpleEvent {
    private AdorufuModule toggledModule;
    private ModuleState toggleState;

    public AdorufuModuleTogglePostEvent(AdorufuModule toggledModule, ModuleState toggleState){
        this.toggledModule= toggledModule;
        this.toggleState= toggleState;
    }

    public ModuleState getToggleState() {
        return toggleState;
    }

    public AdorufuModule getToggledModule() {
        return toggledModule;
    }
}

