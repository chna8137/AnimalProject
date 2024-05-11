package kopo.animal.dto;

public record AbandonedDTO (
        String abdmIdntfyNo, // 유기 고유 번호

        String receptDe, // 접수 일자

        String discvryPlcInfo, // 발견 장소

        String stateNm, // 상태

        String pblancIdntfyNo, // 공고 고유 번호

        String pblancBeginDe, // 공고 시작 일자

        String pblancEndDe, // 공조 종료 일자

        String speciesNm, // 품종

        String colorNm, // 색상

        String ageInfo, // 나이

        String bdwghInfo, // 체중

        String sexNm, // 성별

        String neutYn, // 중성화 여부

        String sfetrInfo, // 특징

        String shterNm, // 보호소명

        String shterTelno, // 보호소 전화번호

        String protectPlc, // 보호장소

        String refineRoadmnAddr, // 보호소 도로명 주소

        String refineLotnoAddr, // 보호소 지번주소

        String refineZipCd, // 보호소 우편번호

        String jurisdInstNm, // 관할기관

        String chrgpsnNm, // 담당자

        String chrgpsnContctNo, // 담당자 연락처

        String partclrMatr, // 특이사항

        String imageCours, // 이미지 경로

        String lat, // 위도

        String lot // 경도
        
){
}
