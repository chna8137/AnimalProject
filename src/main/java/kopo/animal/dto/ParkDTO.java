package kopo.animal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;


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
        String coursSpotLo // 경로지점경도
) {
}
