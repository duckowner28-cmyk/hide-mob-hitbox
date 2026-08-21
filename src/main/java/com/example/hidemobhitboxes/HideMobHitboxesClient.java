package com.example.hidemobhitboxes.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderManager.class, priority = 100)
public abstract class EntityRenderDispatcherMixin {

	@Inject(method = "renderHitboxes", at = @At("HEAD"), cancellable = true)
	private static void hideMobHitboxes(
			MatrixStack matrices,
			EntityRenderState state,
			EntityHitboxAndView hitbox,
			VertexConsumerProvider vertexConsumers,
			CallbackInfo ci
	) {
		if (!(state instanceof PlayerEntityRenderState)) {
			ci.cancel();
		}
	}
}
