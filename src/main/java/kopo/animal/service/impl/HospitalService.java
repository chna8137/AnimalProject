package kopo.animal.service.impl;

import kopo.animal.dto.HospitalDTO;
import kopo.animal.dto.ParkDTO;
import kopo.animal.persistance.mapper.IHospitalMapper;
import kopo.animal.service.IHospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HospitalService implements IHospitalService {

    private final IHospitalMapper hospitalMapper;

    @Override
    public List<HospitalDTO> getHospitalInfo(int page, int itemsPerPage, HospitalDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 동물병원 조회하기 시작!");

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "ANIMAL_HOSPITAL_MAP";

        // 매퍼를 통해 공원 정보 가져오기
        List<HospitalDTO> rList = hospitalMapper.getHospitalInfo(colNm, page, itemsPerPage, pDTO);

        log.info(this.getClass().getName() + ".service 동물병원 조회하기 종료!");

        return rList;
    }
}
