package com.sparta.newspeed.newsfeed.service;

import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.newsfeed.dto.NewsfeedRequestDto;
import com.sparta.newspeed.newsfeed.dto.NewsfeedResponseDto;
import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.entity.Ott;
import com.sparta.newspeed.newsfeed.repository.NewsfeedRespository;
import com.sparta.newspeed.newsfeed.repository.OttRepository;
import com.sparta.newspeed.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsfeedService {

    private final NewsfeedRespository newsfeedRespository;
    private final OttRepository ottRepository;

    public List<NewsfeedResponseDto> getNewsfeeds() {
        List<Newsfeed> newsfeedList = newsfeedRespository.findAllByOrderByCreatedAtDesc();
        if(newsfeedList.isEmpty()){
            throw new CustomException(ErrorCode.NEWSFEED_EMPTY);
        }
        return newsfeedList.stream().map(NewsfeedResponseDto::new)
                .toList();
    }
    public NewsfeedResponseDto getNewsfeed(Long newsfeedSeq) {
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq);
        return new NewsfeedResponseDto(newsfeed);
    }

    public NewsfeedResponseDto createNewsFeed(NewsfeedRequestDto request, User user) {
        Ott ott = findOtt(request);
        if (!isRemainMembersValid(ott, request.getRemainMember())) {
            throw new CustomException(ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER);
        }

        // Newsfeed 엔티티 생성 및 저장
        Newsfeed newsfeed = Newsfeed.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .remainMember(request.getRemainMember())
                .user(user)
                .ott(ott)
                .like(0L)
                .build();

        Newsfeed saveNewsfeed = newsfeedRespository.save(newsfeed);

        return new NewsfeedResponseDto(saveNewsfeed);
    }

    @Transactional
    public NewsfeedResponseDto updateNewsFeed(Long newsfeedSeq, NewsfeedRequestDto request, User user) {
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq,user);
        Ott ott = findOtt(request);
        if (!isRemainMembersValid(ott, request.getRemainMember())) {
            throw new CustomException(ErrorCode.NEWSFEED_REMAIN_MEMBER_OVER);
        }
        newsfeed.updateNewsfeed(request,ott);

        return new NewsfeedResponseDto(newsfeed);
    }

    public void deleteNewsFeed(Long newsfeedSeq, User user) {
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq, user);
        newsfeedRespository.delete(newsfeed);
    }

    // 좋아요 증감 함수
    @Transactional
    public void increaseNewsfeedLike(Long newsfeedSeq){
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq);
        newsfeed.increaseLike();
    }

    @Transactional
    public void decreaseNewsfeedLike(Long newsfeedSeq){
        Newsfeed newsfeed = findNewsfeed(newsfeedSeq);
        newsfeed.decreaseLike();
    }

    //조회는 id 값만 존재하다면 user 상관없이 조회되어야함.
    public Newsfeed findNewsfeed(Long newsfeedSeq) {
        return newsfeedRespository.findById(newsfeedSeq)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWSFEED_NOT_FOUND));

    }

    // 좋아요 유효성 검사
    public void validateNewsfeedLike(Long userId, Long newsfeedSeq) {
        Newsfeed newsfeed = newsfeedRespository.findById(newsfeedSeq).orElseThrow(() ->
                new CustomException(ErrorCode.NEWSFEED_NOT_FOUND));
        if (newsfeed.getUser().getUserSeq().equals(userId)) {
            throw new CustomException(ErrorCode.NEWSFEED_SAME_USER);
        }
    }

    //삭제, 수정에 필요한 newsfeed 가져오는 메서드
    private Newsfeed findNewsfeed(Long newsfeedSeq, User user) {
        if (!newsfeedRespository.existsById(newsfeedSeq)) {
            throw new CustomException(ErrorCode.NEWSFEED_NOT_FOUND);
        }
        return newsfeedRespository.findByNewsFeedSeqAndUser(newsfeedSeq, user)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWSFEED_NOT_USER));
    }

    private Ott findOtt(NewsfeedRequestDto request) {
        return ottRepository.findByOttName(request.getOttName()).
                orElseThrow(()-> new CustomException(ErrorCode.OTT_NOT_FOUND));
    }


    private boolean isRemainMembersValid(Ott ott, int remainMembers) {
        return remainMembers <= ott.getMaxMember();
    }

}
