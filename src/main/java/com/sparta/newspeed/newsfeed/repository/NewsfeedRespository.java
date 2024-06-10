package com.sparta.newspeed.newsfeed.repository;

import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewsfeedRespository extends JpaRepository<Newsfeed, Long> {
    Optional<Newsfeed> findByNewsFeedSeqAndUser(Long newsfeedSeq, User user);

    Page<Newsfeed> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
