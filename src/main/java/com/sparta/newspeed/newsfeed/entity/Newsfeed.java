package com.sparta.newspeed.newsfeed.entity;

import com.sparta.newspeed.common.Timestamped;
import com.sparta.newspeed.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "newsfeed")
public class Newsfeed extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "newsfeed_seq")
    private Long newsFeedSeq;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "remain_member")
    private int remain_member;

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private User user;

    @OneToOne
    @JoinColumn(name = "ott_seq")
    private Ott ott;
}
