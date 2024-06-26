package kopo.animal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record AbandonedDTO(
        String idntfyNo, // 유기 고유 번호

        String receptDe, // 접수 일자

        String plcInfo, // 발견 장소

        String state, // 상태

        String pblancIdntfuNo, // 공고 고유 번호

        String pblancBeginDe, // 공고 시작 일자

        String pblancEndDe, // 공조 종료 일자

        String species, // 품종

        String color, // 색상

        String age, // 나이

        String weight, // 체중

        String sex, // 성별

        String neut, // 중성화 여부

        String sfetrInfo, // 특징

        String shterNm, // 보호소명

        String shterTelno, // 보호소 전화번호

        String protectPlc, // 보호장소

        String refineRoadnmAddr, // 보호소 도로명 주소

        String refineLotnoAddr, // 보호소 지번주소

        String refineZipCd, // 보호소 우편번호

        String jurisdInstNm, // 관할기관

        String chrgpsnNm, // 담당자

        String chrgpsnContctNo,// 담당자 연락처

        String partclrMatr, // 특이사항

        String imageCours, // 이미지 경로

        String thumbImageCours, // 썸네일 이미지 경로

        String lat,// 위도

        String lon // 경도

) {
}
