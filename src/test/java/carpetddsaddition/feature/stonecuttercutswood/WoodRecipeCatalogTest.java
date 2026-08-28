/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutswood;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

final class WoodRecipeCatalogTest {
    @Test void exposesAllFamiliesOn12111(){
        List<WoodFamily> families=WoodRecipeCatalog.families(12111); assertEquals(12,families.size());
        assertEquals(Set.of("oak","spruce","birch","jungle","acacia","dark_oak","crimson","warped","mangrove","cherry","bamboo","pale_oak"),families.stream().map(WoodFamily::key).collect(java.util.stream.Collectors.toSet()));
    }

    @Test void generatesOnlyUniqueValidOneInputRecipes(){
        Set<String> ids=new HashSet<String>();
        for(WoodRecipeSpec spec:WoodRecipeCatalog.recipes(12111)){assertFalse(spec.input().isEmpty()); assertFalse(spec.output().isEmpty()); assertTrue(spec.count()>0); assertTrue(ids.add(spec.input()+"->"+spec.output()),"duplicate "+spec.input()+" -> "+spec.output());}
    }

    @Test void keepsOakFinalRecipeTable(){
        assertSpec("oak_log","oak_planks",4); assertSpec("oak_log","oak_button",4); assertSpec("oak_log","oak_stairs",4); assertSpec("oak_log","oak_slab",8);
        assertSpec("oak_log","oak_door",2); assertSpec("oak_log","oak_fence",4); assertSpec("oak_log","oak_fence_gate",1); assertSpec("oak_log","oak_pressure_plate",2);
        assertSpec("oak_log","oak_shelf",1); assertSpec("oak_log","oak_sign",2); assertSpec("oak_log","oak_trapdoor",4); assertSpec("oak_log","stripped_oak_log",1);
    }

    @Test void keepsPlankAndStairRecipeTable(){
        assertSpec("oak_planks","oak_button",1); assertSpec("oak_planks","oak_fence",1); assertSpec("oak_planks","oak_slab",2); assertSpec("oak_planks","oak_stairs",1); assertSpec("oak_planks","oak_trapdoor",1); assertSpec("oak_stairs","oak_slab",1);
        Set<String> forbidden=Set.of("oak_door","oak_pressure_plate","oak_fence_gate","oak_sign","oak_shelf");
        assertTrue(WoodRecipeCatalog.recipes(12111).stream().noneMatch(r->r.input().equals("oak_planks")&&forbidden.contains(r.output())));
    }

    @Test void appliesSourceTableToWoodAndStrippedVariants(){
        assertSpec("oak_wood","oak_fence",4); assertSpec("stripped_oak_log","oak_trapdoor",4); assertSpec("stripped_oak_wood","oak_shelf",1);
        assertSpec("crimson_stem","crimson_door",2); assertSpec("stripped_warped_hyphae","warped_sign",2);
    }

    @Test void keepsBambooSourceRatios(){
        assertSpec("bamboo_block","bamboo_planks",2); assertSpec("bamboo_block","bamboo_button",2); assertSpec("bamboo_block","bamboo_stairs",2); assertSpec("bamboo_block","bamboo_slab",4);
        assertSpec("bamboo_block","bamboo_door",1); assertSpec("bamboo_block","bamboo_fence",2); assertSpec("bamboo_block","bamboo_fence_gate",1); assertSpec("bamboo_block","bamboo_pressure_plate",1);
        assertSpec("bamboo_block","bamboo_shelf",1); assertSpec("bamboo_block","bamboo_sign",1); assertSpec("bamboo_block","bamboo_trapdoor",1);
    }

    @Test void respectsFamilyIntroductionVersions(){assertFalse(keys(11904).contains("cherry")); assertTrue(keys(12001).contains("cherry")); assertFalse(keys(12103).contains("pale_oak")); assertTrue(keys(12104).contains("pale_oak"));}

    private static void assertSpec(String input,String output,int count){assertTrue(WoodRecipeCatalog.recipes(12111).stream().anyMatch(r->r.input().equals(input)&&r.output().equals(output)&&r.count()==count),input+" -> "+output+" x"+count);}
    private static Set<String> keys(int mc){Set<String> out=new HashSet<String>(); for(WoodFamily family:WoodRecipeCatalog.families(mc))out.add(family.key()); return out;}
}
