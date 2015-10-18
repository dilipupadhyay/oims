package com.wms.core.business.catalog.product.dao.file;

import com.wms.core.business.catalog.product.model.Product;
import com.wms.core.business.catalog.product.model.file.DigitalProduct;
import com.wms.core.business.generic.dao.SalesManagerEntityDao;
import com.wms.core.business.merchant.model.MerchantStore;

public interface DigitalProductDao extends SalesManagerEntityDao<Long, DigitalProduct> {

	DigitalProduct getByProduct(MerchantStore store, Product product);


}
