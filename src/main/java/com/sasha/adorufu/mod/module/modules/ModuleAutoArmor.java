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
import com.sasha.adorufu.mod.module.AdorufuCategory;
import com.sasha.adorufu.mod.module.AdorufuModule;
import com.sasha.adorufu.mod.module.ModuleInfo;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Sasha at 11:03 AM on 8/28/2018
 * im like half asleep so liek i dont care if its a messs lol
 */
@ModuleInfo(description = "automatically switches armour to better variants in your inventory")
public class ModuleAutoArmor extends AdorufuModule {

    public ModuleAutoArmor() {
        super("AutoArmor", AdorufuCategory.COMBAT, false);
    }

    @Override
    public void onTick() {
        if (!this.isEnabled()) return;
        ItemStack helmet = AdorufuMod.minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ItemStack chest = AdorufuMod.minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack legs = AdorufuMod.minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        ItemStack feet = AdorufuMod.minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (AdorufuMod.minecraft.currentScreen == null) {
            if (isHelmet(helmet.getItem())) {
                upgradeHelmetCheck(helmet, ArmorType.HELMET);
            } else {
                upgradeHelmetCheck(null, ArmorType.HELMET);
            }
            if (isChestplate(chest.getItem())) {
                upgradeHelmetCheck(chest, ArmorType.TOP);
            } else {
                upgradeHelmetCheck(null, ArmorType.TOP);
            }
            if (isLegging(legs.getItem())) {
                upgradeHelmetCheck(legs, ArmorType.BOTTOM);
            } else {
                upgradeHelmetCheck(null, ArmorType.BOTTOM);
            }
            if (isBoot(feet.getItem())) {
                upgradeHelmetCheck(feet, ArmorType.BOOTS);
            } else {
                upgradeHelmetCheck(null, ArmorType.BOOTS);
            }
        }
    }

    public boolean isHelmet(Item item) {
        return item instanceof ItemArmor && ((ItemArmor) item).getEquipmentSlot() == EntityEquipmentSlot.HEAD;
    }

    public boolean isChestplate(Item item) {
        return item instanceof ItemArmor && ((ItemArmor) item).getEquipmentSlot() == EntityEquipmentSlot.CHEST;
    }

    public boolean isLegging(Item item) {
        return item instanceof ItemArmor && ((ItemArmor) item).getEquipmentSlot() == EntityEquipmentSlot.LEGS;
    }

    public boolean isBoot(Item item) {
        return item instanceof ItemArmor && ((ItemArmor) item).getEquipmentSlot() == EntityEquipmentSlot.FEET;
    }

    public void upgradeHelmetCheck(@Nullable ItemStack currentHelmet, ArmorType mode) {
        ItemStack alternativeHelmet = null;
        LinkedHashMap<ItemStack, Integer> helms = new LinkedHashMap<>();
        for (int x = 9; x <= 44; x++) {
            ItemStack s = AdorufuMod.minecraft.player.inventory.getStackInSlot(x);
            if (isHelmet(s.getItem())) {
                helms.put(s, x); // put all helmets in the map for processing, with their respective slot id
                switch (mode) {
                    case HELMET:
                        if (alternativeHelmet == null) {
                            alternativeHelmet = s;
                        }
                        break;
                }

            }
        }
        AtomicReference<ItemStack> betterHelmet = new AtomicReference<>();
        AtomicReference<Integer> betterSlot = new AtomicReference<>();
        betterHelmet.set(null);
        final ItemStack finalAlternativeHelmet = alternativeHelmet;
        helms.forEach((helm, slot) -> {
            if (isBetter(betterHelmet.get() == null ? (currentHelmet == null ? finalAlternativeHelmet : currentHelmet) : betterHelmet.get(), helm, mode)) {
                betterHelmet.set(helm);
                betterSlot.set(slot);
            }
        });
        if (betterHelmet.get() == null) {
            return; // there aren't any helmets better than what the player currently has.
        }
        AdorufuMod.minecraft.playerController.windowClick(0, getSlotId(mode), 0, ClickType.PICKUP, AdorufuMod.minecraft.player);
        AdorufuMod.minecraft.playerController.windowClick(0, betterSlot.get(), 0, ClickType.PICKUP, AdorufuMod.minecraft.player);
        AdorufuMod.minecraft.playerController.windowClick(0, getSlotId(mode), 0, ClickType.PICKUP, AdorufuMod.minecraft.player);
    }

