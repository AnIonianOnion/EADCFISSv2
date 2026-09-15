package com.anionianonion.eadcfiss_v2;

import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {

    //https://forums.minecraftforge.net/topic/150394-how-do-i-create-custom-damagesource-in-forge-1201/
    public static final DamageType DAMAGE_OVER_TIME = new DamageType(String.format("%s:damage_over_time", AnIonianOnionsDamageMegacompatMod.MOD_ID), DamageScaling.ALWAYS, 1f, DamageEffects.HURT);
}
