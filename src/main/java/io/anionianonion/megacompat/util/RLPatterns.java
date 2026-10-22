package io.anionianonion.megacompat.util;

public class RLPatterns {

    //the mod id/namespace is already taken care of by Helper.makeRL(), and is equal to this mod's MOD_ID.
    //this class defines the paths.

    public static class Attributes {
        public static final String ELEMENT_DAMAGE = "%s_damage";
        public static final String ELEMENT_PROJECTILE_ATTACK_DAMAGE = "%s_projectile_attack_damage";
        public static final String ELEMENT_IMMUNITY = "%s_immunity";
    }

    public static class Enchantments {

        public static final String INCREASED_ELEMENT_DAMAGE = "increased_element_dmg/%s";
        public static final String ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE = "added_element_projectile_attack_dmg/%s";
        public static final String ELEMENT_IMMUNITY = "element_immunity/%s";
    }
}
