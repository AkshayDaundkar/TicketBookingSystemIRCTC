package org.example;

import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.UserBookingService;
import org.example.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;
        Train trainSelectedForBooking = null;

        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.out.println("There is something wrong: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        while (option != 7) {
            System.out.println("Choose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Enter the username to signup");
                    String nameToSignUp = scanner.next();
                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = scanner.next();
                    User userToSignup = new User(nameToSignUp, passwordToSignUp, UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(), UUID.randomUUID().toString());
                    userBookingService.signUp(userToSignup);
                    System.out.println("Register Successful!");
                    break;

                case 2:
                    System.out.println("Enter the username to Login");
                    String nameToLogin = scanner.next();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = scanner.next();

                    try {
                        userBookingService = new UserBookingService(new User(nameToLogin, passwordToLogin, null, new ArrayList<>(), null));
                        Boolean value = userBookingService.loginUser();

                        if (value) {
                            System.out.println("✅ Logged In Successfully!");
                        } else {
                            System.out.println("❌ Invalid Credentials. Try Again.");
                            userBookingService = null;
                        }
                    } catch (IOException ex) {
                        System.out.println("❌ ERROR: " + ex.getMessage());
                        userBookingService = null;
                    }
                    break;

                case 3:
                    System.out.println("Fetching your bookings...");
                    userBookingService.fetchBookings();
                    break;

                case 4:
                    System.out.println("Type your source station:");
                    String source = scanner.next();
                    System.out.println("Type your destination station:");
                    String dest = scanner.next();

                    List<Train> trains = userBookingService.getTrains(source, dest);

                    if (trains.isEmpty()) {
                        System.out.println("❌ No trains found for " + source + " → " + dest);
                        trainSelectedForBooking = null;
                        break;
                    }

                    System.out.println("Available Trains:");
                    for (int i = 0; i < trains.size(); i++) {
                        Train t = trains.get(i);
                        System.out.println((i + 1) + ". Train ID: " + t.getTrainId() + " | Number: " + t.getTrainNo());
                    }

                    System.out.println("Select a train by typing its number (1, 2, 3...)");
                    int selectedIndex = scanner.nextInt() - 1;

                    if (selectedIndex >= 0 && selectedIndex < trains.size()) {
                        trainSelectedForBooking = trains.get(selectedIndex);  // ✅ Train selection persists now
                        System.out.println("✅ Selected Train: " + trainSelectedForBooking.getTrainId());
                    } else {
                        System.out.println("❌ Invalid train selection.");
                        trainSelectedForBooking = null;
                    }
                    break;

                case 5:
                    if (trainSelectedForBooking == null) {
                        System.out.println("❌ No train selected! Please search for a train first (Option 4).");
                        break;
                    }

                    System.out.println("Select a seat out of these seats:");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);

                    if (seats == null || seats.isEmpty()) {
                        System.out.println("❌ No seats available for this train.");
                        break;
                    }

                    for (List<Integer> row : seats) {
                        for (Integer val : row) {
                            System.out.print(val + " ");
                        }
                        System.out.println();
                    }

                    System.out.println("Select the seat by typing the row and column");
                    System.out.println("Enter the row:");
                    int row = scanner.nextInt();
                    System.out.println("Enter the column:");
                    int col = scanner.nextInt();

                    System.out.println("Booking your seat....");
                    Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);

                    if (booked.equals(Boolean.TRUE)) {
                        System.out.println("✅ Booked! Enjoy your journey.");
                    } else {
                        System.out.println("❌ Can't book this seat.");
                    }
                    break;
                case 6:
                    if (userBookingService == null ) {
                        System.out.println("❌ ERROR: No user logged in! Please login first.");
                        break;
                    }

                    System.out.println("Enter the Ticket ID to cancel:");
                    String ticketIdToCancel = scanner.next();

                    Boolean isCancelled = userBookingService.cancelBooking(ticketIdToCancel);

                    if (isCancelled) {
                        System.out.println("✅ Your ticket has been successfully canceled.");
                    } else {
                        System.out.println("❌ Ticket cancellation failed or ticket not found.");
                    }
                    break;



                default:
                    break;
            }
        }
    }
}
