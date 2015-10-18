package com.wms.web.tags;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.wms.core.business.catalog.product.service.PricingService;
import com.wms.core.business.merchant.model.MerchantStore;
import com.wms.core.business.reference.currency.model.Currency;
import com.wms.core.utils.ProductPriceUtils;
import com.wms.web.constants.Constants;

public class ShopProductPriceFormatTag extends RequestContextAwareTag  {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShopProductPriceFormatTag.class);

	@Autowired
	private PricingService pricingService;
	
	@Autowired
	private ProductPriceUtils productPriceUtils;
	
	
	
	private BigDecimal value;
	


	private Currency currency;


	


	public Currency getCurrency() {
		return currency;
	}


	public void setCurrency(Currency currency) {
		this.currency = currency;
	}


	@Override
	protected int doStartTagInternal() throws Exception {
		if (pricingService == null || productPriceUtils==null) {
			LOGGER.debug("Autowiring productPriceUtils");
            WebApplicationContext wac = getRequestContext().getWebApplicationContext();
            AutowireCapableBeanFactory factory = wac.getAutowireCapableBeanFactory();
            factory.autowireBean(this);
        }
		
		HttpServletRequest request = (HttpServletRequest) pageContext
		.getRequest();

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		String formatedPrice = null;
		
		if(this.getCurrency()!=null) {
			formatedPrice = productPriceUtils.getFormatedAmountWithCurrency(this.getCurrency(), this.getValue());
		} else {
			formatedPrice = pricingService.getDisplayAmount(this.getValue(), store);
		}
		
		pageContext.getOut().print(formatedPrice);
		
		return SKIP_BODY;

	}


	public int doEndTag() {
		return EVAL_PAGE;
	}


	public void setValue(BigDecimal value) {
		this.value = value;
	}


	public BigDecimal getValue() {
		return value;
	}




	

}
