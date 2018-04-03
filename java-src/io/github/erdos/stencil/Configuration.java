package io.github.erdos.stencil;

import java.io.File;

/**
 * This class is not used at the moment.
 */
@SuppressWarnings("unused")
public interface Configuration {

    /**
     * Temporarily unzips files here.
     */
    File getTempDirectory();

    /**
     * Renders result documents here.
     */
    File getTargetDirectory();

    /**
     * Source templates come here.
     */
    File getTemplatesDirectory();

    /**
     * Preprocessed templates come here.
     */
    File getCacheDirectory();
}
