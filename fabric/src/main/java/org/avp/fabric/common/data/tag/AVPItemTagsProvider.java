package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import org.avp.common.item.AVPToolItems;
import org.avp.common.item.AVPWeaponItems;
import org.avp.common.tag.AVPItemTags;

public class AVPItemTagsProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(AVPItemTags.GUNS)
            .add(AVPWeaponItems.INSTANCE.WEAPON_37_12_SHOTGUN.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_AK_47.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_F90_RIFLE.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_FLAMETHROWER_SEVASTOPOL.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_M4_CARBINE.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_M41A_PULSE_RIFLE.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_M56_SMARTGUN.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_M83A2_SADAR.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_M88MOD4_COMBAT_PISTOL.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_OLD_PAINLESS.get())
            .add(AVPWeaponItems.INSTANCE.WEAPON_SNIPER_RIFLE.get());

        getOrCreateTagBuilder(AVPItemTags.THREATENS_PREDATORS)
            .addTag(AVPItemTags.GUNS)
            .addOptionalTag(ItemTags.SWORDS)
            .addOptionalTag(ItemTags.AXES)
            .add(Items.TRIDENT);

        getOrCreateTagBuilder(ItemTags.AXES)
            .add(AVPToolItems.INSTANCE.ALUMINUM_AXE.get())
            .add(AVPToolItems.INSTANCE.ORIONITE_AXE.get())
            .add(AVPToolItems.INSTANCE.TITANIUM_AXE.get())
            .add(AVPToolItems.INSTANCE.VERITANIUM_AXE.get());

        getOrCreateTagBuilder(ItemTags.HOES)
            .add(AVPToolItems.INSTANCE.ALUMINUM_HOE.get())
            .add(AVPToolItems.INSTANCE.ORIONITE_HOE.get())
            .add(AVPToolItems.INSTANCE.TITANIUM_HOE.get())
            .add(AVPToolItems.INSTANCE.VERITANIUM_HOE.get());

        getOrCreateTagBuilder(ItemTags.PICKAXES)
            .add(AVPToolItems.INSTANCE.ALUMINUM_PICKAXE.get())
            .add(AVPToolItems.INSTANCE.ORIONITE_PICKAXE.get())
            .add(AVPToolItems.INSTANCE.TITANIUM_PICKAXE.get())
            .add(AVPToolItems.INSTANCE.VERITANIUM_PICKAXE.get());

        getOrCreateTagBuilder(ItemTags.SHOVELS)
            .add(AVPToolItems.INSTANCE.ALUMINUM_SHOVEL.get())
            .add(AVPToolItems.INSTANCE.ORIONITE_SHOVEL.get())
            .add(AVPToolItems.INSTANCE.TITANIUM_SHOVEL.get())
            .add(AVPToolItems.INSTANCE.VERITANIUM_SHOVEL.get());

        getOrCreateTagBuilder(ItemTags.SWORDS)
            .add(AVPToolItems.INSTANCE.ALUMINUM_SWORD.get())
            .add(AVPToolItems.INSTANCE.ORIONITE_SWORD.get())
            .add(AVPToolItems.INSTANCE.TITANIUM_SWORD.get())
            .add(AVPToolItems.INSTANCE.VERITANIUM_SWORD.get());
    }
}
