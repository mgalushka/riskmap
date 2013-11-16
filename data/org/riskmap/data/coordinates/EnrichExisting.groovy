package org.riskmap.data.coordinates

import org.testng.util.Strings

/**
 * @author Maxim Galushka
 */

def EXISTING = new FileReader("D:\\projects\\riskmap\\docs\\citi-existing.txt")
def IN = new FileReader("D:\\projects\\riskmap\\docs\\citi-coordinates.txt")

def OUT = new PrintWriter("D:\\projects\\riskmap\\docs\\citi-coordinates-final.txt")

def map = [:]
def line
while ((line = EXISTING.readLine()) != null) {
    if (Strings.isNullOrEmpty(line)) continue
    def split = line.split(",")
    map.put(split[0], split[1])
}

println(map)

while ((line = IN.readLine()) != null) {
    if (Strings.isNullOrEmpty(line)) continue
    def enriched = false
    for (String s : map.keySet()) {
        if (line.contains(s)) {
            println("Enrich ${line}")
            line = line.replaceAll("count:10", "count:${map.get(s)}")
            enriched = true
            OUT.println(line)
        }
    }
    if (!enriched) {
        println "Leave as is line: ${line}"
        OUT.println(line)
    }
}

OUT.flush()
OUT.close()