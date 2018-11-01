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

package com.sasha.adorufu.mod.module.modules;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.events.server.ServerLoadChunkEvent;
import com.sasha.adorufu.mod.misc.AdorufuRender;
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sasha at 8:10 PM on 9/22/2018
 * ok so this module is way too harsh on the computer rn...
 */
@ModuleInfo(description = "Highlights blocks that were added since the last time you were in that chunk")
public class ModuleChunkCheck extends AdorufuModule implements SimpleListener {

    protected static volatile ArrayList<ChunkCheckData> chunkDatas = new ArrayList<>();
    private static ArrayList<BlockPos> changedBlocks = new ArrayList<>();

    public static boolean didPrep = false;

    public ModuleChunkCheck() {
        super("ChunkCheck", AdorufuCategory.RENDER, false);
    }

    @Override
    public void onEnable() {
        if (AdorufuMod.minecraft.getCurrentServerData() == null) {
            this.toggle();
            return;
        }
        chunkDatas = new ArrayList<>();
        new Thread(() -> {
            chunkDatas.clear();
            ModuleChunkCheckData.loadData(AdorufuMod.minecraft.getCurrentServerData().serverIP);
            AdorufuMod.logMsg(false, "Loaded " + chunkDatas.size() + " entries");
            didPrep = true;
        }).start();
    }

    @Override
    public void onTick() {
        if (chunkDatas.isEmpty()) {
            onEnable();
        }

    }

    @Override
    public void onRender() {
        if (!this.isEnabled() || changedBlocks == null || changedBlocks.isEmpty()) return;
        changedBlocks.forEach(pos -> AdorufuRender.ghostBlock(pos.x, pos.y, pos.z, 0.2f, 1.0f, 0.5f, 0.4f));
    }

    @SimpleEventHandler
    public void onChunkLoad(ServerLoadChunkEvent e) {
        if (!this.isEnabled()) return;
        new Thread(() -> {
            boolean havePreviousChunk = false;
            ChunkCheckData prevCachedChunk = null;
            for (ChunkCheckData chunkData : chunkDatas) {
                if (chunkData.chunkXPos == e.getChunkX() && chunkData.chunkZPos == e.getChunkZ()) {
                    havePreviousChunk = true;
                    prevCachedChunk = chunkData;
                    break;
                }
            }
            // make a new chunk obj to compare to the old one
            ChunkCheckData thisNewChunk = new ChunkCheckData(e.getChunkX(), e.getChunkZ());
            for (int x = (e.getChunkX() * 16); x < (e.getChunkX() * 16) + 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = (e.getChunkZ() * 16); z < (e.getChunkZ() * 16) + 16; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        //Block block = AdorufuMod.minecraft.world.getBlockState(pos).getBlock();
                        thisNewChunk.add(pos);
                    }
                }
            }
            if (!havePreviousChunk) {
                if (AdorufuMod.minecraft.getCurrentServerData() == null) {
                    this.toggle();
                    return;
                }
                /* {
                    //ModuleChunkCheckData.saveData(thisNewChunk, AdorufuMod.minecraft.getCurrentServerData().serverIP);
                } catch (IOException e1) {
                    AdorufuMod.logErr(false, "Error occured while saving check data " + e1.getMessage());
                    e1.printStackTrace();
                }*/
                return;
            }
            // compare
            List<BlockPos> newBlocks = thisNewChunk.populate();
            List<BlockPos> prevBlocks = prevCachedChunk.populate();
            for (BlockPos newBlock : newBlocks) {
                if (!prevBlocks.contains(newBlock)) {
                    // new block
                    AdorufuMod.logMsg(false, "changge cblock");
                    changedBlocks.add(newBlock);
                }
            }
        }).start();
    }
}

class ModuleChunkCheckData {

    public static void saveData(ChunkCheckData data, String ip) throws IOException {
        if (!ModuleChunkCheck.chunkDatas.contains(data)) ModuleChunkCheck.chunkDatas.add(data);
        File dir = new File("checkdata");
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (!dir.isDirectory()) {
            dir.delete();
            dir.mkdir();
        }
        String ipformat = ip.toLowerCase().replace("_", "-").replace(".", "_");
        File ipDir = new File(dir, ipformat);
        if (!ipDir.exists()) {
            ipDir.mkdir();
            return;
            // no data
        }
        if (!ipDir.isDirectory()) {
            ipDir.delete();
            ipDir.mkdir();
            return;
            // no data
        }
        File file = new File(ipDir, data.chunkXPos + "_" + data.chunkZPos + ".mcdat");
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(data);
        //AdorufuMod.logMsg("Wrote ChunkCheck data for " + ip + ", " + data.chunkXPos + " " + data.chunkZPos);
        oos.close();
    }

    public static void loadData(String ip) {
        File dir = new File("checkdata");
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (!dir.isDirectory()) {
            dir.delete();
            dir.mkdir();
        }
        String ipformat = ip.toLowerCase().replace("_", "-").replace(".", "_");
        File ipDir = new File(dir, ipformat);
        if (!ipDir.exists()) {
            ipDir.mkdir();
            return;
            // no data
        }
        if (!ipDir.isDirectory()) {
            ipDir.delete();
            ipDir.mkdir();
            return;
            // no data
        }
        File[] datas = ipDir.listFiles();
        if (datas == null) {
            return;
            // no data
        }
        Arrays.stream(datas).filter(f -> f.getName().endsWith(".mcdat")).forEach(dat -> {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dat));
                ChunkCheckData obj = (ChunkCheckData) ois.readObject();
                ModuleChunkCheck.chunkDatas.add(obj);
                //AdorufuMod.logMsg("Loaded ChunkCheck data for " + ip + ", " + obj.chunkXPos + " " + obj.chunkZPos);
            } catch (IOException | ClassNotFoundException e) {
                return;
            }
        });
    }

}

class ChunkCheckData implements Serializable {

    private String serverIp;
    final Integer chunkXPos;
    final Integer chunkZPos;
    /**
     * ugh this needs to be serialisable and i dont think blockpos is serialisable
     */
    private List<Integer> blockX = new ArrayList<>();
    private List<Integer> blockY = new ArrayList<>();
    private List<Integer> blockZ = new ArrayList<>();

    public ChunkCheckData(int chunkXPos, int chunkZPos) {
        this.chunkXPos = chunkXPos;
        this.chunkZPos = chunkZPos;
    }

    public void add(int x, int y, int z) {
        this.blockX.add(x);
        this.blockY.add(y);
        this.blockZ.add(z);
    }

    public void add(BlockPos pos) {
        this.blockX.add(pos.x);
        this.blockY.add(pos.y);
        this.blockZ.add(pos.z);
    }

    public final List<Integer> getXBlocks() {
        return blockX;
    }

    public final List<Integer> getYBlocks() {
        return blockY;
    }

    public final List<Integer> getZBlocks() {
        return blockZ;
    }

    public List<BlockPos> populate() {
        List<BlockPos> list = new ArrayList<>();
        for (Integer x : this.blockX) {
            for (Integer y : this.blockY) {
                for (Integer z : this.blockZ) {
                    BlockPos pos = new BlockPos(x, y, z);
                    list.add(pos);
                }
            }
        }
        return list;
    }

}
