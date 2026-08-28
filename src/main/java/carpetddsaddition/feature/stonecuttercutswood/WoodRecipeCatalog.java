/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutswood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Single source of truth for supported vanilla wood families and recipe specs. */
public final class WoodRecipeCatalog {
    private static final WoodFamily[] FAMILIES={
            WoodFamily.standard(11404,"oak"),WoodFamily.standard(11404,"spruce"),WoodFamily.standard(11404,"birch"),WoodFamily.standard(11404,"jungle"),
            WoodFamily.standard(11404,"acacia"),WoodFamily.standard(11404,"dark_oak"),WoodFamily.nether(11601,"crimson"),WoodFamily.nether(11601,"warped"),
            WoodFamily.standard(11900,"mangrove"),WoodFamily.standard(12000,"cherry"),WoodFamily.bamboo(12000),WoodFamily.standard(12104,"pale_oak")
    };
    private WoodRecipeCatalog(){}

    public static List<WoodFamily> families(int mc){List<WoodFamily> out=new ArrayList<WoodFamily>(); for(WoodFamily family:FAMILIES)if(family.supports(mc))out.add(family); return Collections.unmodifiableList(out);}
    public static List<WoodRecipeSpec> recipes(){List<WoodRecipeSpec> out=new ArrayList<WoodRecipeSpec>(); for(WoodFamily family:FAMILIES)out.addAll(family.recipes()); return Collections.unmodifiableList(out);}
    public static List<WoodRecipeSpec> recipes(int mc){List<WoodRecipeSpec> out=new ArrayList<WoodRecipeSpec>(); for(WoodFamily family:FAMILIES)if(family.supports(mc))out.addAll(family.recipes()); return Collections.unmodifiableList(out);}
}
