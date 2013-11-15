package org.riskmap.data.courts

import com.google.api.client.repackaged.com.google.common.base.Strings
import org.htmlparser.Parser
import org.htmlparser.filters.AndFilter
import org.htmlparser.filters.HasAttributeFilter
import org.htmlparser.filters.TagNameFilter
import org.riskmap.http.IOUtils

import java.util.regex.Pattern

/**
 * @author Maxim Galushka
 */

def IDS = new FileReader("D:\\projects\\riskmap\\docs\\courts\\ids.txt")

def PATTERN = Pattern.compile("(.*Злочини\\s+проти\\s+волі.*)|" +
        "(.*Торгівля\\s+людьми.*)|(.*не\\s+визначено.*)", Pattern.DOTALL)

//def count = 0
//def MAX_COUNT = 100

def line
while ((line = IDS.readLine()) != null) {
    if (Strings.isNullOrEmpty(line)) continue

    String id = line
    println "Processing ${id}"

    def FROM = "D:\\projects\\riskmap\\docs\\courts\\details\\${id}.html"
    def fromFile = new File(FROM)

    if (fromFile.exists()) {

        def html = IOUtils.convertStreamToString(new FileInputStream(fromFile), "UTF-8")
        def file = new File("D:\\projects\\riskmap\\docs\\courts\\details-filtered\\${id}.html")

        // filter relevant documents
        def categoryParser = new Parser(html).parse(
                new AndFilter(
                        new TagNameFilter("div"),
                        new HasAttributeFilter("id", "divcasecat")
                )
        )

        def empty = categoryParser.toNodeArray().length == 0
        def category = ""
        if (!empty) {
            category = categoryParser.toNodeArray()[0].toHtml(true)
        }

        if (empty || PATTERN.matcher(category).matches()) {

            println "Matched ${category}"
            def iframe = new Parser(html).parse(
                    new AndFilter(
                            new TagNameFilter("textarea"),
                            new HasAttributeFilter("id", "txtdepository")
                    )
            )

            def text
            if (iframe.toNodeArray().length == 0) {
                text = iframe.toHtml(true)
            } else {
                text = iframe.toNodeArray()[0].toHtml(true)
            }
            text = text.substring(text.indexOf(">") + 1)
            text = text.replaceAll("&nbsp;", " ")

            def TO0 = new PrintWriter(file, "windows-1251");
            TO0.println(text)
            TO0.flush()
            TO0.close()

            //if (count++ >= MAX_COUNT) break;
        }
    }
}