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

import com.sasha.adorufu.mod.gui.GuiDisconnectedAuto;
import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.adorufu.mod.feature.impl.AutoReconnectFeature;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Mixin(value = GuiConnecting.class, priority = 999)
public class MixinGuiConnecting extends MixinGuiScreen {

    @Shadow
    @Final
    public static Logger LOGGER;

    @Shadow
    public boolean cancel;

    @Shadow
    public NetworkManager networkManager;

    @Shadow
    @Final
    public GuiScreen previousGuiScreen;

    /**
     * @author Sasha Stevens
     * @reason autoreconnect
     */
    @Overwrite
    private void connect(final String ip, final int port) {
        LOGGER.info("Connecting to {}, {}", ip, Integer.valueOf(port));
        new Thread(() -> {
            InetAddress inetaddress = null;

            try {
                if (MixinGuiConnecting.this.cancel) {
                    return;
                }

                inetaddress = InetAddress.getByName(ip);
                MixinGuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, port, MixinGuiConnecting.this.mc.gameSettings.isUsingNativeTransport());
                MixinGuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(MixinGuiConnecting.this.networkManager, MixinGuiConnecting.this.mc, MixinGuiConnecting.this.previousGuiScreen));
                MixinGuiConnecting.this.networkManager.sendPacket(new C00Handshake(ip, port, EnumConnectionState.LOGIN, true));
                MixinGuiConnecting.this.networkManager.sendPacket(new CPacketLoginStart(MixinGuiConnecting.this.mc.getSession().getProfile()));
            } catch (UnknownHostException unknownhostexception) {
                if (MixinGuiConnecting.this.cancel) {
                    return;
                }

                MixinGuiConnecting.LOGGER.error("Couldn't connect to server", (Throwable) unknownhostexception);
                if (Manager.Feature.isFeatureEnabled(AutoReconnectFeature.class)) {
                    MixinGuiConnecting.this.mc.displayGuiScreen(new GuiDisconnectedAuto(MixinGuiConnecting.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"}), AutoReconnectFeature.delay, AutoReconnectFeature.serverData));
                }
                else {
                    MixinGuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(MixinGuiConnecting.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"})));
                }
            } catch (Exception exception) {
                if (MixinGuiConnecting.this.cancel) {
                    return;
                }

                GuiConnecting.LOGGER.error("Couldn't connect to server", (Throwable) exception);
                String s = exception.toString();

                if (inetaddress != null) {
                    String s1 = inetaddress + ":" + port;
                    s = s.replaceAll(s1, "");
                }

                if (Manager.Feature.isFeatureEnabled(AutoReconnectFeature.class)) {
                    MixinGuiConnecting.this.mc.displayGuiScreen(new GuiDisconnectedAuto(MixinGuiConnecting.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{s}), AutoReconnectFeature.delay, AutoReconnectFeature.serverData));
                }
                else {
                    MixinGuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(MixinGuiConnecting.this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{s})));
                }
            }
        }).start();
    }
}
