public class ParkingAction {
    public enum ActionType { PARK, LEAVE }

    private final ActionType type;
    private final int slot;
    private final String licensePlate;

    public ParkingAction(ActionType type, int slot, String licensePlate) {
        this.type = type;
        this.slot = slot;
        this.licensePlate = licensePlate;
    }

    public ActionType getType() { return type; }
    public int getSlot() { return slot; }
    public String getLicensePlate() { return licensePlate; }
}