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

package com.sasha.adorufu.mod.mixins.client;

import com.mojang.authlib.properties.PropertyMap;
import com.sasha.adorufu.api.AdorufuPlugin;
import com.sasha.adorufu.api.AdorufuPluginLoader;
import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.client.ClientGetMusicTypeEvent;
import com.sasha.adorufu.mod.events.client.ClientMouseClickEvent;
import com.sasha.adorufu.mod.events.client.ClientScreenChangedEvent;
import com.sasha.adorufu.mod.events.playerclient.PlayerBlockPlaceEvent;
import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.adorufu.mod.feature.deprecated.AdorufuModule;
import com.sasha.adorufu.mod.feature.impl.CPUControlFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.io.IOException;

import static net.minecraft.client.Minecraft.getSystemTime;

/**
 * Created by Sasha on 10/08/2018 at 1:40 PM
 **/
@Mixin(value = Minecraft.class, priority = 999)
public abstract class MixinMinecraft {
    //@Inject(method = "runTickKeyboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyBindState(IZ)V", ordinal = 2), cancellable = true)
    @Shadow
    @Final
    public PropertyMap profileProperties;

    @Shadow
    public abstract void processKeyBinds();

    @Shadow
    public GameSettings gameSettings;

    @Shadow
    public boolean actionKeyF3;

    @Shadow
    public abstract void updateDebugProfilerName(int keyCount);

    @Shadow
    public abstract void displayInGameMenu();

    @Shadow
    public abstract boolean processKeyF3(int auxKey);

    @Shadow
    @Nullable
    public GuiScreen currentScreen;

    @Shadow
    public EntityRenderer entityRenderer;

    @Shadow
    public abstract void dispatchKeypresses();

    @Shadow
    public long debugCrashKeyPressTime;

    @Shadow
    public RayTraceResult objectMouseOver;

    @Shadow
    public boolean inGameHasFocus;

    @Shadow
    public WorldClient world;

    @Shadow
    public int leftClickCounter;

    @Inject(method = "rightClickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;swingArm(Lnet/minecraft/util/EnumHand;)V"))
    public void rightClickMouse(CallbackInfo info) {
        PlayerBlockPlaceEvent event = new PlayerBlockPlaceEvent(AdorufuMod.minecraft.player.itemStackMainHand);
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
    }

    /**
     * @author Sasha Stevens
     * @reason ugh
     */
    @Overwrite
    public void runTickKeyboard() throws IOException {
        while (Keyboard.next()) {
            int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            if (this.debugCrashKeyPressTime > 0L) {
                if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                    throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                }

                if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                    this.debugCrashKeyPressTime = -1L;
                }
            } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                this.actionKeyF3 = true;
                this.debugCrashKeyPressTime = getSystemTime();
            }

            this.dispatchKeypresses();

            if (this.currentScreen != null) {
                this.currentScreen.handleKeyboardInput();
            }

            boolean flag = Keyboard.getEventKeyState();

            if (flag) {
                if (i == 62 && this.entityRenderer != null) {
                    this.entityRenderer.switchUseShader();
                }

                boolean flag1 = false;

                if (this.currentScreen == null) {
                    if (i == 1) {
                        this.displayInGameMenu();
                    }

                    flag1 = Keyboard.isKeyDown(61) && this.processKeyF3(i);
                    this.actionKeyF3 |= flag1;

                    if (i == 59) {
                        this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                    }
                }

                if (flag1) {
                    KeyBinding.setKeyBindState(i, false);
                } else {
                    KeyBinding.setKeyBindState(i, true);
                    KeyBinding.onTick(i);
                }

                if (this.gameSettings.showDebugProfilerChart) {
                    if (i == 11) {
                        this.updateDebugProfilerName(0);
                    }

                    for (int j = 0; j < 9; ++j) {
                        if (i == 2 + j) {
                            this.updateDebugProfilerName(j + 1);
                        }
                    }
                }
            } else {
                KeyBinding.setKeyBindState(i, false);
                Manager.Module.moduleRegistry.stream().filter(m -> m.getKeyBind() == i).forEach(AdorufuModule::toggle);

                if (i == 61) {
                    if (this.actionKeyF3) {
                        this.actionKeyF3 = false;
                    } else {
                        this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                        this.gameSettings.showDebugProfilerChart = this.gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown();
                        this.gameSettings.showLagometer = this.gameSettings.showDebugInfo && GuiScreen.isAltKeyDown();
                    }
                }
            }
            net.minecraftforge.fml.common.FMLCommonHandler.instance().fireKeyInput();
        }

        this.processKeyBinds();
    }

    @Inject(method = "middleClickMouse", at = @At("HEAD"), cancellable = true)
    public void middleClickMouse(CallbackInfo iinfo) {
        ClientMouseClickEvent.Middle event = new ClientMouseClickEvent.Middle();
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) iinfo.cancel();
    }

    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    protected void rightClickMouse$0(CallbackInfo iinfo) {
        ClientMouseClickEvent.Right event = new ClientMouseClickEvent.Right();
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) iinfo.cancel();
    }

    @Inject(method = "getAmbientMusicType", at = @At("RETURN"), cancellable = true)
    public void getAmbientMusicType(CallbackInfoReturnable<MusicTicker.MusicType> info) {
        ClientGetMusicTypeEvent event = new ClientGetMusicTypeEvent(info.getReturnValue());
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        info.setReturnValue(event.getMusicType());
    }

    @Inject(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;create(Lorg/lwjgl/opengl/PixelFormat;)V"))
    public void createDisplay(CallbackInfo info) {
        Display.setTitle("Minecraft 1.12.2 with " + AdorufuMod.NAME + " " + AdorufuMod.VERSION);
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    public void displayGuiScreen(GuiScreen screenIn, CallbackInfo info) {
        ClientScreenChangedEvent event = new ClientScreenChangedEvent(screenIn);
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            info.cancel();
        }
        screenIn = event.getScreen();
    }

    /**
     * @author Sasha Stevens
     * @reason CPUControl
     */
    @Overwrite
    public int getLimitFramerate() {
        System.out.println(this.leftClickCounter); // THIS IS A TEST
        if (this.world == null && this.currentScreen != null) {
            return 30;
        }
        if (!Display.isActive() && Manager.Feature.isFeatureEnabled(CPUControlFeature.class)) {
            return 1;
        }
        return (this.gameSettings.limitFramerate);
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"), cancellable = true)
    public void shutdownMinecraftApplet(CallbackInfo info) {
        AdorufuPluginLoader.getLoadedPlugins().forEach(AdorufuPlugin::onDisable);
    }
}
