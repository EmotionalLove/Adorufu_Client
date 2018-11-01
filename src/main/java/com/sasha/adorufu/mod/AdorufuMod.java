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

import com.sasha.adorufu.api.AdorufuPlugin;
import com.sasha.adorufu.api.AdorufuPluginLoader;
import com.sasha.adorufu.api.adapter.MinecraftMappingUpdater;
import com.sasha.adorufu.mod.command.CommandHandler;
import com.sasha.adorufu.mod.command.commands.*;
import com.sasha.adorufu.mod.desktop.AdorufuSystemTrayManager;
import com.sasha.adorufu.mod.desktop.AdorufuWindowsBatteryManager;
import com.sasha.adorufu.mod.events.adorufu.AdorufuDataFileRetrievedEvent;
import com.sasha.adorufu.mod.events.client.ClientInputUpdateEvent;
import com.sasha.adorufu.mod.events.client.ClientOverlayRenderEvent;
import com.sasha.adorufu.mod.exception.AdorufuException;
import com.sasha.adorufu.mod.feature.IAdorufuFeature;
import com.sasha.adorufu.mod.feature.impl.AdorufuDiscordPresense;
import com.sasha.adorufu.mod.feature.impl.deprecated.ModuleEntitySpeed;
import com.sasha.adorufu.mod.feature.impl.deprecated.ModuleJoinLeaveMessages;
import com.sasha.adorufu.mod.friend.FriendManager;
import com.sasha.adorufu.mod.gui.fonts.FontManager;
import com.sasha.adorufu.mod.gui.hud.AdorufuHUD;
import com.sasha.adorufu.mod.gui.hud.renderableobjects.*;
import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.adorufu.mod.misc.ModuleState;
import com.sasha.adorufu.mod.misc.TPS;
import com.sasha.adorufu.mod.remote.RemoteDataManager;
import com.sasha.adorufu.mod.waypoint.WaypointManager;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleEventManager;
import com.sasha.eventsys.SimpleListener;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import com.sasha.simplesettings.SettingHandler;
import com.sun.jna.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.sasha.adorufu.mod.misc.Manager.Renderable.renderableRegistry;

@Mod(modid = AdorufuMod.MODID, name = AdorufuMod.NAME, version = AdorufuMod.VERSION, canBeDeactivated = true, clientSideOnly = true)
public class AdorufuMod implements SimpleListener {
    public static final String MODID = "adorufuforge";
    public static final String NAME = "Adorufu";
    public static final String JAP_NAME = "\u30A2\u30C9\u30EB\u30D5";
    public static final String VERSION = "1.5.3";


