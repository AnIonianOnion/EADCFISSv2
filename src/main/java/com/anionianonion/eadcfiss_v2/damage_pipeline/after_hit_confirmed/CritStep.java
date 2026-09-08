package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import com.anionianonion.eadcfiss_v2.util.Helpers;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class CritStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       DamageContext damageContext) {
        //though the damage that causes damage over time can crit, damage from damage over time instances themselves cannot
        if(damageContext.getTags().contains("dot")) return initialDamage;

        Set<ResourceLocation> attackerCritChanceAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes("self", "crit", "chance");
        Set<ResourceLocation> attackerCritDamageAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes("self", "crit", "damage", "dealt");
        float attackerCritChance = AdvancedARPGAttributesAPI.getResult(attackerStatContainer, attackerCritChanceAttributes);
        float attackerCritDamage = AdvancedARPGAttributesAPI.getResult(attackerStatContainer, attackerCritDamageAttributes);

        Set<ResourceLocation> defenderAntiCritDamageAttribute = AdvancedARPGAttributesAPI.getFilteredAttributes("self", "crit", "damage", "taken");
        float defenderAntiCritDamageTaken = AdvancedARPGAttributesAPI.getResult(defenderStatContainer, defenderAntiCritDamageAttribute);

        float critRoll = (float) Math.random();
        float finalDamage = initialDamage;

        if(attackerCritChance >= critRoll) finalDamage *= Math.max(1, attackerCritDamage - defenderAntiCritDamageTaken);

        return finalDamage;
    }

    @Override
    public String toString() {
        return "CritStep";
    }
}
