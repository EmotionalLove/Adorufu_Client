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
import com.sasha.adorufu.mod.events.client.ClientEntityCollideEvent;
import com.sasha.adorufu.mod.events.client.ClientPushOutOfBlocksEvent;
import com.sasha.adorufu.mod.events.client.EntityMoveEvent;
import com.sasha.adorufu.mod.events.playerclient.PlayerKnockbackEvent;
import com.sasha.adorufu.mod.feature.impl.FreecamFeature;
import com.sasha.adorufu.mod.feature.impl.SafewalkFeature;
import com.sasha.adorufu.mod.misc.Manager;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = 999)
public abstract class MixinEntity {

    @Shadow
    public int entityId;

    @Shadow
    public World world;

    @Shadow
    public abstract String getName();

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo info) {
        EntityMoveEvent event = new EntityMoveEvent(this.world, this.entityId, type, x, y, z);
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) return;
        x = event.getX();
        y = event.getY();
        z = event.getZ();
    }

    @Inject(method = "isInsideOfMaterial", at = @At("HEAD"), cancellable = true)
    public void isInsideOfMaterial(Material materialIn, CallbackInfoReturnable<Boolean> info) {
        if (Manager.Feature.isFeatureEnabled(FreecamFeature.class)) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void applyEntityCollision(Entity entityIn, CallbackInfo info) {
        ClientEntityCollideEvent event = new ClientEntityCollideEvent(entityIn);
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    protected void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        ClientPushOutOfBlocksEvent event = new ClientPushOutOfBlocksEvent(x, y, z);
        AdorufuMod.EVENT_MANAGER.invokeEvent(event);
        if (event.isCancelled()) {
            info.setReturnValue(false);
            info.cancel();
        }
    }

    @Inject(
            method = "setVelocity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preSetVelocity(double x, double y, double z, CallbackInfo ci) {
        // >mixin doesn't allow this == mc.player
        // (thinking)
        // mfw
        if (this.equals(AdorufuMod.minecraft.player)) {
            PlayerKnockbackEvent event = new PlayerKnockbackEvent(x, y, z);
            AdorufuMod.EVENT_MANAGER.invokeEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    /**
     * Makes safewalk work (thanks brady)
     *
     * @param self ze entity
     */
    @Redirect(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/entity/Entity.isSneaking()Z",
                    ordinal = 0
            )
    )
    private boolean isSneaking(Entity self) {
        if (self instanceof EntityPlayerSP) { // dont make other players(?) safewalk cuz they cant
            // im not sure if entityPlayermp counts as a player movertype
            return self.isSneaking() || Manager.Feature.isFeatureEnabled(SafewalkFeature.class);
        } else {
            return self.isSneaking();
        }
    }
}
