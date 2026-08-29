package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.eadcfiss_v2.AnIonianOnionsDamageMegacompatMod;
import com.anionianonion.eadcfiss_v2.util.Helpers;
import net.minecraft.resources.ResourceLocation;

import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import com.anionianonion.damage_pipeline_api.DamageContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AttackerInitialDamageStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       LivingEntity attacker, LivingEntity defender,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       Entity directEntity, DamageContext damageContext) {

        //only if source is "self" do we get the direct attributes of the attacker
            //source is "self" when damage#getTags contains "self" and source#getSource is "self"
        //if source is "minion", then we must get the attacker minion's owner/summoner and then get the latter's "self" attributes.

        AdvancedARPGAttributesAPI aaaAPI = new AdvancedARPGAttributesAPI();

        var ownTags = new HashSet<>(damageContext.getTags());
        ownTags.add("self");
        Set<ResourceLocation> ownAttributes = aaaAPI.getFilteredAttributes(ownTags);

        //decided to handle keep it simple in the step, and handle giving the minion tag outside this step.
        //assumes a minion is a living entity that is not a pet.
        if(damageContext.getSource().equals("minion")) {

            var minionStatContainer = directEntity.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            if(minionStatContainer == null) {
                AnIonianOnionsDamageMegacompatMod.LOGGER.info("minion stat container is null");
                return 0;
            }
            Helpers.logDataFromStatContainer(minionStatContainer);

            HashMap<String, String> tagsToReplace = new HashMap<>();
            tagsToReplace.put("minion", "self");
            tagsToReplace.put("spell", "attack");

            StatContainer remappedSummonerStatsOntoMinionStatContainer = Helpers.getNewStatContainerByRemappingBtoA(minionStatContainer, attackerStatContainer, tagsToReplace);

            var result = aaaAPI.getResult(remappedSummonerStatsOntoMinionStatContainer, ownAttributes);
            AnIonianOnionsDamageMegacompatMod.LOGGER.info("minion damage: " + result);
            return result;
        }

        var ownDamage = aaaAPI.getResult(attackerStatContainer, ownAttributes);
        //AnIonianOnionsDamageMegacompatMod.LOGGER.info("own damage: " + ownDamage);
        return ownDamage;

    }

    @Override
    public String toString() {
        return "AttackerInitialDamageStep";
    }
}
