package kopo.animal.service.impl;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kopo.animal.dto.FileDTO;
import kopo.animal.service.IS3Service;
import kopo.animal.util.DateUtil;
import kopo.animal.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service implements IS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${naver.access_key_id}")
    private String naverClientId;

    @Value("${naver.secret_key}")
    private String naverSecretKey;

    // Object Storage 접근 public IP
    final String endPoint = "https://kr.object.ncloudstorage.com";

    final String bucketName = "animalfile";

    @Override
    public FileDTO uploadFile(MultipartFile mf, String ext) throws Exception {

        log.info(this.getClass().getName() + ".uploadFile 서비스 시작!");

        // 데이터 저장을 위한 경로 생성
        String uploadFilePath = FileUtil.mkdirForData();
        // 업로드할 파일명 생성 (날짜시간 + 확장자)
        String uploadFileName = DateUtil.getDateTime("yyyyMMddHHmmss") + ext;
        String uploadFileUrl = "";  // 업로드된 파일 URL 초기화

        // 업로드할 파일의 메타데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(mf.getSize()); // 파일 크기 설정
        objectMetadata.setContentType(mf.getContentType()); // 파일 타입 설정

        try (InputStream inputStream = mf.getInputStream()) {

            // S3에 저장될 객체 키 생성
            String keyName = uploadFilePath + uploadFileName; // ex) 구분/년/월/일/파일.확장자
            // S3에 접근할 URL 생성
            uploadFileUrl = endPoint + "/" + bucketName + keyName;

            log.info("uploadFileName : " + uploadFileName);
            log.info("uploadFilePath : " + uploadFilePath);
            log.info("keyName : " + keyName);

            // AWS 인증 정보 설정
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(naverClientId, naverSecretKey);
            // Amazon S3 클라이언트 생성 및 엔드포인트 설정
            AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
            amazonS3Client.setEndpoint(endPoint);

            // S3에 폴더 및 파일 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata).
                            withCannedAcl(CannedAccessControlList.PublicRead));  // 공개권한 부여


            // S3에 업로드한 폴더 및 파일 URL
            uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString();

        } catch (IOException e) {
            e.printStackTrace();
            // 파일 업로드 실패 시 로그 출력
            log.error("Filed upload failed", e);

        }

        // 업로드된 파일 정보를 담은 FileDTO 객체 반환
        return FileDTO.builder()
                .fileUrl(uploadFileUrl)
                .fileName(uploadFileName)
                .build();

    }

}
