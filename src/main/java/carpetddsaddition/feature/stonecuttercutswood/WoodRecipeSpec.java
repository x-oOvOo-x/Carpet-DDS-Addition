/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026 DDS and contributors
 */
package carpetddsaddition.feature.stonecuttercutswood;

/** Version-agnostic one-input wood stonecutter recipe specification. */
public final class WoodRecipeSpec {
    private final String input,output;
    private final int count;

    public WoodRecipeSpec(String input,String output,int count){
        if(input==null||output==null||input.isEmpty()||output.isEmpty()||count<1)throw new IllegalArgumentException("Invalid wood recipe");
        this.input=input; this.output=output; this.count=count;
    }
    public String input(){return input;} public String output(){return output;} public int count(){return count;}
}
