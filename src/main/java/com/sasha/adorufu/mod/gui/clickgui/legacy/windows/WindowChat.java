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

package com.sasha.adorufu.mod.gui.clickgui.legacy.windows;

import com.sasha.adorufu.mod.gui.clickgui.legacy.elements.AdorufuWindow;
import com.sasha.adorufu.mod.gui.clickgui.legacy.elements.WindowType;

import static com.sasha.adorufu.mod.module.AdorufuCategory.CHAT;

public class WindowChat extends AdorufuWindow {
	
	public WindowChat() {
		super("Chat", 206, 12, true, WindowType.MODULE);
		this.loadButtonsFromCategory(CHAT);
	}
}

