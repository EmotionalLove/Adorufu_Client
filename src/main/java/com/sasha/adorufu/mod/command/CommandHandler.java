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

package com.sasha.adorufu.mod.command;

import com.sasha.adorufu.mod.events.adorufu.AdorufuCommandEvent;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;

import static com.sasha.adorufu.mod.AdorufuMod.COMMAND_PROCESSOR;

/**
 * Created by Sasha on 08/08/2018 at 7:51 AM
 **/
public class CommandHandler implements SimpleListener {

    @SimpleEventHandler
    public void onAdorufuCommand(AdorufuCommandEvent e) {
        if (e.getMsg().startsWith(COMMAND_PROCESSOR.getCommandPrefix()))
            COMMAND_PROCESSOR.processCommand(e.getMsg());
    }
}
