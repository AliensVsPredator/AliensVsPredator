package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import org.avp.common.item.AVPArmorItems;
import org.avp.common.item.AVPItems;
import org.avp.common.item.AVPToolItems;
import org.avp.common.item.AVPWeaponItems;
import org.avp.common.registry.AVPDeferredBlockRegistry;
import org.avp.common.tag.AVPItemTags;

public class AVPItemTagsProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(AVPItemTags.ACID_IMMUNE)
            .add(AVPItems.INSTANCE.royalJelly.get())
            .add(AVPItems.INSTANCE.xenomorphChitin.get())
            .add(AVPArmorItems.INSTANCE.xenomorphHelmet.get())
            .add(AVPArmorItems.INSTANCE.xenomorphBody.get())
            .add(AVPArmorItems.INSTANCE.xenomorphLeggings.get())
            .add(AVPArmorItems.INSTANCE.xenomorphBoots.get());

        getOrCreateTagBuilder(AVPItemTags.GUNS)
            .add(AVPWeaponItems.INSTANCE.weapon3712Shotgun.get())
            .add(AVPWeaponItems.INSTANCE.weaponAk47.get())
            .add(AVPWeaponItems.INSTANCE.weaponF90Rifle.get())
            .add(AVPWeaponItems.INSTANCE.weaponFlamethrowerSevastopol.get())
            .add(AVPWeaponItems.INSTANCE.weaponM4Carbine.get())
            .add(AVPWeaponItems.INSTANCE.weaponM41APulseRifle.get())
            .add(AVPWeaponItems.INSTANCE.weaponM56Smartgun.get())
            .add(AVPWeaponItems.INSTANCE.weaponM83A2Sadar.get())
            .add(AVPWeaponItems.INSTANCE.weaponM88Mod4CombatPistol.get())
            .add(AVPWeaponItems.INSTANCE.weaponOldPainless.get())
            .add(AVPWeaponItems.INSTANCE.weaponSniperRifle.get());

        getOrCreateTagBuilder(AVPItemTags.THREATENS_PREDATORS)
            .addTag(AVPItemTags.GUNS)
            .addOptionalTag(ItemTags.SWORDS)
            .addOptionalTag(ItemTags.AXES)
            .add(Items.TRIDENT);

        getOrCreateTagBuilder(ItemTags.AXES)
            .add(AVPToolItems.INSTANCE.aluminumAxe.get())
            .add(AVPToolItems.INSTANCE.orioniteAxe.get())
            .add(AVPToolItems.INSTANCE.titaniumAxe.get())
            .add(AVPToolItems.INSTANCE.veritaniumAxe.get());

        getOrCreateTagBuilder(ItemTags.HOES)
            .add(AVPToolItems.INSTANCE.aluminumHoe.get())
            .add(AVPToolItems.INSTANCE.orioniteHoe.get())
            .add(AVPToolItems.INSTANCE.titaniumHoe.get())
            .add(AVPToolItems.INSTANCE.veritaniumHoe.get());

        getOrCreateTagBuilder(ItemTags.PICKAXES)
            .add(AVPToolItems.INSTANCE.aluminumPickaxe.get())
            .add(AVPToolItems.INSTANCE.orionitePickaxe.get())
            .add(AVPToolItems.INSTANCE.titaniumPickaxe.get())
            .add(AVPToolItems.INSTANCE.veritaniumPickaxe.get());

        getOrCreateTagBuilder(ItemTags.SHOVELS)
            .add(AVPToolItems.INSTANCE.aluminumShovel.get())
            .add(AVPToolItems.INSTANCE.orioniteShovel.get())
            .add(AVPToolItems.INSTANCE.titaniumShovel.get())
            .add(AVPToolItems.INSTANCE.veritaniumShovel.get());

        getOrCreateTagBuilder(ItemTags.SWORDS)
            .add(AVPToolItems.INSTANCE.aluminumSword.get())
            .add(AVPToolItems.INSTANCE.orioniteSword.get())
            .add(AVPToolItems.INSTANCE.titaniumSword.get())
            .add(AVPToolItems.INSTANCE.veritaniumSword.get());

        AVPDeferredBlockRegistry.getDataEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var blockData = tuple.second();

            blockData.blockTagData().itemTags().forEach(tag -> getOrCreateTagBuilder(tag).add(block.asItem()));
        });
    }
}
