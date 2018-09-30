package io.github.erdos.stencil;

import stencil.EvaluatedDocument;
import stencil.PreparedTemplate;
import stencil.TemplateData;

/**
 * Evaluates a preprocessed template using a given data set.
 */
public interface Evaluator {

    /**
     * Evaluates a preprocessed template using the given data.
     *
     * @param template preprocessed template file
     * @param data     contains template variables
     * @return evaluated document ready to save to fs
     * @throws IllegalArgumentException when any arg is null
     */
    EvaluatedDocument render(PreparedTemplate template, TemplateData data) throws IllegalArgumentException;
}
