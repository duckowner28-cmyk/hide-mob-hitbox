package com.example.hidemobhitboxes.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla draws the debug (F3+B) hitbox outline for every entity by calling
 * the private static EntityRenderDispatcher#renderHitboxes(...) method once
 * per entity, right before it hands off to the per-entity feature renderers.
 *
 * We inject at the very HEAD of that method and cancel it outright unless
 * the entity being drawn is a player. Because this hooks the single choke
 * point that ALL hitbox drawing passes through - rather than the toggle
 * that turns hitboxes on/off - it keeps working no matter what flipped
 * shouldRenderHitboxes() to true in the first place (vanilla F3+B, a
 * keybind mod, a PvP/ESP-style mod that force-enables hitboxes, etc.).
 *
 * A low mixin priority (set in hidemobhitboxes.mixins.json's owning
 * @Mixin annotation below) makes this injector merge - and therefore run -
 * before most other mods' injectors at the same method, so our cancellation
 * wins the race in the common case. There's no way to *guarantee* priority
 * over literally every other mod (another mod could pick an even lower
 * number, or hook something entirely different like the GL draw call), but
 * this covers the vast majority of hitbox/ESP mods you'll find.
 */
@Mixin(value = EntityRenderDispatcher.class, priority = 100)
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
