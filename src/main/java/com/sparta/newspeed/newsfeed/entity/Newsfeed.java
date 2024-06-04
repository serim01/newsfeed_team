package com.sparta.newspeed.newsfeed.entity;

import com.sparta.newspeed.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Newsfeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "newsfeed_seq")
    private Long newsFeedSeq;

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private User user;
}
