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

package com.sasha.adorufu.mod.misc;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.command.commands.PathCommand;
import com.sasha.adorufu.mod.events.adorufu.AdorufuModuleTogglePostEvent;
import com.sasha.adorufu.mod.events.adorufu.AdorufuModuleTogglePreEvent;
import com.sasha.adorufu.mod.feature.IAdorufuFeature;
import com.sasha.adorufu.mod.feature.IAdorufuRenderableFeature;
import com.sasha.adorufu.mod.feature.IAdorufuTickableFeature;
import com.sasha.adorufu.mod.feature.IAdorufuTogglableFeature;
import com.sasha.adorufu.mod.feature.annotation.ManualListener;
import com.sasha.adorufu.mod.feature.deprecated.AdorufuModule;
import com.sasha.adorufu.mod.gui.hud.RenderableObject;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.sasha.adorufu.mod.AdorufuMod.DATA_MANAGER;

/**
 * Created by Sasha at 9:09 AM on 9/17/2018
 */
public class Manager {

    public static class Feature implements SimpleListener {

        public static List<IAdorufuFeature> featureRegistry = new ArrayList<>();

        public static void registerFeature(IAdorufuFeature feature) {
            featureRegistry.add(feature);
            feature.onLoad();
        }

        /**
         * Tick all Tickable features.
         * If a feature is togglable, it will only tick if it's enabled.
         */
        public static void tickFeatures() {
            featureRegistry
                    .stream()
                    .filter(feature -> feature instanceof IAdorufuTickableFeature)
                    .forEach(tickableFeature -> {
                        if (tickableFeature instanceof IAdorufuTogglableFeature
                                &&
                                ((IAdorufuTogglableFeature) tickableFeature).isEnabled()) {
                            ((IAdorufuTickableFeature) tickableFeature).onTick();
                            return;
                        }
                        ((IAdorufuTickableFeature) tickableFeature).onTick();
                    });
        }
        public static void renderFeatures() {
            featureRegistry
                    .stream()
                    .filter(feature -> feature instanceof IAdorufuRenderableFeature)
                    .forEach(renderableFeature -> {
                        if (renderableFeature instanceof IAdorufuTogglableFeature
                        &&
                        ((IAdorufuTogglableFeature) renderableFeature).isEnabled()) {
                            ((IAdorufuRenderableFeature) renderableFeature).onRender();
                            return;
                        }
                        ((IAdorufuRenderableFeature) renderableFeature).onRender();
                    });
        }

        public static IAdorufuFeature findFeature(Class<? extends IAdorufuFeature> featureClass) {
            for (IAdorufuFeature feature : featureRegistry) {
                if(feature.getClass() == featureClass) return feature;
            }
            return null;
        }

        public static boolean isFeatureEnabled(Class<? extends IAdorufuTogglableFeature> featureClass) {
            for (IAdorufuFeature feature : featureRegistry) {
                if (!(feature instanceof IAdorufuTogglableFeature)) continue;
                if (feature.getClass() == featureClass && ((IAdorufuTogglableFeature) feature).isEnabled()) return true;
            }
            return false;
        }

    }

    public static class Renderable implements SimpleListener {
        public static ArrayList<RenderableObject> renderableRegistry = new ArrayList<>();

        public static void register(RenderableObject robj) {
            renderableRegistry.add(robj);
            AdorufuMod.SETTING_CLASSES.add(robj);
        }
    }

    public static class Module implements SimpleListener {

        public static ArrayList<AdorufuModule> moduleRegistry = new ArrayList<>();

        @SimpleEventHandler
        public void onPreToggle(AdorufuModuleTogglePreEvent e) {
            if (e.getToggledModule().hasForcefulAnnotation(e.getToggledModule().getClass()) && e.getToggleState() == ModuleState.DISABLE) {
                e.setCancelled(true);
                return;
            }
            if (e.getToggleState() == ModuleState.ENABLE && !e.getToggledModule().isPostExec(e.getToggledModule().getClass())) {
                e.getToggledModule().onEnable();
                return;
            }
            if (e.getToggledModule().isPostExec(e.getToggledModule().getClass())) return;
            e.getToggledModule().onDisable();
        }

