package io.github.erdos.stencil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents an already preprocessed template file.
 * <p>
 * These files may be serialized or cached for later use.
 */
@SuppressWarnings("unused")
public interface PreparedTemplate {

    /**
     * Name of the original file.
     *
     * @return original template name
     */
    String getName();

    /**
     * Original template file that was preprocessed.
     *
     * @return original template file
     */
    File getTemplateFile();

    /**
     * Time when the template was processed.
     *
     * @return template preprocess call time
     */
    LocalDateTime creationDateTime();

    /**
     * Contains the preprocess result.
     * <p>
     * Implementation detail. May be used for serializing these objects. May be used for debugging too.
     *
     * @return inner representation of prepared template
     */
    Object getSecretObject();

    /**
     * Set of template variables found in file.
     * <p>
     * A template variable is represented as a path in the template data object
     * where path items are separated by '.' dot characters.
     *
     * @return string represeting paths in acceptable template data object.
     */
    Set<String> getVariables();

}
