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

package com.sasha.adorufu.mod.module;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.adorufu.AdorufuModuleTogglePostEvent;
import com.sasha.adorufu.mod.events.adorufu.AdorufuModuleTogglePreEvent;
import com.sasha.adorufu.mod.exception.AdorufuModuleOptionNotFoundException;
import com.sasha.adorufu.mod.gui.hud.AdorufuHUD;
import com.sasha.adorufu.mod.misc.ModuleState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sasha on 08/08/2018 at 8:30 AM
 * This class is based off of Xdolf 3.x's Module class, however this is redone to make use of the Event system.
 * Fun fact: Xdolf 3.x's Event system was absolutely horrible, it was easier to just <i>not use it</i>...
 **/
public abstract class AdorufuModule {

    private String moduleName, moduleNameColoured;
    private AdorufuCategory moduleCategory;
    private boolean isEnabled = false;
    private String suffix = "";
    private String colour;
    private int keyBind;
    private boolean isRenderable = false;
    private boolean hasOptions = false;
    private boolean optionMode = false;
    private LinkedHashMap<String, Boolean> moduleOptions;

    public static ArrayList<AdorufuModule> displayList = new ArrayList<>(); // used for the hud

    public AdorufuModule(String moduleName, AdorufuCategory moduleCategory, boolean isRenderable) {
        this(moduleName, moduleCategory, isRenderable, false);
    }

    public AdorufuModule(String moduleName, AdorufuCategory moduleCategory, boolean isRenderable, boolean hasOptions) {
        this(moduleName, moduleCategory, isRenderable, hasOptions, false);
    }

    public AdorufuModule(String moduleName, AdorufuCategory moduleCategory, boolean isRenderable, boolean hasOptions, boolean useMode) {
        this.optionMode = useMode;
        this.moduleName = moduleName;
        this.moduleCategory = moduleCategory;
        this.moduleNameColoured = "\247" + moduleCategory.colorCode + moduleName;
        this.isRenderable = isRenderable;
        this.isEnabled = false;
        this.moduleOptions = (this.hasOptions = hasOptions) ? new LinkedHashMap<>() : null;
    }

    public boolean useModeSelection() {
        return optionMode;
    }

    /**
     * gets module name
     *
     * @return String
     */
    public String getModuleName() {
        return moduleName;
    }

    public String getDescription() {
        ModuleInfo d = this.getClass().getAnnotation(ModuleInfo.class);
        return d == null ? "No description provided." : d.description();
    }

    public boolean isPostExec() {
        return this.getClass().isAnnotationPresent(PostToggleExec.class);
    }

    public String getModuleNameColoured() {
        return this.moduleNameColoured;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String s) {
        this.suffix = " \2478[\2477" + s + "\2478]";
    }

    public void setSuffix(String[] s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            if (i == 0) {
                b.append(s[i]);
                continue;
            }
            b.append(", ").append(s[i]);
        }
        this.suffix = " \2478[\2477" + b.toString() + "\2478]";
    }

    public void setSuffix(LinkedHashMap<String, Boolean> boolMap) {
        StringBuilder b = new StringBuilder();
        AtomicInteger counter = new AtomicInteger();
        boolMap.entrySet().stream().filter(Map.Entry::getValue).forEach(strBool -> {
            if (counter.get() == 0) {
                b.append(strBool.getKey());
                counter.getAndIncrement();
                return;
            }
            b.append(", ").append(strBool.getKey());
        });
        if (counter.get() == 0) {
            b.append("\247cNone");
        }
        this.suffix = " \2478[\2477" + b.toString() + "\2478]";
    }

    public LinkedHashMap<String, Boolean> getModuleOptionsMap() {
        return moduleOptions;
    }

    public void addOption(String name, boolean def) {
        this.moduleOptions.put(name.toLowerCase(), AdorufuMod.DATA_MANAGER.getSavedModuleOption(this.getModuleName(), name, def));
    }

    public void toggleOption(String name) {
        String key = name.toLowerCase();
        requireOptionExists(key);
        boolean toggle = !this.moduleOptions.get(key);
        this.moduleOptions.put(key, toggle);
        try {
            AdorufuMod.DATA_MANAGER.saveModuleOption(this.getModuleName(), name, toggle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used if you only want to allow one option at a time
     */
    public void toggleOptionMode(String name) {
        String key = name.toLowerCase();
        requireOptionExists(key);
        boolean toggle = !this.moduleOptions.get(key);
        this.moduleOptions.put(key, toggle);
        try {
            AdorufuMod.DATA_MANAGER.saveModuleOption(this.getModuleName(), name, toggle);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.getModuleOptionsMap().forEach((str, bool) -> {
            if (bool) {
                this.toggleOption(str);
            }
        });
    }

    public boolean getOption(String name) {
        String key = name.toLowerCase();
        requireOptionExists(key);
        return this.moduleOptions.get(key);
    }

    private void requireOptionExists(String key) {
        if (!this.moduleOptions.containsKey(key)) {
            throw new AdorufuModuleOptionNotFoundException("The option" + key + "doesn't exist!");
        }
    }

    public int getKeyBind() {
        return this.keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public void removeSuffix() {
        this.suffix = "";
    }

    /**
     * Gets whether its toggled or not
     *
     * @return bool
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean hasForcefulAnnotation(Class<?> clazz) {
        return clazz.getAnnotation(ForcefulEnable.class) != null;
    }

    /**
     * Gets the module's category
     *
     * @return AdorufuCategory enum
     */
    public AdorufuCategory getModuleCategory() {
        return this.moduleCategory;
    }

    /**
     * whether the module is used to toggle a HUD element
     *
     * @return bool
     */
    public boolean isRenderable() {
        return this.isRenderable;
    }

    public boolean hasOptions() {
        return this.hasOptions;
    }

    /**
     * toggles the module and runs all the needed disable/enable fhnctions
     * invokes an AdorufuModuleTogglePreEvent
     */
    public void toggle() {
        AdorufuModuleTogglePreEvent event = new AdorufuModuleTogglePreEvent(this, (isEnabled ? ModuleState.DISABLE : ModuleState.ENABLE));
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            AdorufuMod.logWarn(true, "Module \"" + this.getModuleName() + "\" toggle was cancelled!");
            return;
        }
        this.isEnabled = !this.isEnabled;
        AdorufuModuleTogglePostEvent post = new AdorufuModuleTogglePostEvent(this, (isEnabled ? ModuleState.ENABLE : ModuleState.DISABLE));
        AdorufuMod.EVENT_MANAGER.invokeEvent(post);
    }

    /**
     * forces the module to become active or inactive
     *
     * @param state                enable or disable
     * @param executeOnStateMethod whether you want to execute onDisable() or onEnable()
     */
    public void forceState(ModuleState state, boolean executeOnStateMethod, boolean resetHud) {
        if (state == ModuleState.ENABLE) {
            this.isEnabled = true;
            if (executeOnStateMethod) {
                this.onEnable();
            }
            if (!this.isRenderable()) {
                AdorufuModule.displayList.add(this);
                return;
            }
            if (!resetHud) {
                return;
            }
            AdorufuHUD.resetHUD();
        } else {
            this.isEnabled = false;
            if (executeOnStateMethod) {
                this.onDisable();
            }
            if (!this.isRenderable()) {
                AdorufuModule.displayList.remove(this);
                return;
            }
            if (!resetHud) {
                return;
            }
            AdorufuHUD.resetHUD();
        }
    }

    public void onEnable() {}
    public void init() {}
    public void onDisable() {}
    public void onRender() {} // called a lot more than 20x per getValue
    public void onTick() {} // callee 20x per getValue
}
