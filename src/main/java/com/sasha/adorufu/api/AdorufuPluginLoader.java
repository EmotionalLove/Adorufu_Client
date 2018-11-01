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

package com.sasha.adorufu.api;

import com.sasha.adorufu.api.exception.AdorufuPluginLoaderException;
import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.misc.YMLParser;
import com.sasha.eventsys.SimpleListener;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Sasha at 9:22 AM on 9/17/2018
 */
public class AdorufuPluginLoader {

    private static final String DIR_NAME = "adorufu_plugins";
    private static LinkedHashMap<File /*jar*/, File /*.yml*/> theRawPlugins = new LinkedHashMap<>();
    private static List<AdorufuPlugin> loadedPlugins = new ArrayList<>();

    /**
     * Find plugin in the plugins folder
     * @return an array of .yml and .jar files in the plugins folder.
     */
    public List<File> findPlugins() throws IOException {
        File dir = new File("adorufu_plugins");
        if (!dir.exists()) {
            dir.mkdir();
            return new ArrayList<>();
        }
        if (!dir.isDirectory()) {
            dir.delete();
            dir.mkdir();
            return new ArrayList<>();
        }
        File[] allFiles = dir.listFiles();
        if (allFiles == null) {
            return new ArrayList<>(); // no files?
        }
        List<File> theFiles = new ArrayList<>();
        Arrays.stream(allFiles).filter(fname -> fname.getName().endsWith(".yml") ||
                fname.getName().endsWith(".jar")).forEach(theFiles::add);
        return theFiles;
    }

    public int preparePlugins(List<File> theFiles) {
        List<File> jarFiles = new ArrayList<>();
        List<File> ymlFiles = new ArrayList<>();
        // seperate the files.
        for (File f : theFiles) {
            if (f.getName().endsWith(".yml")) {
                ymlFiles.add(f);
                continue;
            }
            if (f.getName().endsWith(".jar")) {
                jarFiles.add(f);
                continue;
            }
        }
        int i = 0;
        for (File jarFile : jarFiles) {
            for (File ymlFile : ymlFiles) {
                if (ymlFile.getName().split("\\.")[0].equals(jarFile.getName().split("\\.")[0])) {
                    theRawPlugins.put(jarFile, ymlFile);
                    i++;
                    continue;
                }
            }
        }
        return i;
    }

    public void loadPlugins() {
        theRawPlugins.forEach((jar, yml)->{
            YMLParser parser = new YMLParser(yml);
            // validation checks
            if (!parser.exists("main")) throw new AdorufuPluginLoaderException("\"main\" key does not exist in " + yml.getName() + "!");
            if (!parser.exists("name")) throw new AdorufuPluginLoaderException("\"name\" key does not exist in " + yml.getName() + "!");
            if (!parser.exists("description")) throw new AdorufuPluginLoaderException("\"description\" key does not exist in " + yml.getName() + "!");
            if (!parser.exists("author")) throw new AdorufuPluginLoaderException("\"author\" key does not exist in " + yml.getName() + "!");
            // the class loader
            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, this.getClass().getClassLoader());
                Class mainClass = Class.forName(parser.getString("main"), true, classLoader);
                // verify that the main class extends AdorufuPlugin
                if (mainClass.getSuperclass() == null || mainClass.getSuperclass() != AdorufuPlugin.class) throw new AdorufuPluginLoaderException("The main class does not extend AdorufuPlugin.class!");
                // initialise an instance of the plugin
                AdorufuPlugin plugin = (AdorufuPlugin) mainClass.newInstance();
                // set the plugin's information.
                plugin.setInfo(parser.getString("name"), parser.getString("description"), parser.getString("author"));
                // add to list of loaded plugins
                AdorufuMod.logMsg(true, plugin.getPluginName() + " by " + plugin.getPluginAuthor() + " was loaded and enabled.");
                loadedPlugins.add(plugin);
                // run the onEnable() method
                plugin.onEnable();
                // register if SimpleListener is implemented
                if ((mainClass.getInterfaces() != null && mainClass.getInterfaces().length > 0) && mainClass.getInterfaces()[0] == SimpleListener.class) {
                    AdorufuMod.EVENT_MANAGER.registerListener((SimpleListener) plugin);
                }
            } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }
    public static List<AdorufuPlugin> getLoadedPlugins() {
        return loadedPlugins;
    }
}
