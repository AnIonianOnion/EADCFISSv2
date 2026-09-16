package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import com.anionianonion.eadcfiss_v2.ModDamageSources;
import com.anionianonion.eadcfiss_v2.ModDamageTypes;
import com.anionianonion.elementals_api.AilmentApplier;
import com.anionianonion.elementals_api.api.ElementalsAPI;
import com.anionianonion.elementals_api.data_classes.Ailment;
import com.anionianonion.elementals_api.data_classes.AilmentInstance;
import com.anionianonion.elementals_api.data_classes.Element;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ApplyAilmentsStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       LivingEntity livingAttacker, LivingEntity livingDefender,
                       DamageContext damageContext) {

        //get the actual element based on the key from the damage context's element tag.
        Element element = ElementalsAPI.getElement(damageContext.getElement());
        Set<Ailment> ailments = element.getAilments();

        for(var ailment : ailments) {
            //start simple for now
            var ailmentInstance = new AilmentInstance(livingDefender, 4, 100);
            ailmentInstance.setOnExpire((defender, instance) -> {

                //https://forums.minecraftforge.net/topic/122311-1194-how-to-create-custom-damagesources/
                defender.hurt(new ModDamageSources(defender.level().registryAccess()).getDamageOverTime(), 100);
                return null;
            });
            AilmentApplier.applyAilment(ailment.getName(), livingDefender, ailmentInstance);
        }


        return initialDamage;
    }
}
