package com.danshow.danshowserver.service.video_service;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.modelmapper.internal.util.Assert;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class VideoFileUtils {

    private String ffmpegPath = "/usr/local/bin/ffmpeg";
    //private String ffmpegPath = "C:/Program Files/ffmpeg-4.4-full_build/bin/ffmpeg";
    private String ffprobePath = "/usr/local/bin/ffprobe";
    //private String ffprobePath = "C:/Program Files/ffmpeg-4.4-full_build/bin/ffprobe";

    private FFmpeg fFmpeg;

    private FFprobe fFprobe;

    @PostConstruct
    public void init() {
        try {
            fFmpeg = new FFmpeg(ffmpegPath);
            Assert.isTrue(fFmpeg.isFFmpeg());

            fFprobe = new FFprobe(ffprobePath);
            Assert.isTrue(fFprobe.isFFprobe());

            log.debug("VideoFIleUtils init complete");

        }catch (Exception e) {
            log.error("VideoFileUtils init fail", e);
        }
    }

    public List<String> splitFile(String inputPath, String originalFileName, String outputPath , Integer chunkNumber) throws IOException {
        FFmpegProbeResult probeResult = fFprobe.probe(inputPath);

        Double totalDuration = probeResult.getFormat().duration;
        Double streamSize = totalDuration / chunkNumber;

        String originalFileNameWithoutExtension = originalFileName.substring(0,originalFileName.indexOf("."));
        String originalFileNameWithoutExtensionWithUUID = UUID.randomUUID().toString() + "-" + originalFileNameWithoutExtension;

        List<String> splitFileList = new ArrayList<String>();
        int startPoint = 0;

        File fileJoinPath = new File(outputPath + "/" +originalFileNameWithoutExtension);

        if (!fileJoinPath.exists()) {
            fileJoinPath.mkdirs();
            log.info("Created Directory -> "+ fileJoinPath.getAbsolutePath());
        }

        for(int i = 1; i<=chunkNumber; i++) {

            log.info("output path : " + fileJoinPath);

            String totalPath = fileJoinPath + "/"+originalFileNameWithoutExtensionWithUUID+"_"+i+".mp4";

            FFmpegBuilder builder = new FFmpegBuilder()
                    .overrideOutputFiles(true)
                    .addInput(inputPath)
                    .addExtraArgs("-ss", String.valueOf(startPoint))
                    .addExtraArgs("-t", String.valueOf(streamSize))
                    .addOutput(totalPath)
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);
            executor.createJob(builder, p -> {
                if(p.isEnd()) {
                    log.info("split completed processed" );
                }
            }).run();

            log.info("split done");

            createTxt(totalPath, fileJoinPath.getAbsolutePath(), originalFileNameWithoutExtensionWithUUID);

            splitFileList.add(totalPath);
            startPoint += streamSize;
        }
        return splitFileList;
    }

    /**
     * @param inputPath split과 다르게 inputpath에 만듭니다.
     * @param originalFileName
     * @throws IOException
     */
    public void integrateFiles(String inputPath,String originalFileName) throws IOException {

        String fileList = inputPath + "/" + originalFileName+".txt";

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .addInput(fileList)
                .addExtraArgs("-f","concat")
                .addExtraArgs("-safe", "0")
                .addOutput(inputPath + "/" + originalFileName + "_merged.mp4")
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);
        executor.createJob(builder).run();
    }

    //로컬에 있는 파일로부터 썸네일 생성
    public String extractThumbnail(String inputPath,String originalFileName, String outputPath) throws IOException {

        String originalFileNameWithoutExtension = originalFileName.substring(0,originalFileName.indexOf("."));
        outputPath = outputPath + "/" +originalFileNameWithoutExtension
                + "/" + originalFileNameWithoutExtension + "_thumbnail.gif";

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(inputPath)
                .addExtraArgs("-ss", "00:00:01")
                .addOutput(outputPath)
                .setFrames(1)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);		// FFmpeg 명령어 실행을 위한 FFmpegExecutor 객체 생성
        executor.createJob(builder).run();
        return outputPath;
    }
    
    // 멀티파일을 받아서 로컬에 저장하고 썸네일 생성
    public String extractThumbnail(MultipartFile video) throws IOException {
        String originalFileName = video.getOriginalFilename();
        String outputPath = System.getProperty("user.dir") + "/files";
        String inputPath = System.getProperty("user.dir") + "/files/"
                +originalFileName.substring(0,originalFileName.indexOf("."))+"/"+originalFileName;
        video.transferTo(new File(inputPath));
        return extractThumbnail(inputPath, originalFileName, outputPath);
    }

    //멀티파일과 저장할 파일 이름을 받아서 로컬에 저장하고 썸네일 생성
    public String extractThumbnail(MultipartFile video, String originalFileName) throws IOException {
        String outputPath = System.getProperty("user.dir") + "/files";
        String inputPath = System.getProperty("user.dir") + "/files/"
                +originalFileName.substring(0,originalFileName.indexOf("."))+"/"+originalFileName;
        video.transferTo(new File(inputPath));
        return extractThumbnail(inputPath, originalFileName, outputPath);
    }

    public void createTxt(String totalFilePath, String fileJoinPath, String originalFileNameWithoutExtension) {

        String fileName = fileJoinPath + "/" + originalFileNameWithoutExtension + ".txt";

        try{
            File file = new File(fileName);

            FileWriter fw = new FileWriter(file, true);

            fw.write("file " + "'" + totalFilePath + "'\n");
            fw.flush();
            fw.close();
        }catch (Exception e) {
            log.error("txt file creation fail : " + e);
        }
    }

    public void deleteFile(String filePath) {
        File deleteFile = new File(filePath);
        if(deleteFile.exists()) {
            deleteFile.delete();
        }
    }
}
