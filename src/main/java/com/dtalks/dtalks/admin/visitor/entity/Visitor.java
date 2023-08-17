package com.dtalks.dtalks.admin.visitor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ElementCollection
    @CollectionTable(name = "visitor_ip_addresses", joinColumns = @JoinColumn(name = "visitor_id"))
    @Column(name = "ip_address")
    private List<String> ipAddresses;

    @Column(nullable = false)
    private int count;

    public void increaseCount() {
        this.count++;
    }

    public void setCount(int i) {
        this.count = i;
    }

    public void setDate(LocalDate today) {
        this.date = today;
    }

    public void addIpAddress(String ipAddress) {
        if (ipAddresses == null) {
            ipAddresses = new ArrayList<>();
        }
        ipAddresses.add(ipAddress);
    }

}
