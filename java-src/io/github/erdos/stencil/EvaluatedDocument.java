package io.github.erdos.stencil;

import java.io.InputStream;

/**
 * An evaluated document ready to be converted to the final output format.
 */
public interface EvaluatedDocument {

    OutputDocumentFormats getFormat();

    InputStream getInputStream();

//    EvaluatedStatistics getStatistics();
}

    /*
    interface EvaluatedStatistics {

    Optional<Integer> pageCount();
}
*/