package kopo.animal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record ParkDTO(

        String esntlId, // 고유아이디
        String wlkCoursFlagNm, // 산책경로구분명
        String coursDc, // 경로설명
        String signguNm, // 시군구명
        String coursLtCn, // 경로 길이 내용
        String lnmAddr, // 지번 주소
        String coursSpotLa, // 경로지점위도
        String coursSpotLo, // 경로지점경도
        GeoJsonPoint location, // 위도, 경도 묶은 거
        Double range // 반경
) {
}
