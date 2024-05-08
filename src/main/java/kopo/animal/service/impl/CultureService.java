package kopo.animal.service.impl;

import kopo.animal.dto.CultureDTO;
import kopo.animal.persistance.mapper.ICultureMapper;
import kopo.animal.persistance.mapper.impl.CultureMapper;
import kopo.animal.service.ICultureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CultureService implements ICultureService {

    private final ICultureMapper cultureMapper;

    @Override
    public List<CultureDTO> getCultureInfo(int page, int itemsPerPage, CultureDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 문화시설 정보 조회하기 시작!");

        // MongodB에 저장된 컬렉션 이름
        String colNm = "ANIMAL_CULTURE_MAP";

        // 매퍼를 통해 공원 정보 가져오기
        List<CultureDTO> rList = cultureMapper.getCultureInfo(colNm, page, itemsPerPage, pDTO);

        log.info(this.getClass().getName() + ".service 문화시설 정보 조회하기 종료!");

        return rList;
    }
}
