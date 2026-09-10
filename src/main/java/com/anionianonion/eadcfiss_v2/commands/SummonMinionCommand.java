package com.anionianonion.eadcfiss_v2.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Zombie;

public class SummonMinionCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("summon_minion").executes(ctx -> {

            int randomInt = (int) (Math.random() * 4);

            var entityType = switch (randomInt) {
                case 1 -> EntityType.SKELETON;
                case 2 -> EntityType.STRAY;
                case 3 -> EntityType.HUSK;
                default -> EntityType.ZOMBIE;
            };

            var level = ctx.getSource().getLevel();
            var entity = switch (randomInt) {
                case 1 -> new Skeleton((EntityType<? extends Skeleton>) entityType, level);
                case 2 -> new Stray((EntityType<? extends Stray>) entityType, level);
                case 3 -> new Husk((EntityType<? extends Husk>) entityType, level);
                default -> new Zombie((EntityType<? extends Zombie>) entityType, level);
            };
            entity.setCanPickUpLoot(true);
            entity.setPos(ctx.getSource().getPosition());
            SummonManager.initSummon(ctx.getSource().getPlayer(), entity, 720, new SummonedEntitiesCastData());


            level.addFreshEntity(entity);
            return 0;
        }));
    }
}
