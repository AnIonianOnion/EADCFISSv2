package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
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
                //https://github.com/Glitchfiend/BiomesOPlenty/blob/551bff467ade42cb2a0b23609f439137ca7fa60f/src/main/java/biomesoplenty/common/block/BrambleBlock.java#L67

                //this is kinda ugly
                Holder<DamageType> holder = new Holder<>() {
                    @Override
                    public DamageType value() {
                        return ModDamageTypes.DAMAGE_OVER_TIME;
                    }

                    @Override
                    public boolean isBound() {
                        return false;
                    }

                    @Override
                    public boolean is(ResourceLocation p_205713_) {
                        return false;
                    }

                    @Override
                    public boolean is(ResourceKey<DamageType> p_205712_) {
                        return false;
                    }

                    @Override
                    public boolean is(Predicate<ResourceKey<DamageType>> p_205711_) {
                        return false;
                    }

                    @Override
                    public boolean is(TagKey<DamageType> p_205705_) {
                        return false;
                    }

                    @Override
                    public Stream<TagKey<DamageType>> tags() {
                        return Stream.empty();
                    }

                    @Override
                    public Either<ResourceKey<DamageType>, DamageType> unwrap() {
                        return null;
                    }

                    @Override
                    public Optional<ResourceKey<DamageType>> unwrapKey() {
                        return Optional.empty();
                    }

                    @Override
                    public Kind kind() {
                        return null;
                    }

                    @Override
                    public boolean canSerializeIn(HolderOwner<DamageType> p_255833_) {
                        return false;
                    }
                };

                defender.hurt(new DamageSource(holder), initialDamage);
                return null;
            });
            AilmentApplier.applyAilment(ailment.getName(), livingDefender, ailmentInstance);
        }


        return initialDamage;
    }
}
