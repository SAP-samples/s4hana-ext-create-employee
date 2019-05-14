package com.sap.csc.employeecreationbe.controller;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sap.csc.employeecreationbe.model.dto.QueryBusinessUserDTO;
import com.sap.csc.employeecreationbe.service.SoapClient;

@RestController
@RequestMapping("business-user")
public class BusinessUserController {

	private final SoapClient<String, QueryBusinessUserDTO> queryBusinessUserSoapQueryClient;

	@Autowired
	public BusinessUserController(SoapClient<String, QueryBusinessUserDTO> queryBusinessUserSoapQueryClient) {
		this.queryBusinessUserSoapQueryClient = queryBusinessUserSoapQueryClient;
	}

	@GetMapping
	public QueryBusinessUserDTO getBusinessUser(@RequestParam String userName) {
		return queryBusinessUserSoapQueryClient.send(userName);
	}

	/**
	 * This method fetches all the business users or searched business users who are
	 * assigned a business role in the system.
	 * 
	 * The users will be then sorted on the basis of PersonExternalId.
	 */
	@GetMapping("/all")
	public Set<QueryBusinessUserDTO> businessUsers(@RequestParam(required = false) Set<String> usernames) {
		final ArrayList<String> names = new ArrayList<>(usernames == null ? Collections.emptySet() : usernames);

		return queryBusinessUserSoapQueryClient.sendAll(names).stream()
				.filter(bu -> !bu.getUser().getRole().isEmpty())
				.sorted(comparing(QueryBusinessUserDTO::getPersonExternalID))
				.collect(toCollection(LinkedHashSet::new));
	}

}
