package carpetddsaddition.feature.enhancednametag;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

final class EnhancedNameTagSavedDataContractTest {
    private static Path root(){for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;throw new IllegalStateException("repo root not found");}
    private static String read(String p)throws Exception{return Files.readString(root().resolve(p));}

    @Test void componentEraFactoriesAlwaysProvideDataFixType()throws Exception{
        String c=read("src/main/java/carpetddsaddition/feature/enhancednametag/compat/EnhancedNameTagSavedData.java");
        assertTrue(c.contains("//#if MC >= 12002"));
        assertTrue(c.contains("//$$ import net.minecraft.util.datafix.DataFixTypes;"));
        assertTrue(c.contains("//$$ import com.mojang.serialization.Codec;"));
        assertTrue(c.contains("//$$ import net.minecraft.world.level.saveddata.SavedDataType;"));
        assertTrue(c.contains("//$$     return new SavedData.Factory<>(EnhancedNameTagSavedData::new, EnhancedNameTagSavedData::loadWithRegistries, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);"));
        assertTrue(c.contains("//$$     return new SavedData.Factory<>(EnhancedNameTagSavedData::new, EnhancedNameTagSavedData::loadFactoryLegacy, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);"));
        assertFalse(c.contains("loadWithRegistries, null"));
        assertFalse(c.contains("loadFactoryLegacy, null"));
    }
}
