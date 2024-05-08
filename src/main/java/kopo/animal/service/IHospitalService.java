package kopo.animal.service;

import kopo.animal.dto.HospitalDTO;
import kopo.animal.dto.ParkDTO;

import java.util.List;

public interface IHospitalService {

    /** 수집된 병원 리스트 가져오기*/
    List<HospitalDTO> getHospitalInfo(int page, int itemsPerPage, HospitalDTO pDTO) throws Exception;
}
