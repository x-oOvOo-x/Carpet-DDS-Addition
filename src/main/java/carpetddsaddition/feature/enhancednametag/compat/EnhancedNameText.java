/*
 * This file is part of the Carpet DDS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  DDS and contributors
 *
 * Carpet DDS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet DDS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet DDS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */
package carpetddsaddition.feature.enhancednametag.compat;

//#if MC >= 11904
//$$ import net.minecraft.network.chat.Component;
//$$ import net.minecraft.network.chat.MutableComponent;
//$$ import net.minecraft.network.chat.Style;
//$$ import net.minecraft.network.chat.TextColor;
//$$ import java.util.LinkedHashMap;
//$$ import java.util.Map;
//#endif

/** Parses enhanced name-tag formatting on Minecraft 1.19.4+. */
public final class EnhancedNameText {
    //#if MC >= 11904
    //$$ private static final Map<String, String> SYMBOLS = new LinkedHashMap<>();
    //$$ static {
    //$$     SYMBOLS.put(":warning:", "⚠"); SYMBOLS.put(":check:", "✓"); SYMBOLS.put(":cross:", "✕");
    //$$     SYMBOLS.put(":star:", "★"); SYMBOLS.put(":gear:", "⚙"); SYMBOLS.put(":right:", "→");
    //$$     SYMBOLS.put(":left:", "←"); SYMBOLS.put(":up:", "↑"); SYMBOLS.put(":down:", "↓"); SYMBOLS.put(":heart:", "❤");
    //$$ }
    //#endif
    private EnhancedNameText() {}

    //#if MC >= 11904
    //$$ public static Component parse(String raw) {
    //$$     if (raw == null || raw.isEmpty()) return Component.empty();
    //$$     String input = expandSymbols(raw);
    //$$     MutableComponent result = Component.empty();
    //$$     StringBuilder literal = new StringBuilder();
    //$$     Style style = Style.EMPTY;
    //$$     int i = 0;
    //$$     while (i < input.length()) {
    //$$         char current = input.charAt(i);
    //$$         if (current == '\\' && i + 1 < input.length() && input.charAt(i + 1) == 'n') {
    //$$             literal.append('\n'); i += 2; continue;
    //$$         }
    //$$         if (current != '&' || i + 1 >= input.length()) { literal.append(current); i++; continue; }
    //$$         char next = input.charAt(i + 1);
    //$$         if (next == '&') { literal.append('&'); i += 2; continue; }
    //$$         if (next == '#' && i + 7 < input.length()) {
    //$$             String hex = input.substring(i + 2, i + 8);
    //$$             if (isHexColor(hex)) {
    //$$                 flush(result, literal, style);
    //$$                 style = style.withColor(TextColor.fromRgb(Integer.parseInt(hex, 16)));
    //$$                 i += 8; continue;
    //$$             }
    //$$         }
    //$$         char code = Character.toLowerCase(next);
    //$$         Integer rgb = getLegacyColor(code);
    //$$         if (rgb != null) {
    //$$             flush(result, literal, style);
    //$$             style = style.withColor(TextColor.fromRgb(rgb));
    //$$             i += 2; continue;
    //$$         }
    //$$         switch (code) {
    //$$             case 'l': flush(result, literal, style); style = style.withBold(Boolean.TRUE); i += 2; continue;
    //$$             case 'o': flush(result, literal, style); style = style.withItalic(Boolean.TRUE); i += 2; continue;
    //$$             case 'n': flush(result, literal, style); style = style.withUnderlined(Boolean.TRUE); i += 2; continue;
    //$$             case 'm': flush(result, literal, style); style = style.withStrikethrough(Boolean.TRUE); i += 2; continue;
    //$$             case 'r': flush(result, literal, style); style = Style.EMPTY; i += 2; continue;
    //$$             default: literal.append('&'); i++; break;
    //$$         }
    //$$     }
    //$$     flush(result, literal, style);
    //$$     return result;
    //$$ }
    //$$ private static String expandSymbols(String raw) {
    //$$     String value = raw;
    //$$     for (Map.Entry<String, String> entry : SYMBOLS.entrySet()) value = value.replace(entry.getKey(), entry.getValue());
    //$$     return value;
    //$$ }
    //$$ private static void flush(MutableComponent result, StringBuilder literal, Style style) {
    //$$     if (literal.length() == 0) return;
    //$$     MutableComponent part = Component.literal(literal.toString());
    //$$     part.setStyle(style);
    //$$     result.append(part);
    //$$     literal.setLength(0);
    //$$ }
    //$$ private static boolean isHexColor(String value) {
    //$$     if (value.length() != 6) return false;
    //$$     for (int i = 0; i < 6; i++) {
    //$$         char c = Character.toLowerCase(value.charAt(i));
    //$$         if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))) return false;
    //$$     }
    //$$     return true;
    //$$ }
    //$$ private static Integer getLegacyColor(char code) {
    //$$     switch (code) {
    //$$         case '0': return 0x000000; case '1': return 0x0000AA; case '2': return 0x00AA00; case '3': return 0x00AAAA;
    //$$         case '4': return 0xAA0000; case '5': return 0xAA00AA; case '6': return 0xFFAA00; case '7': return 0xAAAAAA;
    //$$         case '8': return 0x555555; case '9': return 0x5555FF; case 'a': return 0x55FF55; case 'b': return 0x55FFFF;
    //$$         case 'c': return 0xFF5555; case 'd': return 0xFF55FF; case 'e': return 0xFFFF55; case 'f': return 0xFFFFFF;
    //$$         default: return null;
    //$$     }
    //$$ }
    //#endif
}
