package hotel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class MovieTicketBookingSystem {
    private static HashMap<String, HashMap<String, Integer>> reservations = new HashMap<>();
    private static final int TICKET_PRICE = 150; // Ticket price in rupees
    private static final int MAX_SEATS = 100;   // Maximum seats available per showtime

    public static void main(String[] args) {
        // Initialize reservations
        initializeReservations();

        // Display welcome screen
        showWelcomeScreen();
    }

    private static void initializeReservations() {
        reservations.put("Avengers: Endgame", new HashMap<>());
        reservations.put("The Lion King", new HashMap<>());
        reservations.put("Frozen II", new HashMap<>());

        for (String movie : reservations.keySet()) {
            reservations.get(movie).put("12:00 PM", 0);
            reservations.get(movie).put("3:00 PM", 0);
            reservations.get(movie).put("6:00 PM", 0);
        }
    }

    private static void showWelcomeScreen() {
        JFrame frame = new JFrame("Welcome");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JLabel welcomeLabel = new JLabel("Welcome to LA Cinemas!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton proceedButton = new JButton("Proceed to Booking System");
        proceedButton.addActionListener(e -> {
            frame.dispose(); // Close the welcome screen
            showMainMenu();  // Show the main menu
        });

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(welcomeLabel, BorderLayout.CENTER);
        panel.add(proceedButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showMainMenu() {
        JFrame frame = new JFrame("Movie Ticket Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showThankYouScreen(); // Show thank-you message on close
            }
        });

        JButton customerButton = new JButton("Customer Mode");
        JButton staffButton = new JButton("Theater Staff Mode");

        customerButton.addActionListener(e -> openCustomerMode());
        staffButton.addActionListener(e -> openStaffMode());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        panel.add(customerButton);
        panel.add(staffButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void openCustomerMode() {
        JFrame frame = new JFrame("Customer Mode");
        frame.setSize(500, 400);

        JComboBox<String> movieDropdown = new JComboBox<>(reservations.keySet().toArray(new String[0]));
        JComboBox<String> timeDropdown = new JComboBox<>();
        JTextField ticketCountField = new JTextField();

        movieDropdown.addActionListener(e -> {
            String selectedMovie = (String) movieDropdown.getSelectedItem();
            timeDropdown.removeAllItems();
            if (selectedMovie != null) {
                for (String time : reservations.get(selectedMovie).keySet()) {
                    timeDropdown.addItem(time);
                }
            }
        });

        JButton bookButton = new JButton("Book Ticket");
        bookButton.addActionListener(e -> {
            String selectedMovie = (String) movieDropdown.getSelectedItem();
            String selectedTime = (String) timeDropdown.getSelectedItem();
            String ticketCountText = ticketCountField.getText();

            if (selectedMovie != null && selectedTime != null && !ticketCountText.isEmpty()) {
                try {
                    int ticketCount = Integer.parseInt(ticketCountText);
                    if (ticketCount <= 0) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid number of tickets.");
                    } else {
                        int reservedSeats = reservations.get(selectedMovie).get(selectedTime);
                        if (reservedSeats + ticketCount > MAX_SEATS) {
                            JOptionPane.showMessageDialog(frame,
                                    "Not enough seats available! Only " +
                                            (MAX_SEATS - reservedSeats) + " seats left.");
                        } else {
                            reservations.get(selectedMovie).put(selectedTime, reservedSeats + ticketCount);
                            int totalPrice = ticketCount * TICKET_PRICE;

                            // Show bill in sheet form
                            showBill(selectedMovie, selectedTime, ticketCount, reservedSeats + ticketCount, totalPrice);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for tickets.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a movie, time, and enter the number of tickets.");
            }
        });

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.add(new JLabel("Select Movie:"));
        panel.add(movieDropdown);
        panel.add(new JLabel("Select Showtime:"));
        panel.add(timeDropdown);
        panel.add(new JLabel("Number of Tickets:"));
        panel.add(ticketCountField);
        panel.add(bookButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void openStaffMode() {
        JFrame frame = new JFrame("Theater Staff Mode");
        frame.setSize(400, 300);

        JTextField movieField = new JTextField();
        JTextField timeField = new JTextField();
        JButton addButton = new JButton("Add Showtime");

        addButton.addActionListener(e -> {
            String movie = movieField.getText();
            String time = timeField.getText();
            if (!movie.isEmpty() && !time.isEmpty()) {
                reservations.putIfAbsent(movie, new HashMap<>());
                reservations.get(movie).put(time, 0);
                JOptionPane.showMessageDialog(frame, "Showtime added for " + movie + " at " + time + "!");
                movieField.setText("");
                timeField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter both movie name and time.");
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.add(new JLabel("Movie Name:"));
        panel.add(movieField);
        panel.add(new JLabel("Showtime (e.g., 7:00 PM):"));
        panel.add(timeField);
        panel.add(addButton);

        frame.add(panel);
        frame.setLayout(new GridLayout(3, 1));
        frame.setVisible(true);
    }

    private static void showBill(String movie, String time, int tickets, int reservedSeats, int total) {
        JFrame frame = new JFrame("Bill");
        frame.setSize(500, 200);

        String[] columnNames = {"Movie", "Showtime", "Tickets", "Total Amount", "Reserved Seats"};
        String[][] data = {
                {movie, time, String.valueOf(tickets), "₹" + total, reservedSeats + "/" + MAX_SEATS}
        };

        JTable table = new JTable(data, columnNames);
        table.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private static void showThankYouScreen() {
        JOptionPane.showMessageDialog(null, "Thank you for using LA Cinemas!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
    }
}
