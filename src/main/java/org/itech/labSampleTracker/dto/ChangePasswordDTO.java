package org.itech.labSampleTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangePasswordDTO {
	private Integer userId;
	private String oldPassword;
	private String password;
	private String repassword;
}
