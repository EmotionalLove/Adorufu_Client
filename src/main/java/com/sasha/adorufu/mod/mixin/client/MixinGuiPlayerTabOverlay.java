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
import com.sasha.adorufu.mod.feature.impl.ClientIgnoreFeature;
import com.sasha.adorufu.mod.feature.impl.ExtendedTablistFeature;
import com.sasha.adorufu.mod.feature.impl.NameProtectFeature;
import com.sasha.adorufu.mod.misc.Manager;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = GuiPlayerTabOverlay.class, priority = 999)
public abstract class MixinGuiPlayerTabOverlay {

    private List<NetworkPlayerInfo> adorufu$LastPlayerInfoList;

    @Inject(
            method = "renderPlayerlist",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "com/google/common/collect/Ordering.sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void postNetworkInfoSort(int width, Scoreboard scoreboard, ScoreObjective objective, CallbackInfo ci, NetHandlerPlayClient netHandlerPlayClient, List<NetworkPlayerInfo> list) {
        this.adorufu$LastPlayerInfoList = list;

        if (Manager.Feature.isFeatureEnabled(NameProtectFeature.class)) {
            for (NetworkPlayerInfo networkPlayerInfo : list) {
                if (networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(AdorufuMod.minecraft.player.getName())) {
                    list.remove(networkPlayerInfo);
                    break;
                }
            }
        }
        for (NetworkPlayerInfo networkPlayerInfo : list) {
            if (ClientIgnoreFeature.ignorelist.contains(networkPlayerInfo.getGameProfile().getName())) {
                networkPlayerInfo.setDisplayName(new TextComponentString("\2478" + networkPlayerInfo.getGameProfile().getName()));
                continue;
            }
            if (AdorufuMod.FRIEND_MANAGER.getFriendListString().contains(networkPlayerInfo.getGameProfile().getName())) {
                networkPlayerInfo.setDisplayName(new TextComponentString("\247b" + networkPlayerInfo.getGameProfile().getName()));
            }
        }
    }

    @Redirect(
            method = "renderPlayerlist",
            at = @At(
                    value = "INVOKE",
                    target = "java/util/List.subList(II)Ljava/util/List;"
            )
    )
    private List<NetworkPlayerInfo> onTabCutoff(List<NetworkPlayerInfo> list, int startIndex, int toIndex) {
        boolean exTab = Manager.Feature.isFeatureEnabled(ExtendedTablistFeature.class);
        return list.subList(startIndex, exTab ? Math.min(list.size(), 256) : toIndex);
    }

    @ModifyConstant(
            method = "renderPlayerlist",
            constant = @Constant(
                    intValue = 20
            )
    )
    private int getDumbCompareThing(int i) {
        boolean exTab = Manager.Feature.isFeatureEnabled(ExtendedTablistFeature.class);
        return (exTab && this.adorufu$LastPlayerInfoList.size() > 80) ? 25 : i;
    }
}
