package carpetddsaddition.integration.gca.fakeplayer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

final class DdsFakePlayerIntervalContractTest {
    private static final Path SOURCE=findRoot()
            .resolve("src/main/java/carpetddsaddition/integration/gca/fakeplayer/DdsFakePlayerActions.java");

    @Test void intervalRangeMatchesPositiveIntClientContract() throws IOException {
        String source=Files.readString(SOURCE);
        assertTrue(source.contains("MIN_INTERVAL = 1, MAX_INTERVAL = Integer.MAX_VALUE"));
        assertTrue(source.contains("return Math.max(MIN_INTERVAL, value)"));
        assertFalse(source.contains("MAX_INTERVAL = 12000"));
        assertFalse(source.contains("Math.min(MAX_INTERVAL, value)"));
    }

    @Test void intervalRequestStillUsesCarpetIntervalActionWithoutArithmeticExpansion() throws IOException {
        String source=Files.readString(SOURCE);
        assertTrue(source.contains("Action.interval(clampInterval(requested))"));
        assertTrue(source.contains("case ATTACK_INTERVAL: setInterval(pack, ActionType.ATTACK, value)"));
        assertTrue(source.contains("case USE_INTERVAL: setInterval(pack, ActionType.USE, value)"));
        assertTrue(source.contains("pack.start(type, null)"));
        assertFalse(source.contains("Action.once()"));
    }

    private static Path findRoot(){
        for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())
            if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;
        throw new IllegalStateException("Unable to locate repository root");
    }
}
