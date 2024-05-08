package kopo.animal.service;

import kopo.animal.dto.ParkDTO;

import java.util.List;

public interface IParkService {

    /** 수집된 공원 리스트 가져오기*/
    List<ParkDTO> getParkInfo(int page, int itemsPerPage, ParkDTO pDTO) throws Exception;
}
