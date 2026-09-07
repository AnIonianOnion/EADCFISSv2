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

        ElementalsAPI.regElement("physical");
        ElementalsAPI.regElement("fire");
        ElementalsAPI.regElement("ice");
        ElementalsAPI.regElement("lightning");
        ElementalsAPI.regElement("holy");
        ElementalsAPI.regElement("ender");
        ElementalsAPI.regElement("blood");
        ElementalsAPI.regElement("nature");
        ElementalsAPI.regElement("evocation");
        ElementalsAPI.regElement("eldritch");
        ElementalsAPI.regElement("sound");
        ElementalsAPI.regElement("geo");
        ElementalsAPI.regElement("aqua");
        ElementalsAPI.regElement("technomancy");
        ElementalsAPI.regElement("abyssal");
    }

    private static void initTags() {

        var elements = ElementalsAPI.getAllElementNames();
        var validWeapons = AdvancedARPGAttributesAPI.getValidWeapons();
        var validDamageSources = DamagePipelineAPI.getValidDamageSourceTypeTags();

        AdvancedARPGAttributesAPI.getValidTags().addAll(validWeapons);
        AdvancedARPGAttributesAPI.getValidTags().addAll(elements);
        AdvancedARPGAttributesAPI.getValidTags().addAll(validDamageSources);

        AdvancedARPGAttributesAPI.registerTag("attack");
        AdvancedARPGAttributesAPI.registerTag("spell");
        AdvancedARPGAttributesAPI.registerTag("melee");
        AdvancedARPGAttributesAPI.registerTag("projectile");
        AdvancedARPGAttributesAPI.registerTag("aoe");
        //I forgot this tag below, which is why damage was 0.
        AdvancedARPGAttributesAPI.registerTag("damage");

        AdvancedARPGAttributesAPI.registerTag("resistance");
        AdvancedARPGAttributesAPI.registerTag("penetration");
        AdvancedARPGAttributesAPI.registerTag("exposure");
        AdvancedARPGAttributesAPI.registerTag("crit");
        AdvancedARPGAttributesAPI.registerTag("chance");
        AdvancedARPGAttributesAPI.registerTag("dot"); //damage over time
        AdvancedARPGAttributesAPI.registerTag("dealt");
        AdvancedARPGAttributesAPI.registerTag("taken");
        AdvancedARPGAttributesAPI.registerTag("suppression");
        AdvancedARPGAttributesAPI.registerTag("life");
        AdvancedARPGAttributesAPI.registerTag("dodge");

        //decided to have these tags individually as well
        AdvancedARPGAttributesAPI.registerTag("defense");
        AdvancedARPGAttributesAPI.registerTag("armor");
        AdvancedARPGAttributesAPI.registerTag("evasion");
        AdvancedARPGAttributesAPI.registerTag("energy_shield");
        AdvancedARPGAttributesAPI.registerTag("ward");

        AdvancedARPGAttributesAPI.registerTag("immunity");
    }


    private static void initAttributes() {

        initBasicAttributes();
        //this is used so that we can avoid a concurrent modification exception.
        var allBasicAttributes = new HashMap<>(AdvancedARPGAttributesAPI.getRegistry());

        initMinionAttributes(allBasicAttributes);
        markOriginalBasicAttributesRegistryWithSelfTag(allBasicAttributes);
    }

    private static void initMinionAttributes(HashMap<ResourceLocation, AdvancedARPGAttribute> allBasicAttributes) {
        initAttributesWithTag(allBasicAttributes, "minion");
    }

    private static void markOriginalBasicAttributesRegistryWithSelfTag(HashMap<ResourceLocation, AdvancedARPGAttribute> allBasicAttributes) {

        //take keys from basic attributes and use it to get the AAAttribute from the original registry.
        for(var key : allBasicAttributes.keySet()) {
            AdvancedARPGAttribute advancedARPGAttribute = AdvancedARPGAttributesAPI.getRegistry().get(key);
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

        var elements = ElementalsAPI.getAllElementNames();
        var weapons = AdvancedARPGAttributesAPI.getValidWeapons();

        var attackDamage = new AdvancedARPGAttribute(ResourceLocation.tryParse("minecraft:generic.attack_damage"), Set.of("physical", "attack", "damage"));
        attackDamage.setBaseValue(1);
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:projectile_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("attack", "projectile", "damage"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:melee_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("attack", "melee", "damage"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("spell", "damage"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:minion_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("minion", "damage"));

        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse("minecraft:generic.armor"), Set.of("defense", "armor"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse("minecraft:generic.max_health"), Set.of("life"));

        //elemental damage attributes only have increases and more modifiers,
        //but attacks, spells, projectile attacks, melee attacks and weapon attacks have attributes that buff their flat initial value.
        for(var element : elements) {
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(INCREASED, MORE), Set.of(element, "damage"));
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_projectile_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "attack", "projectile", "damage"));
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_melee_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "attack", "melee", "damage"));
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_immunity", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(ADDED), Set.of(element, "immunity"));

            for(var weapon : weapons) {
                AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_%s_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element, weapon)), Set.of(element, weapon, "attack", "damage"));
            }

            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "spell", "damage"));
        }

        var elementsMinusPhysical = new HashSet<>(elements);
        elementsMinusPhysical.remove("physical");

        for(var element : elementsMinusPhysical) {
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "attack", "damage"));
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "resistance"));
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_penetration", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "penetration"));
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_exposure", AnIonianOnionsDamageMegacompatMod.MOD_ID, element)), Set.of(element, "exposure"));
        }

        for(var weapon : weapons) {
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, weapon)), Set.of(INCREASED, MORE), Set.of(weapon, "attack", "damage"));
        }

        var critChance = new AdvancedARPGAttribute(ResourceLocation.tryParse(String.format("%s:crit_chance", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of("crit", "chance"));
        critChance.setBaseValue(0.05f);
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse("attributeslib:crit_chance"), Set.of("crit", "chance"));

        var critDamage = new AdvancedARPGAttribute(ResourceLocation.tryParse(String.format("%s:crit_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of("crit", "damage", "dealt"));
        critDamage.setBaseValue(1.5f);
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse("attributeslib:crit_damage"), Set.of("crit", "damage", "dealt"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:crit_damage_taken", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of("crit", "damage", "taken"));

        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_suppression_chance", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(ADDED), Set.of("spell", "suppression", "chance"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_suppression", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(ADDED), Set.of("spell", "suppression", "taken"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_dodge_chance", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(ADDED), Set.of("spell", "dodge", "chance"));

    }

    private static void validateAttributes() {
        AdvancedARPGAttributesAPI.validateAttributes();
    }

    private static void initDamagePipeline() {

        //DamagePipelineAPI.addPreHitDamageStep(new SpellDodgeStep());
        //DamagePipelineAPI.addDamageStep(new AttackerInitialDamageStep());
        DamagePipelineAPI.addDamageStep(new AttackerBaseDamageStep());
        DamagePipelineAPI.addDamageStep(new ApplyAilmentsStep());
        DamagePipelineAPI.addDamageStep(new AttackerIncreasedDamageStep());
        DamagePipelineAPI.addDamageStep(new AttackerMoreDamageStep());
        //DamagePipelineAPI.addDamageStep(new CritStep());
        //DamagePipelineAPI.addDamageStep(new ArmorStep());
        //DamagePipelineAPI.addDamageStep(new ElementalResistanceStep());;
        //DamagePipelineAPI.addDamageStep(new SpellSuppressionStep());
    }

    private static void initValidDamageSourceTypesTags() {
        DamagePipelineAPI.addValidDamageSourceTypeTag("self");
        DamagePipelineAPI.addValidDamageSourceTypeTag("minion");
    }

    private static void initSpecialAttributeCapFunctions() {
        AdvancedARPGAttributesAPI.addPlayerExecutedFunctionToAttribute(Attributes.MAX_HEALTH, (player, lockedAttributeValue) -> {
            if(player.isAlive()) player.setHealth(lockedAttributeValue);
        });
    }

    private static void initClassesOfWeaponsAndTags() {

        AdvancedARPGAttributesAPI.registerWeaponClassAndTag(SwordItem.class, "sword");
        AdvancedARPGAttributesAPI.registerWeaponClassAndTag(BowItem.class, "bow");
        AdvancedARPGAttributesAPI.registerWeaponClassAndTag(CrossbowItem.class, "crossbow");
        AdvancedARPGAttributesAPI.registerWeaponClassAndTag(TridentItem.class, "trident");
        AdvancedARPGAttributesAPI.registerWeaponClassAndTag(AxeItem.class, "axe");
        AdvancedARPGAttributesAPI.registerWeaponClassAndTag(StaffItem.class, "staff");
    }

    public static void init() {

        initElements();
        initClassesOfWeaponsAndTags();
        initValidDamageSourceTypesTags();
        initTags();
        initAttributes();
        validateAttributes();
        initDamagePipeline();
        initSpecialAttributeCapFunctions();
    }
}
