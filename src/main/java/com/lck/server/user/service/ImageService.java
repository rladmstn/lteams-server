package com.lck.server.user.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
	@Value("${aws_bucket_name}")
	private String bucket;

	private final AmazonS3 amazonS3;

	public String saveImage(MultipartFile multipartFile){
		if (multipartFile == null) return null;
		String originalFilename = multipartFile.getOriginalFilename();
		String fileName = UUID.randomUUID().toString().concat(Objects.requireNonNull(originalFilename));
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(multipartFile.getContentType());
		metadata.setContentLength(multipartFile.getSize());

		try (InputStream inputStream = multipartFile.getInputStream()){
			amazonS3.putObject(bucket,fileName,inputStream,metadata);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return amazonS3.getUrl(bucket,fileName).toString();
	}

	public void deleteImage(String imageUrl){
		String splitStr = ".com/";
		String fileName = imageUrl.substring(imageUrl.lastIndexOf(splitStr) + splitStr.length());
		String file = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
		amazonS3.deleteObject(bucket,file);
	}
}
