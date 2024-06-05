package com.sparta.newspeed.newsfeed.repository;

import com.sparta.newspeed.newsfeed.entity.Ott;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OttRepository extends JpaRepository<Ott, Long> {
    Optional<Ott> findByOttName(String ottName);
}