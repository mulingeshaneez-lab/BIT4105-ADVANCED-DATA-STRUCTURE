import javax.swing.SwingUtilities;

public class Main {
    static void main(String[] args) {
        // Create the background engine logic with 5 parking spaces
        ParkingLot lot = new ParkingLot(5);

        // Boot up the visual graphical screen on the UI thread
        SwingUtilities.invokeLater(() -> {
            ParkingCLI gui = new ParkingCLI(lot);
            gui.setVisible(true); // Makes the actual window pop up on your monitor
        });
    }