package io.anionianonion.megacompat.mixins;

import io.anionianonion.damage_pipeline_api.attachments.ModAttachments;
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
        var damageContext = livingAttackerOrCaster.getData(ModAttachments.DAMAGE_CONTEXT);

        if(damageContext.getTags().contains("physical")) return;

        cir.setReturnValue(amount);
    }
}