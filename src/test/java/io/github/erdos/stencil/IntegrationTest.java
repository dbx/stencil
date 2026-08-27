package io.github.erdos.stencil;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a test that needs a LibreOffice process in the background.
 *
 * <p>Such tests can be selected with {@code -Dgroups=integration} and excluded with
 * {@code -DexcludedGroups=integration}.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("integration")
public @interface IntegrationTest {
}
