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

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.client.ClientEnderPearlSpawnEvent;
import com.sasha.adorufu.mod.events.client.ClientItemSpawnEvent;
import com.sasha.adorufu.mod.events.playerclient.PlayerKnockbackEvent;
import com.sasha.adorufu.mod.events.server.ServerLoadChunkEvent;
import com.sasha.adorufu.mod.gui.GuiDisconnectedAuto;
import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.adorufu.mod.feature.impl.AutoReconnectFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = NetHandlerPlayClient.class, priority = 999)
public class MixinNetHandlerPlayClient {
    @Shadow
    private Minecraft client;

    @Shadow
    @Final
    private GuiScreen guiScreenServer;

    @Inject(method = "handleChunkData",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/multiplayer/WorldClient;getChunk(II)Lnet/minecraft/world/chunk/Chunk;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleChunkData(SPacketChunkData packetIn, CallbackInfo info, /*local*/ Chunk chunk) {
        ServerLoadChunkEvent event = new ServerLoadChunkEvent(chunk, packetIn, chunk.x, chunk.z);
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
    }

    @Inject(method = "handleSpawnObject", at = @At(value = "NEW", target = "net/minecraft/entity/item/EntityItem"))
    public void handleSpawnObject(SPacketSpawnObject packetIn, CallbackInfo info) {
        ClientItemSpawnEvent event = new ClientItemSpawnEvent((int) packetIn.getX(), (int) packetIn.getY(), (int) packetIn.getZ());
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
    }

    @Inject(method = "handleSpawnObject", at = @At(value = "NEW", target = "net/minecraft/entity/item/EntityEnderPearl"))
    public void handleSpawnObject$0(SPacketSpawnObject packetIn, CallbackInfo info) {
        ClientEnderPearlSpawnEvent event = new ClientEnderPearlSpawnEvent((int) packetIn.getX(), (int) packetIn.getY(), (int) packetIn.getZ());
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
    }

    @Inject(method = "handleExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Explosion;doExplosionB(Z)V"), cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleExplosion(SPacketExplosion packetIn, CallbackInfo info, Explosion explosion) {
        explosion.doExplosionB(true);
        PlayerKnockbackEvent event = new PlayerKnockbackEvent(packetIn.getMotionX(), packetIn.getMotionY(), packetIn.getMotionZ());
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) info.cancel();
        this.client.player.motionX += event.getMotionX();
        this.client.player.motionY += event.getMotionY();
        this.client.player.motionZ += event.getMotionZ();
        info.cancel();
    }

    /**
     * @param reason autoreconnect and also cuz im lazy
     * @author Sasha Stevens
     */
    @Overwrite
    public void onDisconnect(ITextComponent reason) {
        this.client.loadWorld((WorldClient) null);

        if (this.guiScreenServer != null) {
            if (this.guiScreenServer instanceof GuiScreenRealmsProxy) {
                this.client.displayGuiScreen((new DisconnectedRealmsScreen(((GuiScreenRealmsProxy) this.guiScreenServer).getProxy(), "disconnect.lost", reason)).getProxy());
            } else {
                if (Manager.Feature.isFeatureEnabled(AutoReconnectFeature.class)) {
                    this.client.displayGuiScreen(new GuiDisconnectedAuto(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason, AutoReconnectFeature.delay, AutoReconnectFeature.serverData));
                } else {
                    this.client.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason));
                }
            }
        } else {
            if (Manager.Feature.isFeatureEnabled(AutoReconnectFeature.class)) {
                this.client.displayGuiScreen(new GuiDisconnectedAuto(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason, AutoReconnectFeature.delay, AutoReconnectFeature.serverData));
            } else {
                this.client.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason));
            }
        }
    }
}
