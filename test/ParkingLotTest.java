import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParkingLotTest {

    // ==========================================
    // NORMAL CASES (Core Functionality)
    // ==========================================

    @Test
    void testParkVehicleSuccessfully() {
        ParkingLot lot = new ParkingLot(5);
        assertTrue(lot.parkVehicle(1, "KAA 123A"));
        assertEquals("KAA 123A", lot.getOccupiedSlots().get(1));
    }

    @Test
    void testLeaveVehicleSuccessfully() {
        ParkingLot lot = new ParkingLot(5);
        lot.parkVehicle(2, "KBB 456B");
        assertTrue(lot.leaveVehicle(2));
        assertFalse(lot.getOccupiedSlots().containsKey(2));
    }

    @Test
    void testUndoParkingAction() {
        ParkingLot lot = new ParkingLot(5);
        lot.parkVehicle(3, "KCC 789C");
        assertTrue(lot.undoLastAction());
        assertFalse(lot.getOccupiedSlots().containsKey(3));
    }

    @Test
    void testUndoLeavingAction() {
        ParkingLot lot = new ParkingLot(5);
        lot.parkVehicle(4, "KDD 101D");
        lot.leaveVehicle(4); // Car leaves

        assertTrue(lot.undoLastAction()); // Undo the exit
        assertEquals("KDD 101D", lot.getOccupiedSlots().get(4)); // Car should be back
    }

    @Test
    void testCapacityInitialization() {
        ParkingLot lot = new ParkingLot(10);
        assertEquals(10, lot.getCapacity());
    }

    @Test
    void testMultipleVehiclesParking() {
        ParkingLot lot = new ParkingLot(5);
        assertTrue(lot.parkVehicle(1, "KAA 111A"));
        assertTrue(lot.parkVehicle(2, "KBB 222B"));
        assertEquals(2, lot.getOccupiedSlots().size());
    }

    // ==========================================
    // EDGE CASES (Boundary Conditions)
    // ==========================================

    @Test
    void testParkAtFirstValidSlot() {
        ParkingLot lot = new ParkingLot(5);
        assertTrue(lot.parkVehicle(1, "KAA 001A")); // Lower boundary
    }

    @Test
    void testParkAtLastValidSlot() {
        ParkingLot lot = new ParkingLot(5);
        assertTrue(lot.parkVehicle(5, "KAA 005A")); // Upper boundary
    }

    @Test
    void testMultipleUndosInARow() {
        ParkingLot lot = new ParkingLot(5);
        lot.parkVehicle(1, "KAA 123A");
        lot.parkVehicle(2, "KBB 456B");

        assertTrue(lot.undoLastAction()); // Undoes slot 2
        assertTrue(lot.undoLastAction()); // Undoes slot 1
        assertTrue(lot.getOccupiedSlots().isEmpty());
    }

    // ==========================================
    // ERROR CASES (Invalid Inputs & Constraints)
    // ==========================================

    @Test
    void testParkInOccupiedSlotFails() {
        ParkingLot lot = new ParkingLot(5);
        lot.parkVehicle(1, "KAA 123A");
        assertFalse(lot.parkVehicle(1, "KBB 456B")); // Same slot
    }

    @Test
    void testParkInSlotZeroFails() {
        ParkingLot lot = new ParkingLot(5);
        assertFalse(lot.parkVehicle(0, "KAA 123A")); // Out of bounds low
    }

    @Test
    void testParkInSlotBeyondCapacityFails() {
        ParkingLot lot = new ParkingLot(5);
        assertFalse(lot.parkVehicle(6, "KAA 123A")); // Out of bounds high
    }

    @Test
    void testLeaveAlreadyEmptySlotFails() {
        ParkingLot lot = new ParkingLot(5);
        assertFalse(lot.leaveVehicle(3)); // Slot was never parked in
    }

    @Test
    void testLeaveInvalidSlotFails() {
        ParkingLot lot = new ParkingLot(5);
        assertFalse(lot.leaveVehicle(99)); // Completely invalid slot
    }

    @Test
    void testUndoWithEmptyHistoryFails() {
        ParkingLot lot = new ParkingLot(5);
        assertFalse(lot.undoLastAction()); // Nothing has happened yet
    }

    @Test
    void testParkNegativeSlotFails() {
        ParkingLot lot = new ParkingLot(5);
        assertFalse(lot.parkVehicle(-1, "KAA 123A"));
    }
}