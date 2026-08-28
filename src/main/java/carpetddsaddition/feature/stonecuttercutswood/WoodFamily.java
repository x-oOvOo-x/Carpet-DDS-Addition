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

/** One vanilla wood family and its DDS stonecutter recipes. */
public final class WoodFamily {
    private final int minMc,sourceYield;
    private final String key,base,alternate,strippedBase,strippedAlternate,planks,button,stairs,slab,pressure,door,trapdoor,fence,gate,sign,shelf;

    private WoodFamily(int minMc,int sourceYield,String key,String base,String alternate,String strippedBase,String strippedAlternate,String planks,String button,String stairs,String slab,String pressure,String door,String trapdoor,String fence,String gate,String sign,String shelf){
        this.minMc=minMc; this.sourceYield=sourceYield; this.key=key; this.base=base; this.alternate=alternate; this.strippedBase=strippedBase; this.strippedAlternate=strippedAlternate;
        this.planks=planks; this.button=button; this.stairs=stairs; this.slab=slab; this.pressure=pressure; this.door=door; this.trapdoor=trapdoor; this.fence=fence; this.gate=gate; this.sign=sign; this.shelf=shelf;
    }

    public int minMc(){return minMc;} public String key(){return key;} public boolean supports(int mc){return mc>=minMc;}

    public List<WoodRecipeSpec> recipes(){
        List<WoodRecipeSpec> out=new ArrayList<WoodRecipeSpec>();
        add(out,base,strippedBase,1); add(out,alternate,strippedAlternate,1);
        addSource(out,base); addSource(out,alternate); addSource(out,strippedBase); addSource(out,strippedAlternate);
        add(out,planks,button,1); add(out,planks,fence,1); add(out,planks,slab,2); add(out,planks,stairs,1); add(out,planks,trapdoor,1); add(out,stairs,slab,1);
        return Collections.unmodifiableList(out);
    }

    private void addSource(List<WoodRecipeSpec> out,String input){
        if(input==null)return; int half=Math.max(1,sourceYield/2),trapdoors=sourceYield==2?1:sourceYield;
        add(out,input,planks,sourceYield); add(out,input,button,sourceYield); add(out,input,stairs,sourceYield); add(out,input,slab,sourceYield*2);
        add(out,input,door,half); add(out,input,fence,sourceYield); add(out,input,gate,1); add(out,input,pressure,half); add(out,input,shelf,1); add(out,input,sign,half); add(out,input,trapdoor,trapdoors);
    }

    private static void add(List<WoodRecipeSpec> out,String input,String output,int count){if(input!=null&&output!=null&&count>0&&!input.equals(output))out.add(new WoodRecipeSpec(input,output,count));}

    public static WoodFamily standard(int minMc,String key){return new WoodFamily(minMc,4,key,key+"_log",key+"_wood","stripped_"+key+"_log","stripped_"+key+"_wood",key+"_planks",key+"_button",key+"_stairs",key+"_slab",key+"_pressure_plate",key+"_door",key+"_trapdoor",key+"_fence",key+"_fence_gate",key+"_sign",key+"_shelf");}
    public static WoodFamily nether(int minMc,String key){return new WoodFamily(minMc,4,key,key+"_stem",key+"_hyphae","stripped_"+key+"_stem","stripped_"+key+"_hyphae",key+"_planks",key+"_button",key+"_stairs",key+"_slab",key+"_pressure_plate",key+"_door",key+"_trapdoor",key+"_fence",key+"_fence_gate",key+"_sign",key+"_shelf");}
    public static WoodFamily bamboo(int minMc){return new WoodFamily(minMc,2,"bamboo","bamboo_block",null,"stripped_bamboo_block",null,"bamboo_planks","bamboo_button","bamboo_stairs","bamboo_slab","bamboo_pressure_plate","bamboo_door","bamboo_trapdoor","bamboo_fence","bamboo_fence_gate","bamboo_sign","bamboo_shelf");}
}
