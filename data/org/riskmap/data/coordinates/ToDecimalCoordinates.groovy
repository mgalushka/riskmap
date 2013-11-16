package org.riskmap.data.coordinates

import com.google.api.client.repackaged.com.google.common.base.Strings
import com.google.gson.JsonParser
import org.riskmap.http.HttpCallbackHandler

import java.text.DecimalFormat

/**
 * @author Maxim Galushka
 */

def IN = new FileReader("D:\\projects\\riskmap\\docs\\russia-cities.txt")
def OUT = new PrintWriter("D:\\projects\\riskmap\\docs\\russia-coordinates-output.txt")


def line
while ((line = IN.readLine()) != null) {
    if (Strings.isNullOrEmpty(line)) continue

    try {
        def l = line.split(",")
        def city = l[0]
        def lt = l[1]
        def ln = l[2]

        def lat = (lt.endsWith("N") ? 1 : -1) * Double.parseDouble(lt.substring(0, lt.indexOf("°"))) +
                Double.parseDouble(lt.substring(lt.indexOf("°") + 1, lt.indexOf("'"))) / 60.0
        def lon = (ln.endsWith("E") ? 1 : -1) * Double.parseDouble(ln.substring(0, ln.indexOf("°"))) +
                Double.parseDouble(ln.substring(ln.indexOf("°") + 1, ln.indexOf("'"))) / 60.0
        DecimalFormat numberFormat = new DecimalFormat("#.#####");
        lat = numberFormat.format(lat);
        lon = numberFormat.format(lon);
        println(("{city:'${city}',lat:${lat},lng:${lon},count:40}"))
        OUT.println("{city:'${city}',lat:${lat},lng:${lon},count:40},")

        //  Thread.sleep(200)
    }
    catch (Exception ex) {
        ex.printStackTrace()
    }
}

OUT.flush()
OUT.close()
