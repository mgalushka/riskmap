package org.riskmap.data.courts

import org.htmlparser.Parser
import org.htmlparser.filters.AndFilter
import org.htmlparser.filters.HasAttributeFilter
import org.htmlparser.filters.TagNameFilter
import org.htmlparser.tags.LinkTag
import org.htmlparser.tags.TableRow
import org.htmlparser.tags.TableTag
import org.riskmap.http.IOUtils

import java.text.SimpleDateFormat

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/15/13
 */


def FROM_DIR = "D:\\projects\\riskmap\\docs\\court\\"

def records = []
def files = new File(FROM_DIR).listFiles()
def SDF = new SimpleDateFormat("dd.MM.yyyy")

// format function
def F = { String text ->
    if (text == null) return ""
    return text.trim()
}

def counter = 1
files.each { file ->
    println "Processing ${counter} file"

    def html = IOUtils.convertStreamToString(new FileInputStream(FROM_DIR + "out_${counter}.html"), "UTF-8")
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
        r.date = SDF.parse(F(cols[2].getFirstChild().getText()))
        r.form = F(cols[4].getFirstChild().getText())
        r.reference = F(cols[5].getFirstChild().getText())
        r.court = F(cols[6].getFirstChild().getText())
        r.judge = F(cols[7].getFirstChild().getText())

        records << r
    }

}


def OUT = new PrintWriter("D:\\projects\\riskmap\\docs\\court\\records.csv")

// header
OUT.println("id,url,type,date,form,reference,court,judge")

def k=0
records.each { CourtRecord r ->
    OUT.println(r.toCsv())
    if(k++ % 1000 == 0) OUT.flush()
}
OUT.flush()
OUT.close()
