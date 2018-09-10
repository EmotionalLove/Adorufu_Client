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

package com.sasha.adorufu;

import com.sasha.adorufu.command.CommandHandler;
import com.sasha.adorufu.command.commands.*;
import com.sasha.adorufu.events.AdorufuDataFileRetrievedEvent;
import com.sasha.adorufu.events.ClientInputUpdateEvent;
import com.sasha.adorufu.events.ClientOverlayRenderEvent;
import com.sasha.adorufu.exception.AdorufuException;
import com.sasha.adorufu.friend.FriendManager;
import com.sasha.adorufu.gui.clickgui.windows.*;
import com.sasha.adorufu.gui.fonts.FontManager;
import com.sasha.adorufu.gui.hud.AdorufuHUD;
import com.sasha.adorufu.gui.hud.renderableobjects.*;
import com.sasha.adorufu.misc.ModuleState;
import com.sasha.adorufu.misc.TPS;
import com.sasha.adorufu.module.ModuleManager;
import com.sasha.adorufu.module.modules.*;
import com.sasha.adorufu.module.modules.hudelements.*;
import com.sasha.adorufu.remote.RemoteDataManager;
import com.sasha.adorufu.waypoint.WaypointManager;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleEventManager;
import com.sasha.eventsys.SimpleListener;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import net.minecraft.client.Minecraft;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.sasha.adorufu.gui.clickgui.AdorufuClickGUI.registeredWindows;
import static com.sasha.adorufu.module.ModuleManager.loadBindsAndStates;

@Mod(modid = AdorufuMod.MODID, name = AdorufuMod.NAME, version = AdorufuMod.VERSION, canBeDeactivated = true)
public class AdorufuMod implements SimpleListener {
    public static final String MODID = "adorufuforge";
    public static final String NAME = "Adorufu";
    public static final String JAP_NAME = "\u30A2\u30C9\u30EB\u30D5";
    public static final String VERSION = "1.2.2";


