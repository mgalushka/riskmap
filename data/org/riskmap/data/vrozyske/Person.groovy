package org.riskmap.data.vrozyske

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

    @Override
    public String toString() {
        return "Person{" +
                "name=" + name +
                ", birthday=" + birthday +
                ", leavingPlace=" + leavingPlace +
                ", reason=" + reason +
                ", whenLost=" + whenLost +
                ", police=" + police +
                '}';
    }

    public String toCsv() {
        return name +
                "," + birthday +
                "," + leavingPlace +
                "," + reason +
                "," + whenLost +
                "," + police;
    }
}
