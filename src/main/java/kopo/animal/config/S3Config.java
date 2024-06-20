package kopo.animal.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${naver.access_key_id}")
    private String naverClientId;
    @Value("${naver.secret_key}")
    private String naverSecretKey;
    private String region = "kr-standard";

    @Bean
    public AmazonS3Client amazonS3Client() {
        // AWS 계정의 인증 정보를 기반으로 BasicAWSCredentials 객체 생성
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(naverClientId, naverSecretKey);

        // AmazonS3 클라이언트를 빌더를 사용하여 생성
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region) // 사용할 AWS 리전 설정
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)) // 생성한 인증 정보 제공자에 설정
                .build(); // AmazonS3 클라이언트를 빌드하여 반환
    }
}
