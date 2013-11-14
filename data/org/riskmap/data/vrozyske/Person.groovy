package org.riskmap.data.vrozyske

import java.text.SimpleDateFormat

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/13/13
 */
class Person {

    def url;
    def name;
    def birthday;

    // place where lost
    def leavingPlace;
    def reason;
    def whenLost;
    def police;

    def SDF = new SimpleDateFormat("dd-MM-yyyy")

    @Override
    public String toString() {
        return "Person{" +
                "name=" + name +
                ", birthday=" + SDF.format(birthday) +
                ", leavingPlace=" + leavingPlace +
                ", reason=" + reason +
                ", whenLost=" + SDF.format(whenLost) +
                ", police=" + police +
                '}';
    }

    public String toCsv() {
        return name +
                "," + SDF.format(birthday) +
                "," + leavingPlace +
                "," + reason +
                "," + SDF.format(whenLost) +
                "," + police;
    }
}