    public boolean isBetter(ItemStack old, ItemStack neew, ArmorType mode) {
        MineralMaterial oldMineral = null;
        MineralMaterial newMineral = null;
        if (old.getItem().getTranslationKey().toLowerCase().contains("leather")) oldMineral = MineralMaterial.LEATHER;
        if (old.getItem().getTranslationKey().toLowerCase().contains("chain")) oldMineral = MineralMaterial.CHAIN;
        if (old.getItem().getTranslationKey().toLowerCase().contains("iron")) oldMineral = MineralMaterial.IRON;
        if (old.getItem().getTranslationKey().toLowerCase().contains("gold")) oldMineral = MineralMaterial.GOLD;
        if (old.getItem().getTranslationKey().toLowerCase().contains("diamond")) oldMineral = MineralMaterial.DIAMOND;
        if (oldMineral == null) {
            return false; // invalid
        }
        if (neew.getItem().getTranslationKey().toLowerCase().contains("leather")) newMineral = MineralMaterial.LEATHER;
        if (neew.getItem().getTranslationKey().toLowerCase().contains("chain")) newMineral = MineralMaterial.CHAIN;
        if (neew.getItem().getTranslationKey().toLowerCase().contains("iron")) newMineral = MineralMaterial.IRON;
        if (neew.getItem().getTranslationKey().toLowerCase().contains("gold")) newMineral = MineralMaterial.GOLD;
        if (neew.getItem().getTranslationKey().toLowerCase().contains("diamond")) newMineral = MineralMaterial.DIAMOND;
        if (newMineral == null) {
            return false; //invalid
        }
        switch (mode) {
            case HELMET:
                if (!isHelmet(old.getItem()) || !isHelmet(neew.getItem())) {
                    return false;
                }
            case TOP:
                if (!isChestplate(old.getItem()) || !isChestplate(neew.getItem())) {
                    return false;
                }
            case BOTTOM:
                if (!isLegging(old.getItem()) || !isLegging(neew.getItem())) {
                    return false;
                }
            case BOOTS:
                if (!isBoot(old.getItem()) || !isBoot(neew.getItem())) {
                    return false;
                }
        }
        if (oldMineral == MineralMaterial.LEATHER) {
            if (newMineral != MineralMaterial.LEATHER) {
                return true; // yas
            }
            return neew.getItemDamage() > old.getItemDamage();
        }
        if (oldMineral == MineralMaterial.CHAIN) {
            if (newMineral == MineralMaterial.LEATHER) {
                return false;
            }
            if (newMineral != MineralMaterial.CHAIN) {
                return true; // yas
            }
            return neew.getItemDamage() > old.getItemDamage();
        }
        if (oldMineral == MineralMaterial.GOLD) {
            if (newMineral == MineralMaterial.LEATHER || newMineral == MineralMaterial.CHAIN) {
                return false;
            }
            if (newMineral != MineralMaterial.GOLD) {
                return true; // yas
            }
            return neew.getItemDamage() > old.getItemDamage();
        }
        if (oldMineral == MineralMaterial.IRON) {
            if (newMineral == MineralMaterial.LEATHER || newMineral == MineralMaterial.CHAIN
                    || newMineral == MineralMaterial.GOLD) {
                return false;
            }
            if (newMineral != MineralMaterial.IRON) {
                return true; // yas
            }
            return neew.getItemDamage() > old.getItemDamage();
        }
        if (newMineral == MineralMaterial.LEATHER || newMineral == MineralMaterial.CHAIN
                || newMineral == MineralMaterial.GOLD || newMineral == MineralMaterial.IRON) {
            return false;
        }
        if (old.isItemEnchanted()) {
            //todo
        }
        return neew.getItemDamage() > old.getItemDamage();
    }

    private int getSlotId(ArmorType mode) {
        switch (mode) {
            case HELMET:
                return 5;
            case TOP:
                return 6;
            case BOTTOM:
                return 7;
            case BOOTS:
                return 8;
        }
        return 5;
    }
}

enum MineralMaterial {
    LEATHER, CHAIN, IRON, GOLD, DIAMOND
}

enum ArmorType {
    HELMET, TOP, BOTTOM, BOOTS
}
// im a shitty person lol