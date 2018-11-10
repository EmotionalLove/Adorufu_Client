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

package com.sasha.adorufu.mod.mixin.client;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.client.ClientPacketRecieveEvent;
import com.sasha.adorufu.mod.events.client.ClientPacketSendEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

/**
 * Created by Sasha on 08/08/2018 at 8:08 PM
 **/
@Mixin(value = NetworkManager.class, priority = 999)
public abstract class MixinNetworkManager {

    @Shadow
    protected abstract void dispatchPacket(Packet<?> inPacket, @Nullable GenericFutureListener<? extends Future<? super Void>>[] futureListeners);

    @Redirect(
            method = "sendPacket(Lnet/minecraft/network/Packet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/network/NetworkManager.dispatchPacket(Lnet/minecraft/network/Packet;[Lio/netty/util/concurrent/GenericFutureListener;)V"
            )
    )
    private void onDispatchPacket(NetworkManager networkManager, Packet<?> packet, GenericFutureListener<? extends Future<?super Void>>[] futureListeners) {
        ClientPacketSendEvent event = new ClientPacketSendEvent(packet);
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            return;
        }
        this.dispatchPacket(event.getSendPacket(), futureListeners);
    }

    @Redirect(
            method = "channelRead0",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/network/Packet.processPacket(Lnet/minecraft/network/INetHandler;)V"
            )
    )
    private void onProcessPacket(Packet<?> packet, INetHandler netHandler) {
        ClientPacketRecieveEvent event = new ClientPacketRecieveEvent(packet);
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            return;
        }
        ((Packet<INetHandler>) packet).processPacket(netHandler);
    }
}
