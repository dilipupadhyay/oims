package com.wms.web.populator.catalog;

import org.apache.commons.lang.Validate;

import com.wms.core.business.catalog.product.model.price.FinalPrice;
import com.wms.core.business.catalog.product.service.PricingService;
import com.wms.core.business.generic.exception.ConversionException;
import com.wms.core.business.merchant.model.MerchantStore;
import com.wms.core.business.reference.language.model.Language;
import com.wms.core.utils.AbstractDataPopulator;
import com.wms.web.entity.catalog.product.ReadableProductPrice;

public class ReadableProductPricePopulator extends
		AbstractDataPopulator<FinalPrice, ReadableProductPrice> {
	
	
	private PricingService pricingService;

	public PricingService getPricingService() {
		return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	@Override
	public ReadableProductPrice populate(FinalPrice source,
			ReadableProductPrice target, MerchantStore store, Language language)
			throws ConversionException {
		Validate.notNull(pricingService,"pricingService must be set");
		
		try {
			
			target.setOriginalPrice(pricingService.getDisplayAmount(source.getOriginalPrice(), store));
			if(source.isDiscounted()) {
				target.setDiscounted(true);
				target.setFinalPrice(pricingService.getDisplayAmount(source.getDiscountedPrice(), store));
			} else {
				target.setFinalPrice(pricingService.getDisplayAmount(source.getFinalPrice(), store));
			}
			
		} catch(Exception e) {
			throw new ConversionException("Exception while converting to ReadableProductPrice",e);
		}
		
		
		
		return target;
	}

	@Override
	protected ReadableProductPrice createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
