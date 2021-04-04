package com.danshow.danshowserver.domain.composite;

import com.danshow.danshowserver.domain.user.Member;
import com.danshow.danshowserver.domain.video.Video;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "Member_Video")
@IdClass(MemberVideoId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberVideo {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private boolean favorite;
    private boolean passed;
}
