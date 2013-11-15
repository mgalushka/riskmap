package org.riskmap.data.courts

import com.google.api.client.repackaged.com.google.common.base.Strings
import org.apache.http.HttpHost
import org.riskmap.http.HttpCallbackHandler
import org.riskmap.http.HttpHelper
import org.riskmap.http.SessionSupport

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/15/13
 */

def COURT_HOSTNAME = "www.reyestr.court.gov.ua"
def HOST = new HttpHost(COURT_HOSTNAME)
def session = new SessionSupport(HOST);

// TODO: if cannot load this - just use different set of proxies
def httpClient = session.buildProxiedClient();
def h = new HttpHelper<String>(httpClient)
def random = new Random(System.currentTimeMillis())

def TO_DIR = "D:\\projects\\riskmap\\docs\\courts\\details\\"
def IDS = new FileReader("D:\\projects\\riskmap\\docs\\courts\\ids.txt")

def line
while ((line = IDS.readLine()) != null) {
    if(Strings.isNullOrEmpty(line)) continue

    String id = line
    println "Processing ${id}"

    def file = new File(TO_DIR + id + ".html")
    if(file.exists()) {
        println ("File [${file.getPath()}] existed")
        continue
    }
    def OUT = new PrintWriter(file)

    def html = h.get("/Review/${id}", HOST, new HttpCallbackHandler<String>() {
        @Override
        String process(String content) throws Exception {
            return content
        }
    }, "UTF-8")

    OUT.println(html)
    OUT.flush()
    OUT.close()

    Thread.sleep(1000 + random.nextInt(500))
}
