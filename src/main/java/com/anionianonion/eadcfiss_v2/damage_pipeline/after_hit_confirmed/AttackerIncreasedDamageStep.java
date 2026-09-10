package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;

public class AttackerIncreasedDamageStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       LivingEntity livingAttacker, LivingEntity livingDefender,
                       DamageContext damageContext) {

        var filteredAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes(damageContext.getTags());
        return initialDamage * (1 + AdvancedARPGAttributesAPI.getData(livingAttacker, filteredAttributes)[1]);
    }
}
