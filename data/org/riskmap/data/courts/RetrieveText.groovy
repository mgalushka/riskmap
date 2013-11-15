package org.riskmap.data.courts

import org.htmlparser.Parser
import org.htmlparser.beans.StringBean
import org.htmlparser.filters.AndFilter
import org.htmlparser.filters.HasAttributeFilter
import org.htmlparser.filters.TagNameFilter
import org.riskmap.http.IOUtils

/**
 * @author Maxim Galushka
 */

def FROM = "D:\\projects\\riskmap\\docs\\courts\\details\\15350410.html"
def html = IOUtils.convertStreamToString(new FileInputStream(FROM), "UTF-8")

def file = new File("D:\\projects\\riskmap\\docs\\courts\\details-filtered\\15350410.html")

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

def bean = new StringBean();
bean.setLinks(false);
bean.setURL(file.toURI().toURL().toString());

def TO = new PrintWriter(file, "UTF-8")
TO.println(bean.getStrings())
TO.flush()
TO.close()