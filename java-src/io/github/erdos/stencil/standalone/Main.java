package io.github.erdos.stencil.standalone;

import io.github.erdos.stencil.Process;
import io.github.erdos.stencil.ProcessFactory;

/**
 * Standalone template engine runner.
 */
@SuppressWarnings("unused")
public final class Main {

    public static int main(String... args) {
        final Process process = ProcessFactory.fromLocalLibreOffice();

        try {
            process.start();

            return 0;
        } finally {
            process.stop();
        }
    }
}
