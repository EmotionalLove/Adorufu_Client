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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(value = NetHandlerLoginClient.class, priority = 999)
public class MixinNetHandlerLoginClient {
    @Shadow @Final @Nullable private GuiScreen previousGuiScreen;

    @Shadow @Final private Minecraft mc;

    @Shadow @Final private static Logger LOGGER;

    /**
     * @author Sasha Stevens
     * @param reason autoreconnect
     */
    @Overwrite
    public void onDisconnect(ITextComponent reason)
    {
        if (this.previousGuiScreen != null && this.previousGuiScreen instanceof GuiScreenRealmsProxy)
        {
            this.mc.displayGuiScreen((new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.previousGuiScreen).getProxy(), "connect.failed", reason)).getProxy());
        }
        else
        {
            if (Manager.Feature.isFeatureEnabled(AutoReconnectFeature.class)) {
                this.mc.displayGuiScreen(new GuiDisconnectedAuto(this.previousGuiScreen, "connect.failed", reason, AutoReconnectFeature.delay, AutoReconnectFeature.serverData));
                return;
            }
            this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", reason));
        }
    }
}
