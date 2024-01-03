package com.ll.medium.domain.base.genFile.controller;

import com.ll.medium.domain.base.genFile.entity.GenFile;
import com.ll.medium.domain.base.genFile.service.GenFileService;
import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageUploadController {

    private final GenFileService genFileService;
    private final Rq rq;

    @PostMapping("/upload_image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {

        SiteMember member = rq.getMember();
        // 이미지 파일을 저장하고 GenFile 객체를 반환
        GenFile savedFile = genFileService.saveImageByPost(member, file);

        // 클라이언트에 반환할 이미지 URL 생성
        String imageUrl = savedFile.getUrl();

        // ResponseEntity를 사용하여 JSON 형식의 응답을 반환
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}
