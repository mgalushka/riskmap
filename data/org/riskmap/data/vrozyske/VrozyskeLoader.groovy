package org.riskmap.data.vrozyske

import com.riskmap.http.HttpCallbackHandler
import com.riskmap.http.HttpHelper
import org.apache.http.HttpHost
import org.apache.http.impl.client.DefaultHttpClient

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/13/13
 */

def VROZYSKE_HOSTNAME = "http://vrozyske.org"
def people = []

// for all years codes
(1051..1069).each { year ->
    println "Processing year ${year + 949}";

    // for all symbols in cyrillic alphabet (А-Я)
    (0x0410..0x042F).collect { t -> Character.toChars(t) }.each { L ->
        println "Processing letter ${L}";

        def httpClient = new DefaultHttpClient();
        def h = new HttpHelper<Person>(httpClient);

        List<String> urls = new ArrayList<String>();

        // find all span class="pagebar_title" - number of pages
        def N = 1;

        // for all pages
        for (def page = 1; page <= N; page++) {
            def letterEncoded = URLEncoder.encode(L + "", "UTF-8");
            def url = "/catalog/${year}-${page}/find-first/${letterEncoded}";
            println("Processing URL: ${url}");

            def person = h.get(url,
                    new HttpHost(VROZYSKE_HOSTNAME),
                    new HttpCallbackHandler<Person>() {
                        @Override
                        public Person process(String content) throws Exception {
                            return null;
                        }
                    }, "windows-1251");
            people << person;
            println(person)
        }
    }
}