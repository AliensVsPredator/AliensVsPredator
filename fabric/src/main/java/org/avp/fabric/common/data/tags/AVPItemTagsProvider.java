package org.avp.fabric.common.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.avp.common.item.AVPToolItems;
import org.avp.common.item.AVPWeaponItems;
import org.avp.common.tag.AVPItemTags;

import java.util.concurrent.CompletableFuture;

public class AVPItemTagsProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(AVPItemTags.GUNS)
            .add(AVPWeaponItems.WEAPON_37_12_SHOTGUN.get())
            .add(AVPWeaponItems.WEAPON_AK_47.get())
            .add(AVPWeaponItems.WEAPON_F90_RIFLE.get())
            .add(AVPWeaponItems.WEAPON_FLAMETHROWER_SEVASTOPOL.get())
            .add(AVPWeaponItems.WEAPON_M4_CARBINE.get())
            .add(AVPWeaponItems.WEAPON_M41A_PULSE_RIFLE.get())
            .add(AVPWeaponItems.WEAPON_M56_SMARTGUN.get())
            .add(AVPWeaponItems.WEAPON_M83A2_SADAR.get())
            .add(AVPWeaponItems.WEAPON_M88MOD4_COMBAT_PISTOL.get())
            .add(AVPWeaponItems.WEAPON_OLD_PAINLESS.get())
            .add(AVPWeaponItems.WEAPON_SNIPER_RIFLE.get());

        getOrCreateTagBuilder(AVPItemTags.THREATENS_PREDATORS)
            .addTag(AVPItemTags.GUNS)
            .addOptionalTag(ItemTags.SWORDS)
            .addOptionalTag(ItemTags.AXES)
            .add(Items.TRIDENT);

        getOrCreateTagBuilder(ItemTags.AXES)
            .add(AVPToolItems.ALUMINUM_AXE.get())
            .add(AVPToolItems.ORIONITE_AXE.get())
            .add(AVPToolItems.TITANIUM_AXE.get())
            .add(AVPToolItems.VERITANIUM_AXE.get());

        getOrCreateTagBuilder(ItemTags.HOES)
            .add(AVPToolItems.ALUMINUM_HOE.get())
            .add(AVPToolItems.ORIONITE_HOE.get())
            .add(AVPToolItems.TITANIUM_HOE.get())
            .add(AVPToolItems.VERITANIUM_HOE.get());

        getOrCreateTagBuilder(ItemTags.PICKAXES)
            .add(AVPToolItems.ALUMINUM_PICKAXE.get())
            .add(AVPToolItems.ORIONITE_PICKAXE.get())
            .add(AVPToolItems.TITANIUM_PICKAXE.get())
            .add(AVPToolItems.VERITANIUM_PICKAXE.get());

        getOrCreateTagBuilder(ItemTags.SHOVELS)
            .add(AVPToolItems.ALUMINUM_SHOVEL.get())
            .add(AVPToolItems.ORIONITE_SHOVEL.get())
            .add(AVPToolItems.TITANIUM_SHOVEL.get())
            .add(AVPToolItems.VERITANIUM_SHOVEL.get());

        getOrCreateTagBuilder(ItemTags.SWORDS)
            .add(AVPToolItems.ALUMINUM_SWORD.get())
            .add(AVPToolItems.ORIONITE_SWORD.get())
            .add(AVPToolItems.TITANIUM_SWORD.get())
            .add(AVPToolItems.VERITANIUM_SWORD.get());
    }
}
