package io.anionianonion.megacompat.enchantment;

import io.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.elementals_api.data_classes.ElementCategory;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.util.Helper;
import io.anionianonion.megacompat.util.ModTags;
import io.anionianonion.megacompat.util.OtherModsTags;
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
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;


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

        var elements = ElementalsAPI.getAllElementNames();
        var weapons = AdvancedARPGAttributesAPI.getValidWeaponTags();

        for(String elementName : elements) {
            //increased-element-damage
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_DAMAGE, RLPatterns.Enchantments.INCREASED_ELEMENT_DAMAGE, ItemTags.WEAPON_ENCHANTABLE, OtherModsTags.WEAPONS, 5, 10, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(0.15f), AttributeModifier.Operation.ADD_MULTIPLIED_BASE,  null, elementName);

            //added-element-projectile-attack-damage (base), increased and more doesn't exist (maybe it should?)
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_PROJECTILE_ATTACK_DAMAGE, RLPatterns.Enchantments.ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE, ModTags.PROJECTILE_WEAPONS_ENCHANTABLE, OtherModsTags.PROJECTILE_WEAPONS, 5, 10, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(1f, 0.5f), AttributeModifier.Operation.ADD_VALUE,  null, elementName);

            //element immunity
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_IMMUNITY, RLPatterns.Enchantments.ELEMENT_IMMUNITY, ItemTags.ARMOR_ENCHANTABLE, OtherModsTags.ARMOR, 5, 1, 2,
                    Enchantment.constantCost(50), Enchantment.constantCost(100), EquipmentSlotGroup.ARMOR, LevelBasedValue.constant(1f), AttributeModifier.Operation.ADD_VALUE,  null, elementName);

            //added-element-attack-damage (base), increased and more doesn't exist (maybe it should?)
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_ATTACK_DAMAGE, RLPatterns.Enchantments.ADDED_ELEMENT_ATTACK_DAMAGE, ItemTags.WEAPON_ENCHANTABLE, OtherModsTags.WEAPONS, 5, 10, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(1f, 0.5f), AttributeModifier.Operation.ADD_VALUE,  null, elementName);

            //added-weapon-element-attack-damage (base), increased and more doesn't exist (maybe it should?)
            for(var weapon : weapons) {
                var supportedItemsTag = ModTags.WEAPON_TYPE_IDS_TO_ENCHANTABLE_TAGS.get(weapon);
                var primaryItemsTag = ModTags.WEAPON_TYPE_IDS_TO_ACTUAL_TAGS.get(weapon);

                if(supportedItemsTag == null || primaryItemsTag == null) continue;

                makeAttributeEnchant(context, RLPatterns.Attributes.WEAPON_ELEMENT_ATTACK_DAMAGE, RLPatterns.Enchantments.ADDED_WEAPON_ELEMENT_ATTACK_DAMAGE, supportedItemsTag, primaryItemsTag, 5, 10, 2,
                        Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(1f, 0.5f), AttributeModifier.Operation.ADD_VALUE, null, weapon, elementName);
            }

            //added-element-spell-damage (base), increased and more doesn't exist (maybe it should?)
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_SPELL_DAMAGE, RLPatterns.Enchantments.ADDED_ELEMENT_SPELL_DAMAGE, io.redspace.ironsspellbooks.util.ModTags.CAN_BE_IMBUED, io.redspace.ironsspellbooks.util.ModTags.CAN_BE_IMBUED, 5, 10, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(2f, 1f), AttributeModifier.Operation.ADD_VALUE, null, elementName);
        }

        var elementNamesMinusPhysical = new HashSet<>(elements);
        elementNamesMinusPhysical.remove("physical");

        for(String elementName : elementNamesMinusPhysical) {
            //added-element-melee-attack-damage
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_MELEE_ATTACK_DAMAGE, RLPatterns.Enchantments.ADDED_ELEMENT_MELEE_ATTACK_DAMAGE, ModTags.MELEE_WEAPONS_ENCHANTABLE, OtherModsTags.MELEE_WEAPONS, 5, 10, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(1f, 0.5f), AttributeModifier.Operation.ADD_VALUE, null, elementName);

            //added element resistance
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_RESISTANCE, RLPatterns.Enchantments.ADDED_ELEMENT_RESISTANCE, ItemTags.ARMOR_ENCHANTABLE, OtherModsTags.ARMOR, 5, 10, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(0.25f, 0.09f), AttributeModifier.Operation.ADD_VALUE, null, elementName);

            //added element penetration
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_PENETRATION, RLPatterns.Enchantments.ADDED_ELEMENT_PENETRATION, ItemTags.WEAPON_ENCHANTABLE, OtherModsTags.WEAPONS, 5, 4, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(0.10f, 0.05f), AttributeModifier.Operation.ADD_VALUE, null, elementName);

            //added element exposure
            //todo: add keys to ModTags.EXPOSURE_ENCHANTMENT_EXCLUSIVE
            makeAttributeEnchant(context, RLPatterns.Attributes.ELEMENT_EXPOSURE, RLPatterns.Enchantments.ADDED_ELEMENT_EXPOSURE, ItemTags.LEG_ARMOR_ENCHANTABLE, OtherModsTags.LEG_ARMOR, 5, 4, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(0.10f, 0.05f), AttributeModifier.Operation.ADD_VALUE, ModTags.EXPOSURE_ENCHANTMENT_EXCLUSIVE, elementName);

        }

        for(String weaponName : weapons) {
            var supportedItemsTag = ModTags.WEAPON_TYPE_IDS_TO_ENCHANTABLE_TAGS.get(weaponName);
            var primaryItemsTag = ModTags.WEAPON_TYPE_IDS_TO_ACTUAL_TAGS.get(weaponName);

            if(supportedItemsTag == null || primaryItemsTag == null) continue;

            makeAttributeEnchant(context, RLPatterns.Attributes.WEAPON_DAMAGE, RLPatterns.Enchantments.INCREASED_WEAPON_DAMAGE, supportedItemsTag, primaryItemsTag, 5, 4, 2,
                    Enchantment.dynamicCost(10, 20), Enchantment.dynamicCost(60, 20), EquipmentSlotGroup.MAINHAND, LevelBasedValue.perLevel(0.10f, 0.05f), AttributeModifier.Operation.ADD_VALUE, null, weaponName);
        }
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {

        registerElementalCategoryDamageEnchantments(context);
        registerElementalDamageEnchantments(context);

    }

    private static void registerElementalCategoryDamageEnchantments(BootstrapContext<Enchantment> context) {
        initElementalCategoryDamageEnchantmentKeys(context);
        for(var enchantmentResourceKey : ELEMENTAL_CATEGORY_TO_ENCHANTMENT_BUILDER.keySet()) {
            Enchantment.Builder builder = ELEMENTAL_CATEGORY_TO_ENCHANTMENT_BUILDER.get(enchantmentResourceKey);
            register(context, enchantmentResourceKey, builder);

        }
    }

    /**
     If error is "Caused by: java.util.concurrent.CompletionException: java.util.MissingFormatArgumentException: Format specifier '%s'",
     make sure there are enough args in RLPatternReplacements.

     For costs and levelBasedValues: Enchantment ; LevelBasedValue.
     */
    public static void makeAttributeEnchant(BootstrapContext<Enchantment> context,
                                            String attributeRLPattern, String enchantmentRLPattern,
                                            TagKey<Item> supportedItems, TagKey<Item> primaryItems,
                                            int enchantmentWeight, int enchantmentMaxLevel, int anvilCost,
                                            Enchantment.Cost minCost, Enchantment.Cost maxCost,
                                            EquipmentSlotGroup slotGroup,
                                            LevelBasedValue levelBasedValue, AttributeModifier.Operation operation,
                                            @Nullable TagKey<Enchantment> exclusiveEnchantTag,
                                            @NotBlank String... RLPatternReplacements) {

        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        var attributeRL = Helper.makeRL(attributeRLPattern, RLPatternReplacements);
        var attribute = BuiltInRegistries.ATTRIBUTE.get(attributeRL);

        if(attribute != null) {
            var attributeHolder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
            var enchantmentRK = Helper.makeEnchantmentRK(enchantmentRLPattern, RLPatternReplacements);
            var enchantment = Enchantment.enchantment(
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
                                    Helper.makeRL(enchantmentRLPattern, RLPatternReplacements),
                                    attributeHolder,
                                    levelBasedValue,
                                    operation)
                    );

            if(exclusiveEnchantTag != null) {
                enchantment = enchantment.exclusiveWith(enchantments.getOrThrow(exclusiveEnchantTag));
            }

            register(context, enchantmentRK, enchantment);
        }
    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
        registry.register(resourceKey, builder.build(resourceKey.location()));
    }
}
