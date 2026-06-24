import java.util.*;

// Author: MelindaKiplagat
// Data Structures Lead - Role 2

// ------------------------------------------------------------
// Supporting classes
// ------------------------------------------------------------
class Vehicle {
    private final String licensePlate;
    private final boolean isVip;
    private final boolean isEv;

    public Vehicle(String licensePlate) {
        this(licensePlate, false, false);
    }

    public Vehicle(String licensePlate, boolean isVip, boolean isEv) {
        this.licensePlate = licensePlate;
        this.isVip = isVip;
        this.isEv = isEv;
    }

    public String getLicensePlate() { return licensePlate; }
    public boolean isVip() { return isVip; }
    public boolean isEv() { return isEv; }

    @Override
    public String toString() {
        return String.format("Vehicle(%s, VIP=%b, EV=%b)", licensePlate, isVip, isEv);
    }
}

class ParkingSpot {
    private final String id;
    private final int distanceFromEntrance;
    private final boolean isPremium;
    private boolean isOccupied;
    private Vehicle currentVehicle;

    public ParkingSpot(String id, int distanceFromEntrance, boolean isPremium) {
        this.id = id;
        this.distanceFromEntrance = distanceFromEntrance;
        this.isPremium = isPremium;
        this.isOccupied = false;
        this.currentVehicle = null;
    }

    public String getId() { return id; }
    public int getDistanceFromEntrance() { return distanceFromEntrance; }
    public boolean isPremium() { return isPremium; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }
    public void setCurrentVehicle(Vehicle vehicle) { currentVehicle = vehicle; }

    @Override
    public String toString() {
        return String.format("Spot(%s, occupied=%b, premium=%b)", id, isOccupied, isPremium);
    }
}

public class ParkingLot {
    
    // Hash table - O(1) lookup
    private final Map<String, ParkingSpot> spots = new HashMap<>();

    // Stack - O(1) push/pop for undo
    private final Deque<String[]> exitStack = new ArrayDeque<>();

    // Queue - O(1) FIFO for waiting cars
    private final Queue<Vehicle> waitingQueue = new LinkedList<>();

    // Heap - O(log n) for best available spot
    private final PriorityQueue<SpotPriority> availableHeap = new PriorityQueue<>();

    private final Map<String, String> vehicleToSpot = new HashMap<>();
    private final double hourlyRate = 5.0;

    private static class SpotPriority implements Comparable<SpotPriority> {
        int priority;
        String spotId;

        SpotPriority(int priority, String spotId) {
            this.priority = priority;
            this.spotId = spotId;
        }

        @Override
        public int compareTo(SpotPriority other) {
            return Integer.compare(this.priority, other.priority);
        }
    }

    public void addSpot(ParkingSpot spot) {
        spots.put(spot.getId(), spot);
        int priority = spot.isPremium() ? 0 : 100;
        priority += spot.getDistanceFromEntrance();
        availableHeap.offer(new SpotPriority(priority, spot.getId()));
    }

    private ParkingSpot getBestAvailableSpot() {
        while (!availableHeap.isEmpty()) {
            SpotPriority sp = availableHeap.poll();
            ParkingSpot spot = spots.get(sp.spotId);
            if (spot != null && !spot.isOccupied()) {
                return spot;
            }
        }
        return null;
    }

    private void returnSpotToHeap(ParkingSpot spot) {
        int priority = spot.isPremium() ? 0 : 100;
        priority += spot.getDistanceFromEntrance();
        availableHeap.offer(new SpotPriority(priority, spot.getId()));
    }

    public void addToWaitingQueue(Vehicle vehicle) {
        waitingQueue.offer(vehicle);
        System.out.printf("[Queue] %s added. Position: %d%n", vehicle.getLicensePlate(), waitingQueue.size());
    }

    public void processWaitingQueue() {
        while (!waitingQueue.isEmpty() && getBestAvailableSpot() != null) {
            Vehicle nextCar = waitingQueue.poll();
            System.out.printf("[Queue] Processing %s from queue...%n", nextCar.getLicensePlate());
            parkVehicle(nextCar);
        }
    }

    public boolean parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = getBestAvailableSpot();
        if (spot == null) {
            addToWaitingQueue(vehicle);
            return false;
        }
        spot.setOccupied(true);
        spot.setCurrentVehicle(vehicle);
        vehicleToSpot.put(vehicle.getLicensePlate(), spot.getId());
        System.out.printf("[Hash] Parked %s at %s%n", vehicle.getLicensePlate(), spot.getId());
        return true;
    }

    public double exitVehicle(String licensePlate) {
        String spotId = vehicleToSpot.get(licensePlate);
        if (spotId == null) throw new IllegalArgumentException("Vehicle not found");
        ParkingSpot spot = spots.get(spotId);
        if (!spot.isOccupied()) throw new IllegalStateException("Spot already free");
        double fee = hourlyRate;
        exitStack.push(new String[]{licensePlate, spot.getId(), String.valueOf(fee)});
        System.out.printf("[Stack] Pushed exit: %s from %s, fee $%.2f%n", licensePlate, spot.getId(), fee);
        spot.setOccupied(false);
        spot.setCurrentVehicle(null);
        vehicleToSpot.remove(licensePlate);
        returnSpotToHeap(spot);
        processWaitingQueue();
        return fee;
    }

    public boolean undoLastExit() {
        if (exitStack.isEmpty()) {
            System.out.println("[Stack] Nothing to undo");
            return false;
        }
        String[] entry = exitStack.pop();
        String licensePlate = entry[0];
        String spotId = entry[1];
        System.out.printf("[Stack] Undo exit of %s from %s%n", licensePlate, spotId);
        ParkingSpot spot = spots.get(spotId);
        if (spot.isOccupied()) {
            System.out.println("[Stack] Spot already occupied, cannot undo");
            return false;
        }
        Vehicle vehicle = new Vehicle(licensePlate);
        spot.setOccupied(true);
        spot.setCurrentVehicle(vehicle);
        vehicleToSpot.put(licensePlate, spotId);
        System.out.printf("[Stack] Restored %s to %s%n", licensePlate, spotId);
        return true;
    }

    public void displayStatus() {
        long occupied = spots.values().stream().filter(ParkingSpot::isOccupied).count();
        System.out.println("\n=== Status ===");
        System.out.printf("Total: %d, Occupied: %d, Free: %d%n", spots.size(), occupied, spots.size() - occupied);
        System.out.printf("Waiting: %d, Undo stack: %d%n", waitingQueue.size(), exitStack.size());
        System.out.println("==============\n");
    }

    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot();
        lot.addSpot(new ParkingSpot("A1", 5, true));
        lot.addSpot(new ParkingSpot("A2", 10, false));
        lot.addSpot(new ParkingSpot("B1", 20, false));
        lot.addSpot(new ParkingSpot("B2", 25, false));
        lot.addSpot(new ParkingSpot("P1", 2, true));

        lot.parkVehicle(new Vehicle("ABC123", true, false));
        lot.parkVehicle(new Vehicle("XYZ789", false, false));
        lot.parkVehicle(new Vehicle("EV456", false, true));
        lot.displayStatus();

        lot.exitVehicle("XYZ789");
        lot.displayStatus();

        lot.undoLastExit();
        lot.displayStatus();

        lot.exitVehicle("XYZ789");
        lot.parkVehicle(new Vehicle("CAR4"));
        lot.parkVehicle(new Vehicle("CAR5"));
        lot.displayStatus();

        lot.exitVehicle("ABC123");
        lot.displayStatus();
    }
}
    

