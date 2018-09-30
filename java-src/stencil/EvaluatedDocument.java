package stencil;

import java.io.InputStream;

/**
 * An evaluated document ready to be converted to the final output format.
 */
public interface EvaluatedDocument {

    /**
     * Content of document as input stream.
     */
    InputStream getInputStream();

    TemplateDocumentFormats getFormat();
}
