package com.anionianonion.eadcfiss_v2.util;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.DamagePipeline;
import com.anionianonion.damage_pipeline_api.capability.DamageContextCapability;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class Helpers {

    public static String getSelfOrMinion(DamageContext damageContext) {
        if(damageContext.getSource().equals("self")) return "self";
        else return "minion";
    }

    /**
    @return true if entity is a minion
     */
    public static boolean isMinion(Entity entity) {
        return entity instanceof LivingEntity && SummonManager.getOwner(entity) != null;
    }

    public static boolean getIfContinuePipeline(Entity directEntity, LivingEntity livingAttackerOrCaster, LivingEntity livingDefender) {

        DamageContext damageContext = livingAttackerOrCaster.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingAttackerOrCasterStatContainer = livingAttackerOrCaster.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingAttackerOrCasterStatContainer == null || livingDefenderStatContainer == null) return true;

        boolean continuePipeline;
        if(isMinion(directEntity)) {

            var minionStatContainer = directEntity.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            if(minionStatContainer == null) return true;

            continuePipeline = DamagePipeline.didHitSucceed(livingAttackerOrCasterStatContainer, minionStatContainer, livingDefenderStatContainer, damageContext);
        }
        else if(isMinion(livingAttackerOrCaster)) {

            var summoner = SummonManager.getOwner(livingAttackerOrCaster);
            if(!(summoner instanceof LivingEntity livingSummoner)) return true;

            var summonerStatContainer = livingSummoner.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            continuePipeline = DamagePipeline.didHitSucceed(summonerStatContainer, livingAttackerOrCasterStatContainer, livingDefenderStatContainer, damageContext);
        }
        else {
            continuePipeline = DamagePipeline.didHitSucceed(null, livingAttackerOrCasterStatContainer, livingDefenderStatContainer, damageContext);
        }

        return continuePipeline;
    }
}
