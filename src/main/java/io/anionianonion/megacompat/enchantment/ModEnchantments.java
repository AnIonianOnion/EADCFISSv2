package io.anionianonion.megacompat.enchantment;

import io.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.elementals_api.data_classes.ElementCategory;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
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
import net.minecraft.world.item.Item;
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

    private static void registerElementalDamageEnchantments(BootstrapContext<Enchantment> context) {
        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        for(String elementName : ElementalsAPI.getAllElementNames()) {
            //increased element damage
            var elementDamageAttributeResourceLocation = Helper.makeRL(RLPatterns.Attributes.ELEMENT_DAMAGE, elementName);
            var elementDamageAttribute = BuiltInRegistries.ATTRIBUTE.get(elementDamageAttributeResourceLocation);
            if(elementDamageAttribute != null) {
                var elementalDamageAttributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(elementDamageAttribute);
                var elementalDamageEnchantmentResourceKey = Helper.makeEnchantmentRK(RLPatterns.Enchantments.INCREASED_ELEMENT_DAMAGE, elementName);
                register(context, elementalDamageEnchantmentResourceKey, Enchantment.enchantment(
                        Enchantment.definition(
                                items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                5, 10,
                                Enchantment.dynamicCost(10, 20),
                                Enchantment.dynamicCost(60, 20),
                                2, EquipmentSlotGroup.MAINHAND))
                        .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        Helper.makeRL(RLPatterns.Enchantments.INCREASED_ELEMENT_DAMAGE, elementName),
                                        elementalDamageAttributeHolder,
                                        LevelBasedValue.perLevel(0.15f),
                                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                        )
                );
            }

            //element projectile attack damage (base added), increased and more doesn't exist
            var projectileElementalAttackDamageAttributeResourceLocation = Helper.makeRL(RLPatterns.Attributes.ELEMENT_PROJECTILE_ATTACK_DAMAGE, elementName);
            var projectileElementalAttackDamageAttribute = BuiltInRegistries.ATTRIBUTE.get(projectileElementalAttackDamageAttributeResourceLocation);
            if(projectileElementalAttackDamageAttribute != null) {
                var projectileElementalAttackDamageAttributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(projectileElementalAttackDamageAttribute);
                var projectileElementalAttackDamageEnchantmentResourceKey = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE, elementName);
                register(context, projectileElementalAttackDamageEnchantmentResourceKey,
                        Enchantment.enchantment(
                                Enchantment.definition(
                                        items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.parse(String.format("%s:projectile_weapons", AnIonianOnionsDamageMegacompatMod.MOD_ID)))),
                                        items.getOrThrow(TagKey.create(Registries.ITEM, ResourceLocation.parse(String.format("%s:projectile_weapons", AnIonianOnionsDamageMegacompatMod.MOD_ID)))),
                                        5, 10,
                                        Enchantment.dynamicCost(10, 20),
                                        Enchantment.dynamicCost(60, 20),
                                        2, EquipmentSlotGroup.MAINHAND))
                                .withEffect(EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(Helper.makeRL(RLPatterns.Enchantments.ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE, elementName), projectileElementalAttackDamageAttributeHolder, LevelBasedValue.perLevel(1f, 0.5f), AttributeModifier.Operation.ADD_VALUE)));
            }

            //element immunity
            var elementImmunityAttributeResourceLocation = Helper.makeRL(RLPatterns.Attributes.ELEMENT_IMMUNITY, elementName);
            var elementImmunityAttribute = BuiltInRegistries.ATTRIBUTE.get(elementImmunityAttributeResourceLocation);
            if(elementImmunityAttribute != null) {
                var elementImmunityAttributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(elementImmunityAttribute);
                var elementImmunityEnchantmentResourceKey = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ELEMENT_IMMUNITY, elementName);
                register(context, elementImmunityEnchantmentResourceKey,
                        Enchantment.enchantment(
                                Enchantment.definition(
                                        items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                                        5,
                                        1,
                                        Enchantment.dynamicCost(10, 0),
                                        Enchantment.dynamicCost(100, 0),
                                        2, EquipmentSlotGroup.ARMOR))
                        .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        Helper.makeRL(RLPatterns.Enchantments.ELEMENT_IMMUNITY, elementName),
                                        elementImmunityAttributeHolder,
                                        LevelBasedValue.constant(1f),
                                        AttributeModifier.Operation.ADD_VALUE)
                        )
                );
            }

            //added-values
            var elementAttackDamageAttributeResourceLocation = Helper.makeRL(RLPatterns.Attributes.ELEMENT_ATTACK_DAMAGE, elementName);
            var elementAttackDamageAttribute = BuiltInRegistries.ATTRIBUTE.get(elementAttackDamageAttributeResourceLocation);
            if(elementAttackDamageAttribute != null) {
                var elementAttackDamageAttributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(elementAttackDamageAttribute);
                var addedElementAttackDamageEnchantmentResourceKey = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENT_ATTACK_DAMAGE, elementName);
                register(context, addedElementAttackDamageEnchantmentResourceKey,
                        Enchantment.enchantment(
                                Enchantment.definition(
                                        items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                                        5,
                                        10,
                                        Enchantment.dynamicCost(10, 20),
                                        Enchantment.dynamicCost(60, 20),
                                        2, EquipmentSlotGroup.MAINHAND))
                        .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                                new EnchantmentAttributeEffect(
                                        Helper.makeRL(RLPatterns.Enchantments.ADDED_ELEMENT_ATTACK_DAMAGE, elementName),
                                        elementAttackDamageAttributeHolder,
                                        LevelBasedValue.perLevel(1f, 0.5f),
                                        AttributeModifier.Operation.ADD_VALUE)
                        )
                );
            }
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

    public static void makeAttributeEnchant(BootstrapContext<Enchantment> context,
                                            String attributeRLPattern, String enchantmentRLPattern,
                                            TagKey<Item> supportedItems, TagKey<Item> primaryItems,
                                            int enchantmentWeight, int enchantmentMaxLevel,
                                            Enchantment.Cost minCost, Enchantment.Cost maxCost,
                                            int anvilCost, EquipmentSlotGroup slotGroup,
                                            LevelBasedValue levelBasedValue, AttributeModifier.Operation operation,
                                            String... RLPatternRplacements) {

        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        var attributeRL = Helper.makeRL(attributeRLPattern, RLPatternRplacements);
        var attribute = BuiltInRegistries.ATTRIBUTE.get(attributeRL);

        if(attribute != null) {
            var attributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
            var enchantmentRK = Helper.makeEnchantmentRK(enchantmentRLPattern, RLPatternRplacements);
            register(context, enchantmentRK,
                    Enchantment.enchantment(
                                    Enchantment.definition(
                                            items.getOrThrow(supportedItems),
                                            items.getOrThrow(primaryItems),
                                            enchantmentWeight,
                                            enchantmentMaxLevel,
                                            minCost,
                                            maxCost,
                                            anvilCost, slotGroup))
                            .withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                                    new EnchantmentAttributeEffect(
                                            Helper.makeRL(enchantmentRLPattern, RLPatternRplacements),
                                            attributeHolder,
                                            levelBasedValue,
                                            operation)
                            )
            );
        }
    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
        registry.register(resourceKey, builder.build(resourceKey.location()));
    }
}
