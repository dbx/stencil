package io.github.erdos.stencil;

import java.util.Collections;
import java.util.Map;

/**
 * Contains data to fill template documents.
 */
public final class TemplateData {

    private final Map<String, Object> data;

    private TemplateData(Map<String, Object> data) {
        if (data == null)
            throw new IllegalArgumentException("Data parameter nem lehet null!");

        this.data = Collections.unmodifiableMap(data);
    }

    /**
     * Constructs a template data instance holding a map data structure.
     *
     * @param data map of template data. Possibly nested: values might contain maps or vectors recursively.
     * @return constructed data holder. Never null.
     * @throws IllegalArgumentException when input is null
     */
    @SuppressWarnings("unused")
    public static TemplateData fromMap(Map<String, Object> data) {
        if (data == null) throw new IllegalArgumentException("Template data must not be null!");
        return new TemplateData(data);
    }

    /**
     * Returns contained data as a possibly nested map.
     *
     * @return template data map. Not null.
     */
    public final Map<String, Object> getData() {
        return data;
    }
}
