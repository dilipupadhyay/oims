package com.wms.core.business.system.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.wms.core.business.system.model.QMerchantConfiguration;
import com.wms.core.business.generic.dao.SalesManagerEntityDaoImpl;
import com.wms.core.business.merchant.model.MerchantStore;
import com.wms.core.business.system.model.MerchantConfiguration;
import com.wms.core.business.system.model.MerchantConfigurationType;

@Repository("merchantConfigurationDao")
public class MerchantConfigurationDaoImpl extends SalesManagerEntityDaoImpl<Long, MerchantConfiguration>
		implements MerchantConfigurationDao {


	@Override
	public MerchantConfiguration getMerchantConfiguration(String key, MerchantStore store) {
		
		
		
		QMerchantConfiguration qMerchantConfiguration = QMerchantConfiguration.merchantConfiguration;

		
		JPQLQuery query = new JPAQuery (getEntityManager());
		query.from(qMerchantConfiguration)
			.innerJoin(qMerchantConfiguration.merchantStore).fetch()
			.where(qMerchantConfiguration.merchantStore.id.eq(store.getId())
			.and(qMerchantConfiguration.key.eq(key)));
		
		return query.uniqueResult(qMerchantConfiguration);

	}
	
	@Override
	public List<MerchantConfiguration> getMerchantConfigurations(MerchantStore store) {

		QMerchantConfiguration qMerchantConfiguration = QMerchantConfiguration.merchantConfiguration;

		
		JPQLQuery query = new JPAQuery (getEntityManager());
		query.from(qMerchantConfiguration)
			.innerJoin(qMerchantConfiguration.merchantStore).fetch()
			.where(qMerchantConfiguration.merchantStore.id.eq(store.getId()));
		
		return query.list(qMerchantConfiguration);

	}
	
	@Override
	public List<MerchantConfiguration> listByType(MerchantConfigurationType type, MerchantStore store) {
		

		QMerchantConfiguration qMerchantConfiguration = QMerchantConfiguration.merchantConfiguration;

		
		JPQLQuery query = new JPAQuery (getEntityManager());
		query.from(qMerchantConfiguration)
			.innerJoin(qMerchantConfiguration.merchantStore).fetch()
			.where(qMerchantConfiguration.merchantStore.id.eq(store.getId())
			.and(qMerchantConfiguration.merchantConfigurationType.eq(type)));
		
		return query.list(qMerchantConfiguration);

	}
}
