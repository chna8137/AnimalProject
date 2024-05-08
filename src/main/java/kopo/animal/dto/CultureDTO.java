package kopo.animal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record CultureDTO (

        String fcltyNm, // 이름
        String ctgryThreeNm, // 카테고리
        String lcLa, // 위도
        String lcLo, // 경도
        String zipNo, // 우편번호
        String rdnmadrNm, // 주소
        GeoJsonPoint location, // 위도, 경도 묶은 거
        Double range // 반경
){
}
