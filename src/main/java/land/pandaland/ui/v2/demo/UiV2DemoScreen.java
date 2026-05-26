package land.pandaland.ui.v2.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.data.UiListItem;
import land.pandaland.ui.v2.data.UiOption;
import land.pandaland.ui.v2.data.UiRichTextSpan;
import land.pandaland.ui.v2.data.UiTableColumn;
import land.pandaland.ui.v2.data.UiTableRow;
import land.pandaland.ui.v2.data.UiTreeItem;
import land.pandaland.ui.v2.event.UiTextValidator;
import land.pandaland.ui.v2.event.UiValidationResult;
import land.pandaland.ui.v2.layout.UiRect;
import land.pandaland.ui.v2.render.UiCustomDraw;
import land.pandaland.ui.v2.render.UiRenderCommand;
import land.pandaland.ui.v2.render.UiRenderList;
import land.pandaland.ui.v2.state.UiState;
import land.pandaland.ui.v2.style.UiColor;

/**
 * Compact sample screen that exercises the public v2 UI-kit API.
 */
public final class UiV2DemoScreen {
    private static final int WIDTH = 360;

    private UiV2DemoScreen() {
    }

    /**
     * Creates the demo screen.
     *
     * @return demo screen
     */
    public static UiScreen create() {
        final UiState<String> nickname = UiState.of("yudiplease");
        final UiState<String> password = UiState.of("demo");
        final UiState<String> money = UiState.of("1250");
        final UiState<String> keybind = UiState.of("KEY_R");
        final UiState<String> region = UiState.of("spawn");
        final UiState<String> tab = UiState.of("inputs");
        final UiState<Boolean> enabled = UiState.of(Boolean.TRUE);
        final UiState<Float> progress = UiState.of(Float.valueOf(0.64F));
        final UiState<Integer> accent = UiState.of(Integer.valueOf(0x24E0D0));
        final List<String> selectedPermissions = new ArrayList<String>();
        selectedPermissions.add("chat");
        selectedPermissions.add("commands");
        final List<String> selectedRows = new ArrayList<String>();
        selectedRows.add("owner");

        return Ui.screen("pandaland-v2-demo")
            .root(new Ui.RootBuilderConsumer() {
                /**
                 * Populates the demo root tree.
                 *
                 * @param root root builder
                 */
                public void build(Ui.NodeBuilder root) {
                    root.column()
                        .padding(18)
                        .gap(6)
                        .label("PandaLand UI v2.1", WIDTH, 14)
                        .richText(intro(), WIDTH, 18)
                        .tabs(tabs(), tab, WIDTH, 18)
                        .panel(new Ui.PanelBuilderConsumer() {
                            public void build(Ui.NodeBuilder panel) {
                                panel.column()
                                    .size(WIDTH, 270)
                                    .padding(8)
                                    .gap(5)
                                    .label("Inputs", WIDTH - 16, 12)
                                    .textInput(nickname, "Nickname", WIDTH - 16, 18, 16, null, null, nicknameValidator())
                                    .passwordInput(password, "Password", WIDTH - 16, 18, 32)
                                    .numericInput(money, "Coins", WIDTH - 16, 18, 8)
                                    .keybindInput("Reload key", keybind, WIDTH - 16, 18)
                                    .checkbox("Feature enabled", enabled)
                                    .slider("Progress", progress, WIDTH - 16, 14)
                                    .searchableSelect("Region", region, regions(), WIDTH - 16, 18)
                                    .multiSelect("Permissions", permissions(), selectedPermissions, WIDTH - 16, 28)
                                    .colorPicker("Accent", accent, WIDTH - 16, 18)
                                    .table(columns(), rows(), WIDTH - 16, 46)
                                    .dataGrid(columns(), rows(), selectedRows, WIDTH - 16, 46);
                            }
                        })
                        .panel(new Ui.PanelBuilderConsumer() {
                            public void build(Ui.NodeBuilder panel) {
                                panel.column()
                                    .size(WIDTH, 86)
                                    .padding(8)
                                    .gap(5)
                                    .label("Advanced", WIDTH - 16, 12)
                                    .tree(tree(), selectedRows, WIDTH - 16, 32)
                                    .virtualList(activity(), selectedRows, WIDTH - 16, 10);
                            }
                        })
                        .panel(new Ui.PanelBuilderConsumer() {
                            public void build(Ui.NodeBuilder panel) {
                                panel.column()
                                    .size(WIDTH, 74)
                                    .padding(8)
                                    .gap(5)
                                    .label("Canvas", WIDTH - 16, 12)
                                    .canvas(neonCanvas(), WIDTH - 16, 44);
                            }
                        });
                }
            })
            .build();
    }

