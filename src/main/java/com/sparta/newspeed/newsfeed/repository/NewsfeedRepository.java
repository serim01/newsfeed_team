package com.sparta.newspeed.newsfeed.repository;

import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 임시 구현
@Repository
public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long> {
}