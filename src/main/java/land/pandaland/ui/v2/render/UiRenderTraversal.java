package land.pandaland.ui.v2.render;

import land.pandaland.ui.v2.core.UiNode;
import land.pandaland.ui.v2.core.UiRuntime;
import land.pandaland.ui.v2.style.UiTheme;

public final class UiRenderTraversal {
    private UiRenderTraversal() {
    }

    public static UiRenderList render(UiRuntime runtime, UiTheme theme) {
        if (runtime == null) {
            throw new IllegalArgumentException("runtime cannot be null");
        }
        if (theme == null) {
            throw new IllegalArgumentException("theme cannot be null");
        }
        UiRenderList commands = new UiRenderList();
        renderNode(commands, runtime.screen().root(), theme);
        for (UiNode modal : runtime.modals()) {
            renderNode(commands, modal, theme);
        }
        for (UiNode toast : runtime.toasts()) {
            renderNode(commands, toast, theme);
        }
        return commands;
    }

    private static void renderNode(UiRenderList commands, UiNode node, UiTheme theme) {
        if (node == null || !node.visible()) {
            return;
        }
        if (!node.texture().isEmpty()) {
            commands.add(UiRenderCommand.texture(node.texture(), node.bounds()));
        } else if (node.type() == UiNode.Type.PANEL) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
        } else if (node.type() == UiNode.Type.BUTTON) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.text(node.text(), node.bounds(), node.enabled() ? theme.textPrimary() : theme.textMuted()));
        } else if (node.type() == UiNode.Type.LABEL) {
            commands.add(UiRenderCommand.text(node.text(), node.bounds(), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.PROGRESS) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.progress(node.bounds(), theme.primaryAccent(), valueAmount(node)));
        } else if (node.type() == UiNode.Type.SLIDER) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.progress(node.bounds(), theme.primaryAccent(), valueAmount(node)));
            commands.add(UiRenderCommand.text(sliderText(node), node.bounds(), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.MODAL) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.panelRadius(), theme.panelBase()));
            commands.add(UiRenderCommand.text(node.text(), node.bounds(), theme.textPrimary()));
        } else if (node.type() == UiNode.Type.TOAST) {
            commands.add(UiRenderCommand.roundedRect(node.bounds(), theme.buttonRadius(), theme.buttonBase()));
            commands.add(UiRenderCommand.text(node.text(), node.bounds(), theme.textPrimary()));
        }

        for (UiNode child : node.children()) {
            renderNode(commands, child, theme);
        }
    }

    private static String sliderText(UiNode node) {
        if (node.valueState() == null || node.valueState().get() == null) {
            return node.text();
        }
        return node.text() + ": " + Math.round(node.valueState().get().floatValue() * 100.0F) + "%";
    }

    private static float valueAmount(UiNode node) {
        if (node.valueState() == null || node.valueState().get() == null) {
            return 1.0F;
        }
        return node.valueState().get().floatValue();
    }
}
