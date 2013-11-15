package org.riskmap.data.courts

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/15/13
 */
class CourtRecord {

    def id

    // Вирок
    def type
    def date

    // Кримінальне
    def form

    // unique ref to match by
    def reference

    def court

    def judge
}
