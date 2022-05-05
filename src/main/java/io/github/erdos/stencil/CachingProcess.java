package io.github.erdos.stencil;

import org.jodconverter.core.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CachingProcess extends Process {

    private static final Logger LOGGER = LoggerFactory.getLogger(Process.class);

    private final Map<CacheKey, CachedPreparedTemplate> templateCache = new ConcurrentHashMap<>();

    CachingProcess(final File libreOfficeHome) {
        super(libreOfficeHome);
    }

    CachingProcess(final OfficeManager officeManager) {
        super(officeManager);
    }

    @Override
    public void stop() {
        super.stop();
        LOGGER.debug("Cleaning up cached templates");
        templateCache.values().forEach(CachedPreparedTemplate::destroy);
        templateCache.clear();
    }

    @Override
    public PreparedTemplate prepareTemplateFile(final File templateFile, final PrepareOptions prepareOptions) throws IOException {
        final CacheKey key = createCacheKey(templateFile, prepareOptions);
        final CachedPreparedTemplate existingTemplate = templateCache.get(key);
        if (existingTemplate == null
            || Files.getLastModifiedTime(templateFile.toPath()).to(TimeUnit.SECONDS)
               > existingTemplate.creationDateTime().toEpochSecond(ZoneOffset.UTC)) {
            final CachedPreparedTemplate newTemplate = createCachedTemplateFromFile(templateFile, prepareOptions);
            final CachedPreparedTemplate overwrittenTemplate = templateCache.put(key, newTemplate);
            if (overwrittenTemplate != null) {
                LOGGER.debug("Cleaning up refreshed template");
                overwrittenTemplate.destroy();
            }
        }
        return templateCache.get(key);
    }

    private CachedPreparedTemplate createCachedTemplateFromFile(final File templateFile, final PrepareOptions options) throws IOException {
        LOGGER.debug("Preparing cached template from {}", templateFile.getAbsolutePath());
        return new CachedPreparedTemplate(super.prepareTemplateFile(templateFile, options));
    }

    private static CacheKey createCacheKey(final File f, final PrepareOptions options) {
        return new CacheKey(f.getAbsolutePath(), options.isOnlyIncludes());
    }

    private static final class CacheKey {
        private final String filePath;
        private final boolean onlyIncludes;

        private CacheKey(final String filePath, final boolean onlyIncludes) {
            this.filePath = Objects.requireNonNull(filePath);
            this.onlyIncludes = onlyIncludes;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CacheKey)) {
                return false;
            }

            final CacheKey cacheKey = (CacheKey) o;

            if (onlyIncludes != cacheKey.onlyIncludes) {
                return false;
            }
            return filePath.equals(cacheKey.filePath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(filePath, onlyIncludes);
        }
    }

    private static final class CachedPreparedTemplate implements PreparedTemplate {
        private final PreparedTemplate delegate;

        private CachedPreparedTemplate(final PreparedTemplate delegate) {
            this.delegate = Objects.requireNonNull(delegate);
        }

        @Override
        public File getTemplateFile() {
            return delegate.getTemplateFile();
        }

        @Override
        public LocalDateTime creationDateTime() {
            return delegate.creationDateTime();
        }

        @Override
        public Object getSecretObject() {
            return delegate.getSecretObject();
        }

        @Override
        public TemplateVariables getVariables() {
            return delegate.getVariables();
        }

        @Override
        public void cleanup() {
            //NOOP
        }

        public void destroy() {
            delegate.cleanup();
        }

        @Override
        protected void finalize() throws Throwable {
            delegate.cleanup();
        }
    }
}

