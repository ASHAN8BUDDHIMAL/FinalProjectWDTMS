package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sam3_clients")
public class Client {

    @Id
    private Long clientId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "client_id")
    private UserRegistration user;

    private String clientLevel; // Optional: For example, Premium, Regular, etc.

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setUser(UserRegistration user) {
        this.user = user;
    }

    public void setClientLevel(String clientLevel) {
        this.clientLevel = clientLevel;
    }

    public Long getClientId() {
        return clientId;
    }

    public UserRegistration getUser() {
        return user;
    }

    public String getClientLevel() {
        return clientLevel;
    }
}
