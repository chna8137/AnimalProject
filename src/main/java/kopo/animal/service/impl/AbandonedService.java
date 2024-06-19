package kopo.animal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
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
import org.springframework.web.util.UriComponentsBuilder;

import javax.swing.text.Document;
import java.net.URI;
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

    @Scheduled(cron = "59 59 23 * * 0")
    @Override
    public int collectAbandoned() throws Exception {


        int res = 0;

        // 생성할 컬렉션명
        String colNm = "ANIMAL_ABANDONED";

        // 기존 컬렉션 삭제
        abandonedMapper.deleteAbandoned(colNm);

        String url = "https://openapi.gg.go.kr/AbdmAnimalProtect";
        String type = "json";
        String pSize = "1000"; // 페이지 당 최대 항목 수
        int pageNo = 1; // 초기 페이지 번호
        boolean hasMoreData = true; // 더 많은 데이터가 있는지 여부

        List<AbandonedDTO> pList = new LinkedList<>();

        while (hasMoreData) {
            String apiParam = "?KEY=" + key + "&Type=" + type + "&pSize=" + pSize + "&pIndex=" + pageNo;
            log.info("apiParam : " + apiParam);

            String json = NetworkUtil.get(url + apiParam);
            log.info("json : " + json);

            // JSON 구조를 Map 데이터 구조로 변경하기
            Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

            // 유기동물 정보를 갖고 있는 row 키의 값 가져오기
            List<Map<String, Object>> abdmAnimalProtectList = (List<Map<String, Object>>) rMap.get("AbdmAnimalProtect");

            if (abdmAnimalProtectList != null && !abdmAnimalProtectList.isEmpty()) {
                boolean hasRows = false;
                for (Map<String, Object> item : abdmAnimalProtectList) {
                    if (item.containsKey("row")) {
                        hasRows = true;
                        List<Map<String, Object>> rowList = (List<Map<String, Object>>) item.get("row");
                        for (Map<String, Object> rowMap : rowList) {
                            String idntfyNo = String.valueOf(rowMap.get("ABDM_IDNTFY_NO"));
                            String receptDe = String.valueOf(rowMap.get("RECEPT_DE"));
                            String plcInfo = String.valueOf(rowMap.get("DISCVRY_PLC_INFO"));
                            String state = String.valueOf(rowMap.get("STATE_NM"));
                            String pblancIdntfuNo = String.valueOf(rowMap.get("PBLANC_IDNTFY_NO"));
                            String pblancBeginDe = String.valueOf(rowMap.get("PBLANC_BEGIN_DE"));
                            String pblancEndDe = String.valueOf(rowMap.get("PBLANC_END_DE"));
                            String species = String.valueOf(rowMap.get("SPECIES_NM"));
                            String color = String.valueOf(rowMap.get("COLOR_NM"));
                            String age = String.valueOf(rowMap.get("AGE_INFO"));
                            String weight = String.valueOf(rowMap.get("BDWGH_INFO"));
                            String sex = String.valueOf(rowMap.get("SEX_NM"));
                            String neut = String.valueOf(rowMap.get("NEUT_YN"));
                            String sfetrInfo = String.valueOf(rowMap.get("SFETR_INFO"));
                            String shterNm = String.valueOf(rowMap.get("SHTER_NM"));
                            String shterTelno = String.valueOf(rowMap.get("SHTER_TELNO"));
                            String protectPlc = String.valueOf(rowMap.get("PROTECT_PLC"));
                            String refineRoadnmAddr = String.valueOf(rowMap.get("REFINE_ROADNM_ADDR"));
                            String refineLotnoAddr = String.valueOf(rowMap.get("REFINE_LOTNO_ADDR"));
                            String refineZipCd = String.valueOf(rowMap.get("REFINE_ZIP_CD"));
                            String jurisdInstNm = String.valueOf(rowMap.get("JURISD_INST_NM"));
                            String chrgpsnNm = String.valueOf(rowMap.get("CHRGPSN_NM"));
                            String chrgpsnContctNo = String.valueOf(rowMap.get("CHRGPSN_CONTCT_NO"));
                            String partclrMatr = String.valueOf(rowMap.get("PARTCLR_MATR"));
                            String imageCours = String.valueOf(rowMap.get("IMAGE_COURS"));
                            String thumbImageCours = String.valueOf(rowMap.get("THUMB_IMAGE_COURS"));
                            String lat = String.valueOf(rowMap.get("REFINE_WGS84_LAT"));
                            String lon = String.valueOf(rowMap.get("REFINE_WGS84_LOGT"));

                            AbandonedDTO pDTO = AbandonedDTO.builder()
                                    .idntfyNo(idntfyNo)
                                    .receptDe(receptDe)
                                    .plcInfo(plcInfo)
                                    .state(state)
                                    .pblancIdntfuNo(pblancIdntfuNo)
                                    .pblancBeginDe(pblancBeginDe)
                                    .pblancEndDe(pblancEndDe)
                                    .species(species)
                                    .color(color)
                                    .age(age)
                                    .weight(weight)
                                    .sex(sex)
                                    .neut(neut)
                                    .sfetrInfo(sfetrInfo)
                                    .shterNm(shterNm)
                                    .shterTelno(shterTelno)
                                    .protectPlc(protectPlc)
                                    .refineRoadnmAddr(refineRoadnmAddr)
                                    .refineLotnoAddr(refineLotnoAddr)
                                    .refineZipCd(refineZipCd)
                                    .jurisdInstNm(jurisdInstNm)
                                    .chrgpsnNm(chrgpsnNm)
                                    .chrgpsnContctNo(chrgpsnContctNo)
                                    .partclrMatr(partclrMatr)
                                    .imageCours(imageCours)
                                    .thumbImageCours(thumbImageCours)
                                    .lat(lat)
                                    .lon(lon)
                                    .build();

                            pList.add(pDTO);
                        }
                    }
                }
                if (!hasRows) {
                    hasMoreData = false;
                } else {
                    pageNo++;
                }
            } else {
                hasMoreData = false;
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
