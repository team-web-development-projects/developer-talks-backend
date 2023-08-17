package com.dtalks.dtalks.admin.visitor.repository;

import com.dtalks.dtalks.admin.visitor.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findByDate(LocalDate date);

    Optional<Visitor> findByDateAndIpAddressesContains(LocalDate today, String ipAddress);
}
