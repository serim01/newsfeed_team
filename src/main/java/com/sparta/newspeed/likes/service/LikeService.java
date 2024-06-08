package com.sparta.newspeed.likes.service;

import com.sparta.newspeed.comment.entity.Comment;
import com.sparta.newspeed.comment.repository.CommentRepository;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.likes.entity.Like;
import com.sparta.newspeed.likes.entity.LikeEnum;
import com.sparta.newspeed.likes.repository.LikeRepository;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.repository.NewsfeedRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;
    private final NewsfeedRespository newsfeedRespository;
    private final CommentRepository commentRepository;

    // 게시물, 댓글별 좋아요 수 count
    public int getLikesCount(Long contentId, LikeEnum contentType) {
        return likeRepository.countByContentIdAndContentType(contentId, contentType);
    }

    // 게시물, 댓글별 좋아요 toggle 기능
    public ResponseEntity<String> toggleLike(Long userId, Long contentId, LikeEnum contentType) {
        validateLikeAction(userId, contentId, contentType);

        Optional<Like> likeOptional = findLike(userId, contentId, contentType);
        if (likeOptional.isPresent()) {
            likeRepository.delete(likeOptional.get());
            return ResponseEntity.ok("Like removed");
        } else {
            Like like = new Like(userId, contentId, contentType);
            likeRepository.save(like);
            return ResponseEntity.ok("Like added");
        }
    }

    // 좋아요 유효성 검사
    private void validateLikeAction(Long userId, Long contentId, LikeEnum contentType) {
        if (contentType == LikeEnum.NEWSFEED) {
            Newsfeed newsfeed = newsfeedRespository.findById(contentId).orElseThrow(() ->
                    new CustomException(ErrorCode.NEWSFEED_NOT_FOUND));
            if (newsfeed.getUser().getUserSeq().equals(userId)) {
                throw new CustomException(ErrorCode.NEWSFEED_SAME_USER);
            }
        } else if (contentType == LikeEnum.COMMENT) {
            Comment comment = commentRepository.findById(contentId).orElseThrow(() ->
                    new CustomException(ErrorCode.COMMENT_NOT_FOUND));
            if (comment.getUser().getUserSeq().equals(userId)) {
                throw new CustomException(ErrorCode.COMMENT_SAME_USER);
            }
        }
    }

    // 좋아요 객체 찾기
    private Optional<Like> findLike(Long userId, Long contentId, LikeEnum contentType) {
        return likeRepository.findByUserIdAndContentIdAndContentType(userId, contentId, contentType);
    }

}
