package kopo.animal.service.impl;

import kopo.animal.dto.UserInfoDTO;
import kopo.animal.persistance.mapper.IProfileMapper;
import kopo.animal.service.IProfileService;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService implements IProfileService {

    private final IProfileMapper profileMapper;


    @Override
    public UserInfoDTO getProfile(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getProfile Start!");

        UserInfoDTO rDTO = Optional.ofNullable(profileMapper.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getProfile End!");

        return rDTO;
    }

    @Override
    public void updateProfile(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateProfile Start!");

        profileMapper.updateProfile(pDTO);

        log.info(this.getClass().getName() + ".updateProfile End!");

    }

    @Override
    public void deleteUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        profileMapper.deleteUserInfo(pDTO);

        log.info(this.getClass().getName() + ".deleteUserInfo End!");
    }
}

