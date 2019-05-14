package com.sap.csc.employeecreationbe.model.dto;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.sap.xi.aba.NOSC_BusinessRoleID;
import com.sap.xi.aba.NOSC_UserAccountID;
import com.sap.xi.aba.NOSC_UserName;
import com.sap.xi.aba.RoleResponse;
import com.sap.xi.aba.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueryUserDTO {
	
	@Data
	@AllArgsConstructor
	private static class Role {
	    
		private String roleName;
	    
	}
	
    private String userID;
    private String userName;
    
    private List<Role> role;

    private QueryUserDTO(String userID, String userName, RoleResponse[] role) {
        this.userID = userID;
        this.userName = userName;
        
        this.role = Stream.of(role)
                .map(RoleResponse::getRoleName)
                .map(NOSC_BusinessRoleID::toString)
                .map(Role::new)
                .collect(toList());
    }

    public static QueryUserDTO of(UserResponse userResponse) {
        final String userID = Optional.ofNullable(userResponse.getUserID())
                .map(NOSC_UserAccountID::toString)
                .orElse("");
        
        final String userName = Optional.ofNullable(userResponse.getUserName())
                .map(NOSC_UserName::toString)
                .orElse("");
        
        final RoleResponse[] role = Optional.ofNullable(userResponse.getRole())
                .orElse(new RoleResponse[]{});
        
        return new QueryUserDTO(userID, userName, role);
    }

}
