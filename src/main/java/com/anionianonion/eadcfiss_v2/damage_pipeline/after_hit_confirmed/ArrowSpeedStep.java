package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import net.minecraft.world.entity.LivingEntity;

public class ArrowSpeedStep implements IDamageStep {

    @Override
    public float apply(float initialDamage, StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       LivingEntity livingAttacker, LivingEntity livingDefender,
                       DamageContext damageContext) {

        //based on https://www.reddit.com/r/technicalminecraft/comments/488gc9/arrow_damage_calculation/
        if(!damageContext.getTags().contains("bow") && !damageContext.getTags().contains("crossbow")) return initialDamage;
        return (float) (initialDamage * Math.sqrt(damageContext.getProjectileSpeed()));
    }
}
