package org.riskmap.data.courts

import com.google.api.client.repackaged.com.google.common.base.Strings
import org.htmlparser.Parser
import org.htmlparser.filters.AndFilter
import org.htmlparser.filters.HasAttributeFilter
import org.htmlparser.filters.TagNameFilter
import org.riskmap.http.IOUtils

/**
 * @author Maxim Galushka
 */

def IDS = new FileReader("D:\\projects\\riskmap\\docs\\courts\\ids.txt")

def count = 0
def MAX_COUNT = 100
def line
while ((line = IDS.readLine()) != null) {
    if (Strings.isNullOrEmpty(line)) continue

    String id = line
    println "Processing ${id}"

    def FROM = "D:\\projects\\riskmap\\docs\\courts\\details\\${id}.html"
    def html = IOUtils.convertStreamToString(new FileInputStream(FROM), "UTF-8")

    def file = new File("D:\\projects\\riskmap\\docs\\courts\\details-filtered\\${id}.html")

    if (file.exists()) {

        def iframe = new Parser(html).parse(
                new AndFilter(
                        new TagNameFilter("textarea"),
                        new HasAttributeFilter("id", "txtdepository")
                )
        )

        def text = iframe.toNodeArray()[0].toHtml(true)
        text = text.substring(text.indexOf(">") + 1)
        text = text.replaceAll("&nbsp;", " ")

        def TO0 = new PrintWriter(file, "windows-1251");
        TO0.println(text)
        TO0.flush()
        TO0.close()

        if (count++ >= MAX_COUNT) break;
    }

}