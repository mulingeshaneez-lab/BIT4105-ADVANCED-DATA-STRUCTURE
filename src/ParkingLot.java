import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ParkingLot {
    private final int capacity;
    private final Map<Integer, String> occupiedSlots;
    // Stack to track history for the Undo feature
    private final Stack<ParkingAction> actionHistory;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        this.occupiedSlots = new HashMap<>();
        this.actionHistory = new Stack<>();
    }

    // Method to park a vehicle
    public boolean parkVehicle(int slot, String licensePlate) {
        if (slot < 1 || slot > capacity || occupiedSlots.containsKey(slot)) {
            return false;
        }
        occupiedSlots.put(slot, licensePlate);

        // Record the action to history
        actionHistory.push(new ParkingAction(ParkingAction.ActionType.PARK, slot, licensePlate));
        return true;
    }

    // Method for a vehicle to leave
    public boolean leaveVehicle(int slot) {
        if (occupiedSlots.containsKey(slot)) {
            String licensePlate = occupiedSlots.get(slot);
            occupiedSlots.remove(slot);

            // Record the action to history
            actionHistory.push(new ParkingAction(ParkingAction.ActionType.LEAVE, slot, licensePlate));
            return true;
        }
        return false;
    }

    // The Undo Method
    public boolean undoLastAction() {
        if (actionHistory.isEmpty()) {
            return false; // Nothing to undo
        }

        // Get the last action performed
        ParkingAction lastAction = actionHistory.pop();

        if (lastAction.getType() == ParkingAction.ActionType.PARK) {
            // Reverse a PARK action by removing the vehicle
            occupiedSlots.remove(lastAction.getSlot());
        } else if (lastAction.getType() == ParkingAction.ActionType.LEAVE) {
            // Reverse a LEAVE action by putting the vehicle back
            occupiedSlots.put(lastAction.getSlot(), lastAction.getLicensePlate());
        }

        return true;
    }

    public Map<Integer, String> getOccupiedSlots() {
        return new HashMap<>(occupiedSlots);
    }

    public int getCapacity() {
        return capacity;
    }
}