package com.danshow.danshowserver.controller;

import com.danshow.danshowserver.service.CrewService;
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

@Api(tags = {"3.Crew"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class CrewController {
    private final CrewService crewService;

    @ApiOperation(value = "크루 생성" , notes = "댄서가 크루를 생성합니다.")
    @PostMapping("crew/save")
    public ResponseEntity<String> crewSave(@RequestBody CrewSaveRequestDto crewSaveRequestDto) {
        crewService.save(crewSaveRequestDto);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "크루 업데이트" , notes = "댄서가 관리하는 크루를 업데이트 합니다.")
    @PostMapping("crew/update")
    public ResponseEntity<String> crewUpdate(@RequestBody CrewSaveRequestDto crewSaveRequestDto) {
        crewService.update(crewSaveRequestDto);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @ApiOperation(value = "크루 조회", notes = "크루 정보를 조회합니다.")
    @GetMapping("crew/info/{id}")
    public ResponseEntity<CrewResponseDto> getCrewInfo(@ApiParam(value = "크루 식별")@PathVariable Long id) {
        return new ResponseEntity<>(crewService.findById(id), HttpStatus.OK);
    }

}