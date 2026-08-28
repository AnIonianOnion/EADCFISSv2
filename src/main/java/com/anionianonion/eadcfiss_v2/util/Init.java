package com.anionianonion.eadcfiss_v2.util;

import com.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import com.anionianonion.damage_pipeline_api.api.DamagePipelineAPI;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.eadcfiss_v2.AnIonianOnionsDamageMegacompatMod;
import com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed.*;
import com.anionianonion.eadcfiss_v2.damage_pipeline.before_hit_confirmed.SpellDodgeStep;
import com.anionianonion.elementals_api.api.ElementalsAPI;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;

import static com.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute.ModifierType.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Init {

    private static void initElements() {
        ElementalsAPI eAPI = new ElementalsAPI();

        eAPI.createElement("physical");
        eAPI.createElement("fire");
        eAPI.createElement("ice");
        eAPI.createElement("lightning");
        eAPI.createElement("holy");
        eAPI.createElement("ender");
        eAPI.createElement("blood");
        eAPI.createElement("nature");
        eAPI.createElement("evocation");
        eAPI.createElement("eldritch");
        eAPI.createElement("sound");
        eAPI.createElement("geo");
        eAPI.createElement("aqua");
        eAPI.createElement("technomancy");
        eAPI.createElement("abyssal");
    }

    private static void initTags() {
        ElementalsAPI eAPI = new ElementalsAPI();
        AdvancedARPGAttributesAPI aaaAPI = new AdvancedARPGAttributesAPI();

        var elements = eAPI.getAllElements();
        var validWeapons = aaaAPI.getValidWeapons();

        aaaAPI.getValidTags().addAll(validWeapons);
        aaaAPI.getValidTags().addAll(elements);
        aaaAPI.registerTag("attack");
        aaaAPI.registerTag("spell");
        aaaAPI.registerTag("melee");
        aaaAPI.registerTag("projectile");
        aaaAPI.registerTag("aoe");
        //I forgot this tag below, which is why damage was 0.
        aaaAPI.registerTag("damage");

        aaaAPI.registerTag("resistance");
        aaaAPI.registerTag("penetration");
        aaaAPI.registerTag("exposure");
        aaaAPI.registerTag("crit");
        aaaAPI.registerTag("chance");
        aaaAPI.registerTag("dot"); //damage over time
        aaaAPI.registerTag("dealt");
        aaaAPI.registerTag("taken");
        aaaAPI.registerTag("suppression");
        aaaAPI.registerTag("life");
        aaaAPI.registerTag("dodge");

        //decided to have these tags individually as well
        aaaAPI.registerTag("defense");
        aaaAPI.registerTag("armor");
        aaaAPI.registerTag("evasion");
        aaaAPI.registerTag("energy_shield");
        aaaAPI.registerTag("ward");

        aaaAPI.registerTag("immunity");

        aaaAPI.registerTag("minion");
        aaaAPI.registerTag("self");
    }


    private static void initAttributes() {

        AdvancedARPGAttributesAPI aaaAPI = new AdvancedARPGAttributesAPI();

        initBasicAttributes();
        //this is used so that we can avoid a concurrent modification exception.
        var allBasicAttributes = new HashMap<>(aaaAPI.getRegistry());

        initMinionAttributes(allBasicAttributes);
        markOriginalBasicAttributesRegistryWithSelfTag(allBasicAttributes);
    }

    private static void initMinionAttributes(HashMap<ResourceLocation, AdvancedARPGAttribute> allBasicAttributes) {
        initAttributesWithTag(allBasicAttributes, "minion");
    }

    private static void markOriginalBasicAttributesRegistryWithSelfTag(HashMap<ResourceLocation, AdvancedARPGAttribute> allBasicAttributes) {
        AdvancedARPGAttributesAPI aaaAPI = new AdvancedARPGAttributesAPI();

        //take keys from basic attributes and use it to get the AAAttribute from the original registry.
        for(var key : allBasicAttributes.keySet()) {
            AdvancedARPGAttribute advancedARPGAttribute = aaaAPI.getRegistry().get(key);
            var oldTags = advancedARPGAttribute.getTags();
            var newTags = new HashSet<>(oldTags);
            newTags.add("self");
            advancedARPGAttribute.setTags(newTags);
        }
    }

    /**
     * Creates a copy of allBasicAttributes. Each entry key / ResourceLocation has its path prefixed with the tag, and the new
     * AAAttribute has the new tag included in the AAAttribute's tags as well.
    @param allBasicAttributes base attributes to copy from
     @param tag new tag to include, and to prefix the attribute with, no underscore needed.
     */
    private static void initAttributesWithTag(HashMap<ResourceLocation, AdvancedARPGAttribute> allBasicAttributes, String tag) {
        for(var attributeEntry : allBasicAttributes.entrySet()) {
            ResourceLocation rl = attributeEntry.getKey();
            AdvancedARPGAttribute originalAttribute = attributeEntry.getValue();

            var path = rl.getPath();
            path = String.format("%s_%s", tag, path);

            ResourceLocation newRL = ResourceLocation.tryParse(String.format("%s:%s", AnIonianOnionsDamageMegacompatMod.MOD_ID, path));

            Set<String> originalTags = originalAttribute.getTags();
            Set<String> newTags = new HashSet<>(originalTags);
            newTags.add(tag);

            //constructor automatically registers the attribute, but we may use api#regAttribute if we don't need to do anything else with it, but in this case, we want a 1:1 copy of the original for the new. Just with the new tags, however.
            var newAttribute = new AdvancedARPGAttribute(newRL, originalAttribute.getAllowedModifierTypes(), newTags);
            newAttribute.setBaseValue(originalAttribute.getBaseValue());
        }
    }

    private static void initBasicAttributes() {
        ElementalsAPI eAPI = new ElementalsAPI();
        AdvancedARPGAttributesAPI aaaAPI = new AdvancedARPGAttributesAPI();

        var elements = eAPI.getAllElements();
        var weapons = aaaAPI.getValidWeapons();

        var attackDamage = new AdvancedARPGAttribute(ResourceLocation.tryParse("minecraft:generic.attack_damage"), Set.of("physical", "attack", "damage"));
        attackDamage.setBaseValue(1);
        aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:projectile_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("attack", "projectile", "damage"));
        aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:melee_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("attack", "melee", "damage"));
        aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("spell", "damage"));
        aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:minion_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("minion", "damage"));

        aaaAPI.regAttribute(ResourceLocation.tryParse("minecraft:generic.armor"), Set.of("defense", "armor"));
        aaaAPI.regAttribute(ResourceLocation.tryParse("minecraft:generic.max_health"), Set.of("life"));

        //elemental damage attributes only have increases and more modifiers,
        //but attacks, spells, projectile attacks, melee attacks and weapon attacks have attributes that buff their flat initial value.
        for(var element : elements) {
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(INCREASED, MORE), Set.of(element, "damage"));
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_projectile_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "attack", "projectile", "damage"));
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_melee_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "attack", "melee", "damage"));
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_immunity", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(ADDED), Set.of(element, "immunity"));

            for(var weapon : weapons) {
                aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_%s_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element, weapon)), Set.of(element, weapon, "attack", "damage"));
            }

            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "spell", "damage"));
        }

        var elementsMinusPhysical = new HashSet<>(elements);
        elementsMinusPhysical.remove("physical");

        for(var element : elementsMinusPhysical) {
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "attack", "damage"));
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "resistance"));
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_penetration", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "penetration"));
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_exposure", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "exposure"));
        }

        for(var weapon : weapons) {
            aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, weapon)), Set.of(INCREASED, MORE), Set.of(weapon, "attack", "damage"));
        }

        var critChance = new AdvancedARPGAttribute(ResourceLocation.tryParse(String.format("%s:crit_chance", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of("crit", "chance"));
        critChance.setBaseValue(0.05f);
        aaaAPI.regAttribute(ResourceLocation.tryParse("attributeslib:crit_chance"), Set.of("crit", "chance"));

        var critDamage = new AdvancedARPGAttribute(ResourceLocation.tryParse(String.format("%s:crit_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of("crit", "damage", "dealt"));
        critDamage.setBaseValue(1.5f);
        aaaAPI.regAttribute(ResourceLocation.tryParse("attributeslib:crit_damage"), Set.of("crit", "damage", "dealt"));
        aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:crit_damage_taken", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of("crit", "damage", "taken"));

        aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_suppression_chance", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(ADDED), Set.of("spell", "suppression", "chance"));
        aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_suppression", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(ADDED), Set.of("spell", "suppression", "taken"));
        aaaAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_dodge_chance", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(ADDED), Set.of("spell", "dodge", "chance"));

    }

    private static void validateAttributes() {
        AdvancedARPGAttributesAPI aaaAPI = new AdvancedARPGAttributesAPI();

        aaaAPI.validateAttributes();
    }

    private static void initDamagePipeline() {

        DamagePipelineAPI dpAPI = new DamagePipelineAPI();

        //dpAPI.addPreHitDamageStep(new SpellDodgeStep());
        dpAPI.addDamageStep(new AttackerInitialDamageStep());
        /*
        dpAPI.addDamageStep(new CritStep());
        dpAPI.addDamageStep(new ArmorStep());
        dpAPI.addDamageStep(new ElementalResistanceStep());;
        dpAPI.addDamageStep(new SpellSuppressionStep());

         */
    }

    private static void initSpecialAttributeCapFunctions() {
        AdvancedARPGAttributesAPI api = new AdvancedARPGAttributesAPI();

        api.addPlayerExecutedFunctionToAttribute(Attributes.MAX_HEALTH, (player, lockedAttributeValue) -> {
            if(player.isAlive()) player.setHealth(lockedAttributeValue);
        });
    }

    private static void initClassesOfWeaponsAndTags() {
        AdvancedARPGAttributesAPI api = new AdvancedARPGAttributesAPI();

        api.registerWeaponClassAndTag(SwordItem.class, "sword");
        api.registerWeaponClassAndTag(BowItem.class, "bow");
        api.registerWeaponClassAndTag(CrossbowItem.class, "crossbow");
        api.registerWeaponClassAndTag(TridentItem.class, "trident");
        api.registerWeaponClassAndTag(AxeItem.class, "axe");
        api.registerWeaponClassAndTag(StaffItem.class, "staff");
    }

    public static void init() {

        initElements();
        initClassesOfWeaponsAndTags();
        initTags();
        initAttributes();
        validateAttributes();
        initDamagePipeline();
        initSpecialAttributeCapFunctions();
    }
}
