package com.sap.csc.employeecreationbe.model.dto;

import java.util.Optional;

import com.sap.xi.aba.BU_LANGUAGEINDEPENDENT_LONG_Name;
import com.sap.xi.aba.BusinessUserSimplyByElementsResponse;
import com.sap.xi.aba.NOSC_PersonExternalID;
import com.sap.xi.aba.NOSC_PersonId;
import com.sap.xi.aba.NOSC_PersonUUID;
import com.sap.xi.aba.PersonalInformationResponse;
import com.sap.xi.aba.UserResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryBusinessUserDTO {
	
    private String personExternalID;
    private String personID;
    private String personUUID;
    private String personFullName;

    private QueryUserDTO user;

    public static QueryBusinessUserDTO of(BusinessUserSimplyByElementsResponse businessUserSimplyByElementsResponse) {
        final String personExternalID = Optional.ofNullable(businessUserSimplyByElementsResponse.getPersonExternalID())
                .map(NOSC_PersonExternalID::toString)
                .orElse("");
        
        final String personID = Optional.ofNullable(businessUserSimplyByElementsResponse.getPersonID())
                .map(NOSC_PersonId::toString)
                .orElse("");
        
        final String personUUID = Optional.ofNullable(businessUserSimplyByElementsResponse.getPersonUUID())
                .map(NOSC_PersonUUID::toString)
                .orElse("");
        
        final String personFullName = Optional.ofNullable(businessUserSimplyByElementsResponse.getPersonalInformation())
                .map(PersonalInformationResponse::getPersonFullName)
                .map(BU_LANGUAGEINDEPENDENT_LONG_Name::toString)
                .orElse("");
        
        final UserResponse user = Optional.ofNullable(businessUserSimplyByElementsResponse.getUser())
                .orElse(new UserResponse());
        
        return new QueryBusinessUserDTO(
                personExternalID,
                personID,
                personUUID,
                personFullName,
                QueryUserDTO.of(user)
        );
    }
    
}
