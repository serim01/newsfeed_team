package com.sparta.newspeed.comment.entity;

import com.sparta.newspeed.comment.dto.CommentRequestDto;
import com.sparta.newspeed.common.Timestamped;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comments")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq")
    private Long commentSeq;

    @NotBlank
    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private User user;

    @ManyToOne
    @JoinColumn(name = "newsfeed_seq")
    private Newsfeed newsfeed;

    public void update(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}