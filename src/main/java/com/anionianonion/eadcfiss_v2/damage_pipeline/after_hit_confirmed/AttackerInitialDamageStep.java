package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.eadcfiss_v2.AnIonianOnionsDamageMegacompatMod;
import com.anionianonion.eadcfiss_v2.util.Helpers;
import io.redspace.ironsspellbooks.entity.mobs.MagicSummon;
import net.minecraft.resources.ResourceLocation;

import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import com.anionianonion.damage_pipeline_api.DamageContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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

        //the first predicate should always true if the rest is true, but we need it to access an instance of the magic summon class
        //unfortunately the attacker is never the minion, but the summoner, so the first part is always false. and we still need to get the minion somehow.
        if(attacker instanceof MagicSummon minion && damageContext.getSource().equals("minion")) {
            AnIonianOnionsDamageMegacompatMod.LOGGER.info("attacker is instance of magic summon");
            var summonerStatContainer = minion.getSummoner().getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

            //ownStatContainer can be extracted out, for use in the case where it's only themselves
            //---^^^
            if(summonerStatContainer == null) return 0;

            //ownTags can also be extracted out as shared
            //---^^^
            var withMinionTagsToAddToSummoner = new HashSet<>(damageContext.getTags());

            //assuming that the tags do not contain the source tag, to make it easier on ourselves where we can add the tags manually
            //ownTags extracted out as shared
            //---^^^
            withMinionTagsToAddToSummoner.add("minion");

            //filteredSummonedAttributes assumes it comes from a summoned entity and that the attributes are its own. But if the entity is not summoned, it's still its own attributes. So we can extract it and renamed it to ownAttributes.
            //---^^^
            //Set<ResourceLocation> filteredSummonerAttributes = aaaAPI.getFilteredAttributes(withMinionTagsToAddToSummoner);

            StatContainer remappedSummonerStatsOntoMinionStatContainer = Helpers.getNewStatContainerByRemappingBtoA(ownStatContainer, summonerStatContainer, "minion", "self");

            return aaaAPI.getResult(remappedSummonerStatsOntoMinionStatContainer, ownAttributes);
        }
        return aaaAPI.getResult(attackerStatContainer, ownAttributes);

    }

    @Override
    public String toString() {
        return "AttackerInitialDamageStep";
    }
}
