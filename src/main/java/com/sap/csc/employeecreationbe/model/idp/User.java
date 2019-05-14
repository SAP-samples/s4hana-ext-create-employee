package com.sap.csc.employeecreationbe.model.idp;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class User {
	
	@Data
	private class Name {
		
		private final String givenName;
		private final String familyName;

		private Name(String givenName, String familyName) {
			this.givenName = givenName;
			this.familyName = familyName;
		}
		
	}

	@Data
	private class Email {
		
		private final String value;

		Email(String value) {
			this.value = value;
		}
		
	}
	
	public static class UserBuilder {
		
		private String userName;
		private String sapUserName;
		private String givenName;
		private String familyName;
		private String email;
		private String externalId;
		
		private List<String> scenarios;

		public UserBuilder setExternalId(String externalId) {
			this.externalId = externalId;
			return this;
		}

		public UserBuilder setUserName(String userName) {
			this.userName = userName;
			return this;
		}

		public UserBuilder setSapUserName(String sapUserName) {
			this.sapUserName = sapUserName;
			return this;
		}

		public UserBuilder setGivenName(String givenName) {
			this.givenName = givenName;
			return this;
		}

		public UserBuilder setFamilyName(String familyName) {
			this.familyName = familyName;
			return this;
		}

		public UserBuilder setEmail(String email) {
			this.email = email;
			return this;
		}

		public UserBuilder setScenarios(List<String> scenarios) {
			this.scenarios = scenarios;
			return this;
		}

		public User createUser() {
			return new User(userName, sapUserName, givenName, familyName, email, scenarios, externalId);
		}
		
	}

	private final String userName;
	private final String sapUserName;
	private final String passwordStatus = "initial";

	private final Name name;

	private final List<Email> emails;
	private final List<String> scenarios;

	@JsonIgnore
	private final String externalId;

	private User(String userName, String sapUserName, String givenName, String familyName, String email,
			List<String> scenarios, String externalId) {
		
		this.userName = userName;
		this.sapUserName = sapUserName;
		this.externalId = externalId;
		this.name = new Name(givenName, familyName);
		this.emails = Collections.singletonList(new Email(email));
		this.scenarios = scenarios;
	}

}
