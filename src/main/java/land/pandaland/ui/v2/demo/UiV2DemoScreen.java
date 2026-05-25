package land.pandaland.ui.v2.demo;

import land.pandaland.ui.v2.api.Ui;
import land.pandaland.ui.v2.core.UiScreen;
import land.pandaland.ui.v2.event.UiTextValidator;
import land.pandaland.ui.v2.event.UiValidationResult;
import land.pandaland.ui.v2.state.UiState;

/**
 * Small sample screen that exercises the v2 fluent API.
 */
public final class UiV2DemoScreen {
    private UiV2DemoScreen() {
    }

    /**
     * Creates the demo screen.
     *
     * @return demo screen
     */
    public static UiScreen create() {
        final UiState<String> name = UiState.of("");
        final UiState<Boolean> enabled = UiState.of(Boolean.TRUE);
        final UiState<Integer> tab = UiState.of(Integer.valueOf(0));
        return Ui.screen("pandaland-v2-demo")
            .root(new Ui.RootBuilderConsumer() {
                /**
                 * Populates the demo root tree.
                 *
                 * @param root root builder
                 */
                public void build(Ui.NodeBuilder root) {
                    root.column()
                        .padding(12)
                        .gap(8)
                        .label("PandaLand UI v2", 180, 14)
                        .textInput(name, "Nickname", 180, 22, 16, null, null, new UiTextValidator() {
                            /**
                             * Rejects spaces in the demo nickname field.
                             *
                             * @param candidate proposed nickname
                             * @return validation result
                             */
                            public UiValidationResult validate(String candidate) {
                                return candidate.indexOf(' ') >= 0 ? UiValidationResult.invalid("Spaces are not allowed") : UiValidationResult.ok();
                            }
                        })
                        .checkbox("Enabled", enabled)
                        .tabs(new String[] {"Main", "Advanced"}, tab, 180, 20);
                }
            })
            .build();
    }
}
