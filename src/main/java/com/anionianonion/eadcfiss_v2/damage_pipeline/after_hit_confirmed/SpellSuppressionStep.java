package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class SpellSuppressionStep implements IDamageStep {


    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       LivingEntity livingAttacker, LivingEntity livingDefender,
                       DamageContext damageContext) {

        if(!damageContext.getTags().contains("spell")) return initialDamage;

        Set<ResourceLocation> spellSuppressionChanceAttribute = AdvancedARPGAttributesAPI.getFilteredAttributes("self", "spell", "suppression", "chance");
        Set<ResourceLocation> spellSuppressionAmountAttribute = AdvancedARPGAttributesAPI.getFilteredAttributes("self", "spell", "suppression", "taken");

        float spellSuppressionChance = AdvancedARPGAttributesAPI.getResult(livingDefender, spellSuppressionChanceAttribute);
        float spellSuppressionAmount = AdvancedARPGAttributesAPI.getResult(livingDefender, spellSuppressionAmountAttribute);


        float suppressionRoll = (float) Math.random();
        float finalDamage = initialDamage;

        if(spellSuppressionChance >= suppressionRoll) finalDamage *= (1 - spellSuppressionAmount);

        return finalDamage;
    }

    @Override
    public String toString() {
        return "SpellSuppressionStep";
    }
}
