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

package com.sasha.adorufu.mod;

import com.sasha.adorufu.mod.exception.AdorufuNoSuchElementInDataFileException;
import com.sasha.adorufu.mod.friend.Friend;
import com.sasha.adorufu.mod.gui.clickgui.elements.AdorufuGuiWindow;
import com.sasha.adorufu.mod.gui.clickgui.legacy.elements.AdorufuWindow;
import com.sasha.adorufu.mod.gui.hud.RenderableObject;
import com.sasha.adorufu.mod.gui.hud.ScreenCornerPos;
import com.sasha.adorufu.mod.misc.PlayerIdentity;
import com.sasha.adorufu.mod.misc.YMLParser;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.modules.ModuleJoinLeaveMessages;
import com.sasha.adorufu.mod.waypoint.Waypoint;
import net.minecraft.block.Block;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sasha.adorufu.mod.AdorufuMod.logMsg;
import static com.sasha.adorufu.mod.AdorufuMod.logWarn;
import static com.sasha.adorufu.mod.misc.Manager.Module.moduleRegistry;

/**
 * Created by Sasha on 08/08/2018 at 12:47 PM
 **/
// i love making simple tasks overly complex :3:3:3:3
// maybe im just a complex kind of girl
public class AdorufuDataManager {
    private final Lock threadLock = new ReentrantLock();
    private final Lock waypointLock = new ReentrantLock();
    private final Lock identityLock = new ReentrantLock();
    private final String dataFileName = "AdorufuData.yml";

    public LinkedHashMap<String, PlayerIdentity> identityCacheMap = new LinkedHashMap<>();

    AdorufuDataManager() {}

