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
import org.htmlparser.tags.Bullet
import org.htmlparser.tags.LinkTag

import org.apache.log4j.Level
import org.apache.log4j.Logger

import java.text.SimpleDateFormat

def log = Logger.getLogger(getClass())

Logger.rootLogger.level = Level.WARN

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/13/13
 */

def OUT = new PrintWriter("D:\\projects\\riskmap\\vrozyske.csv")
OUT.println("name,birthday,leavingPlace,reason,whenLost,police")

def TERR = new PrintWriter("D:\\projects\\riskmap\\vrozyske-territories.csv")

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
            return Integer.parseInt(link.getFirstChild().getText())
        }
    } catch (Exception e) {
        e.printStackTrace()
    }
    return 1;
}

def parse_person_urls = { String html ->
    def links = [];
    try {
        def parser = new Parser(html).parse(
                new AndFilter(
                        new TagNameFilter("div"),
                        new HasAttributeFilter("class", "catalog_list_item")
                )
        );

        org.htmlparser.util.NodeList nds = parser.extractAllNodesThatMatch(new TagNameFilter("a"), true);

        org.htmlparser.Node[] nodes = nds.toNodeArray();
        if (nodes.length > 0) {
            for (def i = 0; i < nodes.length; i++) {
                def link = (LinkTag) nodes[i] as LinkTag

                // relative!
                def linkUrl = link.getLink()

                if (linkUrl.endsWith(".html")) {
                    links << link.getLink()
                    break;
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace()
    }
    return links;
}

def ALL_TAGS = [] as TreeSet<String>

def SDF = new SimpleDateFormat("MM/dd/yyyy")
def parse_person_details = { String html ->
    def person = new Person();
    def stopped = false
    try {
        def nameParser = new Parser(html).parse(
                new AndFilter(
                        new TagNameFilter("div"),
                        new HasAttributeFilter("class", "con_heading")
                )
        );

        // full name here
        def name = nameParser.toNodeArray()[0].getFirstChild().getText()
        person.name = name;

        //uc_detaillist
        def detailsParser = new Parser(html).parse(
                new AndFilter(
                        new TagNameFilter("ul"),
                        new HasAttributeFilter("class", "uc_detaillist")
                )
        );
        org.htmlparser.Node[] detailsNodes = detailsParser.toNodeArray();

        def country_where_lost = null;
        def region_where_lost = null;
        def district_where_lost = null;
        def city_where_lost = null;

        def region_where_born = null;
        def city_where_born = null;
        def district_where_born = null;

        def police_dept = null;

        if (detailsNodes.length > 0) {
            for (org.htmlparser.Node tag : detailsNodes[0].getChildren().toNodeArray()) {
                if (tag instanceof Bullet) {
                    def bullet = (Bullet) tag as Bullet
                    org.htmlparser.Node[] det = bullet.getChildrenAsNodeArray()
                    if (det.length >= 4) {
                        def code = det[1].getText().replaceAll("\\,|\\.|\\:", "").trim()
                        ALL_TAGS << code
                        def value
                        if (det[3] instanceof LinkTag) {
                            value = ((LinkTag) det[3]).getFirstChild().getText().replaceAll("\\,|\\.|\\:", "").trim()
                        } else {
                            value = det[3].getText().replaceAll("\\,|\\.|\\:", "").trim()
                        }

                        // reason
                        if (code.toLowerCase().contains("причина розыска")) person.reason = value

                        // born place
                        if (code.toLowerCase().contains("нас пункт рожд")) city_where_born = value
                        if (code.toLowerCase().contains("область рождения")) region_where_born = value
                        if (code.toLowerCase().contains("район жительства")) district_where_lost = value

                        // leaving place
                        if (code.toLowerCase().contains("нас пункт прож")) city_where_lost = value
                        if (code.toLowerCase().contains("область жительства")) region_where_lost = value

                        if (code.toLowerCase().contains("пропал на территории")) person.leavingPlace = value

                        // dates
                        if (code.toLowerCase().contains("дата объявления в розыск")) person.whenLost = SDF.parse(value)
                        if (code.toLowerCase().contains("объявлен в розыск (дата)")) person.whenLost = SDF.parse(value)
                        if (code.toLowerCase().contains("объявлен в розыск")) person.whenLost = SDF.parse(value)
                        if (code.toLowerCase().contains("дата пропажи")) person.whenLost = SDF.parse(value)

                        if (code.toLowerCase().contains("пропал без вести")) person.whenLost = SDF.parse(value)
                        if (code.toLowerCase().contains("пропал без вести (дата)")) person.whenLost = SDF.parse(value)

                        if (code.toLowerCase().contains("дата рождения")) person.birthday = SDF.parse(value)

                        // TODO: log all police names here...
                        if (code.toLowerCase().contains("орган-инициатор")) person.police = value
                        if (code.toLowerCase().contains("орган")) person.police = value

                        if (code.toLowerCase().contains("розыск прекращен (дата)") ||
                                code.toLowerCase().contains("Прекращение розыска") ||
                                code.toLowerCase().contains("Причина прекращения")){
                            stopped = true
                        }

                        if(code.toLowerCase().contains("пропал на территории")) {
                            TERR.println("${code} - ${value}")
                        }

                    }
                }
            }
        }

        if (person.leavingPlace == null) {
            if (city_where_lost != null) person.leavingPlace = city_where_lost
            else {
                if (region_where_lost != null) person.leavingPlace = region_where_lost
                else {
                    if (district_where_lost != null) person.leavingPlace = district_where_lost
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace()
    }
    return stopped ? null : person;
}


def VROZYSKE_HOSTNAME = "vrozyske.org"
def HOST = new HttpHost(VROZYSKE_HOSTNAME)
def session = new SessionSupport(HOST);
def httpClient = session.buildProxiedClient();

// list of people parsed
def people = []

// for all years codes
(1051..1069).each { year ->
    println "Processing year ${year + 949}, code: [${year}]";

    // for all symbols in cyrillic alphabet (А-Я)
    (0x0410..0x042F).collect { t -> Character.toChars(t) }.each { L ->
        println "Processing letter ${L}";

        // number of peple pages on screen
        def hs = new HttpHelper<Integer>(httpClient);

        // parsed links for people pages
        def hl = new HttpHelper<List<String>>(httpClient)

        // parsed list of people on single page
        def hp = new HttpHelper<Person>(httpClient);

        // find all a class="pagebar_page" - number of pages
        def letterEncoded = URLEncoder.encode(L.toString(), "windows-1251")
        def firstPageUrl = "/catalog/${year}/find-first/${letterEncoded}";
        def N = hs.get(firstPageUrl, HOST,
                new HttpCallbackHandler<Integer>() {
                    @Override
                    public Integer process(String content) throws Exception {
                        return parse_number_of_pages(content);
                    }
                }, "windows-1251");

        println "Total pages for letter: [${L}] : [${N}]"

        def links = []
        // for all pages
        for (def page = 1; page <= N; page++) {
            def url = "/catalog/${year}-${page}/find-first/${letterEncoded}";
            println("Processing URL: ${url}");

            def personLink = hl.get(url, HOST,
                    new HttpCallbackHandler<List<String>>() {
                        @Override
                        public List<String> process(String content) throws Exception {
                            return parse_person_urls(content);
                        }
                    }, "windows-1251");
            links.addAll(personLink);
        }
        println "Processing links: ${links}";

        for (String url : links) {
            println("Processing person page URL: ${url}");
            def personDetails = hp.get(url, HOST,
                    new HttpCallbackHandler<Person>() {
                        @Override
                        public Person process(String content) throws Exception {
                            return parse_person_details(content);
                        }
                    }, "windows-1251");

            // not include stop search
            if(personDetails != null){
                personDetails.url = url;
                people << personDetails
                OUT.println(personDetails.toCsv())
            }
        }
    }
}

println(people)
println ALL_TAGS
OUT.flush()
OUT.close()

TERR.flush()
TERR.close()
