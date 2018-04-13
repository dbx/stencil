package io.github.erdos.stencil.impl;

import io.github.erdos.stencil.PreparedTemplate;
import io.github.erdos.stencil.TemplateFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.Arrays.stream;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

/**
 * Watches file system and automatically loads template files on file changes.
 */
@SuppressWarnings("unused")
public final class DirWatcherTemplateFactory implements TemplateFactory {

    private final File templatesDirectory;
    private final TemplateFactory factory;

    private final DelayQueue<DelayedContainer<File>> delayQueue = new DelayQueue<>();
    private final Map<File, DelayedContainer<File>> delays = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Default ctor.
     *
     * @param templatesDirectory not null absolute path directory
     * @param factory            wrapped factory
     */
    public DirWatcherTemplateFactory(File templatesDirectory, TemplateFactory factory) {
        if (templatesDirectory == null)
            throw new IllegalArgumentException("Template directory parameter is null!");
        if (!templatesDirectory.exists())
            throw new IllegalArgumentException("Templates directory does not exist: " + templatesDirectory);
        if (!templatesDirectory.isDirectory())
            throw new IllegalArgumentException("Templates directory parameter is not a directory!");
        if (factory == null)
            throw new IllegalArgumentException("Parent factory is missing!");

        this.templatesDirectory = templatesDirectory;
        this.factory = factory;
    }

    public File getTemplatesDirectory() {
        return templatesDirectory;
    }

    private Optional<PreparedTemplate> handle(File f) {
        assert (f.isAbsolute());

        try {
            final PreparedTemplate template = factory.prepareTemplateFile(f);
            // TODO: we may use logging here
            return Optional.of(template);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Starts directory watcher and tries to load all files.
     *
     * @throws IOException           on file system errors
     * @throws IllegalStateException if already started
     */
    public void start() throws IOException, IllegalStateException {
        if (running.getAndSet(true))
            throw new IllegalStateException("Already running!");

        final WatchService ws = templatesDirectory.toPath().getFileSystem().newWatchService();
        final WatchKey waka = templatesDirectory.toPath().register(ws, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        // TODO: benyalni az egesz alap konyvtarat es mindegyiket legyartani.

        // TOOD: unregister
        new Thread(() -> {
            try {
                initAllFiles();
            } catch (Exception ignored) {
                // intentionally left blank
            }

            try {
                while (running.get()) {
                    if (delayQueue.isEmpty()) {
                        addEvents(ws.take());
                    } else {
                        List<DelayedContainer<File>> elems = new LinkedList<>();
                        if (0 < delayQueue.drainTo(elems)) {
                            elems.forEach((x) -> handle(x.getElem()));
                        } else {
                            long delay = delayQueue.peek().getDelay(TimeUnit.MILLISECONDS);
                            WatchKey poll = ws.poll(delay, TimeUnit.MILLISECONDS);
                            if (poll != null) {
                                addEvents(poll);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initAllFiles() {
        System.out.println("Starting with " + templatesDirectory);
        System.out.println("Initially all files are: " + recurse(templatesDirectory).collect(toList()));
        recurse(templatesDirectory).forEach(this::handle);
    }

    private Stream<File> recurse(File f) {
        if (f != null && f.isDirectory())
            return stream(f.list()).map(x -> new File(f, x)).flatMap(this::recurse);
        else
            return Stream.empty();
    }

    @SuppressWarnings("unchecked")
    private void addEvents(WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            final WatchEvent<Path> ev = (WatchEvent<Path>) event;
            final File f = new File(templatesDirectory, ev.context().toFile().getName());
            if (delays.containsKey(f)) {
                DelayedContainer<File> container = delays.get(f);
                delays.remove(f);
                delayQueue.remove(container);
            }

            final DelayedContainer<File> newCont = new DelayedContainer<>(1000, f);
            delays.put(f, newCont);
            delayQueue.add(newCont);
        }
    }

    public void stop() {
        if (!running.getAndSet(false))
            throw new IllegalStateException("Already stopped!");
        delays.clear();
        delayQueue.clear();
    }

    @Override
    public PreparedTemplate prepareTemplateFile(File templateFile) throws IOException {
        if (templateFile == null)
            throw new IllegalArgumentException("templateFile argument must not be null!");
        if (templateFile.isAbsolute())
            throw new IllegalArgumentException("templateFile must not be an absolute file!");
        else
            return handle(templateFile)
                    .orElseThrow(() -> new IllegalArgumentException("Can not build template file: " + templateFile));
    }

    private final class DelayedContainer<X> implements Delayed {
        private final long expiration;
        private final X contents;

        private DelayedContainer(long millis, X contents) {
            if (millis <= 0)
                throw new IllegalArgumentException("Millis must be positive!");
            this.expiration = currentTimeMillis() + millis;
            this.contents = contents;
        }

        private X getElem() {
            return contents;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expiration - currentTimeMillis(), MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed delayed) {
            return (int) (getDelay(MILLISECONDS) - delayed.getDelay(MILLISECONDS));
        }
    }
}
