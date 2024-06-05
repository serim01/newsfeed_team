package com.sparta.newspeed.comment.repository;

import com.sparta.newspeed.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
