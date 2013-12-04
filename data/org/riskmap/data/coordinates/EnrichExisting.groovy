package org.riskmap.data.coordinates

import org.testng.util.Strings

/**
 * @author Maxim Galushka
 */

def ROOT = "D:\\projects\\riskmap\\riskmap\\docs\\"

def EXISTING = new FileReader("${ROOT}citi-existing.txt")
def IN = new FileReader("${ROOT}citi-coordinates.txt")

def OUT = new PrintWriter("${ROOT}citi-coordinates-final.txt")
def OUT2 = new PrintWriter("${ROOT}citi-major-coordinates-final.txt")

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
            OUT2.println(line)
        }
    }
    if (!enriched) {
        println "Leave as is line: ${line}"
        OUT.println(line)
    }
}

OUT.flush()
OUT.close()

OUT2.flush()
OUT2.close()