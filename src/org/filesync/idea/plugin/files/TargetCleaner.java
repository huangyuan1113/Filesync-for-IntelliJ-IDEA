package org.filesync.idea.plugin.files;

import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class TargetCleaner implements FileVisitor<Path> {

    private static final Logger LOGGER = Logger.getInstance(TargetCleaner.class);

    private Path source;
    private Path target;

    public TargetCleaner(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        deleteIfNotExistsInSource(file);

        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        deleteIfNotExistsInSource(dir);

        return FileVisitResult.CONTINUE;
    }

    private void deleteIfNotExistsInSource(Path dir) throws IOException {
        Path pathInSource = source.resolve(target.relativize(dir));
        if (!Files.exists(pathInSource)) {
            LOGGER.debug("Delete " + dir);
            Files.delete(dir);
        }
    }
}
