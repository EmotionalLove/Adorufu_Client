package com.sasha.adorufu.mod.events;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.client.ClientInputUpdateEvent;
import com.sasha.adorufu.mod.events.client.ClientOverlayRenderEvent;
import com.sasha.eventsys.SimpleEventManager;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Defines Forge Event Subscribers used to redirect to the primary {@link SimpleEventManager}
 *
 * @author Brady
 * @since 10/31/2018
 */
public enum ForgeEventHandler {

    /**
     * Singleton Instance
     */
    INSTANCE;

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
