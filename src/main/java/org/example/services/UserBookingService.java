package org.example.services;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserBookingService {
    private User user;

    private List<User> userList;

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String USER_FILE_PATH = "src/main/resources/localDB/users.json";

    public UserBookingService(User user1) throws IOException {
        loadUserListFromFile();

        // Find user from users.json based on name
        Optional<User> foundUser = userList.stream()
                .filter(u -> u.getName().equals(user1.getName()))
                .findFirst();

        if (foundUser.isPresent()) {
            this.user = foundUser.get();  // ✅ Set user object correctly
            System.out.println("✅ User loaded: " + user.getName() + " | ID: " + user.getUserId());

            if (this.user.getUserId() == null) {
                this.user.setUserId(UUID.randomUUID().toString()); // Assign userId if missing
                updateUserBooking(); // Save updated userId
            }
        } else {
            System.out.println("❌ ERROR: User not found in database.");
            throw new IOException("User not found.");
        }
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {
        });
    }

    public Boolean signUp(User user1) {
        try {
            if (user1.getUserId() == null) {
                user1.setUserId(UUID.randomUUID().toString());
            }
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1->{
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public void fetchBookings(){
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets(userFetched.get().getName());
        }
    }

    public List<Train> getTrains(String source, String destination) {
        try {
            TrainService trainService = new TrainService();
            List<Train> foundTrains = trainService.searchTrains(source, destination);

            if (foundTrains.isEmpty()) {
                System.out.println("🚨 DEBUG: No trains found for " + source + " → " + destination);
            } else {
                System.out.println("✅ DEBUG: Found " + foundTrains.size() + " trains.");
            }

            return foundTrains;
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }



    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public List<List<Integer>> fetchSeats(Train train) {
        if (train == null) {
            System.out.println("❌ ERROR: Train object is null in fetchSeats()!");
            return null;
        }

        List<List<Integer>> seats = train.getSeats();

        if (seats == null) {
            System.out.println("❌ ERROR: No seat data found for Train ID: " + train.getTrainId());
        }

        return seats;
    }


    public Boolean bookTrainSeat(Train train, int row, int seat) {
        if (this.user == null) {
            System.out.println("❌ ERROR: No user is logged in! Cannot book a seat.");
            return false;
        }

        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();

            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1); // Mark seat as booked
                    train.setSeats(seats);
                    trainService.updateTrain(train); // Save train seat update

                    // Create ticket
                    Ticket newTicket = new Ticket(UUID.randomUUID().toString(), user.getUserId(), "bangalore", "delhi", new Date(), train);

                    // Add ticket to user
                    user.getTicketBooked().add(newTicket);

                    // Update user list and save to file
                    updateUserBooking();

                    return true; // Booking successful
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false; // Booking failed
    }


    private void updateUserBooking() throws IOException {
        if (user == null) {
            System.out.println("❌ ERROR: User object is null.");
            return;
        }

        if (user.getUserId() == null) {
            System.out.println("❌ ERROR: User ID is null. Assigning a new one.");
            user.setUserId(UUID.randomUUID().toString()); // Fix missing userId
        }

        for (User u : userList) {
            if (u.getUserId().equals(user.getUserId())) {
                u.setTicketBooked(user.getTicketBooked()); // Update ticket list
                System.out.println("✅ Updated Booking for User: " + u.getName());
                System.out.println("New Ticket List: " + u.getTicketBooked());
                break;
            }
        }
        saveUserListToFile();
    }



    public Boolean cancelBooking(String ticketId) {
        if (this.user == null) {
            System.out.println("❌ ERROR: No user is logged in! Cannot cancel booking.");
            return false;
        }

        // Find the ticket to cancel
        Optional<Ticket> ticketToRemove = user.getTicketBooked().stream()
                .filter(ticket -> ticket.getTicketId().equals(ticketId))
                .findFirst();

        if (ticketToRemove.isPresent()) {
            user.getTicketBooked().remove(ticketToRemove.get());

            try {
                updateUserBooking(); // Save updated booking list
                System.out.println("✅ Ticket with ID " + ticketId + " has been canceled.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("❌ No ticket found with ID: " + ticketId);
            return false;
        }
    }

}

