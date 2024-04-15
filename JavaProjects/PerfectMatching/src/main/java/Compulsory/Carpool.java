package Compulsory;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Carpool {
    private List<Person> people;

    private static final Logger logger = Logger.getLogger(Carpool.class.getName());

    public Carpool(List<Person> people) {
        this.people = people;
    }

    /**
     * To compare with the criterion of age
     */
    private class AgeComparator implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            return Integer.compare(p1.getAge(), p2.getAge());
        }
    }

    /**
     * To compare with the criterion of name
     */
    private class NameComparator implements Comparator<Person> {
        @Override
        public int compare(Person p1, Person p2) {
            return p1.getName().compareTo(p2.getName());
        }
    }

    /**
     * Sort drivers by age
     * @return a list of drivers
     */
    public LinkedList<Driver> getDriversSortedByAge() {
        return people.stream()
                .filter(p -> p instanceof Driver)
                .map(p -> (Driver) p)
                .sorted(new AgeComparator())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Sort drivers by name
     * @return a list of drivers
     */
    public TreeSet<Passenger> getPassengersSortedByName() {
        return people.stream()
                .filter(p -> p instanceof Passenger)
                .map(p -> (Passenger) p)
                .collect(Collectors.toCollection(() -> new TreeSet<>(new NameComparator())));
    }

    /**
     * Creates random people
     * @param numberOfPeople number of persons to be created
     * @return a list of random people
     */
    public static List<Person> createRandomPeople(int numberOfPeople) {
        List<Person> people = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numberOfPeople; i++) {
            String name = "Person" + i;
            int age = random.nextInt(60) + 18;
            Destination destination = new Destination("Destination" + random.nextInt(10));
            Destination startingPoint = new Destination("Starting point" + random.nextInt(10));
            if (random.nextBoolean()) {
                people.add(new Driver(name, age, startingPoint, destination));
            } else {
                people.add(new Passenger(name, age, startingPoint, destination));
            }
        }
        return people;
    }

    /**
     *
      */
    public static void main(String[] args) {
        List<Person> randomPeople = Carpool.createRandomPeople(10);

        Carpool carpool = new Carpool(randomPeople);
        LinkedList<Driver> driversSortedByAge = carpool.getDriversSortedByAge();
        TreeSet<Passenger> passengersSortedByName = carpool.getPassengersSortedByName();

        logger.info("\n---> Drivers sorted by age:");

        Iterator<Driver> iterator1 = driversSortedByAge.iterator();
        while (iterator1.hasNext()) {
            Driver driver = iterator1.next();
            logger.info("[LOGGER] " + driver.toString());
        }

        logger.info("\n---> Passengers sorted by name:");

        Iterator<Passenger> iterator2 = passengersSortedByName.iterator();
        while (iterator2.hasNext()) {
            Passenger passenger = iterator2.next();
            logger.info("[LOGGER] " + passenger.toString());
        }

    }
}

