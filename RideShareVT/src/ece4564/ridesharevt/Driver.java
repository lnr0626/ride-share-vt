package ece4564.ridesharevt;

public class Driver {

    public String id;
    public String name;
    public String numSeats;
    public String status;
    public String tod;
    public String startLoc;
    public String endLoc;
    public String smoke;
    public String email;

    public String toString() {
        return name + " " + smoke + " " + tod;
    }
}
