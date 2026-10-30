package io.anionianonion.megacompat.util;

public class RLPatterns {

    //the mod id/namespace is already taken care of by Helper.makeRL(), and is equal to this mod's MOD_ID.
    //this class defines the paths.

    public static class Attributes {
        public static final String ELEMENT_DAMAGE = "%s_damage";
        public static final String ELEMENT_PROJECTILE_ATTACK_DAMAGE = "%s_projectile_attack_damage";
        public static final String ELEMENT_IMMUNITY = "%s_immunity";
        public static final String ELEMENT_ATTACK_DAMAGE = "%s_attack_damage";
        public static final String WEAPON_ELEMENT_ATTACK_DAMAGE = "%s_%s_attack_damage";
        public static final String ELEMENT_SPELL_DAMAGE =  "%s_spell_damage";
        public static final String ELEMENT_MELEE_ATTACK_DAMAGE = "%s_melee_attack_damage";
        public static final String ELEMENT_RESISTANCE =  "%s_resistance";
        public static final String ELEMENT_PENETRATION = "%s_penetration";
        public static final String ELEMENT_EXPOSURE = "%s_exposure";
        public static final String WEAPON_DAMAGE = "%s_damage";
    }

    public static class Enchantments {

        public static final String INCREASED_ELEMENT_DAMAGE = "increased_element_dmg/%s";
        public static final String ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE = "added_element_projectile_attack_dmg/%s";
        public static final String ELEMENT_IMMUNITY = "element_immunity/%s";
        public static final String ADDED_ELEMENT_ATTACK_DAMAGE = "added_element_attack_dmg/%s";
        public static final String ADDED_WEAPON_ELEMENT_ATTACK_DAMAGE = "added_weapon_element_attack_dmg/%s/%s";
        public static final String ADDED_ELEMENT_SPELL_DAMAGE = "added_element_spell_dmg/%s";
        public static final String ADDED_ELEMENT_MELEE_ATTACK_DAMAGE = "added_element_melee_attack_dmg/%s";
        public static final String ADDED_ELEMENT_RESISTANCE = "added_element_resistance/%s";
        public static final String ADDED_ELEMENT_PENETRATION = "added_element_penetration/%s";
        public static final String ADDED_ELEMENT_EXPOSURE = "added_element_exposure/%s";
        public static final String INCREASED_WEAPON_DAMAGE = "increased_weapon_dmg/%s";
    }
}
