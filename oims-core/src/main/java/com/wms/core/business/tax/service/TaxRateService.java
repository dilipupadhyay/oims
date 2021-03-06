package com.wms.core.business.tax.service;

import java.util.List;

import com.wms.core.business.generic.exception.ServiceException;
import com.wms.core.business.generic.service.SalesManagerEntityService;
import com.wms.core.business.merchant.model.MerchantStore;
import com.wms.core.business.reference.country.model.Country;
import com.wms.core.business.reference.language.model.Language;
import com.wms.core.business.reference.zone.model.Zone;
import com.wms.core.business.tax.model.taxclass.TaxClass;
import com.wms.core.business.tax.model.taxrate.TaxRate;

public interface TaxRateService extends SalesManagerEntityService<Long, TaxRate> {

	public List<TaxRate> listByStore(MerchantStore store) throws ServiceException;

	List<TaxRate> listByCountryZoneAndTaxClass(Country country, Zone zone,
			TaxClass taxClass, MerchantStore store, Language language)
			throws ServiceException;

	List<TaxRate> listByCountryStateProvinceAndTaxClass(Country country,
			String stateProvince, TaxClass taxClass, MerchantStore store,
			Language language) throws ServiceException;

	 TaxRate getByCode(String code, MerchantStore store)
			throws ServiceException;

	List<TaxRate> listByStore(MerchantStore store, Language language)
			throws ServiceException;
	
	

}
