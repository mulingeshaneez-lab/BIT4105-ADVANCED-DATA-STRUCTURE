import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ParkingCLI extends JFrame {
    private final ParkingLot parkingLot;
    private final Map<Integer, JLabel> slotLabels; // Visual indicators for slots
    private final JTextField txtSlot;
    private final JTextField txtPlate;
    private final JTextArea logArea;

    public ParkingCLI(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        this.slotLabels = new HashMap<>();

        // Configure the main window window
        setTitle("Parking Lot Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL: Input Controls ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        inputPanel.add(new JLabel("Slot No:"));
        txtSlot = new JTextField(4);
        inputPanel.add(txtSlot);

        inputPanel.add(new JLabel("License Plate:"));
        txtPlate = new JTextField(8);
        inputPanel.add(txtPlate);

        JButton btnPark = new JButton("Park");
        JButton btnLeave = new JButton("Leave/Exit");
        JButton btnUndo = new JButton("Undo Action");

        inputPanel.add(btnPark);
        inputPanel.add(btnLeave);
        inputPanel.add(btnUndo);
        add(inputPanel, BorderLayout.NORTH);

        // --- CENTER PANEL: Visual Parking Grid ---
        JPanel gridPanel = new JPanel(new GridLayout(1, parkingLot.getCapacity(), 10, 10));
        gridPanel.setBorder(BorderFactory.createTitledBorder("Parking Bay Status"));

        for (int i = 1; i <= parkingLot.getCapacity(); i++) {
            JLabel lblSlot = new JLabel("Slot " + i + "\n[ EMPTY ]", SwingConstants.CENTER);
            lblSlot.setOpaque(true);
            lblSlot.setBackground(Color.GREEN);
            lblSlot.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            lblSlot.setFont(new Font("Arial", Font.BOLD, 12));

            slotLabels.put(i, lblSlot);
            gridPanel.add(lblSlot);
        }
        add(gridPanel, BorderLayout.CENTER);

        // --- BOTTOM PANEL: System Activity Log ---
        logArea = new JTextArea(6, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        add(scrollPane, BorderLayout.SOUTH);

        // --- BUTTON ACTIONS (Connecting to your Backend) ---
        btnPark.addActionListener(e -> handlePark());
        btnLeave.addActionListener(e -> handleLeave());
        btnUndo.addActionListener(e -> handleUndo());

        updateVisualGrid(); // Initial paint
    }

    private void handlePark() {
        try {
            int slot = Integer.parseInt(txtSlot.getText().trim());
            String plate = txtPlate.getText().trim().toUpperCase();

            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a License Plate.");
                return;
            }

            if (parkingLot.parkVehicle(slot, plate)) {
                logMessage("SUCCESS: Vehicle " + plate + " parked at Slot " + slot);
                clearInputs();
                updateVisualGrid();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Slot " + slot + " is occupied or out of bounds.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric slot number.");
        }
    }

    private void handleLeave() {
        try {
            int slot = Integer.parseInt(txtSlot.getText().trim());

            if (parkingLot.leaveVehicle(slot)) {
                logMessage("SUCCESS: Slot " + slot + " has been vacated.");
                clearInputs();
                updateVisualGrid();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Slot " + slot + " is already empty.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric slot number.");
        }
    }

    private void handleUndo() {
        if (parkingLot.undoLastAction()) {
            logMessage("UNDO: Reverted the last change.");
            updateVisualGrid();
        } else {
            logMessage("NOTICE: Nothing left in history stack to undo.");
        }
    }

    private void updateVisualGrid() {
        Map<Integer, String> occupied = parkingLot.getOccupiedSlots();
        for (int i = 1; i <= parkingLot.getCapacity(); i++) {
            JLabel label = slotLabels.get(i);
            if (occupied.containsKey(i)) {
                label.setText("<html><center>Slot " + i + "<br><b style='color:red;'>" + occupied.get(i) + "</b></center></html>");
                label.setBackground(new Color(255, 204, 204)); // Soft Red
            } else {
                label.setText("<html><center>Slot " + i + "<br><b style='color:green;'>[ EMPTY ]</b></center></html>");
                label.setBackground(new Color(204, 255, 204)); // Soft Green
            }
        }
    }

    private void logMessage(String msg) {
        logArea.append(msg + "\n");
    }

    private void clearInputs() {
        txtSlot.setText("");
        txtPlate.setText("");
    }
}