package kopo.animal.persistance.mapper;

import kopo.animal.dto.CultureDTO;

import java.util.List;

public interface ICultureMapper {

    /**
     * 문화시설 지도
     *
     * @param colNm 조회할 컬렉션 이름
     * @return 문화시설 지도 리스트
     */
    List<CultureDTO> getCultureInfo(String colNm, int page, int itemsPerPage, CultureDTO pDTO) throws Exception;
}
