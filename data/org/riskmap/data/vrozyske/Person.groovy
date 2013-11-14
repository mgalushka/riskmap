package org.riskmap.data.vrozyske

/**
 * <p></p>
 * @author Maxim Galushka
 * @since 11/13/13
 */
class Person {

    def name;
    def birthday;
    def leavingPlace;
    def whereLost;
    def reason;
    def whenLost;
    def police;

    @Override
    public String toString() {
        return "Person{" +
                "name=" + name +
                ", birthday=" + birthday +
                ", leavingPlace=" + leavingPlace +
                ", whereLost=" + whereLost +
                ", reason=" + reason +
                ", whenLost=" + whenLost +
                ", police=" + police +
                '}';
    }
}
