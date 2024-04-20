package kopo.animal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserInfoDTO {

    private String userId;

    private String userName;

    private String password;

    private String email;

    private String addr1;

    private String addr2;

    private String regId;

    private String regDt;

    private String chgId;

    private String chgDt;

    private String image; // 프로필 이미지

    // 회원가입시, 중복가입을 방지를 위해 사용할 변수
    private String existsYn;
    // 이메일 중복체크를 위한 중복체크
    private int authNumber;
}
