package kopo.animal.service.impl;

import kopo.animal.dto.ParkDTO;
import kopo.animal.persistance.mapper.IParkMapper;
import kopo.animal.service.IParkService;
import kopo.animal.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParkService implements IParkService {

    private final IParkMapper parkMapper;

    @Override
    public List<ParkDTO> getParkInfo(int page, int itemsPerPage, ParkDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 공원정보 조회하기 시작!");

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "ANIMAL_PARK_MAP";

        // 매퍼를 통해 공원 정보 가져오기
        List<ParkDTO> rList = parkMapper.getParkInfo(colNm, page, itemsPerPage, pDTO);

        log.info(this.getClass().getName() + ".service 공원정보 조회하기 종료!");

        return rList;
    }
}
