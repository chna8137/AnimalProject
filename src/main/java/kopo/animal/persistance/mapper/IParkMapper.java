package kopo.animal.persistance.mapper;

import kopo.animal.dto.ParkDTO;

import java.util.List;

public interface IParkMapper {


    /**
     * 지도
     *
     * @param colNm 조회할 컬렉션 이름
     * @return 지도 리스트
     */
    List<ParkDTO> getParkInfo(String colNm) throws Exception;
}
