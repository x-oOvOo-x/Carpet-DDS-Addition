package carpetddsaddition.build;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

final class PreprocessorMarkerGuardContractTest {
    private static Path root(){for(Path p=Path.of("").toAbsolutePath();p!=null;p=p.getParent())if(Files.exists(p.resolve("settings.json"))&&Files.isDirectory(p.resolve("src/main")))return p;throw new IllegalStateException("repo root not found");}
    private static boolean bad(String s){String t=s.stripLeading();return t.startsWith("//$")&&!t.startsWith("//$$");}

    @Test void frameworkVerifierRejectsSingleDollarMarker()throws Exception{
        String s=Files.readString(root().resolve("scripts/verify-framework-core.py"));
        assertTrue(s.contains("stripped.startswith(\"//$\") and not stripped.startswith(\"//$$\")"));
        assertTrue(s.contains("invalid single-dollar ReplayMod preprocess marker"));
    }

    @Test void trackedJavaSourcesContainNoSingleDollarMarkers()throws Exception{
        Path repo=root();
        for(String name:new String[]{"src","versions"}){
            Path dir=repo.resolve(name);if(!Files.exists(dir))continue;
            try(var paths=Files.walk(dir)){
                for(Path p:paths.filter(x->x.toString().endsWith(".java")&&!x.toString().contains(FileSystems.getDefault().getSeparator()+"build"+FileSystems.getDefault().getSeparator())).toList()){
                    int line=0;for(String s:Files.readAllLines(p)){line++;assertFalse(bad(s),repo.relativize(p)+":"+line);}
                }
            }
        }
    }
}