        @SimpleEventHandler
        public void onModPostToggle(AdorufuModuleTogglePostEvent e) {
            try {
                DATA_MANAGER.saveModuleStates(true);
            } catch (IOException e1) {
                AdorufuMod.logErr(false, "Couldn't save feature state. " + e1.getMessage());
                e1.printStackTrace();
            }
            if (e.getToggleState() == ModuleState.ENABLE) {
                if (e.getToggledModule().isPostExec(e.getToggledModule().getClass())) e.getToggledModule().onEnable();

                if (!e.getToggledModule().isRenderable()) {
                    AdorufuModule.displayList.add(e.getToggledModule());
                    return;
                }
                return;
            }
            if (e.getToggledModule().isPostExec(e.getToggledModule().getClass())) e.getToggledModule().onDisable();
            if (!e.getToggledModule().isRenderable()) {
                AdorufuModule.displayList.remove(e.getToggledModule());
                return;
            }
        }

        /**
         * This will automatically register listeners to the Event Manager if they implement SimpleListener
         */
        public static void register(AdorufuModule mod) {
            moduleRegistry.add(mod);
            AdorufuMod.SETTING_CLASSES.add(mod);
            if (mod.getClass().getInterfaces().length != 0) {
                if (mod.getClass().getInterfaces()[0] == SimpleListener.class) {
                    if (mod.getClass().getAnnotation(ManualListener.class) != null) {
                        return;
                    }
                    AdorufuMod.logMsg(true, mod.getModuleName() + " is listening for events.");
                    AdorufuMod.EVENT_MANAGER.registerListener((SimpleListener) mod);
                }
            }
            mod.init();
        }

        @Deprecated
        public static void loadBindsAndStates() {
            Module.moduleRegistry.forEach(mod -> {
                try {
                    if (DATA_MANAGER.getSavedModuleState(mod.getModuleName())) {
                        mod.forceState(ModuleState.ENABLE, false, true);
                    }
                    if (mod.hasForcefulAnnotation(mod.getClass())) {
                        mod.forceState(ModuleState.ENABLE, false, true);
                    }
                    mod.setKeyBind(DATA_MANAGER.getSavedKeybind(mod));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public static AdorufuModule getModule(String key) {
            for (AdorufuModule m : moduleRegistry) {
                if (m.getModuleName().equalsIgnoreCase(key)) return m;
            }
            return null;
        }

        public static AdorufuModule getModule(Class<? extends AdorufuModule> clazz) {
            for (AdorufuModule m : moduleRegistry) {
                if (m.getClass() == clazz) return m;
            }
            return null;
        }

        public static void tickModules() {
            long l = System.currentTimeMillis();
            moduleRegistry.forEach(mod -> {
                try {
                    mod.onTick();
                } catch (Exception e) {
                    AdorufuMod.logErr(false, "A severe uncaught exception occurred inside of a feature onTick() function");
                    mod.forceState(ModuleState.DISABLE, false, true);
                    StringWriter sw = new StringWriter();
                    PrintWriter w = new PrintWriter(sw);
                    e.printStackTrace(w);
                    AdorufuMod.logMsg("\247c" + sw.toString().replace("\r\n", "\n"));
                }
            });
            PathCommand.tick();
            AdorufuMod.PERFORMANCE_ANAL.recordNewNormalTime((int) (System.currentTimeMillis() - l));
        }

        public static void renderModules() {
            long l = System.currentTimeMillis();
            moduleRegistry.forEach(AdorufuModule::onRender);
            AdorufuMod.PERFORMANCE_ANAL.recordNewRenderTime((int) (System.currentTimeMillis() - l));
        }

    }

    public static Set<Class> getClassesInPackage(String pckge, Class sub) {
        Reflections reflections = new Reflections(pckge);
        return reflections.getSubTypesOf(sub);
    }

}
