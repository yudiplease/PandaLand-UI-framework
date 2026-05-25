# PandaLand UI Framework

Client-side Forge 1.7.10 UI framework for PandaLand custom mods.

The primary v2 public API is `land.pandaland.ui.v2.api.Ui`. Feature mods should build new screens through the v2 fluent Java API and treat runtime, render, integration, layout, event, and theme packages as framework implementation details.

The legacy `land.pandaland.ui.api` package remains temporarily for existing PandaLand client UI code and will be removed after `D:\PandaLand mods` is ported.

## Requirements

- Minecraft Forge 1.7.10.
- Java 8 JDK, not a JRE and not a newer JDK. ForgeGradle 1.2 expects the Java compiler from a Java 8 JDK.

On PowerShell, point `JAVA_HOME` at a Java 8 JDK before building:

```powershell
$env:JAVA_HOME = "C:\Path\To\jdk8"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build
```

This workspace may include a local portable JDK at `.tools\jdk8u492-b09`. If it is present, it can be used without committing tools into source control:

```powershell
$env:JAVA_HOME = "$PWD\.tools\jdk8u492-b09"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build
```

## Build

```powershell
.\gradlew.bat build
```

The build produces `build\libs\pandaland-ui-framework-0.1.0.jar`.

## Run Demo

```powershell
.\gradlew.bat runClient
```

Once the dev client launches successfully, press `P` to open the v2 demo screen through `UiV2ScreenAdapter`. The demo is intended to validate the retained tree, fluent API, layout, events, slider state, render command traversal, and Forge adapter.

Current local note: `runClient` may still be blocked by the legacy ForgeGradle/Minecraft 1.7.10 asset index setup in some environments. Treat the in-game demo as intended behavior once the asset index setup is repaired; until then, use `build` and JVM tests for documentation/API checks.

## Minimal Screen

```java
import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiScreen;

public final class ExampleScreenFactory {
    public UiScreen create() {
        return Ui.screen("example")
            .root(new Ui.RootBuilderConsumer() {
                public void build(Ui.NodeBuilder root) {
                    root.column()
                        .panel(new Ui.PanelBuilderConsumer() {
                            public void build(Ui.NodeBuilder panel) {
                                panel.label("Example")
                                    .button("OK", new Runnable() {
                                        public void run() {
                                            // Update feature state or close the screen here.
                                        }
                                    });
                            }
                        });
                }
            })
            .build();
    }
}
```

Open a v2 screen from client code with `new UiV2ScreenAdapter(screen)` and `Minecraft.getMinecraft().displayGuiScreen(...)`.

## Minimal HUD Overlay

```java
import land.pandaland.ui.api.PandaHudOverlay;
import land.pandaland.ui.api.PandaLabel;
import land.pandaland.ui.api.PandaRect;
import land.pandaland.ui.api.PandaRenderer;
import land.pandaland.ui.api.PandaUi;

public final class ExampleHud extends PandaHudOverlay {
    private final PandaLabel label = PandaLabel.text("HUD ready");

    public ExampleHud() {
        label.setBounds(new PandaRect(8, 8, 120, 12));
    }

    public int priority() {
        return 20;
    }

    public void render(PandaRenderer renderer) {
        renderer.label(label);
    }
}
```

## Toasts And Motion

Show managed toast notifications through `PandaUi.toast(message)` or `PandaUi.toast(message, durationMs)`. The framework updates toast duration from HUD ticks and removes expired toasts automatically.

Use `PandaUi.setReducedMotion(true)` to make framework progress and interaction animations snap to their target values.

Register the overlay from feature-mod client initialization code:

```java
import land.pandaland.ui.api.PandaUi;

public final class ExampleHudClientUi {
    private final ExampleHud hud = new ExampleHud();

    public void initClientUi() {
        PandaUi.registerHud(hud);
    }

    public void shutdownClientUi() {
        PandaUi.unregisterHud(hud);
    }
}
```
