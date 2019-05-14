package com.sap.csc.employeecreationbe.service.businessuser;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.cloudplatform.servlet.RequestContextExecutor;
import com.sap.csc.employeecreationbe.model.business.BusinessUser;
import com.sap.csc.employeecreationbe.model.dto.QueryBusinessUserDTO;
import com.sap.csc.employeecreationbe.model.dto.QueryUserDTO;
import com.sap.csc.employeecreationbe.repository.jpa.BusinessUserRepository;
import com.sap.csc.employeecreationbe.service.SoapClient;
import com.sap.xi.aba.ActionCode;
import com.sap.xi.aba.BusinessUserMaintainConfirmationBundle;
import com.sap.xi.aba.BusinessUserMaintainRequestBundle;
import com.sap.xi.aba.NOSC_BusinessRoleID;
import com.sap.xi.aba.NOSC_PersonId;
import com.sap.xi.aba.Role;
import com.sap.xi.aba.User;

import lombok.extern.slf4j.Slf4j;

/**
 * Used to periodically assign business roles to newly created business users.
 */
@Component
@Slf4j
public class BusinessUserScheduler {

	private final BusinessUserRepository businessUserRepository;

	private final SoapClient<String, QueryBusinessUserDTO> queryBusinessUser;
	private final SoapClient<BusinessUserMaintainRequestBundle, BusinessUserMaintainConfirmationBundle> maintainBusinessUser;

	@Autowired
	public BusinessUserScheduler(BusinessUserRepository businessUserRepository,
			SoapClient<String, QueryBusinessUserDTO> queryBusinessUserClient,
			SoapClient<BusinessUserMaintainRequestBundle, BusinessUserMaintainConfirmationBundle> manageBusinessUserClient) {

		this.businessUserRepository = businessUserRepository;
		this.maintainBusinessUser = manageBusinessUserClient;
		this.queryBusinessUser = queryBusinessUserClient;
	}

	@Scheduled(fixedRateString = "${role-assignment-period}")
	@Transactional
	public void assignBusinessRoles() {
		// Look for pending users in local database whose needs roles to be assigned
		final List<BusinessUser> businessUsers = businessUserRepository.findAll();
		if (businessUsers.isEmpty()) {
			log.info("No users found...");
			return;
		}

		final List<String> userNames = getUserNames(businessUsers);
		try {
			new RequestContextExecutor().execute(() -> {
				// Query freshly created business users in S/4 HANA system
				final List<QueryBusinessUserDTO> existingUsers = queryBusinessUser.sendAll(userNames);
				
				// Assign roles to the users
				final List<BusinessUserMaintainRequestBundle> businessUsersWithRoles = createBusinessUserRequestWithRoles(
						businessUsers, existingUsers);
				
				// Send request to update users with their roles
				maintainBusinessUser.sendAll(businessUsersWithRoles);
				
				existingUsers.stream()
					.map(QueryBusinessUserDTO::getUser)
					.map(QueryUserDTO::getUserName)
					.forEach(businessUserRepository::deleteByUserNameIgnoreCase);
			});
		} catch (Exception e) {
			log.error("Assigning business roles failed...", e);
		}
	}

	private static List<String> getUserNames(List<BusinessUser> businessUsers) {
		return businessUsers.stream().map(BusinessUser::getUserName).collect(Collectors.toList());
	}

	private static List<BusinessUserMaintainRequestBundle> createBusinessUserRequestWithRoles(List<BusinessUser> businessUsers,
			List<QueryBusinessUserDTO> createdUsers) {

		// Create object that needs to be sent to the ManageBusinessUser API
		return createdUsers.stream().map(createdUser -> {
			// Match local users from database with existing users in S/4HANA
			final BusinessUser businessUser = businessUsers.stream()
					.filter(bu -> bu.getUserName().equalsIgnoreCase(createdUser.getUser().getUserName())).findFirst()
					.orElse(BusinessUser.of(createdUser.getUser().getUserName(), Collections.emptySet()));

			return createBusinessUserRequest(createdUser.getPersonID(), businessUser.getBusinessRoles());
		}).collect(Collectors.toList());
	}

	private static BusinessUserMaintainRequestBundle createBusinessUserRequest(String personId, Set<String> roleIds) {
		// Create the User object that will be sent in the update request
		final BusinessUserMaintainRequestBundle businessUserRequest = new BusinessUserMaintainRequestBundle();
		
		businessUserRequest
				.setPersonID(NOSC_PersonId.Factory.fromString(personId, NOSC_PersonId.MY_QNAME.getNamespaceURI()));
		businessUserRequest.setActionCode(ActionCode.value2);

		final User user = new User();
		user.setActionCode(ActionCode.value2);

		// Create role objects and add them to user
		roleIds.stream()
			.map(BusinessUserScheduler::toRole)
			.forEach(user::addRole);

		businessUserRequest.setUser(user);

		return businessUserRequest;
	}

	protected static Role toRole(String roleId) {
		final Role role = new Role();
		
		role.setActionCode(ActionCode.value1);
		role.setRoleName(
				NOSC_BusinessRoleID.Factory.fromString(roleId, NOSC_BusinessRoleID.MY_QNAME.getNamespaceURI()));

		return role;
	}

}
