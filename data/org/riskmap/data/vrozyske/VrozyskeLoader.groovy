package org.riskmap.data.vrozyske

import com.riskmap.http.HttpCallbackHandler
import com.riskmap.http.HttpHelper
import com.riskmap.http.SessionSupport
import org.apache.http.HttpHost
import org.htmlparser.Parser
import org.htmlparser.filters.AndFilter
import org.htmlparser.filters.HasAttributeFilter
import org.htmlparser.filters.StringFilter
import org.htmlparser.filters.TagNameFilter
import org.htmlparser.tags.LinkTag

import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger(getClass())

Logger.rootLogger.level = Level.WARN

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/13/13
 */

def parse_number_of_pages = { String html ->
    try {
        def parser = new Parser(html).parse(
                new AndFilter(
                        new TagNameFilter("a"),
                        new HasAttributeFilter("class", "pagebar_page")
                )
        );

        org.htmlparser.Node[] nodes = parser.toNodeArray();
        if (nodes.length > 2) {
            def link = (LinkTag) nodes[nodes.length - 3]
            return Integer.parseInt(link.getText())
        }
    } catch (Exception e) {
        return 1;
    }
}

def parse_person_url = { String html ->
    try {
        def parser = new Parser(html).parse(
                new AndFilter(
                        new TagNameFilter("div"),
                        new HasAttributeFilter("class", "catalog_list_item")
                )
        );

        org.htmlparser.util.NodeList nodes = parser.extractAllNodesThatMatch(
                new AndFilter(
                        new TagNameFilter("a"),
                        new StringFilter("class"))
        );
        if (nodes.length > 2) {
            def link = (LinkTag) nodes[nodes.length - 3]
            return Integer.parseInt(link.getText())
        }
    } catch (Exception e) {
        return 1;
    }
}


def VROZYSKE_HOSTNAME = "vrozyske.org"
def HOST = new HttpHost(VROZYSKE_HOSTNAME)
def session = new SessionSupport(HOST);
def httpClient = session.buildProxiedClient();

// list of people parsed
def people = []

// for all years codes
(1051..1069).each { year ->
    println "Processing year ${year + 949}";

    // for all symbols in cyrillic alphabet (А-Я)
    (0x0410..0x042F).collect { t -> Character.toChars(t) }.each { L ->
        println "Processing letter ${L}";

        def hs = new HttpHelper<String>(httpClient);

        // parsed list of people on single page
        def hp = new HttpHelper<List<Person>>(httpClient);

        // find all a class="pagebar_page" - number of pages
        def letterEncoded = URLEncoder.encode(L.toString(), "windows-1251")
        def firstPageUrl = "/catalog/${year}/find-first/${letterEncoded}";
        def N = hs.get(firstPageUrl, HOST,
                new HttpCallbackHandler<String>() {
                    @Override
                    public String process(String content) throws Exception {
                        return parse_number_of_pages(content);
                    }
                }, "windows-1251");

        // for all pages
        for (def page = 1; page <= N; page++) {
            def url = "/catalog/${year}-${page}/find-first/${letterEncoded}";
            println("Processing URL: ${url}");

            def person = hp.get(url, HOST,
                    new HttpCallbackHandler<List<Person>>() {
                        @Override
                        public List<Person> process(String content) throws Exception {
                            return Collections.emptyList();
                        }
                    }, "windows-1251");
            people << person;
            println(person)
        }
    }
}