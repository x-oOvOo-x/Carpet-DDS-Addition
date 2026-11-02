/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 */
package carpetddsaddition.platform.carpet;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class DdsSettingsManagerLifecycleContractTest {
    @Test
    void detachReleasesWorldContextAfterCarpetReset() throws Exception {
        Path root = Path.of("").toAbsolutePath();
        while (root != null
                && !(Files.exists(root.resolve("settings.json"))
                && Files.isDirectory(root.resolve("src/main")))) {
            root = root.getParent();
        }
        if (root == null) throw new IllegalStateException("repo root not found");

        String source = Files.readString(root.resolve(
                "src/main/java/carpetddsaddition/platform/carpet/DdsSettingsManager.java"));
        int detach = source.indexOf("public void detachServer()");
        int reset = source.indexOf("super.detachServer();", detach);
        int clear = source.indexOf("server = null;", reset);
        int dropOrigin = source.indexOf("origin.remove();", clear);
        int baseline = source.indexOf("DDSRules.captureCurrentState();", clear);
        assertTrue(detach >= 0 && reset > detach && clear > reset
                        && dropOrigin > clear && baseline > dropOrigin,
                "CDA must release the detached server after Carpet resets rule values");
    }
}
