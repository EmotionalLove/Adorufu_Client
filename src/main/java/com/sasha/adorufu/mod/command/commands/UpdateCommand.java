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

package com.sasha.adorufu.mod.command.commands;

import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.sasha.adorufu.mod.AdorufuMod.logErr;

/**
 * Created by Sasha on 08/08/2018 at 7:42 AM
 **/
@SimpleCommandInfo(description = "Opens a browser to the download page for Adorufu", syntax = {""})
public class UpdateCommand extends SimpleCommand {

    public UpdateCommand() {
        super("update");
    }

    @Override
    public void onCommand() {
        if (!Desktop.isDesktopSupported()) {
            logErr(false, "This action isn't supported on your OS");
            return;
        }
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/EmotionalLove/Adorufu_Client/releases/"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
