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

        if(!damageContext.getTags().contains("bow")) return initialDamage;
        return initialDamage * damageContext.getProjectileSpeed();
    }
}
