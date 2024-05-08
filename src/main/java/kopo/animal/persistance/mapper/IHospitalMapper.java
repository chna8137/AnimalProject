package kopo.animal.persistance.mapper;

import kopo.animal.dto.HospitalDTO;

import java.util.List;

public interface IHospitalMapper {

    /**
     *
     * 병원 지도
     *
     * @param colNm 조회할 컬렉션 이름
     * @return 동물병원 리스트
     * */
    List<HospitalDTO> getHospitalInfo(String colNm, int page, int itemsPerPage, HospitalDTO pDTO) throws Exception;

}
