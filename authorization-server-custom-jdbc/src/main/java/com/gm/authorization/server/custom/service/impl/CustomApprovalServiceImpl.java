package com.gm.authorization.server.custom.service.impl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Service;
import com.gm.authorization.server.custom.entity.CustomApproval;
import com.gm.authorization.server.custom.service.CustomApprovalService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;

@Service
public class CustomApprovalServiceImpl implements CustomApprovalService {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public CustomApproval findApprovalById(Integer id) {
		// TODO Auto-generated method stub
		String sql = "select expiresAt,status,lastModifiedAt as lastUpdatedAt,userId,clientId,scope from oauth_approvals where id=?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, id);
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(CustomApproval.class));
		return (CustomApproval) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomApproval> listClientDetails(String userId, CustomApproval customApproval) {
		// TODO Auto-generated method stub
		StringBuffer sql = new StringBuffer(
				"select expiresAt,status,lastModifiedAt as lastUpdatedAt,userId,clientId,scope,id from oauth_approvals where userId=?");
		if (StringUtils.isNotEmpty(customApproval.getClientId())) {
			sql.append(" and clientId=?");
		}
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, userId);
		if (StringUtils.isNotEmpty(customApproval.getClientId())) {
			query.setParameter(2, customApproval.getClientId());
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.aliasToBean(CustomApproval.class));
		return (List<CustomApproval>) query.getResultList();

	}

}
