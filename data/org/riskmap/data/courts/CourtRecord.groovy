package org.riskmap.data.courts

import com.google.api.client.repackaged.com.google.common.base.Strings

import java.text.SimpleDateFormat

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/15/13
 */
class CourtRecord {

    def id

    def url

    // Вирок
    def type
    def date

    // Кримінальне
    def form

    // unique ref to match by
    def reference

    def court

    def judge


    @Override
    public String toString() {
        return "CourtRecord{" +
                "id=" + id +
                ", url=" + url +
                ", type=" + type +
                ", date=" + date +
                ", form=" + form +
                ", reference=" + reference +
                ", court=" + court +
                ", judge=" + judge +
                '}';
    }


    def SDF = new SimpleDateFormat("dd-MM-yyyy")

    public String toCsv() {
        try {
            return id +
                    "," + url +
                    "," + type +
                    "," + ((date != null) ? SDF.format(date) : "") +
                    "," + form +
                    "," + reference +
                    "," + court +
                    "," + judge;
        } catch (e) {
            println "Cannot format ${this}";
            return null
        }
    }
}
