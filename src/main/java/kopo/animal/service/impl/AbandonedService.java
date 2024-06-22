package kopo.animal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.animal.dto.AbandonedDTO;
import kopo.animal.persistance.mapper.IAbandonedMapper;
import kopo.animal.service.IAbandonedService;
import kopo.animal.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AbandonedService implements IAbandonedService {

    private final IAbandonedMapper abandonedMapper;

    private final RestTemplate restTemplate;
    @Value("${abandoned.api_key}")
    private String key;

    // 유기동물 정보 수집 스케줄링 (매주 일요일 밤 11시 59분 59초에 실행)
    @Scheduled(cron = "59 59 23 * * 0")
    public int collectAbandoned() throws Exception {


        int res = 0;

        // 생성할 컬렉션명
        String colNm = "ANIMAL_ABANDONED";

        // 기존 컬렉션 삭제
        abandonedMapper.deleteAbandoned(colNm);

        // API 호출 관련 변수 설정
        String url = "https://openapi.gg.go.kr/AbdmAnimalProtect";
        String type = "json";
        String pSize = "1000"; // 페이지 당 최대 항목 수
        int pageNo = 1; // 초기 페이지 번호
        boolean hasMoreData = true; // 더 많은 데이터가 있는지 여부

        // 수집된 유기동물 정보를 담을 리스트
        List<AbandonedDTO> pList = new LinkedList<>();

        // 더 많은 데이터가 있을 경우 반복하여 수집
        while (hasMoreData) {
            // API 요청 파라미터 설정
            String apiParam = "?KEY=" + key + "&Type=" + type + "&pSize=" + pSize + "&pIndex=" + pageNo;
            log.info("apiParam : " + apiParam);

            // API 요청 후 응답 데이터 JSON 형태로 받기
            String json = NetworkUtil.get(url + apiParam);
            log.info("json : " + json);

            // JSON 구조를 Map 데이터 구조로 변경하기
            Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

            // 유기동물 정보를 갖고 있는 row 키의 값 가져오기
            List<Map<String, Object>> abdmAnimalProtectList = (List<Map<String, Object>>) rMap.get("AbdmAnimalProtect");

            // 유기동물 정보가 있을 경우
            if (abdmAnimalProtectList != null && !abdmAnimalProtectList.isEmpty()) {
                boolean hasRows = false;
                // 유기동물 리스트 순회
                for (Map<String, Object> item : abdmAnimalProtectList) {
                    // 항목이 "row" 키를 포함하는지 확인
                    if (item.containsKey("row")) {
                        hasRows = true;
                        // "row" 리스트 가져오기
                        List<Map<String, Object>> rowList = (List<Map<String, Object>>) item.get("row");
                        // "row" 리스트 순회
                        for (Map<String, Object> rowMap : rowList) {
                            // 유기동물 정보 추출
                            String idntfyNo = String.valueOf(rowMap.get("ABDM_IDNTFY_NO")); // 식별 번호
                            String receptDe = String.valueOf(rowMap.get("RECEPT_DE")); // 접수 일자
                            String plcInfo = String.valueOf(rowMap.get("DISCVRY_PLC_INFO")); // 발견 장소 정보
                            String state = String.valueOf(rowMap.get("STATE_NM")); // 상태명
                            String pblancIdntfuNo = String.valueOf(rowMap.get("PBLANC_IDNTFY_NO")); // 공고 식별 번호
                            String pblancBeginDe = String.valueOf(rowMap.get("PBLANC_BEGIN_DE")); // 공고 시작 일자
                            String pblancEndDe = String.valueOf(rowMap.get("PBLANC_END_DE")); // 공고 종료 일자
                            String species = String.valueOf(rowMap.get("SPECIES_NM")); // 종
                            String color = String.valueOf(rowMap.get("COLOR_NM")); // 색상
                            String age = String.valueOf(rowMap.get("AGE_INFO")); // 나이 정보
                            String weight = String.valueOf(rowMap.get("BDWGH_INFO")); // 체중 정보
                            String sex = String.valueOf(rowMap.get("SEX_NM")); // 성별
                            String neut = String.valueOf(rowMap.get("NEUT_YN")); // 중성화 여부
                            String sfetrInfo = String.valueOf(rowMap.get("SFETR_INFO")); // 특징 정보
                            String shterNm = String.valueOf(rowMap.get("SHTER_NM")); // 보호소 이름
                            String shterTelno = String.valueOf(rowMap.get("SHTER_TELNO")); // 보호소 전화번호
                            String protectPlc = String.valueOf(rowMap.get("PROTECT_PLC")); // 보호 장소
                            String refineRoadnmAddr = String.valueOf(rowMap.get("REFINE_ROADNM_ADDR")); // 도로명 주소
                            String refineLotnoAddr = String.valueOf(rowMap.get("REFINE_LOTNO_ADDR")); // 지번 주소
                            String refineZipCd = String.valueOf(rowMap.get("REFINE_ZIP_CD")); // 우편번호
                            String jurisdInstNm = String.valueOf(rowMap.get("JURISD_INST_NM")); // 관할 기관명
                            String chrgpsnNm = String.valueOf(rowMap.get("CHRGPSN_NM")); // 담당자 이름
                            String chrgpsnContctNo = String.valueOf(rowMap.get("CHRGPSN_CONTCT_NO")); // 담당자 연락처
                            String partclrMatr = String.valueOf(rowMap.get("PARTCLR_MATR")); // 특이 사항
                            String imageCours = String.valueOf(rowMap.get("IMAGE_COURS")); // 이미지 경로
                            String thumbImageCours = String.valueOf(rowMap.get("THUMB_IMAGE_COURS")); // 썸네일 이미지 경로
                            String lat = String.valueOf(rowMap.get("REFINE_WGS84_LAT")); // 위도
                            String lon = String.valueOf(rowMap.get("REFINE_WGS84_LOGT")); // 경도

                            // 유기동물 DTO 생성 및 리스트에 추가
                            AbandonedDTO pDTO = AbandonedDTO.builder()
                                    .idntfyNo(idntfyNo) // 식별 번호 설정
                                    .receptDe(receptDe) // 접수 일자 설정
                                    .plcInfo(plcInfo) // 발견 장소 정보 설정
                                    .state(state) // 상태명 설정
                                    .pblancIdntfuNo(pblancIdntfuNo) // 공고 식별 번호 설정
                                    .pblancBeginDe(pblancBeginDe) // 공고 시작 일자 설정
                                    .pblancEndDe(pblancEndDe) // 공고 종료 일자 설정
                                    .species(species) // 종 설정
                                    .color(color) // 색상 설정
                                    .age(age) // 나이 정보 설정
                                    .weight(weight) // 체중 정보 설정
                                    .sex(sex) // 성별 설정
                                    .neut(neut) // 중성화 여부 설정
                                    .sfetrInfo(sfetrInfo) // 특징 정보 설정
                                    .shterNm(shterNm) // 보호소 이름 설정
                                    .shterTelno(shterTelno) // 보호소 전화번호 설정
                                    .protectPlc(protectPlc) // 보호 장소 설정
                                    .refineRoadnmAddr(refineRoadnmAddr) // 도로명 주소 설정
                                    .refineLotnoAddr(refineLotnoAddr) // 지번 주소 설정
                                    .refineZipCd(refineZipCd) // 우편번호 설정
                                    .jurisdInstNm(jurisdInstNm) // 관할 기관명 설정
                                    .chrgpsnNm(chrgpsnNm) // 담당자 이름 설정
                                    .chrgpsnContctNo(chrgpsnContctNo) // 담당자 연락처 설정
                                    .partclrMatr(partclrMatr) // 특이 사항 설정
                                    .imageCours(imageCours) // 이미지 경로 설정
                                    .thumbImageCours(thumbImageCours) // 썸네일 이미지 경로 설정
                                    .lat(lat) // 위도 설정
                                    .lon(lon) // 경도 설정
                                    .build(); // DTO 빌드

                            pList.add(pDTO); // DTO를 리스트에 추가
                        }
                    }
                }
                if (!hasRows) {
                    hasMoreData = false;  // "row" 항목이 없으면 더 이상 데이터가 없음을 표시
                } else {
                    pageNo++; // "row" 항목이 있으면 페이지 번호 증가
                }
            } else {
                hasMoreData = false; // "row" 키가 없으면 더 이상 데이터가 없음을 표시
            }
        }

//        log.info("pList : " + pList);

        // MongoDB에 데이터 저장하기
        res = abandonedMapper.insertAbandoned(pList, colNm);

        log.info(this.getClass().getName() + ".service 유기동물 저장 End!!");

        return res;
    }



    @Override
    public List<AbandonedDTO> getAbandonedList() throws Exception {

        log.info(this.getClass().getName() + ".service 유기동물 리스트 시작!!");

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "ANIMAL_ABANDONED";

        // 유기동물 리스트 가져오기
        List<AbandonedDTO> rList = abandonedMapper.getAbandonedList(colNm);

        log.info(this.getClass().getName() + ".service 유기동물 리스트 종료!!");

        return rList;
    }

    // 2024.05.21 ( 파라미터 값 변경 밑 로직 변경 )
    @Override
    public AbandonedDTO getAbandoned(String colName, String idntfyNo) throws Exception {
        log.info(this.getClass().getName() + ".service 유기동물 상세보기 시작!");

        AbandonedDTO rDTO = abandonedMapper.getAbandoned(colName, idntfyNo);

        log.info(this.getClass().getName() + ".service 유기동물 상세보기 종료!");

        return rDTO;
    }

    @Override
    public List<AbandonedDTO> getUrgentAbandonedList(String colNm) throws Exception {
        // 모든 유기동물 리스트를 가져오기
        List<AbandonedDTO> allAbandonedList = abandonedMapper.getAbandonedList(colNm);

        // 날짜 형식을 "yyyyMMdd"로 정의
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 현재 날짜의 시작 지점을 가져오기
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayStart = cal.getTime();

        // 내일 날짜의 시작 지점을 가져오기
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrowStart = cal.getTime();

        // 마감기한이 1일 이하로 남은 유기동물만 필터링하고, 마감기한이 가까운 순으로 정렬하여 상위 6개의 데이터를 반환
        return allAbandonedList.stream()
                // 마감기한이 1일 이하로 남은 유기동물만 필터링
                .filter(dto -> {
                    try {
                        // 유기동물의 마감기한을 Date 객체로 변환
                        Date endDate = sdf.parse(dto.pblancEndDe());
                        // 마감기한이 오늘 또는 내일인 경우 true를 반환
                        return !endDate.before(todayStart) && endDate.before(tomorrowStart);
                    } catch (Exception e) {
                        // 날짜 변환 중 오류가 발생하면 로그를 남기고 false를 반환
                        log.error("Date parsing error", e);
                        return false;
                    }
                })
                // 마감기한이 가까운 순으로 정렬
                .sorted(Comparator.comparing(dto -> {
                    try {
                        // 유기동물의 마감기한을 Date 객체로 변환하여 정렬에 사용
                        return sdf.parse(dto.pblancEndDe());
                    } catch (Exception e) {
                        // 날짜 변환 중 오류가 발생하면 최대값 날짜를 반환하여 정렬에서 최하위로 배치
                        return new Date(Long.MAX_VALUE);
                    }
                }))
                // 상위 6개의 데이터를 제한
                .limit(6)
                // 결과를 리스트로 변환하여 반환
                .collect(Collectors.toList());

    }

    @Override
    public int dropAbandoned() throws Exception {

        log.info(this.getClass().getName() + "service 유기동물 삭제 시작!");

        int res = 0;

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "ANIMAL_ABANDONED";

        // 기존 수집된 유기동물 수집한 컬렉션 삭제하기
        res = abandonedMapper.deleteAbandoned(colNm);

        log.info(this.getClass().getName() + ".service 유기동물 삭제 종료!");

        return res;
    }

}
