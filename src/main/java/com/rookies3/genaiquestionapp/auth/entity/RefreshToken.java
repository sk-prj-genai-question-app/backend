package com.rookies3.genaiquestionapp.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String email;

    private String refreshToken;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
}
