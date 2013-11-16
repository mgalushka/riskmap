package org.riskmap.data.courts

import org.htmlparser.Parser
import org.htmlparser.filters.AndFilter
import org.htmlparser.filters.HasAttributeFilter
import org.htmlparser.filters.TagNameFilter
import org.htmlparser.tags.LinkTag
import org.htmlparser.tags.TableRow
import org.htmlparser.tags.TableTag
import org.riskmap.http.IOUtils
import org.testng.util.Strings

import java.text.SimpleDateFormat

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/15/13
 */


def FROM_DIR = "D:\\projects\\riskmap\\docs\\courts-trafficking\\"

def records = []
def files = new File(FROM_DIR).listFiles(new FilenameFilter() {
    @Override
    boolean accept(File dir, String name) {
        return name.endsWith(".html")
    }
})
def SDF = new SimpleDateFormat("dd.MM.yyyy")

// format function
def F = { String text ->
    if (text == null) return ""
    return text.trim()
}

files.each { file ->
    println "Processing [${file}] file"

    def html = IOUtils.convertStreamToString(new FileInputStream(file), "UTF-8")
    def recordTable = new Parser(html).parse(
            new AndFilter(
                    new TagNameFilter("div"),
                    new HasAttributeFilter("id", "divresult")
            )
    );

    def table = (TableTag) recordTable.toNodeArray()[0].getChildren().toNodeArray()[1]

    def rows = table.getRows()

    // skip 1st row - header
    for (def j = 1; j < rows.length; j++) {
        def row = rows[j] as TableRow
        def r = new CourtRecord()

        def cols = row.getColumns()

        r.id = F(((LinkTag) cols[0].getChildren().toNodeArray()[1]).getFirstChild().getText())
        r.url = F(((LinkTag) cols[0].getChildren().toNodeArray()[1]).getLink())
        r.type = F(cols[1].getFirstChild().getText())
        try {
            r.date = SDF.parse(F(cols[2].getFirstChild().getText()))
        } catch (Exception e) {
            println "Unparseable date: [${F(cols[2].getFirstChild().getText())}]"
        }
        r.form = F(cols[4].getFirstChild().getText())
        r.reference = F(cols[5].getFirstChild().getText())
        r.court = F(cols[6].getFirstChild().getText())
        r.judge = F(cols[7].getFirstChild().getText())

        records << r
    }
}


def OUT = new PrintWriter("${FROM_DIR}\\records.csv", "windows-1251")

// header
OUT.println("id,url,type,date,form,reference,court,judge")

def k=0
records.each { CourtRecord r ->
    def line = r.toCsv()
    if(!Strings.isNullOrEmpty(line)){
        OUT.println()
        if(k++ % 1000 == 0) OUT.flush()
    }
}
OUT.flush()
OUT.close()