    private static UiTextValidator nicknameValidator() {
        return new UiTextValidator() {
            public UiValidationResult validate(String candidate) {
                if (candidate == null || candidate.length() < 3) {
                    return UiValidationResult.invalid("Nickname is too short");
                }
                return candidate.indexOf(' ') >= 0 ? UiValidationResult.invalid("Spaces are not allowed") : UiValidationResult.ok();
            }
        };
    }

    private static List<UiRichTextSpan> intro() {
        return Arrays.asList(
            new UiRichTextSpan("Compact API showcase: ", 0xDFFBFF, true, false),
            new UiRichTextSpan("inputs, data, canvas", 0x37EAD9, false, false)
        );
    }

    private static List<UiOption> tabs() {
        return Arrays.asList(
            new UiOption("inputs", "Inputs"),
            new UiOption("data", "Data"),
            new UiOption("render", "Render")
        );
    }

    private static List<UiOption> regions() {
        return Arrays.asList(
            new UiOption("spawn", "Spawn"),
            new UiOption("arena", "Arena"),
            new UiOption("market", "Market"),
            new UiOption("locked", "Locked", true)
        );
    }

    private static List<UiOption> permissions() {
        return Arrays.asList(
            new UiOption("chat", "Chat"),
            new UiOption("commands", "Commands"),
            new UiOption("build", "Build"),
            new UiOption("admin", "Admin", true)
        );
    }

    private static List<UiTableColumn> columns() {
        return Arrays.asList(
            new UiTableColumn("role", "Role", 64, true),
            new UiTableColumn("user", "User", 76),
            new UiTableColumn("status", "State", 54)
        );
    }

    private static List<UiTableRow> rows() {
        return Arrays.asList(
            row("owner", "Founder", "Yudi", "On"),
            row("mod", "Mod", "Akari", "Busy")
        );
    }

    private static UiTableRow row(String id, String role, String user, String status) {
        Map<String, String> cells = new LinkedHashMap<String, String>();
        cells.put("role", role);
        cells.put("user", user);
        cells.put("status", status);
        return new UiTableRow(id, cells);
    }

    private static List<UiTreeItem> tree() {
        return Arrays.asList(
            new UiTreeItem("hud", "HUD"),
            new UiTreeItem("menu", "Menus")
        );
    }

    private static List<UiListItem> activity() {
        return Arrays.asList(
            new UiListItem("auth", "Auth passed"),
            new UiListItem("owner", "Files synced"),
            new UiListItem("game", "Game ready")
        );
    }

    private static UiCustomDraw neonCanvas() {
        return new UiCustomDraw() {
            public void draw(UiRenderList commands, UiRect bounds) {
                UiRect inset = new UiRect(bounds.x + 4, bounds.y + 4, bounds.width - 8, bounds.height - 8);
                commands.add(UiRenderCommand.gradientRect(inset, 7, new UiColor(0x8024E0D0), new UiColor(0x70FF4DD8), false));
                commands.add(UiRenderCommand.border(inset, 7, new UiColor(0xCCF4F7FF), 1));
                commands.add(UiRenderCommand.text("Custom canvas", new UiRect(inset.x + 10, inset.y + 10, inset.width - 20, 12), new UiColor(0xFFFFFFFF)));
            }
        };
    }
}
