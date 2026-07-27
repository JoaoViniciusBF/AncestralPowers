package dev.joaq.ancestralpowers.mixin;

import dev.joaq.ancestralpowers.item.SolarAxeItem;
import dev.joaq.ancestralpowers.item.LunarAxeItem;
import dev.joaq.ancestralpowers.util.DayNightDamageUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    
    @ModifyVariable(
        method = "damage",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private float modifySolarDamage(float amount, DamageSource source) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            LivingEntity self = (LivingEntity) (Object) this;
            World world = self.getWorld();
            ItemStack mainHand = player.getMainHandStack();
            ItemStack offHand = player.getOffHandStack();
            
            if (mainHand.getItem() instanceof SolarAxeItem) {
                return amount * DayNightDamageUtils.getSolarMultiplier(world);
            }
            if (mainHand.getItem() instanceof LunarAxeItem) {
                return amount * DayNightDamageUtils.getLunarMultiplier(world);
            }
            
            if (offHand.getItem() instanceof SolarAxeItem) {
                return amount * DayNightDamageUtils.getSolarMultiplier(world);
            }
            if (offHand.getItem() instanceof LunarAxeItem) {
                return amount * DayNightDamageUtils.getLunarMultiplier(world);
            }
        }
        
        return amount;
    }
}
