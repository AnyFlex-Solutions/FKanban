package com.fkanban.fkanban.appuser;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserRequest {
    private String nickname;
    private String Password;
    private String SuccessPassword;
}
