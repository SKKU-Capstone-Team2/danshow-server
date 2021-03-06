package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.config.auth.TokenProvider;
import com.danshow.danshowserver.domain.user.Role;
import com.danshow.danshowserver.service.user_service.DancerService;
import com.danshow.danshowserver.service.user_service.MemberService;
import com.danshow.danshowserver.web.dto.user.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1.User"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final MemberService memberService;
    private final DancerService dancerService;
    private final TokenProvider tokenProvider;

    @ApiOperation(value = "로그인" , notes = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("user/login")
    public ResponseEntity<String> login(
            @ApiParam(value = "이메일", required = true) @RequestBody LoginDto loginDto) {
        String Jwt = memberService.login(loginDto);
        if(Jwt.equals("")) {
            return new ResponseEntity<>("Fail to login", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(Jwt, HttpStatus.OK);
    }


    @ApiOperation(value = "Update Member" , notes = "Member can update the profile")
    @PostMapping("user/member-update")
    public ResponseEntity<String> memberUpdate(@ApiParam(value = "수정 할 정보", required = false) @RequestBody MemberUpdateRequestDto updateRequestDto,
                                               @ApiParam(required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt) {
        String email = tokenProvider.getUserPk(Jwt);
        memberService.update(updateRequestDto, email);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "To Dancer",notes = "Change the type of user - member to dancer")
    @PostMapping("user/member-to-dancer")
    public ResponseEntity<String> memberToDancer(@ApiParam(required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt) {
        String email = tokenProvider.getUserPk(Jwt);
        Long result = memberService.toDancer(email);
        if(result>=0)
            return new ResponseEntity<>("success", HttpStatus.OK);
        return new ResponseEntity<>("already dancer", HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "Update Dancer",notes = "Update the profile of dancer")
    @PostMapping("user/dancer-update")
    public ResponseEntity<String> dancerUpdate(@RequestBody DancerUpdateRequestDto updateRequestDto,
                                               @ApiParam(required = true) @RequestHeader(value="X-AUTH-TOKEN") String Jwt) {
        String email = tokenProvider.getUserPk(Jwt);
        dancerService.update(updateRequestDto, email);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "유저 정보 조회", notes = "유저의 정보를 조회합니다.")
    @GetMapping("user/info/{id}") //TODO 이메일로 조회할지?
    public ResponseEntity<MemberResponseDto> getUserInfo(@ApiParam(value = "유저 식별")@PathVariable Long id) {
        return new ResponseEntity<>(memberService.findById(id),HttpStatus.OK);
    }

    @ApiOperation(value = "회원가입", notes = "넘겨 준 정보로 회원가입을 합니다.")
    @PostMapping("user/sign-up")
    public ResponseEntity<String> save(@RequestBody MemberSaveRequestDto requestDto) {
        try {
            Long result = memberService.save(requestDto);
            if(result < 0) {
                return new ResponseEntity<>("Same email is already exist.",HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Need correct information", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>("Sign up success",HttpStatus.OK);
    }
}
