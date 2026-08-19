package com.example.hidemobhitboxes;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideMobHitboxesClient implements ClientModInitializer {
	public static final String MOD_ID = "hide-mob-hitboxes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// All the real work happens in EntityRenderDispatcherMixin, which is
		// applied automatically by the mixin system. Nothing to wire up here.
		LOGGER.info("[HideMobHitboxes] loaded - non-player hitboxes will be hidden.");
	}
}
