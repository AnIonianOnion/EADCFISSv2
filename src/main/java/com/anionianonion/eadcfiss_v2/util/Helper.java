package com.anionianonion.eadcfiss_v2.util;

import com.anionianonion.eadcfiss_v2.AnIonianOnionsDamageMegacompatMod;
import com.anionianonion.eadcfiss_v2.ModDamageTypes;
import com.anionianonion.elementals_api.AilmentDamageSource;
import net.minecraft.world.entity.LivingEntity;

public class Helper {

    public static void hurtDoT(LivingEntity target, float amount) {
        if(amount == 0) return;
        target.hurt(new AilmentDamageSource(AilmentDamageSource.getHolderFromResource(target, ModDamageTypes.DAMAGE_OVER_TIME)), amount);
        target.hurtDuration = 0;
    }

    public static void info(String log) {
        AnIonianOnionsDamageMegacompatMod.LOGGER.info(log);
    }
}
