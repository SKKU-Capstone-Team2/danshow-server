package com.danshow.danshowserver.domain.video;

import com.danshow.danshowserver.domain.BaseTimeEntity;
import com.danshow.danshowserver.domain.user.Dancer;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public class Video extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="video_id")
    private Long id;

    private String title;
    private Integer difficulty;
    private String video_address;
    private String gender;
    private String directory;
    private String genre;
    private Integer click;
    private Integer length;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Dancer dancer;

    public void setDancer(Dancer dancer) {
        this.dancer = dancer;
        //무한루프 방지
        if(!dancer.getVideoList().contains(this)) {
            dancer.getVideoList().add(this);
        }
    }
}
