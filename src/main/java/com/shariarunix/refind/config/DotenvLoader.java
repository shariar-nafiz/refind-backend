package com.shariarunix.refind.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

/**
 * Utility to load environment variables from a local {@code .env} file into
 * System properties if they are not already set in the OS environment.
 * Enables seamless local running via IDEs (IntelliJ IDEA, Eclipse, VS Code)
 * and command-line execution without requiring manual IDE run-configuration exports.
 */
public final class DotenvLoader {

    private DotenvLoader() {}

    public static void load() {
        // Look for .env in current working directory or user dir
        File envFile = new File(".env");
        if (!envFile.exists() || !envFile.isFile()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    // Only set if not already defined as a System property or OS environment variable
                    if (System.getProperty(key) == null && System.getenv(key) == null) {
                        System.setProperty(key, value);
                    }
                }
            }
        } catch (Exception ignored) {
            // Silently proceed with standard defaults
        }
    }
}
