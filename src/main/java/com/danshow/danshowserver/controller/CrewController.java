package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.config.auth.TokenProvider;
import com.danshow.danshowserver.service.CrewService;
import com.danshow.danshowserver.web.dto.Thumbnail;
import com.danshow.danshowserver.web.dto.crew.CrewResponseDto;
import com.danshow.danshowserver.web.dto.crew.CrewSaveRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Api(tags = {"3.Crew"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class CrewController {
    private final CrewService crewService;
    private final TokenProvider tokenProvider;

    @ApiOperation(value = "크루 생성" , notes = "댄서가 크루를 생성합니다.")
    @PostMapping("crew/save")
    public ResponseEntity<String> crewSave(@RequestBody CrewSaveRequestDto crewSaveRequestDto,
                                           @RequestPart MultipartFile image, //TODO
                                           @RequestHeader(value="X-AUTH-TOKEN") String Jwt) {
        try {
            crewService.save(image,crewSaveRequestDto,tokenProvider.getUserPk(Jwt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "크루 업데이트" , notes = "댄서가 관리하는 크루를 업데이트 합니다.")
    @PostMapping("crew/update")
    public ResponseEntity<String> crewUpdate(@RequestBody CrewSaveRequestDto crewSaveRequestDto,
                                             @RequestPart MultipartFile image,
                                             @RequestHeader(value="X-AUTH-TOKEN") String Jwt) throws IOException {
        crewService.update(image, crewSaveRequestDto,tokenProvider.getUserPk(Jwt));
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "크루 조회", notes = "크루 정보를 조회합니다.")
    @GetMapping("crew/info/{id}")
    public ResponseEntity<CrewResponseDto> getCrewInfo(@ApiParam(value = "크루 식별")@PathVariable Long id) {
        return new ResponseEntity<>(crewService.findById(id), HttpStatus.OK);
    }
    
    @ApiOperation(value = "크루 메인페이지", notes = "크루 6개의 썸네일을 제공합니다")
    @GetMapping("crew/main")
    public ResponseEntity<List<Thumbnail>> crewMain() {
        return new ResponseEntity<>(crewService.crewMainList(), HttpStatus.OK);
    }

    @ApiOperation(value = "테스트 하려고 만든 메서드", notes = "테스트하려고만든메서드")
    @PostMapping("/test")
    public ResponseEntity<String> testController(String testValue) {
        return new ResponseEntity<String>(testValue,HttpStatus.OK );
    }
}
