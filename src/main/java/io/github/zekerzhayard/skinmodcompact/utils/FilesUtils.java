package io.github.zekerzhayard.skinmodcompact.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import customskinloader.CustomSkinLoader;

public class FilesUtils {
    public static void cleanDirectory(File directory, final long timeStamp) {
        FileUtils.listFiles(directory, new AbstractFileFilter() {
            @Override()
            public boolean accept(File file) {
                try {
                    if (Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().to(TimeUnit.MILLISECONDS) < timeStamp) {
                        FileUtils.forceDelete(file);
                        if (file.getParentFile().list().length == 0) {
                            FileUtils.forceDelete(file.getParentFile());
                        }
                    }
                } catch (Exception e) {
                    CustomSkinLoader.logger.warning("Exception occurs while cleaning cache: " + e.toString());
                }
                return false;
            }
        }, TrueFileFilter.INSTANCE);
    }
}
