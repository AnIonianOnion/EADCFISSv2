package com.anionianonion.eadcfiss_v2;

import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

//from https://forums.minecraftforge.net/topic/122311-1194-how-to-create-custom-damagesources/ 7194.f.null's response.
public class ModDamageSources {

    private final Registry<DamageType> damageTypes;

    //the line below is one of the things I changed
    private final DamageSource damageOverTime;

    public ModDamageSources(RegistryAccess pRegistry) {
        this.damageTypes = pRegistry.registryOrThrow(Registries.DAMAGE_TYPE);
        //the line below is one of the things I changed
        this.damageOverTime = this.source(ModDamageTypes.DAMAGE_OVER_TIME);
    }

    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey));
    }

    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pEntity) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey), pEntity);
    }

    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey, @Nullable Entity pCausingEntity, @Nullable Entity pDirectEntity) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey), pCausingEntity, pDirectEntity);
    }

    //the function below is one of the things I changed
    public DamageSource getDamageOverTime() {
        return damageOverTime;
    }
}
