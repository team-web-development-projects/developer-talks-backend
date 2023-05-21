package com.dtalks.dtalks.base.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String path;

    @Column(nullable = false)
    private String inputName;

    @Column(nullable = false)
    private String storeName;
}
