package com.anionianonion.eadcfiss_v2.mixins;

import com.anionianonion.damage_pipeline_api.capability.DamageContextCapability;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ArmorDoesntAffectElementalDamageMixin {


    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void armorDoesntAffectElementalDamage(DamageSource damageSource, float amount, CallbackInfoReturnable<Float> cir) {
        var attackerOrCaster = damageSource.getEntity();

        if(!(attackerOrCaster instanceof LivingEntity livingAttackerOrCaster)) return;
        var damageContext = livingAttackerOrCaster.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null) return;
        if(damageContext.getTags().contains("physical")) return;

        cir.setReturnValue(amount);
    }
}