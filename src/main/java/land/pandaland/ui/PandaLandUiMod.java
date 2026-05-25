package land.pandaland.ui;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import land.pandaland.ui.integration.CommonProxy;

@Mod(
    modid = PandaLandUiMod.MOD_ID,
    name = PandaLandUiMod.MOD_NAME,
    version = PandaLandUiMod.VERSION,
    acceptedMinecraftVersions = "[1.7.10]"
)
public final class PandaLandUiMod {
    public static final String MOD_ID = "pandaland_ui";
    public static final String MOD_NAME = "PandaLand UI Framework";
    // Forge 1.7.10 annotations require compile-time constants; keep this in sync with gradle.properties.
    public static final String VERSION = "0.1.0";

    @SidedProxy(
        clientSide = "land.pandaland.ui.integration.ClientProxy",
        serverSide = "land.pandaland.ui.integration.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }
}
