package kopo.animal.service;

import kopo.animal.dto.CultureDTO;

import java.util.List;

public interface ICultureService {

    /** 수집된 문화시설 리스트 가져오기 */
    List<CultureDTO> getCultureInfo(int page, int itemsPerPage, CultureDTO pDTO) throws Exception;
}
