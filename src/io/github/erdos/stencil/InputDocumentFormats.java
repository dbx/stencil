package io.github.erdos.stencil;

import java.util.Optional;

import static stencil.impl.FileHelper.extension;

/**
 * Supported raw template file types.
 * <p>
 * You can write the original template files in these file types.
 */
public enum InputDocumentFormats {
    DOCX(TemplateDocumentFormats.DOCX, false, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    DOC(TemplateDocumentFormats.DOCX, true, "application/msword"),
    ODT(TemplateDocumentFormats.DOCX, true, "application/vnd.oasis.opendocument.text"),
    RTF(TemplateDocumentFormats.DOCX, true, "application/rtf"),
    HTML(TemplateDocumentFormats.XML, false, "text/html"),
    XHTML(TemplateDocumentFormats.XML, false, "application/xhtml+xml"),
    XML(TemplateDocumentFormats.XML, false, "application/xml"),
    TXT(TemplateDocumentFormats.TXT, false, "text/plain");

    private final TemplateDocumentFormats templateFormat;
    private final boolean templateShouldConvert;
    private final String mimeType;

    InputDocumentFormats(TemplateDocumentFormats templateFormat, boolean templateShouldConvert, String mimeType) {
        this.templateFormat = templateFormat;
        this.templateShouldConvert = templateShouldConvert;
        this.mimeType = mimeType;
    }

    /**
     * Tries to find a format instance for a given file extension.
     *
     * @param extension 3 or 4 letter string. May be null or empty.
     * @return a matching format instance or an empty optional. Returns empty on empty input.
     */
    public static Optional<InputDocumentFormats> ofExtension(String extension) {
        if (extension == null || extension.trim().isEmpty())
            return Optional.empty();
        extension = extension(extension);
        for (InputDocumentFormats f : values())
            if (f.getExtension().equals(extension))
                return Optional.of(f);
        return Optional.empty();
    }

    /**
     * Returns a 3-4 letter lowercase string of the file extension for this type.
     *
     * @return lowercase string
     */
    public String getExtension() {
        return name().toLowerCase();
    }

    /**
     * Returns the associated template document format.
     */
    public TemplateDocumentFormats templateConvertTo() {
        return templateFormat;
    }

    /**
     * Indicates that a conversion should be called before we start preprocessing the template.
     */
    public boolean templateShouldConvert() {
        return templateShouldConvert;
    }


    /**
     * Returns mime type as string value.
     */
    public String getMimeType() {
        return mimeType;
    }
}
