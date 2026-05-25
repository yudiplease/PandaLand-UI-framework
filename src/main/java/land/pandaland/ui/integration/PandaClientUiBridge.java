package land.pandaland.ui.integration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import land.pandaland.ui.api.PandaScreen;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public final class PandaClientUiBridge {
    private PandaClientUiBridge() {
    }

    public static void open(PandaScreen screen) {
        Minecraft.getMinecraft().displayGuiScreen(screen);
    }
}
