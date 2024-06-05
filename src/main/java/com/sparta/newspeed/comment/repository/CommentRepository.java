package com.sparta.newspeed.comment.repository;

import com.sparta.newspeed.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNewsfeedSeq(long newsfeedSeq);
}