package io.github.erdos.stencil;

import org.jodconverter.office.OfficeManager;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;

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

    /**
     * Tries to construct a process instance and tries to find local libreoffice install.
     * <p>
     * First, looks for LIBRE_OFFICE_HOME env variable.
     * Second, looks at /usr/lib64/libreoffice (Fedora default install destination).
     * Third, looks at /opt/libreoffice* and tries to load largest version number.
     *
     * @return a new Process instance with a running LibreOffice
     * @throws IllegalStateException when no standard directory has been found.
     */
    public static Process fromLocalLibreOffice() throws IllegalStateException {
        if (null != System.getenv("LIBRE_OFFICE_HOME")) {
            File home = new File(System.getenv("LIBRE_OFFICE_HOME"));

        }

        final File home1 = new File("/usr/lib64/libreoffice");
        if (home1.exists()) {
            return fromLibreOfficeHome(home1);
        }

        final File[] optFiles = new File("/opt").listFiles((dir, name) -> name.startsWith("libreoffice"));
        if (optFiles != null && optFiles.length > 0) {
            List<File> homes = asList(optFiles);
            homes.sort(Comparator.reverseOrder());
            return fromLibreOfficeHome(homes.get(0));
        }

        throw new IllegalStateException("Could not find local LibreOffice home!");
    }
}
