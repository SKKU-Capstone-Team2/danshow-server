package com.danshow.danshowserver.web.dto.user;

import com.danshow.danshowserver.domain.user.Dancer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DancerUpdateRequestDto {
    private String dancer_description;

    @Builder
    public DancerUpdateRequestDto(String dancer_description) {
        this.dancer_description = dancer_description;
    }

    public Dancer toEntity(Dancer dancer) {
        if(dancer_description!=null)
            dancer.setDancer_description(dancer_description);
        return dancer;
    }
}
