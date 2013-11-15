package org.riskmap.data.courts

import org.apache.http.*
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HTTP
import org.riskmap.http.*

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

// all records
def records = []

def firstPageUrl = "/";

def parameters = new ArrayList<NameValuePair>();
parameters.add(new BasicNameValuePair("CaseNumber", ""));
parameters.add(new BasicNameValuePair("ChairmenName", ""));
parameters.add(new BasicNameValuePair("ImportDateBegin", ""));
parameters.add(new BasicNameValuePair("ImportDateEnd", ""));
parameters.add(new BasicNameValuePair("Liga", "false"));
parameters.add(new BasicNameValuePair("PagingInfo.ItemsPerPage", "1000"));
parameters.add(new BasicNameValuePair("RegDateBegin", ""));
parameters.add(new BasicNameValuePair("RegDateEnd", ""));
parameters.add(new BasicNameValuePair("RegNumber", ""));
parameters.add(new BasicNameValuePair("RegDateEnd", ""));
parameters.add(new BasicNameValuePair("SearchExpression", "ст. 149 КК"));
parameters.add(new BasicNameValuePair("Sort", "0"));
parameters.add(new BasicNameValuePair("UserCourtCode", ""));
parameters.add(new BasicNameValuePair("VRType[]", "1"));
def postEnty = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);

def h = new HttpHelper<String>(httpClient)

println "requesting 1 page"

session.initCookie(httpClient);
def html1 = h.post(firstPageUrl, postEnty, HOST, new HttpCallbackHandler<String>() {
    @Override
    String process(String content) throws Exception {
        return content
    }
}, "UTF-8")

// dump to file
def O1 = new PrintWriter("D:\\projects\\riskmap\\docs\\court\\out_1.html")
O1.println(html1)
O1.flush()
O1.close()

Thread.sleep(1000)

for (def i = 2; i <= 8; i++) {
    println "requesting ${i} page"
    session.initCookie(httpClient);
    def html = h.get("/Page/${i}", HOST, new HttpCallbackHandler<String>() {
        @Override
        String process(String content) throws Exception {
            return content
        }
    }, "UTF-8")

    // dump to file
    def O = new PrintWriter("D:\\projects\\riskmap\\docs\\court\\out_${i}.html")
    O.println(html)
    O.flush()
    O.close()

    Thread.sleep(1000)
}
