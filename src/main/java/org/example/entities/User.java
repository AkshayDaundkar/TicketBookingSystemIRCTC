package org.example.entities;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Optional;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String name;
    private String password;
    private String hashedPassword;
    private List<Ticket> ticketBooked;
    private String userId;


    public User() {
    }
    public User(String name, String password, String hashPassword, List<Ticket> ticketBooked, String userId) {
        this.name = name;
        this.password = password;
        this.hashedPassword = hashPassword;
        this.ticketBooked = ticketBooked;
        this.userId = userId;
    }

    public void printTickets(String name){
        for(int i=0;i<ticketBooked.size();i++){
            System.out.println(ticketBooked.get(i).getTicketInfo(name));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashPassword) {
        this.hashedPassword = hashPassword;
    }

    public List<Ticket> getTicketBooked() {
        return ticketBooked;
    }

    public void setTicketBooked(List<Ticket> ticketBooked) {
        this.ticketBooked = ticketBooked;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
