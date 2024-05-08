package kopo.animal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record HospitalDTO (

        String fcltyNm, // 동물병원 이름
        String rdnmadrNm, // 주소
        String fcltyLa, // 위도
        String fcltyLo, // 경도
        GeoJsonPoint location, // 위도, 경도 묶은 거
        Double range // 반경
){

}
