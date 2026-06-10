public class Vehicle {
    private final String licensePlate;

    // Constructor
    public Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    // Getter method to retrieve the plate
    public String getLicensePlate() {
        return licensePlate;
    }

    // Optional: Overriding toString makes debugging much easier later
    @Override
    public String toString() {
        return licensePlate;
    }
}