    private static Logger logger = LogManager.getLogger("Adorufu " + VERSION);
    public static SimpleEventManager EVENT_MANAGER = new SimpleEventManager();
    public static AdorufuDataManager DATA_MANAGER = new AdorufuDataManager();
    public static FriendManager FRIEND_MANAGER;
    public static FontManager FONT_MANAGER;
    public static WaypointManager WAYPOINT_MANAGER;
    public static RemoteDataManager REMOTE_DATA_MANAGER = new RemoteDataManager();
    public static SimpleCommandProcessor COMMAND_PROCESSOR;
    public static AdorufuPerformanceAnalyser PERFORMANCE_ANAL = new AdorufuPerformanceAnalyser(); // no, stop, this ISN'T lewd... I SWEAR!!!
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static Minecraft minecraft = Minecraft.getMinecraft();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
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
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Adorufu is initialising...");
        logMsg(true, "Registering commands, renderables and modules...");
        scheduler.schedule(() -> {
            try {
                COMMAND_PROCESSOR = new SimpleCommandProcessor("-");
                this.registerCommands();
                this.registerModules();
                this.registerRenderables();
                EVENT_MANAGER.registerListener(new CommandHandler());
                EVENT_MANAGER.registerListener(new ModuleManager());
                AdorufuHUD.setupHUD();
                EVENT_MANAGER.registerListener(new AdorufuHUD());
                ModuleXray.xrayBlocks = DATA_MANAGER.getXrayBlocks();
                TPS.INSTANCE = new TPS();
                EVENT_MANAGER.registerListener(TPS.INSTANCE);
                ModuleManager.loadBindsAndStates();
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
        FONT_MANAGER.loadFonts(); // I would load this on a seperate thread if I could, because it takes forEVER to exectute.
    }

    public void reload(boolean async) {
        Thread thread = new Thread(() -> {
            try {
                ModuleManager.moduleRegistry.forEach(m -> m.forceState(ModuleState.DISABLE, false, false));
                this.registerRenderables();
                ModuleXray.xrayBlocks = DATA_MANAGER.getXrayBlocks();
                loadBindsAndStates();
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
    }

    @Deprecated // needs to be reworked - later
    private void registerWindows() throws Exception {
        registeredWindows.clear();
        registeredWindows.add(new WindowChat());
        registeredWindows.add(new WindowCombat());
        registeredWindows.add(new WindowHUD());
        registeredWindows.add(new WindowMisc());
        registeredWindows.add(new WindowMovement());
        registeredWindows.add(new WindowRender());

    }

    private void registerModules() throws Exception {

        ModuleManager.moduleRegistry.clear();
        ModuleManager.register(new ModuleXray());
        ModuleManager.register(new ModuleWireframe());
        ModuleManager.register(new ModuleNamePlates());
        ModuleManager.register(new ModuleTPS());
        ModuleManager.register(new ModuleFPS());
        ModuleManager.register(new ModuleCoordinates());
        ModuleManager.register(new ModuleSaturation());
        ModuleManager.register(new ModuleInventoryStats());
        ModuleManager.register(new ModuleHorsestats());
        ModuleManager.register(new ModuleHacklist());
        ModuleManager.register(new ModuleWatermark());
        ModuleManager.register(new ModuleKillaura());
        ModuleManager.register(new ModuleStorageESP());
        ModuleManager.register(new ModuleTracers());
        ModuleManager.register(new ModuleAntiHunger());
        ModuleManager.register(new ModuleClickGUI());
        ModuleManager.register(new ModuleNightVision());
        ModuleManager.register(new ModuleNoSlow());
        ModuleManager.register(new ModuleAnnouncer());
        ModuleManager.register(new ModuleAFKFish());
        ModuleManager.register(new ModuleAutoRespawn());
        ModuleManager.register(new ModuleChunkTrace());
        ModuleManager.register(new ModuleFreecam());
        ModuleManager.register(new ModuleCrystalAura());
        ModuleManager.register(new ModuleCrystalLogout());
        ModuleManager.register(new ModuleFlight());
        ModuleManager.register(new ModuleJesus());
        ModuleManager.register(new ModuleClientIgnore());
        ModuleManager.register(new ModuleAutoIgnore());
        ModuleManager.register(new ModuleAutoSprint());
        ModuleManager.register(new ModuleCameraClip());
        ModuleManager.register(new ModuleElytraBoost());
        ModuleManager.register(new ModuleElytraFlight());
        ModuleManager.register(new ModuleEntitySpeed());
        ModuleManager.register(new ModuleLowJump());
        ModuleManager.register(new ModuleMiddleClickBlock());
        ModuleManager.register(new ModuleExtendedTablist());
        ModuleManager.register(new ModuleAntiAFK());
        ModuleManager.register(new ModuleYawLock());
        ModuleManager.register(new ModuleQueueTime());
        ModuleManager.register(new ModuleNoPush());
        ModuleManager.register(new ModulePacketFly());
        ModuleManager.register(new ModulePigControl());
        ModuleManager.register(new ModuleAutoTotem());
        ModuleManager.register(new ModuleWaypointGUI());
        ModuleManager.register(new ModuleWaypoints());
        ModuleManager.register(new ModuleWolfIdentity());
        ModuleManager.register(new ModuleGhostBlockWarning());
        ModuleManager.register(new ModuleAntiFireOverlay());
        ModuleManager.register(new ModuleCreativeMusic());
        ModuleManager.register(new ModuleBlink()); // No clue if this is what blink is suppposed to do... i dont pvp...
        ModuleManager.register(new ModuleAutoArmor());
        ModuleManager.register(new ModuleJoinLeaveMessages());
        ModuleManager.register(new ModuleCraftInventory());
        ModuleManager.register(new ModuleKnockbackSuppress());
        ModuleManager.register(new ModuleEquipmentDamage());
        ModuleManager.register(new ModuleDesktopNotifications());
    }


    private void registerRenderables() {
        AdorufuHUD.registeredHudElements.clear();
        AdorufuHUD.registeredHudElements.add(new RenderableWatermark());
        AdorufuHUD.registeredHudElements.add(new RenderableHacklist());
        AdorufuHUD.registeredHudElements.add(new RenderableCoordinates());
        AdorufuHUD.registeredHudElements.add(new RenderableSaturation());
        AdorufuHUD.registeredHudElements.add(new RenderableInventoryStats());
        AdorufuHUD.registeredHudElements.add(new RenderableHorseStats());
        AdorufuHUD.registeredHudElements.add(new RenderableFramerate());
        AdorufuHUD.registeredHudElements.add(new RenderableTickrate());
        AdorufuHUD.registeredHudElements.add(new RenderableEquipmentDamage());
    }

    public static void logMsg(boolean consoleOnly, String logMsg) {
        logger.log(Level.INFO, logMsg);
        if (consoleOnly) return;
        minecraft.player.sendMessage(new TextComponentString("\2478[\2474Adorufu\2478] \2477" + logMsg));
    }

    public static void logMsg(String logMsg) {
        minecraft.player.sendMessage(new TextComponentString("\2477" + logMsg));
    }

    public static void logErr(boolean consoleOnly, String logMsg) {
        logger.log(Level.ERROR, logMsg);
        if (consoleOnly) return;
        minecraft.player.sendMessage(new TextComponentString("\2478[\2474Adorufu \247cERROR\2478] \247c" + logMsg));
    }

    public static void logWarn(boolean consoleOnly, String logMsg) {
        logger.log(Level.WARN, logMsg);
        if (consoleOnly) return;
        minecraft.player.sendMessage(new TextComponentString("\2478[\2474Adorufu \247eWARNING\2478] \247e" + logMsg));
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
        ClientInputUpdateEvent ciup = new ClientInputUpdateEvent(e.getMovementInput());
        AdorufuMod.EVENT_MANAGER.invokeEvent(ciup);
    }
}
