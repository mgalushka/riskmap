package org.riskmap.data.coordinates

import com.google.api.client.repackaged.com.google.common.base.Strings
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.apache.http.HttpHost
import org.riskmap.http.HttpCallbackHandler
import org.riskmap.http.HttpHelper
import org.riskmap.http.SessionSupport

/**
 * @author Maxim Galushka
 */

def IN = new FileReader("D:\\projects\\riskmap\\docs\\city-urls-largest.txt")
def OUT = new PrintWriter("D:\\projects\\riskmap\\docs\\citi-coordinates.txt")

def GOOGLE = "maps.googleapis.com"
def HOST = new HttpHost(GOOGLE)
def session = new SessionSupport(HOST);

def httpClient = session.buildProxiedClient();
def h = new HttpHelper<String>(httpClient)

def gson = new Gson()

def line
while ((line = IN.readLine()) != null) {
    if (Strings.isNullOrEmpty(line)) continue

    try {
        def l = line.split(",")
        def city = l[0]
        def cityEncoded = URLEncoder.encode(city, "UTF-8")

        def html = h.get("/maps/api/geocode/json?address=${cityEncoded}&sensor=true",
                HOST, new HttpCallbackHandler<String>() {
            @Override
            String process(String content) throws Exception {
                return content
            }
        }, "UTF-8")

        def json = new JsonParser().parse(html);

        def lat = json.getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").get("location").get("lat")
        def lon = json.getAsJsonArray("results").get(0).getAsJsonObject().get("geometry").get("location").get("lng")

        println(("{city:'${city}',lat:${lat},lng:${lon},count:10}"))
        OUT.println("{city:'${city}',lat:${lat},lng:${lon},count:10}")

        Thread.sleep(200)
    }
    catch (Exception ex) {
        ex.printStackTrace()
    }
}

OUT.flush()
OUT.close()