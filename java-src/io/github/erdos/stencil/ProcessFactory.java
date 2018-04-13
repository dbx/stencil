package io.github.erdos.stencil;

import org.jodconverter.office.OfficeManager;

import java.io.File;

/**
 * Helps to construct Process instances.
 */
@SuppressWarnings("unused")
public final class ProcessFactory {

    private ProcessFactory() {
    }

    /**
     * Constructs a process instance from a given office manager.
     *
     * @param officeManager office manager instance
     * @return new process instance
     * @throws IllegalArgumentException when office manager is null
     */
    public static Process fromOfficeManager(OfficeManager officeManager) throws IllegalArgumentException {
        return new Process(officeManager);
    }

    /**
     * Constructs a process instance with a local libreoffice installation.
     * A correct directory must contain a "program/soffice.bin" file.
     * Exception is throw when the directory is not a valid location.
     *
     * @param libreOfficeHomeDirectory place of LO installation.
     * @return new process instance
     * @throws IllegalArgumentException when argument is not valid LO directory
     */
    public static Process fromLibreOfficeHome(File libreOfficeHomeDirectory) throws IllegalArgumentException {
        return new Process(libreOfficeHomeDirectory);
    }
}
