package com.gm.authorization.server.custom.config.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import com.gm.authorization.server.custom.entity.CustomClientDetails;
import com.gm.authorization.server.custom.exception.AlreadyExpiredException;
import com.gm.authorization.server.custom.repository.CustomClientDetailsRepository;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

	@Resource
	CustomClientDetailsRepository customClientDetailsRepository;

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

		CustomClientDetails customClientDetails = customClientDetailsRepository.findByClientId(clientId);
		if (customClientDetails != null) {
			if (customClientDetails.getVisiable() < 0) {
				throw new InvalidClientException(String.format("clientId %s is disabled!", clientId));
			}
			if (customClientDetails.getExpirationTime() != null
					&& customClientDetails.getExpirationTime().compareTo(new Date()) < 0) {
				throw new AlreadyExpiredException(String.format("clientId %s already expired!", clientId));
			}
			BaseClientDetails baseClientDetails = new BaseClientDetails();
			baseClientDetails.setClientId(customClientDetails.getClientId());
			if (!StringUtils.isEmpty(customClientDetails.getResourceIds())) {
				baseClientDetails
						.setResourceIds(StringUtils.commaDelimitedListToSet(customClientDetails.getResourceIds()));
			}
			baseClientDetails.setClientSecret(customClientDetails.getClientSecret());
			if (!StringUtils.isEmpty(customClientDetails.getScope())) {
				baseClientDetails.setScope(StringUtils.commaDelimitedListToSet(customClientDetails.getScope()));
			}
			if (!StringUtils.isEmpty(customClientDetails.getAuthorizedGrantTypes())) {
				baseClientDetails.setAuthorizedGrantTypes(
						StringUtils.commaDelimitedListToSet(customClientDetails.getAuthorizedGrantTypes()));
			} else {
				baseClientDetails.setAuthorizedGrantTypes(StringUtils.commaDelimitedListToSet("authorization_code"));
			}
			if (!StringUtils.isEmpty(customClientDetails.getWebServerRedirectUri())) {
				baseClientDetails.setRegisteredRedirectUri(
						StringUtils.commaDelimitedListToSet(customClientDetails.getWebServerRedirectUri()));
			}
			if (!StringUtils.isEmpty(customClientDetails.getAuthorities())) {
				List<SimpleGrantedAuthority> authorities = new ArrayList<>();
				StringUtils.commaDelimitedListToSet(customClientDetails.getAuthorities()).forEach(s -> {
					authorities.add(new SimpleGrantedAuthority(s));
				});
				baseClientDetails.setAuthorities(authorities);
			}
			if (customClientDetails.getAccessTokenValidity() != null
					&& customClientDetails.getAccessTokenValidity() > 0) {
				baseClientDetails.setAccessTokenValiditySeconds(customClientDetails.getAccessTokenValidity());
			}
			if (customClientDetails.getRefreshTokenValidity() != null
					&& customClientDetails.getRefreshTokenValidity() > 0) {
				baseClientDetails.setRefreshTokenValiditySeconds(customClientDetails.getRefreshTokenValidity());
			}
			if (!StringUtils.isEmpty(customClientDetails.getAutoApprove())) {
				baseClientDetails.setAutoApproveScopes(
						StringUtils.commaDelimitedListToSet(customClientDetails.getAutoApprove()));
			}
			return baseClientDetails;
		} else {
			throw new NoSuchClientException("No client with requested id: " + clientId);
		}
	}
}
