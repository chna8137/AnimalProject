package kopo.animal.persistance.mapper.impl;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import kopo.animal.dto.ParkDTO;
import kopo.animal.persistance.mapper.AbstractMongoDBComon;
import kopo.animal.persistance.mapper.IParkMapper;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class ParkMapper extends AbstractMongoDBComon implements IParkMapper {

    private final MongoTemplate mongodb;


    @Override
    public List<ParkDTO> getParkInfo(String colNm, int page, int itemsPerPage) throws Exception {

        log.info(this.getClass().getName() + ".controller 공원정보 가져오기 시작!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<ParkDTO> rList = new LinkedList<>();

        // 페이지네이션을 위한 파라미터 설정
        int skip = (page - 1) * itemsPerPage;
        int limit = itemsPerPage;

        List<? extends Bson> pipeline = Arrays.asList(
                new Document()
                        .append("$project", new Document()
                                .append("wlkCoursFlagNm", "$WLK_COURS_FLAG_NM")
                                .append("cousrsDc", "$COURS_DC")
                                .append("signguNm", "$SIGNGU_NM")
                                .append("coursLtCn", "$COURS_LT_CN")
                                .append("lnmAddr", "$LNM_ADDR")
                                .append("coursSpotLa", "$COURS_SPOT_LA")
                                .append("coursSpotLo", "$COURS_SPOT_LO")
                                .append("_id", 0)
                        ),
                new Document()
                        .append("$group", new Document()
                                .append("_id", new BsonNull())
                                .append("distinct", new Document()
                                        .append("$addToSet", "$$ROOT")
                                )
                        ),
                new Document()
                        .append("$unwind", new Document()
                                .append("path", "$distinct")
                                .append("preserveNullAndEmptyArrays", false)
                        ),
                new Document()
                        .append("$replaceRoot", new Document()
                                .append("newRoot", "$distinct")
                        ),
                new Document()
                        .append("$sort", new Document()
                                .append("LNM_ADDR", 1)
                        )
        );
        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터 양이 많은 경우 무조건 Aggregate 사용한다.
        MongoCollection<Document> col = mongodb.getCollection(colNm);
        AggregateIterable<Document> rs = col.aggregate(pipeline).allowDiskUse(true);

        for (Document doc : rs) {
            String wlkCoursFlagNm = CmmUtil.nvl(doc.getString("wlkCoursFlagNm"));
            String coursDc = CmmUtil.nvl(doc.getString("coursDc"));
            String signguNm = CmmUtil.nvl(doc.getString("signguNm"));
            String coursLtCn = CmmUtil.nvl(doc.getString("coursLtCn"));
            String lnmAddr = CmmUtil.nvl(doc.getString("lnmAddr"));
            String coursSpotLa = CmmUtil.nvl(doc.getString("coursSpotLa"));
            String coursSpotLo = CmmUtil.nvl(doc.getString("coursSpotLo"));

            log.info("wlkCoursFlagNm : " + wlkCoursFlagNm);
            log.info("coursDc : " + coursDc);
            log.info("signguNm : " + signguNm);
            log.info("coursLtCn : " + coursLtCn);
            log.info("lnmAddr : " + lnmAddr);
            log.info("coursSpotLa : " + coursSpotLa);
            log.info("coursSpotLo : " + coursSpotLo);

            ParkDTO rDTO = ParkDTO.builder()
                    .wlkCoursFlagNm(wlkCoursFlagNm)
                    .coursDc(coursDc)
                    .signguNm(signguNm)
                    .coursLtCn(coursLtCn)
                    .lnmAddr(lnmAddr)
                    .coursSpotLa(coursSpotLa)
                    .coursSpotLo(coursSpotLo).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

            rDTO = null;
            doc = null;

        }

        rs = null;
        col = null;
        pipeline = null;

        log.info(this.getClass().getName() + ".controller 공원 정보 가져오기 종료!");

        return rList;
    }
}