    /**
     * Used for saving a float,double,string,int,long... anything like that, really.
     *
     * @param path    the path in the config to save it to. (recommended is Adorufu.something)
     * @param varName the name of the variable in the cfg file
     * @param obj     the thing to save.
     */
    public synchronized void saveSomeGenericValue(String path, String varName, Object obj) throws IOException {
        threadLock.lock();
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            YMLParser parser = new YMLParser(file);
            parser.set(path + "." + varName, obj);
            parser.save();
        } finally {
            threadLock.unlock();
        }
    }

    /**
     * You'll need to cast the final value to what you saved it as.
     */
    public synchronized Object loadSomeGenericValue(String path, String varName, Object defaultVal) throws IOException {
        threadLock.lock();
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            YMLParser parser = new YMLParser(file);
            if (!parser.exists(path + "." + varName)) {
                if (defaultVal != null) return defaultVal;
                throw new AdorufuNoSuchElementInDataFileException("\"" + path + "\" does not exist in " + dataFileName);
            }
            if (defaultVal != null) return parser.get(path + "." + varName, defaultVal);
            return parser.get(path + "." + varName);
        } finally {
            threadLock.unlock();
        }
    }

    public synchronized void savePlayerIdentity(PlayerIdentity id, boolean delete) throws IOException {
        logMsg(true, "Saving identity " + id.getStringUuid() + "...");
        identityLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File dir = new File("playeridentitycache");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File f = new File("playeridentitycache/" + id.getStringUuid() + ".mcid");
            if (f.exists() || delete) {
                f.delete();
                if (delete) return;
            }
            FileOutputStream fstream = new FileOutputStream(f);
            ObjectOutputStream stream = new ObjectOutputStream(fstream);
            stream.writeObject(id);
            stream.close();
            fstream.close();
        } finally {
            identityLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public PlayerIdentity getPlayerIdentity(String UUID) {
        if (identityCacheMap.containsKey(UUID)) {
            return identityCacheMap.get(UUID);
        }
        return new PlayerIdentity(UUID);
    }

    public synchronized void saveWaypoint(Waypoint waypoint, boolean delete) throws IOException {
        logMsg(true, "Saving waypoint " + waypoint.getName() + "...");
        waypointLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File dir = new File("waypoints");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File f = new File("waypoints/" + waypoint.getName() + ".wypt");
            if (f.exists() || delete) {
                f.delete();
                if (delete) return;
            }
            FileOutputStream fstream = new FileOutputStream(f);
            ObjectOutputStream stream = new ObjectOutputStream(fstream);
            stream.writeObject(waypoint);
            stream.close();
            fstream.close();
        } finally {
            waypointLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void loadPlayerIdentities() throws IOException {
        logMsg(true, "Loading id's...");
        identityLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File("playeridentitycache");
            if (!f.exists()) {
                logMsg(true, "No id's to load, skipping.");
                return; // nothing to load :p
            }
            if (!f.isDirectory()) {
                f.delete();
                return; // nothing to load :p
            }
            List<File> files = Arrays.asList(f.listFiles());
            files.stream().filter(file -> file.getName().endsWith(".mcid")).forEach(wyptFile -> {
                try {
                    FileInputStream inputStream = new FileInputStream(wyptFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Object wayptObj = objectInputStream.readObject();
                    if (wayptObj instanceof PlayerIdentity) {
                        identityCacheMap.put(((PlayerIdentity) wayptObj).getStringUuid(), (PlayerIdentity) wayptObj);
                        objectInputStream.close();
                        inputStream.close();
                        return;
                    }
                    objectInputStream.close();
                    inputStream.close();
                    logWarn(true, wyptFile.getName() + " was not a id, skipping.");
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace(); //dont rly care
                    return;
                }
            });
        } finally {
            identityLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized List<Waypoint> loadWaypoints() throws IOException {
        logMsg(true, "Loading waypoints...");
        waypointLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File("waypoints");
            if (!f.exists()) {
                logMsg(true, "No waypoints to load, skipping.");
                return new ArrayList<>(); // nothing to load :p
            }
            if (!f.isDirectory()) {
                f.delete();
                return new ArrayList<>(); // nothing to load :p
            }
            List<Waypoint> theWaypoints = new ArrayList<>();
            List<File> files = Arrays.asList(f.listFiles());
            files.stream().filter(file -> file.getName().endsWith(".wypt")).forEach(wyptFile -> {
                try {
                    FileInputStream inputStream = new FileInputStream(wyptFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Object wayptObj = objectInputStream.readObject();
                    if (wayptObj instanceof Waypoint) {
                        theWaypoints.add((Waypoint) wayptObj);
                        objectInputStream.close();
                        inputStream.close();
                        return;
                    }
                    objectInputStream.close();
                    inputStream.close();
                    logWarn(true, wyptFile.getName() + " was not a waypoint, skipping.");
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace(); //dont rly care
                    return;
                }
            });
            return theWaypoints;
        } finally {
            waypointLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveIgnorelist(List<String> ignores) throws IOException {
        logMsg(true, "Saving ignorelist...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            parser.set("Adorufu.ignorelist", ignores);
            parser.save();
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveFilterList(List<String> wordlist) throws IOException {
        logMsg(true, "Saving filterlist...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            parser.set("Adorufu.wordfilter", wordlist);
            parser.save();
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveGreeterMsgs(List<List<String>> greets) throws IOException {
        logMsg(true, "Saving greeter messages...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            parser.set("Adorufu.greeter.join", greets.get(0));
            parser.set("Adorufu.greeter.leave", greets.get(1));
            parser.save();
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized List<String> loadIgnorelist() throws IOException {
        logMsg(true, "Getting ignorelist...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            return parser.getStringList("Adorufu.ignorelist");
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized List<String> loadFilterList() throws IOException {
        logMsg(true, "Getting worldfilter...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            return parser.getStringList("Adorufu.wordfilter");
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized List<List<String>> loadGreets() throws IOException {
        logMsg(true, "Getting greets...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
                List<String> defaultJoin = new ArrayList<>();
                List<String> defaultLeave = new ArrayList<>();
                defaultJoin.add("Hi, [player]");
                defaultLeave.add("Bye, [player]");
                ModuleJoinLeaveMessages.defaultLoaded = true;
                List<List<String>> defaults = new ArrayList<>();
                defaults.add(defaultJoin);
                defaults.add(defaultLeave);
                threadLock.unlock();
                saveGreeterMsgs(defaults);
                threadLock.lock();
            }
            YMLParser parser = new YMLParser(f);
            if (!parser.exists("Adorufu.greeter.join") && !parser.exists("Adorufu.greeter.leave")) {
                List<String> defaultJoin = new ArrayList<>();
                List<String> defaultLeave = new ArrayList<>();
                defaultJoin.add("Hi, [player]");
                defaultLeave.add("Bye, [player]");
                ModuleJoinLeaveMessages.defaultLoaded = true;
                List<List<String>> defaults = new ArrayList<>();
                defaults.add(defaultJoin);
                defaults.add(defaultLeave);
                threadLock.unlock();
                saveGreeterMsgs(defaults);
                threadLock.lock();
            }
            List<String> joins = parser.getStringList("Adorufu.greeter.join");
            List<String> leaves = parser.getStringList("Adorufu.greeter.leave");
            List<List<String>> theReturn = new ArrayList<>();
            theReturn.add(joins);
            theReturn.add(leaves);
            return theReturn;
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized boolean getDRPEnabled() throws IOException {
        logMsg(true, "Checking to see if DRP should be enabled.");
        threadLock.lock();
        logWarn(true, "Thread locked.");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                return true; // assuming true
            }
            YMLParser parser = new YMLParser(f);
            if (!parser.exists("Adorufu.discordpresence.enabled")) {
                parser.set("Adorufu.discordpresence.enabled", true);
                return true;
            }
            return parser.getBoolean("Adorufu.discordpresence.enabled");
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread unlocked.");
        }
    }

    public synchronized void saveXrayBlocks(List<Block> blocks) throws IOException {
        logMsg(true, "Saving Xray blocks...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        List<Integer> intBlocks = new ArrayList<>();
        blocks.forEach(b -> intBlocks.add(Block.getIdFromBlock(b))); // In 3.x I used the Localised name, but the localised name changes when you change the game's language
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            parser.set("Adorufu.xray.blocks", intBlocks);
            parser.save();
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized List<Block> getXrayBlocks() throws IOException {
        logMsg(true, "Saving Xray blocks...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        List<Integer> intBlocks;
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?) Returning empty ArrayList...");
                return new ArrayList<>();
            }
            YMLParser parser = new YMLParser(f);
            if (!parser.exists("Adorufu.xray.blocks")) {
                return new ArrayList<>();
            }
            List<Block> realBlocks = new ArrayList<>();
            intBlocks = parser.getIntegerList("Adorufu.xray.blocks");
            intBlocks.forEach(id -> realBlocks.add(Block.getBlockById(id)));
            return realBlocks;
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveGuiElementPos(AdorufuGuiWindow element) throws IOException {
        logMsg(true, "Saving \"" + element.getTitle() + "\"'s current GUI position...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist smh");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            parser.set("Adorufu.newclickgui." + element.getTitle().toLowerCase() + ".x", element.getX());
            parser.set("Adorufu.newclickgui." + element.getTitle().toLowerCase() + ".y", element.getY());
            parser.save();
        } finally {
            threadLock.unlock();
        }
    }

    public synchronized int[] loadGuiElementPos(String elementTitle) throws IOException {
        logMsg(true, "Loading \"" + elementTitle + "\"'s saved GUI position...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist smh");
                f.createNewFile();
                return new int[]{100, 100};
            }
            YMLParser parser = new YMLParser(f);
            int[] coords = new int[2];
            coords[0] = parser.getInt("Adorufu.newclickgui." + elementTitle.toLowerCase() + ".x"
                    , 100);
            coords[1] = parser.getInt("Adorufu.newclickgui." + elementTitle.toLowerCase() + ".y"
                    , 100);
            return coords;
        } finally {
            threadLock.unlock();
        }
    }

    @Deprecated
    public synchronized int[] getLegacySavedGuiPos(AdorufuWindow window) {
        logMsg(true, "Loading \"" + window.getTitle() + "\"'s saved GUI position...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                return new int[]{0, 0};
            }
            YMLParser parser = new YMLParser(f);
            if (!parser.exists("Adorufu.gui.clickgui." + window.getTitle() + ".x") && (!parser.exists("Adorufu.gui.clickgui." + window.getTitle() + ".y"))) {
                parser.set("Adorufu.gui.clickgui." + window.getTitle() + ".x", window.dragX);
                parser.set("Adorufu.gui.clickgui." + window.getTitle() + ".y", window.dragY);
                parser.save();
                return new int[]{0, 0};
            }
            int x = parser.getInt("Adorufu.gui.clickgui." + window.getTitle() + ".x", 0);
            int y = parser.getInt("Adorufu.gui.clickgui." + window.getTitle() + ".y", 0);
            return new int[]{x, y};
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    @Deprecated
    public synchronized void saveLegacyGuiPos(AdorufuWindow window) throws IOException {
        logMsg(true, "Saving \"" + window.getTitle() + "\"'s GUI position...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
            }
            YMLParser parser = new YMLParser(f);
            parser.set("Adorufu.gui.clickgui." + window.getTitle() + ".x", window.dragX);
            parser.set("Adorufu.gui.clickgui." + window.getTitle() + ".y", window.dragY);
            parser.save();
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized ScreenCornerPos getHudPositionState(RenderableObject robj) throws IOException {
        logMsg(true, "Loading \"" + robj.getName() + "\"'s saved HUD position...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File f = new File(dataFileName);
            if (!f.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                f.createNewFile();
                logMsg(true, "Creating new data file with default HUD poaitions.");
                YMLParser parser = new YMLParser(f);
                parser.set("Adorufu.gui.hud." + robj.getName().toLowerCase(), RenderableObject.getPosStr(robj.getDefaultPos()));
                parser.save();
                return robj.getDefaultPos();
            }
            YMLParser parser = new YMLParser(f);
            if (!parser.exists("Adorufu.gui.hud." + robj.getName().toLowerCase())) {
                parser.set("Adorufu.gui.hud." + robj.getName().toLowerCase(), RenderableObject.getPosStr(robj.getDefaultPos()));
                parser.save();
                return robj.getDefaultPos();
            }
            return RenderableObject.getPosEnum(parser.getString("Adorufu.gui.hud." + robj.getName().toLowerCase()));
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized int getSavedKeybind(AdorufuModule module) {
        logMsg(true, "Getting module keybinde...");
        threadLock.lock(); // Don't allow other threads to modify this file until this operation is done.
        logWarn(true, "Thread locking engaged!");
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                return 0;
            }
            YMLParser parser = new YMLParser(file);
            if (!parser.exists("Adorufu.modules." + module.getModuleName().toLowerCase() + ".bind")) {
                parser.set("Adorufu.modules." + module.getModuleName() + ".bind", 0);
                parser.save();
                return 0;
            }
            return parser.getInt("Adorufu.modules." + module.getModuleName().toLowerCase() + ".bind", 0);
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveModuleBind(AdorufuModule module) throws IOException {
        logMsg(true, "Saving module keybinde...");
        threadLock.lock(); // Don't allow other threads to modify this file until this operation is done.
        logWarn(true, "Thread locking engaged!");
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                file.createNewFile();
            }
            YMLParser parser = new YMLParser(file);
            parser.set("Adorufu.modules." + module.getModuleName().toLowerCase() + ".bind", module.getKeyBind());
            parser.save();
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveModuleStates(boolean fileExists) throws IOException {
        logMsg(true, "Updating module savestates...");
        threadLock.lock(); // Don't allow other threads to modify this file until this operation is done.
        logWarn(true, "Thread locking engaged!");
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            YMLParser parser = new YMLParser(file);
            moduleRegistry.forEach(mod -> parser.set("Adorufu.modules." + mod.getModuleName().toLowerCase() + ".state", fileExists && mod.isEnabled()));
            parser.save();
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveModuleOption(String modName, String optionName, boolean state) throws IOException {
        logMsg(true, "Updating module option setting...");
        threadLock.lock(); // Don't allow other threads to modify this file until this operation is done.
        logWarn(true, "Thread locking engaged!");
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            YMLParser parser = new YMLParser(file);
            parser.set("Adorufu.modules." + modName.toLowerCase() + ".option_" + optionName.toLowerCase(), state);
            parser.save();
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized boolean getSavedModuleState(String modName) throws IOException {
        logMsg(true, "Getting module \"" + modName + "\"'s previously saved state...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                AdorufuMod.logErr(true, "Module states file doesn't exist (maybe this is the client's getKey run?)");
                saveModuleStates(false);
                return false;
            }
            YMLParser parser = new YMLParser(file);
            return parser.getBoolean("Adorufu.modules." + modName.toLowerCase() + ".state", false);
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized boolean getSavedModuleOption(String modName, String optionName, boolean def) {
        logMsg(true, "Getting module \"" + modName + "\"'s previously saved options...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                AdorufuMod.logErr(true, "Module states file doesn't exist (maybe this is the client's getKey run?)");
                return def;
            }
            YMLParser parser = new YMLParser(file);
            if (!parser.exists("Adorufu.modules." + modName.toLowerCase() + ".option_" + optionName.toLowerCase(), def)) {
                parser.set("Adorufu.modules." + modName.toLowerCase() + ".option_" + optionName.toLowerCase(), def);
                parser.save();
                return def;
            }
            return parser.getBoolean("Adorufu.modules." + modName.toLowerCase() + ".option_" + optionName.toLowerCase(), def);
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    public synchronized void saveFriends(List<Friend> updatedFriends) throws IOException {
        try {
            logMsg(true, "Updating saved friends...");
            threadLock.lock(); // Don't allow other threads to modify this file until this operation is done.
            logWarn(true, "Thread locking engaged!");
            File file = new File(dataFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            YMLParser parser = new YMLParser(file);
            List<String> strFriends = new ArrayList<>();
            updatedFriends.forEach(f -> strFriends.add(f.getName()));
            if (strFriends.isEmpty()) {
                strFriends.add("");
            }
            parser.set("Adorufu.friends.friendlist", strFriends);//todo test
            parser.save();
        } finally {
            threadLock.unlock();
        }
    }

    public synchronized List<Friend> loadFriends() throws IOException {
        logMsg(true, "Loading saved friends...");
        threadLock.lock();
        logWarn(true, "Thread locking engaged!");
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                AdorufuMod.logErr(true, "Data file doesn't exist (maybe this is the client's getKey run?)");
                return new ArrayList<>();
            }
            YMLParser parser = new YMLParser(file);
            List<String> list = parser.getStringList("Adorufu.friends.friendlist");
            List<Friend> fliet = new ArrayList<>();
            list.forEach(fs -> fliet.add(new Friend(fs)));
            return fliet; // it was supposed to say flist but i made a typo but idc, now its a filet :yum:
        } finally {
            threadLock.unlock();
            logWarn(true, "Thread locking disengaged!");
        }
    }

    // ------------------- plugin api ------------------------- //

    /**
     * Used for saving a float,double,string,int,long... anything like that, really.
     *
     * @param path    the path in the config to save it to. (recommended is Adorufu.something)
     * @param varName the name of the variable in the cfg file
     * @param obj     the thing to save.
     */
    public synchronized void savePluginData(String path, String varName, Object obj) throws IOException {
        threadLock.lock();
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            YMLParser parser = new YMLParser(file);
            parser.set("plugindata." + path + "." + varName, obj);
            parser.save();
        } finally {
            threadLock.unlock();
        }
    }

    public synchronized Object loadPluginData(String path, String varName, Object defaultVal) throws IOException {
        threadLock.lock();
        try {
            File file = new File(dataFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            YMLParser parser = new YMLParser(file);
            if (!parser.exists(path + "." + varName)) {
                if (defaultVal != null) return defaultVal;
                throw new AdorufuNoSuchElementInDataFileException("\"" + path + "\" does not exist in " + dataFileName);
            }
            if (defaultVal != null) return parser.get("plugindata." + path + "." + varName, defaultVal);
            return parser.get(path + "." + varName);
        } finally {
            threadLock.unlock();
        }
    }

}
