package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.damage_pipeline_api.AdvancedARPGAttributeHook;
import com.anionianonion.damage_pipeline_api.api.DamagePipelineAPI;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.eadcfiss_v2.AnIonianOnionsDamageMegacompatMod;

import java.util.HashSet;

@Deprecated
public class AttackerInitialDamageStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       DamageContext damageContext) {

        //these 4 lines seem to be very important, without it damage is 0
        var ownTags = new HashSet<>(damageContext.getTags());
        for(var tag : DamagePipelineAPI.getValidDamageSourceTypeTags()) {
            ownTags.remove(tag);
        }
        ownTags.add("self");


        var filteredAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes(ownTags);
        /*todo: work on a way to not have to include the 4 lines above each and every time in each damage step
        info("filtered attr 1: " + filteredAttributes1);


        info("2 tags: " + damageContext.getTags().toString());
        var filteredAttributes2 = AdvancedARPGAttributesAPI.getFilteredAttributes(damageContext.getTags());
        info("filtered attr 2: " + filteredAttributes2);

         */

        return AdvancedARPGAttributesAPI.getResult(attackerStatContainer, filteredAttributes);

    }

    @Override
    public String toString() {
        return "AttackerInitialDamageStep";
    }

    public void info(String message) {
        AnIonianOnionsDamageMegacompatMod.LOGGER.info(message);
    }
}
