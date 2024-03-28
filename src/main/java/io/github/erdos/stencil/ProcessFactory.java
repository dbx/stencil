package io.github.erdos.stencil;

import org.apache.commons.lang3.StringUtils;
import org.jodconverter.core.office.OfficeManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

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
     * @param officeManager
     *         office manager instance
     *
     * @return new process instance
     *
     * @throws IllegalArgumentException
     *         when office manager is null
     */
    public static Process fromOfficeManager(OfficeManager officeManager) throws IllegalArgumentException {
        return fromOfficeManager(officeManager, false);
    }

    /**
     * Constructs a process instance from a given office manager.
     *
     * @param officeManager
     *         office manager instance
     * @param caching
     *         cache templates
     *
     * @return new process instance
     *
     * @throws IllegalArgumentException
     *         when office manager is null
     */
    public static Process fromOfficeManager(OfficeManager officeManager, boolean caching) throws IllegalArgumentException {
        return caching ? new CachingProcess(officeManager) : new Process(officeManager);
    }

    /**
     * Constructs a process instance with a local libreoffice installation.
     * A correct directory must contain a "program/soffice.bin" file.
     * Exception is throw when the directory is not a valid location.
     *
     * @param libreOfficeHomeDirectory
     *         place of LO installation.
     *
     * @return new process instance
     *
     * @throws IllegalArgumentException
     *         when argument is not valid LO directory
     */
    public static Process fromLibreOfficeHome(File libreOfficeHomeDirectory) throws IllegalArgumentException {
        return fromLibreOfficeHome(libreOfficeHomeDirectory, false);
    }

    /**
     * Constructs a process instance with a local libreoffice installation.
     * A correct directory must contain a "program/soffice.bin" file.
     * Exception is throw when the directory is not a valid location.
     *
     * @param libreOfficeHomeDirectory
     *         place of LO installation.
     *
     * @return new process instance
     *
     * @throws IllegalArgumentException
     *         when argument is not valid LO directory
     */
    public static Process fromLibreOfficeHome(File libreOfficeHomeDirectory, boolean caching) throws IllegalArgumentException {
        return caching ? new CachingProcess(libreOfficeHomeDirectory) : new Process(libreOfficeHomeDirectory);
    }

    /**
     * Tries to construct a process instance and tries to find local libreoffice install.
     * <p>
     * First, looks for LIBRE_OFFICE_HOME env variable.
     * Second, looks at /usr/lib64/libreoffice (Fedora default install destination).
     * Third, looks at /usr/lib/libreoffice (for Ubuntu systems).
     * Finally, looks at /opt/libreoffice* and tries to load largest version number.
     *
     * @return a new Process instance with a running LibreOffice
     *
     * @throws IllegalStateException
     *         when no standard directory has been found.
     */
    public static Process fromLocalLibreOffice() throws IllegalStateException {
        return fromLocalLibreOffice(false);
    }

    /**
     * Tries to construct a process instance and tries to find local libreoffice install.
     * <p>
     * First, looks for LIBRE_OFFICE_HOME env variable.
     * Second, looks at /usr/lib64/libreoffice (Fedora default install destination).
     * Third, looks at /usr/lib/libreoffice (for Ubuntu systems).
     * Finally, looks at /opt/libreoffice* and tries to load largest version number.
     *
     * @return a new Process instance with a running LibreOffice
     *
     * @throws IllegalStateException
     *         when no standard directory has been found.
     */
    public static Process fromLocalLibreOffice(boolean caching) throws IllegalStateException {
        //default locations
        final Optional<Process> defaultLocProcess = Stream.of(System.getenv("LIBRE_OFFICE_HOME"),
                        "/usr/lib64/libreoffice",
                        "/usr/lib/libreoffice")
                .filter(StringUtils::isNotEmpty)
                .map(File::new)
                .filter(File::exists)
                .map(d -> fromLibreOfficeHome(d, caching))
                .findFirst();
        if (defaultLocProcess.isPresent()) {
            return defaultLocProcess.get();
        }
        final Path opt = Paths.get("/opt");
        if (Files.isDirectory(opt)) {
            try (Stream<Path> optFiles = Files.list(opt)) {
                final Optional<Process> optProcess =
                        optFiles.filter(p -> p.getFileName().startsWith("libreoffice"))
                                .max(Comparator.naturalOrder())
                                .map(Path::toFile)
                                .map(d -> fromLibreOfficeHome(d, caching));
                if (optProcess.isPresent()) {
                    return optProcess.get();
                }
            } catch (IOException e) {
                throw new IllegalStateException("Could not list " + opt);
            }
        }
        throw new IllegalStateException("Could not find local LibreOffice home!");
    }

}
