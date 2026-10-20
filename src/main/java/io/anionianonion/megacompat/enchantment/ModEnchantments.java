package io.anionianonion.megacompat.enchantment;

import io.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

import java.util.HashMap;


//https://www.youtube.com/watch?v=r7sEVrQsGtY&t=318s
public class ModEnchantments {

    private static final HashMap<AdvancedARPGAttribute, ResourceKey<Enchantment>> ELEMENTAL_CATEGORY_INCREASED_AND_MORE_DAMAGE_ENCHANTMENTS = new HashMap<>();


    private static void initElementalCategoryIncreasedAndMoreDamageEnchantments() {
        ELEMENTAL_CATEGORY_INCREASED_AND_MORE_DAMAGE_ENCHANTMENTS.put
                (AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.parse(String.format("%s:elemental_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID))),
                        ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse(String.format("%s:elemental_damage_enchantment", AnIonianOnionsDamageMegacompatMod.MOD_ID))));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        initElementalCategoryIncreasedAndMoreDamageEnchantments();

        for(var entry : ELEMENTAL_CATEGORY_INCREASED_AND_MORE_DAMAGE_ENCHANTMENTS.entrySet()) {

            var attribute = BuiltInRegistries.ATTRIBUTE.get(entry.getKey().getRl());
            var holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);

            register(context, entry.getValue(), Enchantment.enchantment(Enchantment.definition(
                    items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                    items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                    5,
                    10,
                    Enchantment.dynamicCost(3, 5),
                    Enchantment.dynamicCost(5, 8),
                    2,
                    EquipmentSlotGroup.MAINHAND
            )).exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                    .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                    entry.getKey().getRl(), holder, LevelBasedValue.perLevel(0.15f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            )));
        }

    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
        registry.register(resourceKey, builder.build(resourceKey.location()));
    }
}
