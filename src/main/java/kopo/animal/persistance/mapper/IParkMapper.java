package kopo.animal.persistance.mapper;

import kopo.animal.dto.ParkDTO;

import java.util.List;

public interface IParkMapper {

    List<ParkDTO> getParkInfo(String colNm, ParkDTO parkDTO) throws Exception;
}
