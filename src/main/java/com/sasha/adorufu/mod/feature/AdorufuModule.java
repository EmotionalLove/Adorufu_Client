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

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.adorufu.AdorufuModuleTogglePostEvent;
import com.sasha.adorufu.mod.events.adorufu.AdorufuModuleTogglePreEvent;
import com.sasha.adorufu.mod.exception.AdorufuModuleOptionNotFoundException;
import com.sasha.adorufu.mod.gui.hud.AdorufuHUD;
import com.sasha.adorufu.mod.misc.ModuleState;
import com.sasha.simplesettings.SettingFlag;
import com.sasha.simplesettings.annotation.SerialiseSuper;
import com.sasha.simplesettings.annotation.Transiant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sasha on 08/08/2018 at 8:30 AM
 * This class is based off of Xdolf 3.x's Module class, however this is redone to make use of the Event system.
 * Fun fact: Xdolf 3.x's Event system was absolutely horrible, it was easier to just _not use it_...
 **/
@Deprecated
@SerialiseSuper
public abstract class AdorufuModule implements SettingFlag {

    @Transiant private String moduleName;
    @Transiant private String moduleNameColoured;
    @Transiant private AdorufuCategory moduleCategory;
    private boolean isEnabled = false;
    @Transiant private String suffix = "";
    private int keyBind;
    @Transiant private boolean isRenderable = false;
    @Transiant private boolean hasOptions = false;
    @Transiant private boolean optionMode = false;
    @Transiant private LinkedHashMap<String, Boolean> moduleOptions;

    public static ArrayList<AdorufuModule> displayList = new ArrayList<>(); // used for the hud

    public AdorufuModule(String moduleName, AdorufuCategory moduleCategory, boolean isRenderable) {
        this.moduleName = moduleName;
        this.moduleCategory = moduleCategory;
        String c;
        if (moduleCategory == AdorufuCategory.COMBAT) {
            c = "4";
        } else if (moduleCategory == AdorufuCategory.CHAT) {
            c = "3";
        } else if (moduleCategory == AdorufuCategory.GUI) {
            c = "7";
        } else if (moduleCategory == AdorufuCategory.MISC) {
            c = "b";
        } else if (moduleCategory == AdorufuCategory.MOVEMENT) {
            c = "6";
        } else if (moduleCategory == AdorufuCategory.RENDER) {
            c = "d";
        } else {
            c = "8";
        }
        this.moduleNameColoured = "\247" + c + moduleName;
        this.isRenderable = isRenderable;
        this.isEnabled = false;
    }


    public AdorufuModule(String moduleName, AdorufuCategory moduleCategory, boolean isRenderable, boolean hasOptions) {
        this.hasOptions = hasOptions;
        if (hasOptions) this.moduleOptions = new LinkedHashMap<>();
        this.moduleName = moduleName;
        this.moduleCategory = moduleCategory;
        String c;
        if (moduleCategory == AdorufuCategory.COMBAT) {
            c = "4";
        } else if (moduleCategory == AdorufuCategory.CHAT) {
            c = "3";
        } else if (moduleCategory == AdorufuCategory.GUI) {
            c = "7";
        } else if (moduleCategory == AdorufuCategory.MISC) {
            c = "b";
        } else if (moduleCategory == AdorufuCategory.MOVEMENT) {
            c = "6";
        } else if (moduleCategory == AdorufuCategory.RENDER) {
            c = "d";
        } else {
            c = "8";
        }
        this.moduleNameColoured = "\247" + c + moduleName;
        this.isRenderable = isRenderable;
        this.isEnabled = false;
    }

    public AdorufuModule(String moduleName, AdorufuCategory moduleCategory, boolean isRenderable, boolean hasOptions, boolean useMode) {
        this.hasOptions = hasOptions;
        this.optionMode = useMode;
        if (hasOptions) this.moduleOptions = new LinkedHashMap<>();
        this.moduleName = moduleName;
        this.moduleCategory = moduleCategory;
        String c;
        if (moduleCategory == AdorufuCategory.COMBAT) {
            c = "4";
        } else if (moduleCategory == AdorufuCategory.CHAT) {
            c = "3";
        } else if (moduleCategory == AdorufuCategory.GUI) {
            c = "7";
        } else if (moduleCategory == AdorufuCategory.MISC) {
            c = "b";
        } else if (moduleCategory == AdorufuCategory.MOVEMENT) {
            c = "6";
        } else if (moduleCategory == AdorufuCategory.RENDER) {
            c = "d";
        } else {
            c = "8";
        }
        this.moduleNameColoured = "\247" + c + moduleName;
        this.isRenderable = isRenderable;
        this.isEnabled = false;
    }

    ///getters


    public boolean useModeSelection() {
        return optionMode;
    }

    /**
     * gets feature name
     *
     * @return String
     */
    public String getModuleName() {
        return moduleName;
    }

    public String getDescription(Class<?> clazz) {
        ModuleInfo d = clazz.getAnnotation(ModuleInfo.class);
        if (d == null) {
            return "No description provided.";
        }
        return d.description();
    }

    public boolean isPostExec(Class<?> clazz) {
        PostToggleExec d = clazz.getAnnotation(PostToggleExec.class);
        return d != null;
    }

    public String getModuleNameColoured() {
        return moduleNameColoured;
    }

    public String getSuffix() {
        return suffix;
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
        if (!this.moduleOptions.containsKey(name.toLowerCase()))
            throw new AdorufuModuleOptionNotFoundException("The option" + name.toLowerCase() + "doesn't exist!");
        boolean toggle = !this.moduleOptions.get(name.toLowerCase());
        this.moduleOptions.put(name.toLowerCase(), toggle);
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
        if (!this.moduleOptions.containsKey(name.toLowerCase()))
            throw new AdorufuModuleOptionNotFoundException("The option" + name.toLowerCase() + "doesn't exist!");
        boolean toggle = !this.moduleOptions.get(name.toLowerCase());
        this.moduleOptions.put(name.toLowerCase(), toggle);
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
        if (!this.moduleOptions.containsKey(name.toLowerCase()))
            throw new AdorufuModuleOptionNotFoundException("The option" + name.toLowerCase() + "doesn't exist!");
        return this.moduleOptions.get(name.toLowerCase());
    }

    public int getKeyBind() {
        return keyBind;
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
        return isEnabled;
    }

    public boolean hasForcefulAnnotation(Class<?> clazz) {
        return clazz.getAnnotation(ForcefulEnable.class) != null;
    }

    /**
     * Gets the feature's category
     *
     * @return AdorufuCategory enum
     */
    public AdorufuCategory getModuleCategory() {
        return moduleCategory;
    }

    /**
     * whether the feature is used to toggle a HUD element
     *
     * @return bool
     */
    public boolean isRenderable() {
        return isRenderable;
    }

    public boolean hasOptions() {
        return hasOptions;
    }

    ///voids

    /**
     * toggles the feature and runs all the needed disable/enable fhnctions
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
     * forces the feature to become active or inactive
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


    public abstract void onEnable();

    public void init() {
    }

    public abstract void onDisable();

    public void onRender() {
    } // called a lot more than 20x per getValue

    public abstract void onTick(); // callee 20x per getValue
}
