package land.pandaland.ui.render;

import land.pandaland.ui.api.PandaButton;
import land.pandaland.ui.api.PandaComponent;
import land.pandaland.ui.api.PandaHudBar;
import land.pandaland.ui.api.PandaIcon;
import land.pandaland.ui.api.PandaLabel;
import land.pandaland.ui.api.PandaLayout;
import land.pandaland.ui.api.PandaList;
import land.pandaland.ui.api.PandaModal;
import land.pandaland.ui.api.PandaPanel;
import land.pandaland.ui.api.PandaProgressBar;
import land.pandaland.ui.api.PandaSlider;
import land.pandaland.ui.api.PandaTabs;
import land.pandaland.ui.api.PandaToast;
import land.pandaland.ui.runtime.PandaScreenRuntime;
import land.pandaland.ui.runtime.PandaUiErrorHandler;

final class PandaRenderTraversal {
    private PandaRenderTraversal() {
    }

    static void render(land.pandaland.ui.api.PandaRenderer renderer, PandaLayout layout, int mouseX, int mouseY) {
        for (PandaComponent component : layout.children()) {
            renderComponent(renderer, component, mouseX, mouseY);
        }
    }

    static void render(land.pandaland.ui.api.PandaRenderer renderer, PandaScreenRuntime runtime, int mouseX, int mouseY) {
        render(renderer, runtime.root(), mouseX, mouseY);
        for (PandaModal modal : runtime.modals()) {
            renderComponent(renderer, modal, mouseX, mouseY);
        }
    }

    private static void renderComponent(land.pandaland.ui.api.PandaRenderer renderer, PandaComponent component, int mouseX, int mouseY) {
        if (!component.visible()) {
            return;
        }

        if (component instanceof PandaPanel) {
            PandaPanel panel = (PandaPanel) component;
            renderSafely("render panel", panel, new RenderCall() {
                public void render() {
                    renderer.panel(panel);
                }
            });
            render(renderer, panel.content(), mouseX, mouseY);
        } else if (component instanceof PandaModal) {
            PandaModal modal = (PandaModal) component;
            if (modal.open()) {
                renderSafely("render modal", modal, new RenderCall() {
                    public void render() {
                        renderer.modal(modal);
                    }
                });
                render(renderer, modal.panel().content(), mouseX, mouseY);
            }
        } else if (component instanceof PandaButton) {
            final PandaButton button = (PandaButton) component;
            renderSafely("render button", button, new RenderCall() {
                public void render() {
                    renderer.button(button, button.hovered());
                }
            });
        } else if (component instanceof PandaIcon) {
            final PandaIcon icon = (PandaIcon) component;
            renderSafely("render icon", icon, new RenderCall() {
                public void render() {
                    renderer.icon(icon);
                }
            });
        } else if (component instanceof PandaLabel) {
            final PandaLabel label = (PandaLabel) component;
            renderSafely("render label", label, new RenderCall() {
                public void render() {
                    renderer.label(label);
                }
            });
        } else if (component instanceof PandaList) {
            final PandaList list = (PandaList) component;
            final int hoveredIndex = hoveredListRow(list, mouseX, mouseY);
            renderSafely("render list", list, new RenderCall() {
                public void render() {
                    renderer.list(list, hoveredIndex);
                }
            });
        } else if (component instanceof PandaProgressBar) {
            final PandaProgressBar progressBar = (PandaProgressBar) component;
            renderSafely("render progress", progressBar, new RenderCall() {
                public void render() {
                    renderer.progress(progressBar);
                }
            });
        } else if (component instanceof PandaSlider) {
            final PandaSlider slider = (PandaSlider) component;
            renderSafely("render slider", slider, new RenderCall() {
                public void render() {
                    renderer.slider(slider, slider.hovered());
                }
            });
        } else if (component instanceof PandaHudBar) {
            final PandaHudBar hudBar = (PandaHudBar) component;
            renderSafely("render hudBar", hudBar, new RenderCall() {
                public void render() {
                    renderer.hudBar(hudBar);
                }
            });
        } else if (component instanceof PandaTabs) {
            final PandaTabs tabs = (PandaTabs) component;
            final int hoveredIndex = hoveredTab(tabs, mouseX, mouseY);
            renderSafely("render tabs", tabs, new RenderCall() {
                public void render() {
                    renderer.tabs(tabs, hoveredIndex);
                }
            });
        } else if (component instanceof PandaToast) {
            final PandaToast toast = (PandaToast) component;
            renderSafely("render toast", toast, new RenderCall() {
                public void render() {
                    renderer.toast(toast);
                }
            });
        } else if (component instanceof PandaLayout) {
            render(renderer, (PandaLayout) component, mouseX, mouseY);
        }
    }

    private static void renderSafely(String operation, PandaComponent component, RenderCall call) {
        try {
            call.render();
        } catch (RuntimeException error) {
            PandaUiErrorHandler.log(operation, component, error);
        }
    }

    private interface RenderCall {
        void render();
    }

    private static int hoveredTab(PandaTabs tabs, int mouseX, int mouseY) {
        if (tabs.labels().isEmpty() || !tabs.bounds().contains(mouseX, mouseY)) {
            return -1;
        }
        int tabWidth = Math.max(1, tabs.bounds().width / tabs.labels().size());
        return Math.min(tabs.labels().size() - 1, Math.max(0, (mouseX - tabs.bounds().x) / tabWidth));
    }

    private static int hoveredListRow(PandaList list, int mouseX, int mouseY) {
        if (list.rows().isEmpty() || !list.bounds().contains(mouseX, mouseY)) {
            return -1;
        }
        int rowHeight = Math.max(1, list.bounds().height / list.rows().size());
        return Math.min(list.rows().size() - 1, Math.max(0, (mouseY - list.bounds().y) / rowHeight));
    }
}
