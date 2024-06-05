package com.sparta.newspeed.newsfeed.repository;

import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsfeedRespository extends JpaRepository<Newsfeed, Long> {
    Optional<List<Newsfeed>> findAllByOrderByCreatedAtDesc();

    Optional<Newsfeed> findByNewsFeedSeqAndUser(Long newsfeedSeq, User user);
}
