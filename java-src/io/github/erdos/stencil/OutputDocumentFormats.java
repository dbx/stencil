package io.github.erdos.stencil;

import java.util.Optional;

import static io.github.erdos.stencil.impl.FileHelper.extension;

/**
 * Supported output result document formats.
 */
public enum OutputDocumentFormats {

    /**
     * OOXML file (Microsoft Word)
     */
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

    /**
     * Word document (old format, need conversion from DOCX)
     */
    DOC("application/msword"),

    /**
     * LibreOffice format (needs conversion from DOCX).
     */
    ODT("application/vnd.oasis.opendocument.text"),

    /**
     * HTML for the Web
     */
    HTML("text/html"),

    /**
     * PDF document type (needs conversion from DOCX).
     */
    PDF("application/pdf"),

    /**
     * Simple text file.
     */
    TXT("text/plain"),

    /**
     * Rich Text Format (oldie)
     */
    RTF("application/rtf"),

    /**
     * Just XML.
     */
    XML("application/xml");

    private final String mimeType;

    OutputDocumentFormats(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Tries to find an output document format instance for a given extension string.
     *
     * @param extension 3-4 letter string
     * @return format instance of empty when not found or on empty argument.
     */
    public static Optional<OutputDocumentFormats> ofExtension(String extension) {
        if (extension == null || extension.trim().isEmpty())
            return Optional.empty();

        extension = extension(extension);
        for (OutputDocumentFormats f : values())
            if (f.getExtension().equals(extension))
                return Optional.of(f);
        return Optional.empty();
    }

    /**
     * Returns a lowercase 3-4 letter string that is the extension of the current file type.
     *
     * @return lowercase file extension string
     */
    public String getExtension() {
        return name().toLowerCase();
    }

    /**
     * Returns mime type as string value.
     */
    public String getMimeType() {
        return this.mimeType;
    }
}
