package io.anionianonion.megacompat.enchantment;

import io.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.elementals_api.data_classes.ElementCategory;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.ModAttributes;
import io.anionianonion.megacompat.util.Helper;
import io.anionianonion.megacompat.util.RLPatterns;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

import java.util.HashMap;


//https://www.youtube.com/watch?v=r7sEVrQsGtY&t=318s
public class ModEnchantments {

    private static final HashMap<ResourceKey<Enchantment>, Enchantment.Builder> ELEMENTAL_CATEGORY_TO_ENCHANTMENT_BUILDER = new HashMap<>();

    private static void initElementalCategoryDamageEnchantmentKeys(BootstrapContext<Enchantment> context) {
        var items = context.lookup(Registries.ITEM);

        //increases
        for(ElementCategory elementCategory : ElementalsAPI.getElementCategoriesRegistry().values()) {

            AdvancedARPGAttribute advancedARPGAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, String.format("%s_damage", elementCategory.getId())));
            ResourceKey<Enchantment> resourceKey = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, String.format("increased_element_category_dmg/%s", elementCategory.getId())));

            var attribute = BuiltInRegistries.ATTRIBUTE.get(advancedARPGAttribute.getRl());
            assert attribute != null;
            var holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);

            ELEMENTAL_CATEGORY_TO_ENCHANTMENT_BUILDER.put(resourceKey,
                    Enchantment.enchantment(
                            Enchantment.definition(
                                    items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                    items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                    5,
                                    10,
                                    Enchantment.dynamicCost(3, 5),
                                    Enchantment.dynamicCost(5, 8),
                                    2,
                                    EquipmentSlotGroup.MAINHAND
                            )
                    )
                    .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                            new EnchantmentAttributeEffect(advancedARPGAttribute.getRl(), holder, LevelBasedValue.perLevel(0.15f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    )
            );
        }
    }

    /*
    private static void fillEnchantments(BootstrapContext<Enchantment> context,
                                         Collection<?> registryValues,
                                         HashMap<ResourceKey<Enchantment>, Enchantment.Builder> map) {

        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        for(var a : registryValues) {
            AdvancedARPGAttribute advancedARPGAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, String.format("%s_damage", a.getId())));
            ResourceKey<Enchantment> resourceKey = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, String.format("element_categories/%s_enchantment", a.getId())));

            var attribute = BuiltInRegistries.ATTRIBUTE.get(advancedARPGAttribute.getRl());
            var holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);

            map.put(resourceKey,
                    Enchantment.enchantment(
                                    Enchantment.definition(
                                            items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                            items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                            5,
                                            10,
                                            Enchantment.dynamicCost(3, 5),
                                            Enchantment.dynamicCost(5, 8),
                                            2,
                                            EquipmentSlotGroup.MAINHAND
                                    )
                            )
                            .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                            .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                                    new EnchantmentAttributeEffect(advancedARPGAttribute.getRl(), holder, LevelBasedValue.perLevel(0.15f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                            )
            );
        }
    }
     */

    private static void registerElementalDamageEnchantments(BootstrapContext<Enchantment> context) {
        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        for(String elementName : ElementalsAPI.getAllElementNames()) {
            //increased element damage
            var elementalDamageAttributeResourceLocation = Helper.makeRL(RLPatterns.Attributes.ELEMENT_DAMAGE, elementName);
            var elementalDamageAttribute = BuiltInRegistries.ATTRIBUTE.get(elementalDamageAttributeResourceLocation);
            if(elementalDamageAttribute == null) continue;

            var elementalDamageAttributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(elementalDamageAttribute);
            var elementalDamageEnchantmentResourceKey = Helper.makeEnchantmentRK(RLPatterns.Enchantments.INCREASED_ELEMENT_DAMAGE, elementName);
            register(context, elementalDamageEnchantmentResourceKey, Enchantment.enchantment(Enchantment.definition(items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE), items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE), 5, 10, Enchantment.dynamicCost(3, 5), Enchantment.dynamicCost(5, 8), 2, EquipmentSlotGroup.MAINHAND)).withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(elementalDamageAttributeResourceLocation, elementalDamageAttributeHolder, LevelBasedValue.perLevel(0.15f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE)));

            //element projectile attack damage (base added), increased and more doesn't exist

            var projectileElementalAttackDamageAttributeResourceLocation = Helper.makeRL(RLPatterns.Attributes.ELEMENT_PROJECTILE_ATTACK_DAMAGE, elementName);
                    //ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, String.format("%s_projectile_attack_damage", elementName));
            var projectileElementalAttackDamageAttribute = BuiltInRegistries.ATTRIBUTE.get(projectileElementalAttackDamageAttributeResourceLocation);
            if(projectileElementalAttackDamageAttribute == null) continue;

            var projectileElementalAttackDamageAttributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(projectileElementalAttackDamageAttribute);
            var projectileElementalAttackDamageEnchantmentResourceKey = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE, elementName);
            register(context, projectileElementalAttackDamageEnchantmentResourceKey, Enchantment.enchantment(Enchantment.definition(items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.parse(String.format("%s:projectile_weapons", AnIonianOnionsDamageMegacompatMod.MOD_ID)))), items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.parse(String.format("%s:projectile_weapons", AnIonianOnionsDamageMegacompatMod.MOD_ID)))), 5, 10, Enchantment.dynamicCost(3, 5), Enchantment.dynamicCost(5, 8), 2, EquipmentSlotGroup.MAINHAND)).withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(projectileElementalAttackDamageAttributeResourceLocation, projectileElementalAttackDamageAttributeHolder, LevelBasedValue.perLevel(1f), AttributeModifier.Operation.ADD_VALUE)));

            //element immunity
            var elementImmunityAttributeResourceLocation = Helper.makeRL(RLPatterns.Attributes.ELEMENT_IMMUNITY, elementName);
                    //ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, String.format("%s_immunity", elementName));
            var elementImmunityAttribute = BuiltInRegistries.ATTRIBUTE.get(elementImmunityAttributeResourceLocation);
            if(elementImmunityAttribute == null) continue;

            var elementImmunityAttributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(elementImmunityAttribute);
            var elementImmunityEnchantmentResourceKey = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ELEMENT_IMMUNITY, elementName);
            register(context, elementImmunityEnchantmentResourceKey, Enchantment.enchantment(Enchantment.definition(items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE), items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE), 5, 10, Enchantment.dynamicCost(3, 5), Enchantment.dynamicCost(5, 8), 2, EquipmentSlotGroup.ARMOR)).withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(elementImmunityAttributeResourceLocation, elementImmunityAttributeHolder, LevelBasedValue.perLevel(1f), AttributeModifier.Operation.ADD_VALUE)));
        }
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {

        initElementalCategoryDamageEnchantmentKeys(context);
        for(var enchantmentResourceKey : ELEMENTAL_CATEGORY_TO_ENCHANTMENT_BUILDER.keySet()) {
            Enchantment.Builder builder = ELEMENTAL_CATEGORY_TO_ENCHANTMENT_BUILDER.get(enchantmentResourceKey);
            register(context, enchantmentResourceKey, builder);

        }

        registerElementalDamageEnchantments(context);

    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
        registry.register(resourceKey, builder.build(resourceKey.location()));
    }
}
