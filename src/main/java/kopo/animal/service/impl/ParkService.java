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
    public List<ParkDTO> getParkInfo() throws Exception {

        log.info(this.getClass().getName() + ".service 공원정보 조회하기 시작!");

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "PARK_" + DateUtil.getDateTime("yyyyMMdd");

        List<ParkDTO> rList = parkMapper.getParkInfo(colNm); // MongoDB에서 데이터 가져오기

        log.info(this.getClass().getName() + ".service 공원정보 조회하기 종료!");

        return rList;
    }
}
