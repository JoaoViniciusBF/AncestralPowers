package dev.joaq.ancestralpowers.client;

import dev.joaq.ancestralpowers.item.DoubleJumpBootsItem;
import dev.joaq.ancestralpowers.item.DashBootsItem;
import dev.joaq.ancestralpowers.networking.packet.c2s.DoubleJumpPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class DoubleJumpHandler {
    private static boolean hasDoubleJumped = false;
    private static boolean wasJumpPressed = false;
    private static int airTicks = 0;

    public static void onClientTick(net.minecraft.client.MinecraftClient client) {
        if (client.player == null) return;
        
        boolean isJumpPressed = client.options.jumpKey.isPressed();

        if (client.player.isOnGround() || client.player.isClimbing()) {
            hasDoubleJumped = false;
            airTicks = 0;
            wasJumpPressed = isJumpPressed;
            return;
        } else {
            airTicks++;
        }

        if (hasDoubleJumped) {
            wasJumpPressed = isJumpPressed;
            return;
        }

        if (airTicks > 3 && isJumpPressed && !wasJumpPressed && !client.player.getAbilities().flying) {
            ItemStack boots = client.player.getEquippedStack(EquipmentSlot.FEET);
            
            if (boots.getItem() instanceof DoubleJumpBootsItem) {
                hasDoubleJumped = true;
                Vec3d vel = client.player.getVelocity();
                
                client.player.setVelocity(vel.x, 0.42, vel.z);
                PacketByteBuf buf = PacketByteBufs.create();
                DoubleJumpPayload.write(buf);
                ClientPlayNetworking.send(DoubleJumpPayload.ID, buf);
                
            } else if (boots.getItem() instanceof DashBootsItem) {
                hasDoubleJumped = true;
                Vec3d vel = client.player.getVelocity();
                Vec3d look = client.player.getRotationVector();
                
                double boostMultiplier = 0.5;
                client.player.setVelocity(
                    vel.x + (look.x * boostMultiplier), 
                    0.42, 
                    vel.z + (look.z * boostMultiplier)
                );
                PacketByteBuf buf = PacketByteBufs.create();
                DoubleJumpPayload.write(buf);
                ClientPlayNetworking.send(DoubleJumpPayload.ID, buf);
            }
        }
        
        wasJumpPressed = isJumpPressed;
    }
}
