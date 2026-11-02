package carpetddsaddition.core.rules;

import carpetddsaddition.generated.DDSRules;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

final class DdsRuleManifestContractTest {
    @Test
    void packagedManifestMatchesCanonicalRuntimeRegistry() throws Exception {
        InputStream stream = getClass().getClassLoader()
                .getResourceAsStream("META-INF/dds/rules.json");
        assertNotNull(stream, "generated DDS rule manifest must be on the test classpath");

        JsonObject root;
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        }

        assertEquals("carpet-dds-addition", root.get("manager").getAsString());
        assertEquals("1.21.11", root.get("minecraft").getAsString());

        Set<String> manifestNames = new LinkedHashSet<String>();
        JsonArray rules = root.getAsJsonArray("rules");
        for (int i = 0; i < rules.size(); i++)
            manifestNames.add(rules.get(i).getAsJsonObject().get("id").getAsString());

        Set<String> runtimeNames = new LinkedHashSet<String>();
        for (DdsRule<?> rule : DDSRules.registry().available())
            runtimeNames.add(rule.name());

        assertEquals(runtimeNames, manifestNames);
    }
}
