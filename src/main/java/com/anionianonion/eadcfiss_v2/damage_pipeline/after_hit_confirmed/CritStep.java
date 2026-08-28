package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import com.anionianonion.eadcfiss_v2.util.Helpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class CritStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       LivingEntity attacker, LivingEntity defender,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       Entity directEntity, DamageContext damageContext) {
        //though the damage that causes damage over time can crit, damage from damage over time instances themselves cannot
        if(damageContext.getTags().contains("dot")) return initialDamage;

        AdvancedARPGAttributesAPI api = new AdvancedARPGAttributesAPI();

        if(attackerStatContainer == null || defenderStatContainer == null) return initialDamage;

        Set<ResourceLocation> attackerCritChanceAttributes = api.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), "crit", "chance");
        Set<ResourceLocation> attackerCritDamageAttributes = api.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), "crit", "damage", "dealt");
        float attackerCritChance = api.getResult(attackerStatContainer, attackerCritChanceAttributes);
        float attackerCritDamage = api.getResult(attackerStatContainer, attackerCritDamageAttributes);

        Set<ResourceLocation> defenderAntiCritDamageAttribute = api.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), "crit", "damage", "taken");
        float defenderAntiCritDamageTaken = api.getResult(defenderStatContainer, defenderAntiCritDamageAttribute);

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
