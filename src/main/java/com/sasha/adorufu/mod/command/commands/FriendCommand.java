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

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
/**
 * Created by Sasha on 08/08/2018 at 9:26 PM
 **/
@SimpleCommandInfo(description = "Add or remove friends from your friendlist", syntax = {"<'add'/'del'> <player>"})
public class FriendCommand extends SimpleCommand {
    public FriendCommand() {
        super("friend");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 2){
            AdorufuMod.logErr(false, "Arguments required! Try \"-help command friend\"");
            return;
        }
        switch (this.getArguments()[0].toLowerCase()) {
            case "add":
                if (AdorufuMod.FRIEND_MANAGER.isFriended(this.getArguments()[1])) {
                    AdorufuMod.logErr(false, "That person is already friended!");
                    return;
                }
                AdorufuMod.FRIEND_MANAGER.addFriend(this.getArguments()[1]);
                AdorufuMod.logMsg(false, this.getArguments()[1] + " successfully added");
                AdorufuMod.scheduler.schedule(() -> {
                    try { AdorufuMod.DATA_MANAGER.saveFriends(AdorufuMod.FRIEND_MANAGER.getFriendList()); } catch (IOException e) { e.printStackTrace(); }
                }, 0, TimeUnit.NANOSECONDS);
                break;
            case "del":
                if (!AdorufuMod.FRIEND_MANAGER.isFriended(this.getArguments()[1])) {
                    AdorufuMod.logErr(false, "That person isn't friended!");
                    return;
                }
                AdorufuMod.FRIEND_MANAGER.removeFriend(this.getArguments()[1]);
                AdorufuMod.logMsg(false, this.getArguments()[1] + " successfully removed");
                AdorufuMod.scheduler.schedule(() -> {
                    try { AdorufuMod.DATA_MANAGER.saveFriends(AdorufuMod.FRIEND_MANAGER.getFriendList()); } catch (IOException e) { e.printStackTrace(); }
                }, 0, TimeUnit.NANOSECONDS);
                break;
        }
    }
}
