package com.sparta.newspeed.comment.service;

import com.sparta.newspeed.comment.dto.CommentRequestDto;
import com.sparta.newspeed.comment.dto.CommentResponseDto;
import com.sparta.newspeed.comment.entity.Comment;
import com.sparta.newspeed.comment.repository.CommentRepository;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.service.NewsfeedService;
import com.sparta.newspeed.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final NewsfeedService newsfeedService;
    private final CommentRepository commentRepository;

    public CommentResponseDto createComment(
            long newsfeedSeq,
            CommentRequestDto requestDto,
            User user) {

        // DB에 존재하는 Newsfeed
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsfeedSeq);

        // Entity 생성
        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .user(user)
                .newsfeed(newsfeed)
                .build();

        // DB에 저장
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    public List<CommentResponseDto> findAll(long newsfeedSeq) {
        // DB에 존재하는 comment list
        List<Comment> commentList = commentRepository.findByNewsfeedSeq(newsfeedSeq);

        return commentList.stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    @Transactional
    public CommentResponseDto updateComment(
            long newsfeedSeq,
            long commentSeq,
            CommentRequestDto requestDto,
            User user) {

        // DB에 존재하는 Newsfeed
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsfeedSeq);

        // DB에 존재하는 Comment
        Comment comment = findComment(commentSeq);

        // 사용자 일치 여부 판별
        if (!Objects.equals(comment.getUser().getUserName(), user.getUserName())) {
            throw new CustomException(ErrorCode.COMMENT_NOT_USER);
        }

        // Comment 수정
        comment.update(requestDto);

        return new CommentResponseDto(comment);
    }

    public void deleteComment(
            long newsfeedSeq,
            long commentSeq,
            CommentRequestDto requestDto,
            User user) {

        // DB에 존재하는 Newsfeed
        Newsfeed newsfeed = newsfeedService.findNewsfeed(newsfeedSeq);

        // DB에 존재하는 Comment
        Comment comment = findComment(commentSeq);

        // 사용자 일치 여부 판별
        if (!Objects.equals(comment.getUser().getUserName(), user.getUserName())) {
            throw new CustomException(ErrorCode.COMMENT_NOT_USER);
        }

        // Comment 삭제
        commentRepository.delete(comment);
    }

    private Comment findComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }
}