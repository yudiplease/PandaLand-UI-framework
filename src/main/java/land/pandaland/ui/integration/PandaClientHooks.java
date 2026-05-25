package land.pandaland.ui.integration;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import land.pandaland.ui.api.PandaUi;
import land.pandaland.ui.render.MinecraftPandaRenderer;
import land.pandaland.ui.v2.demo.UiV2DemoScreen;
import land.pandaland.ui.v2.forge.UiV2ScreenAdapter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public final class PandaClientHooks {
    private static boolean registered;
    private boolean demoKeyWasDown;
    private long lastHudUpdateMs;

    private PandaClientHooks() {
    }

    public static void register() {
        if (!registered) {
            PandaClientHooks hooks = new PandaClientHooks();
            FMLCommonHandler.instance().bus().register(hooks);
            MinecraftForge.EVENT_BUS.register(hooks);
            registered = true;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        openDemoOnKeyPress();

        long now = System.currentTimeMillis();
        long deltaMs = lastHudUpdateMs == 0L ? 0L : Math.max(0L, now - lastHudUpdateMs);
        lastHudUpdateMs = now;
        PandaUi.updateHud(deltaMs);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        MinecraftPandaRenderer renderer = new MinecraftPandaRenderer(Minecraft.getMinecraft());
        PandaUi.renderHud(renderer);
    }

    private void openDemoOnKeyPress() {
        boolean demoKeyDown = Keyboard.isKeyDown(Keyboard.KEY_P);
        if (demoKeyDown && !demoKeyWasDown) {
            Minecraft minecraft = Minecraft.getMinecraft();
            if (!(minecraft.currentScreen instanceof UiV2ScreenAdapter)) {
                minecraft.displayGuiScreen(new UiV2ScreenAdapter(UiV2DemoScreen.create()));
            }
        }
        demoKeyWasDown = demoKeyDown;
    }
}
