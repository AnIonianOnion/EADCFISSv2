package com.anionianonion.eadcfiss_v2;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

import java.util.Objects;

public class ModDamageTypes {

    //https://forums.minecraftforge.net/topic/122311-1194-how-to-create-custom-damagesources/. 7194.f.null's response.
    public static ResourceKey<DamageType> DAMAGE_OVER_TIME = ResourceKey.create(Registries.DAMAGE_TYPE, Objects.requireNonNull(ResourceLocation.parse(String.format("%s:damage_over_time", AnIonianOnionsDamageMegacompatMod.MOD_ID))));
}
