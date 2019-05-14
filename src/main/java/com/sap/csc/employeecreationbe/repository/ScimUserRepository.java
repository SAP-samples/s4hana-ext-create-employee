package com.sap.csc.employeecreationbe.repository;

import static com.sap.csc.employeecreationbe.config.Constants.APPLICATION_SCIM_JSON;
import static com.sap.csc.employeecreationbe.config.Constants.FILTER;
import static com.sap.csc.employeecreationbe.config.Constants.IDP_DESTINATION;
import static com.sap.csc.employeecreationbe.config.Constants.TOTAL_RESULTS;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_JSON;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.csc.employeecreationbe.exceptions.SapException;
import com.sap.csc.employeecreationbe.model.idp.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScimUserRepository implements UserRepository {
	
    private static final ObjectMapper om = new ObjectMapper();

    /**
   	 * This method makes a call to SCIM API {POST https://<tenant ID>.accounts.ondemand.com/service/scim/Users/} endpoint
   	 *  and creates the User in SAP Identity Authentication Service.
   	 * 
   	 * @see <a href=
   	 *      "https://help.sap.com/viewer/6d6d63354d1242d185ab4830fc04feb1/Cloud/en-US/cea87780bee94c6994b8005c6d6a4815.html">
   	 *      SAP Help Portal</a> for more details on the Create User Resource method of SCIM REST API
   	 */
    @Override
    public void create(User user) {
        final HttpClient client = HttpClientAccessor.getHttpClient(getDestination());
        
        final HttpPost post = new HttpPost(getDestination().getUri());
        post.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_SCIM_JSON);
        post.setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
        
        try {
            post.setEntity(new StringEntity(om.writeValueAsString(user)));
            log.info("Request: {}", post.getEntity());
            log.info("Request Body: {}", om.readTree(post.getEntity().getContent()));
            
            final HttpResponse response = client.execute(post);
            log.info("Response Body: {}", om.readTree(response.getEntity().getContent()));
        } catch (IOException e) {
            throw SapException.create(e.getMessage());
        }
    }

    /**
	 * This method makes a call to SCIM API {POST https://<tenant ID>.accounts.ondemand.com/service/scim/Users/} endpoint
	 *  and filter with {@code email} Id to check if the user exists
	 * 
	 * @return {@code true} if the user with given {@code email} is present
	 * 
   	 * @see <a href=
   	 *      "https://help.sap.com/viewer/6d6d63354d1242d185ab4830fc04feb1/Cloud/en-US/3af7dfae0aad4cf79ab8d822f8322e76.html">
   	 *      SAP Help Portal</a> for more details on the Users Search method of SCIM REST API
	 */
	@Override
	public Boolean findByEmail(String email) {
	    final HttpClient client = HttpClientAccessor.getHttpClient(getDestination());
	    
	    final HttpGet get = new HttpGet(getDestination().getUri() + filterEmailExpression(email));
	    get.setHeader(HttpHeaders.ACCEPT, APPLICATION_SCIM_JSON);
	    
	    return totalResults(client, get) > 0;
	}

	/**
   	 * This method makes a call to SCIM API {GET https://<tenant ID>.accounts.ondemand.com/service/scim/Users/} endpoint
   	 *  and filter with {@code userName} to check if the user exists
   	 * 
	 * @return {@code true} if the user with given {@code userName} is present
	 * 
   	 * @see <a href=
   	 *      "https://help.sap.com/viewer/6d6d63354d1242d185ab4830fc04feb1/Cloud/en-US/3af7dfae0aad4cf79ab8d822f8322e76.html">
   	 *      SAP Help Portal</a> for more details on the Users Search method of SCIM REST API
   	 */
    @Override
    public Boolean findByUserName(String userName) {
        final HttpClient client = HttpClientAccessor.getHttpClient(getDestination());
        
        final HttpGet get = new HttpGet(getDestination().getUri() + filterUserNameExpression(userName));
        get.setHeader(HttpHeaders.ACCEPT, APPLICATION_SCIM_JSON);
        
        return totalResults(client, get) > 0;
    }

    private static String filterEmailExpression(String email) {
        return FILTER + UriUtils.encode("emails eq \"" + email + "\"", UTF_8);
    }

    private static String filterUserNameExpression(String userName) {
        return FILTER + UriUtils.encode("userName eq \"" + userName + "\"", UTF_8);
    }

    private static int totalResults(HttpClient client, HttpGet get) {
        try {
            log.info("Request: {}", get);
            
            final HttpResponse response = client.execute(get);
            final JsonNode responseBody = om.readTree(response.getEntity().getContent());
            log.info("Response: {}", responseBody);
            
            return responseBody.findValue(TOTAL_RESULTS).asInt();
        } catch (IOException e) {
            throw SapException.create(e.getMessage());
        }
    }

    private static Destination getDestination() {
        return DestinationAccessor.getDestination(IDP_DESTINATION);
    }
    
}
