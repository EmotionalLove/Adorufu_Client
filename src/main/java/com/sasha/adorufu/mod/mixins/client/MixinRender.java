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

import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.adorufu.mod.feature.impl.deprecated.ModuleNamePlates;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Render.class, priority = 999)
public abstract class MixinRender {

    @Shadow @Final protected RenderManager renderManager;

    @Shadow public abstract FontRenderer getFontRendererFromRenderManager();

    /**
     * @author s
     * @reason x
     */
    @Overwrite
    protected void renderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance)
    {
        double d0 = entityIn.getDistanceSq(this.renderManager.renderViewEntity);

        if (d0 <= (double)(maxDistance * maxDistance))
        {
            boolean flag = entityIn.isSneaking();
            float f = this.renderManager.playerViewY;
            float f1 = this.renderManager.playerViewX;
            boolean flag1 = this.renderManager.options.thirdPersonView == 2;
            float f2 = entityIn.height + 0.5F - (flag ? 0.25F : 0.0F);
            int i = "deadmau5".equals(str) ? -10 : 0;
            String nameplatestr = str;
            if ((entityIn instanceof EntityOtherPlayerMP) && Manager.Module.getModule("NamePlates").isEnabled()) {
                //todo asuna
                nameplatestr = str + " " + ModuleNamePlates.formatHealthTag(((EntityOtherPlayerMP) entityIn).getHealth());
            }
            EntityRenderer.drawNameplate(this.getFontRendererFromRenderManager(), nameplatestr, (float)x, (float)y + f2, (float)z, i, f, f1, flag1, flag);
        }
    }
}
