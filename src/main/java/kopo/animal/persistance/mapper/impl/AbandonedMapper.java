package kopo.animal.persistance.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import kopo.animal.dto.AbandonedDTO;
import kopo.animal.persistance.mapper.AbstractMongoDBComon;
import kopo.animal.persistance.mapper.IAbandonedMapper;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbandonedMapper extends AbstractMongoDBComon implements IAbandonedMapper {

    private final MongoTemplate mongodb;

    @Override
    public int insertAbandoned(List<AbandonedDTO> pList, String colNm) throws Exception {

        log.info(this.getClass().getName() + ".mapper 유기동물 저장 시작!");

        int res = 0;

        if (pList == null) {
            pList = new LinkedList<>();
        }

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        for (AbandonedDTO pDTO : pList) {
            // 레코드 한 개씩 저장하기
            col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));
        }

        res = 1;

        log.info(this.getClass().getName() + ".mapper 유기동물 저장 종료!");

        return res;
    }

    @Override
    public List<AbandonedDTO> getAbandonedList(String colNm) throws Exception {

        log.info(this.getClass().getName() + ".mapper 유기동물 정보 가져오기 시작!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<AbandonedDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        Document projection = new Document();

        projection.append("age", "$age");
        projection.append("color", "$color");
        projection.append("idntfyNo", "$idntfyNo");
        projection.append("imageCours", "$imageCours");
        projection.append("jurisdInstNm", "$jurisdInstNm");
        projection.append("lat", "$lat");
        projection.append("lon", "$lon");
        projection.append("neut", "$neut");
        projection.append("pblancBeginDe", "$pblancBeginDe");
        projection.append("pblancEndDe", "$pblancEndDe");
        projection.append("pblancIdntfuNo", "$pblancIdntfuNo");
        projection.append("plcInfo", "$plcInfo");
        projection.append("protectPlc", "$protectPlc");
        projection.append("receptDe", "$receptDe");
        projection.append("refineLotnoAddr", "$refineLotnoAddr");
        projection.append("refineRoadnmAddr", "$refineRoadnmAddr");
        projection.append("refineZipCd", "$refineZipCd");
        projection.append("sex", "$sex");
        projection.append("sfetrInfo", "$sfetrInfo");
        projection.append("shterNm", "$shterNm");
        projection.append("shterTelno", "$shterTelno");
        projection.append("species", "$species");
        projection.append("state", "$state");
        projection.append("thumbImageCours", "$thumbImageCours");
        projection.append("weight", "$weight");
        projection.append("_id", 0);

        FindIterable<Document> rs = col.find(new Document()).projection(projection);

        for (Document doc : rs) {
            String age = CmmUtil.nvl(doc.getString("age"));
            String color = CmmUtil.nvl(doc.getString("color"));
            String idntfyNo = CmmUtil.nvl(doc.getString("idntfyNo"));
            String imageCours = CmmUtil.nvl(doc.getString("imageCours"));
            String jurisdInstNm = CmmUtil.nvl(doc.getString("jurisdInstNm"));
            String lat = CmmUtil.nvl(doc.getString("lat"));
            String lon = CmmUtil.nvl(doc.getString("lon"));
            String neut = CmmUtil.nvl(doc.getString("neut"));
            String pblancBeginDe = CmmUtil.nvl(doc.getString("pblancBeginDe"));
            String pblancEndDe = CmmUtil.nvl(doc.getString("pblancEndDe"));
            String pblancIdntfuNo = CmmUtil.nvl(doc.getString("pblancIdntfuNo"));
            String plcInfo = CmmUtil.nvl(doc.getString("plcInfo"));
            String protectPlc = CmmUtil.nvl(doc.getString("protectPlc"));
            String receptDe = CmmUtil.nvl(doc.getString("receptDe"));
            String refineLotnoAddr = CmmUtil.nvl(doc.getString("refineLotnoAddr"));
            String refineRoadnmAddr = CmmUtil.nvl(doc.getString("refineRoadnmAddr"));
            String refineZipCd = CmmUtil.nvl(doc.getString("refineZipCd"));
            String sex = CmmUtil.nvl(doc.getString("sex"));
            String sfetrInfo = CmmUtil.nvl(doc.getString("sfetrInfo"));
            String shterNm = CmmUtil.nvl(doc.getString("shterNm"));
            String shterTelno = CmmUtil.nvl(doc.getString("shterTelno"));
            String species = CmmUtil.nvl(doc.getString("species"));
            String state = CmmUtil.nvl(doc.getString("state"));
            String thumbImageCours = CmmUtil.nvl(doc.getString("thumbImageCours"));
            String weight = CmmUtil.nvl(doc.getString("weight"));

            AbandonedDTO rDTO = AbandonedDTO.builder()
                    .age(age)
                    .color(color)
                    .idntfyNo(idntfyNo)
                    .imageCours(imageCours)
                    .jurisdInstNm(jurisdInstNm)
                    .lat(lat)
                    .lon(lon)
                    .neut(neut)
                    .pblancBeginDe(pblancBeginDe)
                    .pblancEndDe(pblancEndDe)
                    .pblancIdntfuNo(pblancIdntfuNo)
                    .plcInfo(plcInfo)
                    .protectPlc(protectPlc)
                    .receptDe(receptDe)
                    .refineLotnoAddr(refineLotnoAddr)
                    .refineRoadnmAddr(refineRoadnmAddr)
                    .refineZipCd(refineZipCd)
                    .sex(sex)
                    .sfetrInfo(sfetrInfo)
                    .shterNm(shterNm)
                    .shterTelno(shterTelno)
                    .species(species)
                    .state(state)
                    .thumbImageCours(thumbImageCours)
                    .weight(weight)
                    .build();

            rList.add(rDTO);
        }

        log.info(this.getClass().getName() + ".mapper 유기동물 정보 가져오기 종료!");

        return rList;
    }

    // 2024.05.21 ( 파라미터 값 변경 및 로직 변경 )
    @Override
    public AbandonedDTO getAbandoned(String colNm, String idntfyNo) throws Exception {

        log.info(this.getClass().getName() + ".mapper 유기동물 정보 가져오기 시작!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        AbandonedDTO rDTO = null;

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // idntfyNo로 필터링 ( 추가 )
        Document query = new Document("idntfyNo", idntfyNo);

        Document projection = new Document();

        projection.append("age", "$age");
        projection.append("color", "$color");
        projection.append("idntfyNo", "$idntfyNo");
        projection.append("imageCours", "$imageCours");
        projection.append("jurisdInstNm", "$jurisdInstNm");
        projection.append("lat", "$lat");
        projection.append("lon", "$lon");
        projection.append("neut", "$neut");
        projection.append("pblancBeginDe", "$pblancBeginDe");
        projection.append("pblancEndDe", "$pblancEndDe");
        projection.append("pblancIdntfuNo", "$pblancIdntfuNo");
        projection.append("plcInfo", "$plcInfo");
        projection.append("protectPlc", "$protectPlc");
        projection.append("receptDe", "$receptDe");
        projection.append("refineLotnoAddr", "$refineLotnoAddr");
        projection.append("refineRoadnmAddr", "$refineRoadnmAddr");
        projection.append("refineZipCd", "$refineZipCd");
        projection.append("sex", "$sex");
        projection.append("sfetrInfo", "$sfetrInfo");
        projection.append("shterNm", "$shterNm");
        projection.append("shterTelno", "$shterTelno");
        projection.append("species", "$species");
        projection.append("state", "$state");
        projection.append("thumbImageCours", "$thumbImageCours");
        projection.append("weight", "$weight");
        projection.append("_id", 0);

        // find 고유번호로 필터링
        FindIterable<Document> rs = col.find(query).projection(projection);

        Document doc = rs.first();
        if (doc != null) {
            rDTO = AbandonedDTO.builder()
                    .age(CmmUtil.nvl(doc.getString("age")))
                    .color(CmmUtil.nvl(doc.getString("color")))
                    .idntfyNo(CmmUtil.nvl(doc.getString("idntfyNo")))
                    .imageCours(CmmUtil.nvl(doc.getString("imageCours")))
                    .jurisdInstNm(CmmUtil.nvl(doc.getString("jurisdInstNm")))
                    .lat(CmmUtil.nvl(doc.getString("lat")))
                    .lon(CmmUtil.nvl(doc.getString("lon")))
                    .neut(CmmUtil.nvl(doc.getString("neut")))
                    .pblancBeginDe(CmmUtil.nvl(doc.getString("pblancBeginDe")))
                    .pblancEndDe(CmmUtil.nvl(doc.getString("pblancEndDe")))
                    .pblancIdntfuNo(CmmUtil.nvl(doc.getString("pblancIdntfuNo")))
                    .plcInfo(CmmUtil.nvl(doc.getString("plcInfo")))
                    .protectPlc(CmmUtil.nvl(doc.getString("protectPlc")))
                    .receptDe(CmmUtil.nvl(doc.getString("receptDe")))
                    .refineLotnoAddr(CmmUtil.nvl(doc.getString("refineLotnoAddr")))
                    .refineRoadnmAddr(CmmUtil.nvl(doc.getString("refineRoadnmAddr")))
                    .refineZipCd(CmmUtil.nvl(doc.getString("refineZipCd")))
                    .sex(CmmUtil.nvl(doc.getString("sex")))
                    .sfetrInfo(CmmUtil.nvl(doc.getString("sfetrInfo")))
                    .shterNm(CmmUtil.nvl(doc.getString("shterNm")))
                    .shterTelno(CmmUtil.nvl(doc.getString("shterTelno")))
                    .species(CmmUtil.nvl(doc.getString("species")))
                    .state(CmmUtil.nvl(doc.getString("state")))
                    .thumbImageCours(CmmUtil.nvl(doc.getString("thumbImageCours")))
                    .weight(CmmUtil.nvl(doc.getString("weight")))
                    .build();
        }

        log.info("rDTO : " + rDTO);

        log.info(this.getClass().getName() + ".mapper 유기동물 정보 가져오기 종료!");

        return rDTO;
    }

    }
