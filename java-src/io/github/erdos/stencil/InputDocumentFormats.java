package io.github.erdos.stencil;

import java.util.Optional;

import static io.github.erdos.stencil.impl.FileHelper.extension;

/**
 * Supported raw template file types.
 * <p>
 * You can write the original template files in these file types.
 */
public enum InputDocumentFormats {
    DOCX(TemplateDocumentFormats.DOCX, false),
    DOC(TemplateDocumentFormats.DOCX, true),
    ODT(TemplateDocumentFormats.DOCX, true),
    RTF(TemplateDocumentFormats.DOCX, true),
    HTML(TemplateDocumentFormats.XML, false),
    XHTML(TemplateDocumentFormats.XML, false),
    XML(TemplateDocumentFormats.XML, false),
    TXT(TemplateDocumentFormats.TXT, false);
    private final TemplateDocumentFormats templateFormat;
    private final boolean templateShouldConvert;

    InputDocumentFormats(TemplateDocumentFormats templateFormat, boolean templateShouldConvert) {
        this.templateFormat = templateFormat;
        this.templateShouldConvert = templateShouldConvert;
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
}
