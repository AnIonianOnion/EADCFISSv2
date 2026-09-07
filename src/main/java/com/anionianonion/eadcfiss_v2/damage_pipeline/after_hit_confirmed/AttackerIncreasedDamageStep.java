package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.DamagePipelineAPI;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;

import java.util.HashSet;

public class AttackerIncreasedDamageStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       DamageContext damageContext) {

        var ownTags = new HashSet<>(damageContext.getTags());
        for(var tag : DamagePipelineAPI.getValidDamageSourceTypeTags()) {
            ownTags.remove(tag);
        }
        ownTags.add("self");

        var filteredAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes(ownTags);
        return initialDamage * (1 + AdvancedARPGAttributesAPI.getData(attackerStatContainer, filteredAttributes)[1]);
    }
}
