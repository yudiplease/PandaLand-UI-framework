package land.pandaland.ui.v2.data;

/**
 * Immutable styled text fragment for rich text controls.
 */
public final class UiRichTextSpan {
    private final String text;
    private final int color;
    private final boolean bold;
    private final boolean italic;

    /**
     * Creates a plain white span.
     *
     * @param text span text
     */
    public UiRichTextSpan(String text) {
        this(text, 0xFFFFFF, false, false);
    }

    /**
     * Creates a rich text span.
     *
     * @param text span text
     * @param color RGB color
     * @param bold bold style flag
     * @param italic italic style flag
     */
    public UiRichTextSpan(String text, int color, boolean bold, boolean italic) {
        this.text = text == null ? "" : text;
        this.color = color;
        this.bold = bold;
        this.italic = italic;
    }

    /**
     * Returns the span text.
     *
     * @return span text, never {@code null}
     */
    public String text() {
        return text;
    }

    /**
     * Returns the RGB text color.
     *
     * @return text color
     */
    public int color() {
        return color;
    }

    /**
     * Reports whether the span should be bold.
     *
     * @return bold flag
     */
    public boolean bold() {
        return bold;
    }

    /**
     * Reports whether the span should be italic.
     *
     * @return italic flag
     */
    public boolean italic() {
        return italic;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UiRichTextSpan)) {
            return false;
        }
        UiRichTextSpan that = (UiRichTextSpan) other;
        return color == that.color && bold == that.bold && italic == that.italic && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + color;
        result = 31 * result + (bold ? 1 : 0);
        result = 31 * result + (italic ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UiRichTextSpan{text='" + text + "', color=" + color + ", bold=" + bold + ", italic=" + italic + "}";
    }
}
