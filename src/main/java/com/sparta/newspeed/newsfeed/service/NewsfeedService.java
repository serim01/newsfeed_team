package com.sparta.newspeed.newsfeed.service;

import com.sparta.newspeed.newsfeed.entity.Newsfeed;
import com.sparta.newspeed.newsfeed.repository.NewsfeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 임시 구현
@Service
@RequiredArgsConstructor
public class NewsfeedService {

    private final NewsfeedRepository newsfeedRepository;

    public Newsfeed findNewsfeed(long id) {
        return newsfeedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 Id에 맞는 뉴스피드를 찾을 수 없습니다."));
    }
}