    private static Logger logger = LogManager.getLogger("Adorufu " + VERSION);
    public static SimpleEventManager EVENT_MANAGER = new SimpleEventManager();
    @Deprecated public static AdorufuDataManager DATA_MANAGER = new AdorufuDataManager();
    public static SettingHandler SETTING_HANDLER = new SettingHandler("AdorufuSettingData");
    public static List<Object> SETTING_CLASSES = new ArrayList<>();
    /**
     * desktop notifications
     */
    public static AdorufuSystemTrayManager TRAY_MANAGER;
    public static FriendManager FRIEND_MANAGER;
    /**
     * pretty fonts
     */
    public static FontManager FONT_MANAGER;
    public static WaypointManager WAYPOINT_MANAGER;
    public static RemoteDataManager REMOTE_DATA_MANAGER = new RemoteDataManager();
    /**
     * commands
     */
    public static SimpleCommandProcessor COMMAND_PROCESSOR;
    /**
     * checks windows battery level
     */
    public static AdorufuWindowsBatteryManager.SYSTEM_POWER_STATUS BATTERY_MANAGER;
    public static AdorufuPerformanceAnalyser PERFORMANCE_ANAL = new AdorufuPerformanceAnalyser(); // no, stop, this ISN'T lewd... I SWEAR!!!
    public static AdorufuWindowsBatteryManager BATTERY_MANAGER_INTERFACE;
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static Minecraft minecraft = Minecraft.getMinecraft();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftMappingUpdater.updateMappings();
        ((ScheduledThreadPoolExecutor) scheduler).setRemoveOnCancelPolicy(true);
        FRIEND_MANAGER = new FriendManager();
        try {
            if (DATA_MANAGER.getDRPEnabled()) {
                AdorufuDiscordPresense.setupPresense();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    AdorufuDiscordPresense.discordRpc.Discord_Shutdown();
                }));
            }
        } catch (IOException e) {
            e.printStackTrace();
            AdorufuDiscordPresense.setupPresense();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                AdorufuDiscordPresense.discordRpc.Discord_Shutdown();
            }));
        }
        try {
            if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                BATTERY_MANAGER_INTERFACE = (AdorufuWindowsBatteryManager) Native.loadLibrary("Kernel32", AdorufuWindowsBatteryManager.class);
                AdorufuWindowsBatteryManager.SYSTEM_POWER_STATUS batteryStatus = new AdorufuWindowsBatteryManager.SYSTEM_POWER_STATUS();
                BATTERY_MANAGER_INTERFACE.GetSystemPowerStatus(batteryStatus);
                logMsg(true, batteryStatus.getBatteryLifePercent());
                BATTERY_MANAGER = batteryStatus;
            }
        } catch (Exception x) {
            //
        }
        // plugin loading
        AdorufuPluginLoader pluginLoader = new AdorufuPluginLoader();
        try {
            logMsg(true, "Finding plugins...");
            List<File> files = pluginLoader.findPlugins();
            if (files.size() == 0) {
                logMsg(true, "No plugins to load, continuing the initialisation of vanilla Adorufu");
                return;
            }
            logMsg(true, "Preparing plugins...");
            int i = pluginLoader.preparePlugins(files);
            logMsg(true, "Prepared " + i + " plugin(s)");
            pluginLoader.loadPlugins();
            logMsg(true, "Successfully loaded plugins!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Adorufu is initialising...");
        logMsg(true, "Registering commands, renderables and impl...");
        scheduler.schedule(() -> {
            try {
                COMMAND_PROCESSOR = new SimpleCommandProcessor("-");
                this.registerCommands();
                this.registerFeatures();
                this.registerRenderables();
                EVENT_MANAGER.registerListener(new CommandHandler());
                EVENT_MANAGER.registerListener(new Manager.Module());
                AdorufuHUD.setupHUD();
                EVENT_MANAGER.registerListener(new AdorufuHUD());
                TPS.INSTANCE = new TPS();
                EVENT_MANAGER.registerListener(TPS.INSTANCE);
                //loadBindsAndStates();
                ArrayList<List<String>> greets = DATA_MANAGER.loadGreets();
                ModuleJoinLeaveMessages.joinMessages = greets.get(0);
                ModuleJoinLeaveMessages.leaveMessages = greets.get(1);
                ModuleEntitySpeed.speed = (double) AdorufuMod.DATA_MANAGER.loadSomeGenericValue("Adorufu.values", "entityspeed", 2.5d);
                WAYPOINT_MANAGER = new WaypointManager();
                DATA_MANAGER.loadPlayerIdentities();
                DATA_MANAGER.identityCacheMap.forEach((uuid, id) -> {
                    if (id.shouldUpdate()) {
                        id.updateDisplayName();
                    }
                });
                TRAY_MANAGER = new AdorufuSystemTrayManager();
                SETTING_CLASSES.forEach(x -> SETTING_HANDLER.read(x));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, TimeUnit.NANOSECONDS);
        MinecraftForge.EVENT_BUS.register(new ForgeEvent());
        EVENT_MANAGER.registerListener(new AdorufuUpdateChecker());
        EVENT_MANAGER.registerListener(this);
        logMsg(true, "Adorufu cleanly initialised!");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FONT_MANAGER = new FontManager();
        FONT_MANAGER.loadFonts(); // I would load this on a separate thread if I could, because it takes forEVER to execute.
        if (AdorufuPluginLoader.getLoadedPlugins().size() > 0)
            AdorufuMod.logWarn(true, "Adorufu was loaded with plugins! " +
                    "Please make sure that you know ABSOLUTELY EVERYTHING your installed plugins are doing, as" +
                    " developers can run malicious code in their plugins.");
        SETTING_CLASSES.forEach(setting -> SETTING_HANDLER.read(setting));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> SETTING_CLASSES.forEach(x -> SETTING_HANDLER.save(x))));
    }

    private void reload(boolean async) {
        Thread thread = new Thread(() -> {
            try {
                Manager.Module.moduleRegistry.forEach(m -> m.forceState(ModuleState.DISABLE, false, false));
                this.registerRenderables();
                try {
                    ModuleEntitySpeed.speed = (double) AdorufuMod.DATA_MANAGER.loadSomeGenericValue("Adorufu.values", "entityspeed", 2.5d);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AdorufuHUD.resetHUD();
                AdorufuMod.logMsg(true, "Adorufu successfully reloaded.");
            } catch (Exception e) {
                throw new AdorufuException("Severe error occurred whilst reloading client " + e.getMessage());
            }
        });
        if (async) {
            thread.start();
            return;
        }
        thread.run();
    }

    private void registerCommands() throws Exception {
        COMMAND_PROCESSOR.register(AboutCommand.class);
        COMMAND_PROCESSOR.register(ToggleCommand.class);
        COMMAND_PROCESSOR.register(ModulesCommand.class);
        COMMAND_PROCESSOR.register(HelpCommand.class);
        COMMAND_PROCESSOR.register(BindCommand.class);
        COMMAND_PROCESSOR.register(XrayCommand.class);
        COMMAND_PROCESSOR.register(LagCommand.class);
        COMMAND_PROCESSOR.register(IgnoreCommand.class);
        COMMAND_PROCESSOR.register(IgnorelistCommand.class);
        COMMAND_PROCESSOR.register(YawCommand.class);
        COMMAND_PROCESSOR.register(FriendCommand.class);
        COMMAND_PROCESSOR.register(FriendlistCommand.class);
        COMMAND_PROCESSOR.register(WaypointCommand.class);
        COMMAND_PROCESSOR.register(EntitySpeedCommand.class);
        COMMAND_PROCESSOR.register(FilterCommand.class);
        COMMAND_PROCESSOR.register(FilterlistCommand.class);
        COMMAND_PROCESSOR.register(UpdateCommand.class);
        COMMAND_PROCESSOR.register(PluginsCommand.class);
        COMMAND_PROCESSOR.register(AutoReconnectCommand.class);
        COMMAND_PROCESSOR.register(PathCommand.class);
        AdorufuPluginLoader.getLoadedPlugins().forEach(AdorufuPlugin::onCommandRegistration);
    }

    private void registerFeatures() {
        Manager.Feature.featureRegistry.clear();
        Manager.getClassesInPackage(AdorufuDiscordPresense.class.getPackage().getName(), IAdorufuFeature.class)
                .forEach(e -> {
                    try {
                        IAdorufuFeature feature = (IAdorufuFeature) e.getConstructor().newInstance();
                        Manager.Feature.featureRegistry.add(feature);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                        e1.printStackTrace();
                        AdorufuMod.logErr(true, "A severe uncaught exception occurred whilst initialising " +e.getSimpleName() + "!");
                    }
                });
        // todo api
        SETTING_CLASSES.addAll(Manager.Feature.featureRegistry);
    }
/*
    private void registerModules() {
        Manager.Module.moduleRegistry.clear();
        Manager.Module.register(new ModuleXray());
        Manager.Module.register(new ModuleWireframe());
        Manager.Module.register(new ModuleNamePlates());
        Manager.Module.register(new ModuleTPS());
        Manager.Module.register(new ModuleFPS());
        Manager.Module.register(new ModuleCoordinates());
        Manager.Module.register(new ModuleSaturation());
        Manager.Module.register(new ModuleInventoryStats());
        Manager.Module.register(new ModuleHorsestats());
        Manager.Module.register(new ModuleHacklist());
        Manager.Module.register(new ModuleWatermark());
        Manager.Module.register(new ModuleKillaura());
        Manager.Module.register(new ModuleStorageESP());
        Manager.Module.register(new ModuleTracers());
        Manager.Module.register(new AntiHungerFeature());
        Manager.Module.register(new ClickGUIFeature());
        Manager.Module.register(new ModuleNightVision());
        Manager.Module.register(new ModuleNoSlow());
        Manager.Module.register(new AnnouncerFeature());
        Manager.Module.register(new AFKFishFeature());
        Manager.Module.register(new AutoRespawnFeature());
        Manager.Module.register(new ChunkTraceFeature());
        Manager.Module.register(new ModuleFreecam());
        Manager.Module.register(new CrystalAuraFeature());
        Manager.Module.register(new CrystalLogoutFeature());
        Manager.Module.register(new ModuleJesus());
        Manager.Module.register(new ClientIgnoreFeature());
        Manager.Module.register(new AutoIgnoreFeature());
        Manager.Module.register(new AutoSprintFeature());
        Manager.Module.register(new CameraClipFeature());
        Manager.Module.register(new ModuleElytraBoost());
        Manager.Module.register(new ModuleElytraFlight());
        Manager.Module.register(new ModuleEntitySpeed());
        Manager.Module.register(new ModuleLowJump());
        Manager.Module.register(new ModuleMiddleClickBlock());
        Manager.Module.register(new ModuleExtendedTablist());
        Manager.Module.register(new AntiAFKFeature());
        Manager.Module.register(new ModuleYawLock());
        Manager.Module.register(new ModuleQueueTime());
        Manager.Module.register(new ModuleNoPush());
        Manager.Module.register(new ModulePacketFly());
        Manager.Module.register(new ModulePigControl());
        Manager.Module.register(new AutoTotemFeature());
        Manager.Module.register(new ModuleWaypointGUI());
        Manager.Module.register(new ModuleWaypoints());
        Manager.Module.register(new ModuleWolfIdentity());
        Manager.Module.register(new ModuleGhostBlockWarning());
        Manager.Module.register(new AntiFireOverlayFeature());
        Manager.Module.register(new ModuleMinecraftMusic());
        Manager.Module.register(new BlinkFeature()); // No clue if this is what blink is suppposed to do... i dont pvp...
        Manager.Module.register(new ModuleAutoArmor());
        Manager.Module.register(new ModuleJoinLeaveMessages());
        Manager.Module.register(new CraftInventoryFeature());
        Manager.Module.register(new ModuleKnockbackSuppress());
        Manager.Module.register(new ModuleEquipmentDamage());
        Manager.Module.register(new DesktopNotificationsFeature());
        Manager.Module.register(new CPUControlFeature());
        Manager.Module.register(new ModuleBatteryLife());
        Manager.Module.register(new ModulePowerBow());
        Manager.Module.register(new BookForgerFeature());
        Manager.Module.register(new AutoWalkFeature());
        Manager.Module.register(new BoatFlyFeature());
        Manager.Module.register(new AutoReconnectFeature());
        Manager.Module.register(new ModuleNameProtect());
        Manager.Module.register(new ModuleSafeWalk());
        Manager.Module.register(new ModulePortalGodMode());
        Manager.Module.register(new AutoEatFeature());
        Manager.Module.register(new ModuleShulkerSpy());
        AdorufuPluginLoader.getLoadedPlugins().forEach(AdorufuPlugin::onModuleRegistration);
    }
*/

    private void registerRenderables() {
        renderableRegistry.clear();
        Manager.Renderable.register(new RenderableWatermark());
        Manager.Renderable.register(new RenderableFeatureList());
        Manager.Renderable.register(new RenderableCoordinates());
        Manager.Renderable.register(new RenderableSaturation());
        Manager.Renderable.register(new RenderableInventoryStats());
        Manager.Renderable.register(new RenderableHorseStats());
        Manager.Renderable.register(new RenderableFramerate());
        Manager.Renderable.register(new RenderableTickrate());
        Manager.Renderable.register(new RenderableEquipmentDamage());
        Manager.Renderable.register(new RenderableBatteryLife());
        AdorufuPluginLoader.getLoadedPlugins().forEach(AdorufuPlugin::onRenderableRegistration);
    }

    /**
     * Set a key to be pressed
     */
    public static void setPressed(KeyBinding key, boolean pressed) {
        KeyBinding.setKeyBindState(key.getKeyCode(), pressed);
    }

    /**
     * Log a regular message
     */
    public static void logMsg(boolean consoleOnly, String logMsg) {
        logger.log(Level.INFO, logMsg);
        if (consoleOnly) return;
        minecraft.player.sendMessage(new TextComponentString("\2478[\2474" + JAP_NAME + "\2478] \2477" + logMsg));
    }

    /**
     * Log message without the Adorufu branding to chat
     */
    public static void logMsg(String logMsg) {
        minecraft.player.sendMessage(new TextComponentString("\2477" + logMsg));
    }

    /**
     * Log an error
     */
    public static void logErr(boolean consoleOnly, String logMsg) {
        logger.log(Level.ERROR, logMsg);
        if (consoleOnly) return;
        minecraft.player.sendMessage(new TextComponentString("\2478[\2474" + JAP_NAME + " \247cERROR\2478] \247c" + logMsg));
    }

    /**
     * Log a warning
     */
    public static void logWarn(boolean consoleOnly, String logMsg) {
        logger.log(Level.WARN, logMsg);
        if (consoleOnly) return;
        minecraft.player.sendMessage(new TextComponentString("\2478[\2474" + JAP_NAME + " \247eWARNING\2478] \247e" + logMsg));
    }

    @SimpleEventHandler
    public void onDataFileRetrieved(AdorufuDataFileRetrievedEvent e) {
        this.reload(true);
    }
}

class ForgeEvent {
    @SubscribeEvent
    public void onRenderLost(RenderGameOverlayEvent.Post e) {
        RenderGameOverlayEvent.ElementType target = RenderGameOverlayEvent.ElementType.TEXT;
        if (e.getType() == target) {
            ClientOverlayRenderEvent event = new ClientOverlayRenderEvent(e.getPartialTicks());
            AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        }
    }

    @SubscribeEvent
    public void onMoveUpdate(InputUpdateEvent e) {
        ClientInputUpdateEvent updateEvent = new ClientInputUpdateEvent(e.getMovementInput());
        AdorufuMod.EVENT_MANAGER.invokeEvent(updateEvent);
    }
}
