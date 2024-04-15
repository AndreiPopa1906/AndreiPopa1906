package Compulsory;

public class Driver extends Person {
    private Route route;

    public Driver(String name, int age, Destination startingPoint, Destination destination) {
        super(name, age, startingPoint, destination);
    }

    public Driver(String name, int age,  Destination startingPoint, Destination destination, Route route) {
        super(name, age, startingPoint, destination);
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }

    public boolean passesThroughDestination(Destination destination) {
        return route.isEnRoute(destination);
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + super.getName() + '\'' +
                ", age=" + super.getAge() +
                ", destination=" + super.getDestination().getName() +
                ", route=" + (route != null ? route.getStops().toString() : "No route assigned") +
                '}';
    }
}
