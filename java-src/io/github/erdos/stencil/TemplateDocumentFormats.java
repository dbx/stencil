package io.github.erdos.stencil;

/**
 * These types are used when preprocessing a template document.
 */
public enum TemplateDocumentFormats {

    /**
     * Zipped XML files type. See: OOXML.
     */
    DOCX(OutputDocumentFormats.DOCX),

    /**
     * Raw XML file.
     */
    XML(OutputDocumentFormats.HTML),

    /**
     * Simple text file without formatting. Like XML but without a header.
     */
    TXT(OutputDocumentFormats.TXT);

    private final OutputDocumentFormats outputFormat;

    TemplateDocumentFormats(OutputDocumentFormats outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * Output format type associated with this template doc format.
     *
     * @return its equivalent output format
     */
    public OutputDocumentFormats asOutputFormat() {
        return this.outputFormat;
    }
}
