package org.riskmap.data

import java.util.regex.Pattern

/**
  * @author Maxim Galushka 
  */

def PATTERN = Pattern.compile("(.*Злочини\\s+проти\\s+волі.*)|(.*Торгівля\\s+людьми.*)", Pattern.DOTALL)

println(PATTERN.matcher("<td class=\"td2\">Кримінальні справи; Злочини проти волі, честі та гідності особи (усього), з них; Торгівля людьми або інша незаконна угода щодо людини.</td>").matches())
println(PATTERN.matcher("Злочини проти волі, честі та гідності особи (усього)").matches())
println(PATTERN.matcher("Торгівля людьми або інша незаконна угода щодо людини.</td>").matches())
println(PATTERN.matcher("Торгівля</td>").matches())
println(PATTERN.matcher("Злочини проти</td>").matches())
println(PATTERN.matcher("<div id=\"divcasecat\">\n" +
        "        <table cellspacing=\"0\">\n" +
        "            <tr>\n" +
        "                <td class=\"td1\">Категорія&nbsp;справи:</td>\n" +
        "                <td class=\"td2\">Кримінальні справи; Злочини проти волі, честі та гідності особи (усього), з них; Торгівля людьми або інша незаконна угода щодо людини.</td>\n" +
        "            </tr>\n" +
        "            <tr><td colspan=\"2\" align=\"left\"></td></tr>           \n" +
        "        </table>\n" +
        "    </div>").matches())
