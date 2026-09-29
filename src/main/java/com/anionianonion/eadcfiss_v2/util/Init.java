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
import static com.anionianonion.eadcfiss_v2.util.RegisterAilmentsHelper.*;

import java.util.*;

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

    private static void initAilments() {

        regBleed();
        //ignites inflict burns, but so does Righteous Fire, which deals damage scaling from fire, life and dot.
        regIgnite();


        //todo
        //ElementalsAPI.pairAilmentToElement("burn", "fire");

        regScorch();

        //alt registration
        ElementalsAPI.regAilment("frostbite");
        ElementalsAPI.regAilment("chill");
        ElementalsAPI.regAilment("freeze");
        ElementalsAPI.regAilment("brittle");
        ElementalsAPI.regAilment("shock");
        ElementalsAPI.regAilment("electrocution");
        ElementalsAPI.regAilment("paralysis");
        ElementalsAPI.regAilment("sap");
        ElementalsAPI.regAilment("petrify");
        ElementalsAPI.regAilment("poison");
        ElementalsAPI.regAilment("ghostflame");

        var ghostFlame = ElementalsAPI.getAilment("ghostflame");
        ghostFlame.setDurationInSeconds(5);
        ghostFlame.setDefenderOnExpire(ailmentInstance -> ElementalsAPI.hurtWithAilment(ailmentInstance.attacker, ailmentInstance.defender, 500));
        ElementalsAPI.pairAilmentToElement("ghostflame", "abyssal");

        ElementalsAPI.setElementsForElementCategory(Set.of("fire", "ice", "lightning"), "elemental");
        ElementalsAPI.setAilmentsForElement(Set.of("ignite"), "fire");
    }

    private static void initTags() {

        var elementIds = ElementalsAPI.getAllElementNames();
        var ailmentIds = ElementalsAPI.getAllAilmentNames();
        var validWeaponIds = AdvancedARPGAttributesAPI.getValidWeaponTags();
        var validDamageSourceIds = DamagePipelineAPI.getValidDamageSourceTypeTags();

        AdvancedARPGAttributesAPI.getValidTags().addAll(elementIds);
        AdvancedARPGAttributesAPI.getValidTags().addAll(ailmentIds);
        AdvancedARPGAttributesAPI.getValidTags().addAll(validWeaponIds);
        AdvancedARPGAttributesAPI.getValidTags().addAll(validDamageSourceIds);

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

        AdvancedARPGAttributesAPI.registerTag("ailment");
        AdvancedARPGAttributesAPI.registerTag("damaging");
        AdvancedARPGAttributesAPI.registerTag("nondamaging");
        AdvancedARPGAttributesAPI.registerTag("effect_strength");
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
        var weapons = AdvancedARPGAttributesAPI.getValidWeaponTags();

        var physicalMeleeAttackDamage = new AdvancedARPGAttribute(ResourceLocation.tryParse("minecraft:generic.attack_damage"), Set.of("physical", "melee", "attack", "damage"));
        physicalMeleeAttackDamage.setInheritBase(true);

        //MULTIPLY_BASE & MULTIPLY_TOTAL attributes only
        //applies to any element's damage for projectiles, melee
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:projectile_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("attack", "projectile", "damage"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:melee_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("attack", "melee", "damage"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("spell", "damage"));
        //realized damage type that applies to anything didn't have a modifier. the nice part about this change, is that when initMinion attributes called, it will create minion_damage instead of minion_minion_damage. 2 birds with 1 stone.
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("damage"));

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

        for(var ailmentId : ElementalsAPI.getAllAilmentNames()) {
            AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:chance_to_inflict_%s", AnIonianOnionsDamageMegacompatMod.MOD_ID, ailmentId)), Set.of(ailmentId, "ailment", "chance"));
            var ailment = ElementalsAPI.getAilment(ailmentId);
            if(!ailment.isDamagingAilment()) {
                AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_effect_strength", AnIonianOnionsDamageMegacompatMod.MOD_ID, ailmentId)), Set.of(INCREASED, MORE), Set.of("nondamaging", ailmentId, "ailment", "effect_strength"));
            }
            else {
                AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:%s_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, ailmentId)), Set.of(INCREASED, MORE), Set.of("damaging", ailmentId, "ailment", "damage"));
            }
        }
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:ailment_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("damaging", "ailment", "damage"));
        AdvancedARPGAttributesAPI.regAttribute(ResourceLocation.tryParse(String.format("%s:nondamaging_ailment_effect_strength", AnIonianOnionsDamageMegacompatMod.MOD_ID)), Set.of(INCREASED, MORE), Set.of("nondamaging", "ailment", "effect_strength"));


        //removing duplicate modifier for physical melee damage (the other is minecraft:generic.attack_damage which functions identically).
        AdvancedARPGAttributesAPI.getRegistry().remove(ResourceLocation.tryParse(String.format("%s:physical_melee_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID)));

        //setting base arrow damage, which is otherwise 0.
        AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:physical_projectile_attack_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID))).setBaseValue(2f);

        AdvancedARPGAttribute selfFireResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:fire_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));
        selfFireResAttribute.setUuidForAddModifier(UUID.fromString("920a8a9d-e1ea-4a7d-b3de-1d8f150b47a3"));

        AdvancedARPGAttribute selfIceResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:ice_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfIceResAttribute.setUuidForAddModifier(UUID.fromString("a95819ec-9de9-4252-bacb-6f2a152c96a7"));

        AdvancedARPGAttribute selfLightningResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:lightning_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfLightningResAttribute.setUuidForAddModifier(UUID.fromString("fb4e55c8-fb12-4831-97c7-97e056e741ca"));

        AdvancedARPGAttribute selfHolyResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:holy_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfHolyResAttribute.setUuidForAddModifier(UUID.fromString("5b7e0b8c-0c5c-482f-a89b-c07e3ac724b0"));

        AdvancedARPGAttribute selfEnderResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:ender_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfEnderResAttribute.setUuidForAddModifier(UUID.fromString("54b8780a-ebc0-4727-ab1e-2e130be723fa"));

        AdvancedARPGAttribute selfBloodResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:blood_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfBloodResAttribute.setUuidForAddModifier(UUID.fromString("bcedd94e-0b6e-484e-8d70-fdf77806c963"));

        AdvancedARPGAttribute selfNatureResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:nature_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfNatureResAttribute.setUuidForAddModifier(UUID.fromString("4a6d5eb3-9e96-4c6d-9142-f83a2a864b94"));

        AdvancedARPGAttribute selfEvocationResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:evocation_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfEvocationResAttribute.setUuidForAddModifier(UUID.fromString("8e2ca31e-c062-4ee7-9bdf-34ad7b5bfb2f"));

        AdvancedARPGAttribute selfEldritchResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:eldritch_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfEldritchResAttribute.setUuidForAddModifier(UUID.fromString("08ed0d27-92ee-47d7-907e-8cbd13cb8a92"));

        AdvancedARPGAttribute selfSoundResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:sound_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfSoundResAttribute.setUuidForAddModifier(UUID.fromString("201ef756-856f-4f80-ab84-85126296a4c5"));

        AdvancedARPGAttribute selfGeoResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:geo_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfGeoResAttribute.setUuidForAddModifier(UUID.fromString("ceef58d5-084b-473d-bde2-a4f83a19d09d"));

        AdvancedARPGAttribute selfAquaResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:aqua_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfAquaResAttribute.setUuidForAddModifier(UUID.fromString("52624651-e9d9-4409-a56c-66be3298bc3c"));

        AdvancedARPGAttribute selfTechnomancyResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:technomancy_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfTechnomancyResAttribute.setUuidForAddModifier(UUID.fromString("73b1e1bb-4ff8-40ba-bcb8-cfd28ce0b0de"));

        AdvancedARPGAttribute selfAbyssalResAttribute = AdvancedARPGAttributesAPI.getRegistry().get(ResourceLocation.tryParse(String.format("%s:abyssal_resistance", AnIonianOnionsDamageMegacompatMod.MOD_ID)));;
        selfAbyssalResAttribute.setUuidForAddModifier(UUID.fromString("edfe7577-2f27-4bd7-b17a-fe2f76a9c058"));

    }

    private static void validateAttributes() {
        AdvancedARPGAttributesAPI.validateAttributes();
    }

    private static void initDamagePipeline() {

        DamagePipelineAPI.addPreHitDamageStep(new SpellDodgeStep());
        //DamagePipelineAPI.addDamageStep(new AttackerInitialDamageStep());
        DamagePipelineAPI.addDamageStep(new AttackerBaseDamageStep());
        DamagePipelineAPI.addDamageStep(new AttackerIncreasedDamageStep());
        DamagePipelineAPI.addDamageStep(new AttackerMoreDamageStep());
        DamagePipelineAPI.addDamageStep(new CritStep());
        DamagePipelineAPI.addDamageStep(new ElementalResistanceStep());;
        DamagePipelineAPI.addDamageStep(new ArmorStep());
        DamagePipelineAPI.addDamageStep(new ArrowSpeedStep());
        DamagePipelineAPI.addDamageStep(new SpellSuppressionStep());
        DamagePipelineAPI.addDamageStep(new ApplyAilmentsStep());
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

        //melee only
        AdvancedARPGAttributesAPI.registerMeleeWeaponClassAndTag(SwordItem.class, "sword");
        AdvancedARPGAttributesAPI.registerMeleeWeaponClassAndTag(AxeItem.class, "axe");
        AdvancedARPGAttributesAPI.registerMeleeWeaponClassAndTag(StaffItem.class, "staff");

        //melee and ranged
        AdvancedARPGAttributesAPI.registerMeleeWeaponClassAndTag(TridentItem.class, "trident");

        //ranged only
        AdvancedARPGAttributesAPI.registerRangedWeaponClassAndTag(BowItem.class, "bow");
        AdvancedARPGAttributesAPI.registerRangedWeaponClassAndTag(CrossbowItem.class, "crossbow");
        AdvancedARPGAttributesAPI.registerRangedWeaponClassAndTag(TridentItem.class, "trident");
    }

    public static void init() {

        initElements();
        initAilments();
        initClassesOfWeaponsAndTags();
        initValidDamageSourceTypesTags();
        initTags();
        initAttributes();
        validateAttributes();
        initDamagePipeline();
        initSpecialAttributeCapFunctions();
    }
}
