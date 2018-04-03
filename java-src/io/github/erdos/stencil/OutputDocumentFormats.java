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
    DOCX,

    /**
     * Word document (old format, need conversion from DOCX)
     */
    DOC,

    /**
     * LibreOffice format (needs conversion from DOCX).
     */
    ODT,

    /**
     * HTML for the Web
     */
    HTML,

    /**
     * PDF document type (needs conversion from DOCX).
     */
    PDF,

    /**
     * Simple text file.
     */
    TXT,

    /**
     * Rich Text Format (oldie)
     */
    RTF;

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
}
