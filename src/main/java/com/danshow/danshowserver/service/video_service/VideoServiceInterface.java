package com.danshow.danshowserver.service.video_service;

import com.danshow.danshowserver.domain.video.AttachFile;
import com.danshow.danshowserver.domain.video.post.VideoPost;
import com.danshow.danshowserver.web.dto.VideoPostSaveDto;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VideoServiceInterface {

    public void save(MultipartFile video, VideoPostSaveDto videoPostSaveDto, String userId, MultipartFile image) throws Exception;

    public AttachFile uploadFile(MultipartFile video) throws IOException;

    public AttachFile getVideo(Long id);

    public VideoPost getVideoPost(Long id);

    ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException;



}
