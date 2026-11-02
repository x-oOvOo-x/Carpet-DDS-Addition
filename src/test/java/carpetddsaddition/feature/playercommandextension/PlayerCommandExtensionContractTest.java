package carpetddsaddition.feature.playercommandextension;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class PlayerCommandExtensionContractTest {
    private static final Path ROOT=findRepositoryRoot(),JAVA=ROOT.resolve("src/main/java");

    @Test void exactStopExtendsCarpetsSharedActionCommandBuilder() throws IOException {
        String mixin=read("carpetddsaddition/mixin/feature/playercommandextension/PlayerCommandMixin.java");
        String config=Files.readString(ROOT.resolve("src/main/resources/carpet-dds-addition.mixins.json"));
        assertTrue(config.contains("feature.playercommandextension.PlayerCommandMixin"));
        assertTrue(mixin.contains("@Mixin(value = PlayerCommand.class, remap = false)"));
        assertTrue(mixin.contains("@Inject(method = \"makeActionCommand\", at = @At(\"RETURN\"))"));
        assertTrue(mixin.contains("getChild(\"stop\")"));
        assertTrue(mixin.contains("manipulation(pack -> pack.start(type, null))"));
        assertFalse(mixin.contains("stopAll"));
        assertFalse(mixin.contains("Action.once()"));
    }

    @Test void exactStopReusesCarpetManipulationPermissionPath() throws IOException {
        String mixin=read("carpetddsaddition/mixin/feature/playercommandextension/PlayerCommandMixin.java");
        assertTrue(mixin.contains("@Shadow"));
        assertTrue(mixin.contains("Command<CommandSourceStack> manipulation(Consumer<EntityPlayerActionPack> action)"));
    }

    private static String read(String path) throws IOException { return Files.readString(JAVA.resolve(path)); }
    private static Path findRepositoryRoot() {
        for (Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if (Files.exists(p.resolve("settings.json")) && Files.isDirectory(p.resolve("src/main"))) return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
