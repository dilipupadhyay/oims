package com.wms.web.populator.order;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;

import com.wms.core.business.catalog.product.model.Product;
import com.wms.core.business.catalog.product.model.attribute.ProductAttribute;
import com.wms.core.business.catalog.product.model.file.DigitalProduct;
import com.wms.core.business.catalog.product.model.price.FinalPrice;
import com.wms.core.business.catalog.product.model.price.ProductPrice;
import com.wms.core.business.catalog.product.service.ProductService;
import com.wms.core.business.catalog.product.service.attribute.ProductAttributeService;
import com.wms.core.business.catalog.product.service.file.DigitalProductService;
import com.wms.core.business.generic.exception.ConversionException;
import com.wms.core.business.merchant.model.MerchantStore;
import com.wms.core.business.order.model.orderproduct.OrderProduct;
import com.wms.core.business.order.model.orderproduct.OrderProductAttribute;
import com.wms.core.business.order.model.orderproduct.OrderProductDownload;
import com.wms.core.business.order.model.orderproduct.OrderProductPrice;
import com.wms.core.business.reference.language.model.Language;
import com.wms.core.business.shoppingcart.model.ShoppingCartAttributeItem;
import com.wms.core.business.shoppingcart.model.ShoppingCartItem;
import com.wms.core.utils.AbstractDataPopulator;
import com.wms.web.constants.ApplicationConstants;

public class OrderProductPopulator extends
		AbstractDataPopulator<ShoppingCartItem, OrderProduct> {
	
	private ProductService productService;
	private DigitalProductService digitalProductService;
	private ProductAttributeService productAttributeService;


	public ProductAttributeService getProductAttributeService() {
		return productAttributeService;
	}

	public void setProductAttributeService(
			ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public DigitalProductService getDigitalProductService() {
		return digitalProductService;
	}

	public void setDigitalProductService(DigitalProductService digitalProductService) {
		this.digitalProductService = digitalProductService;
	}

	/**
	 * Converts a ShoppingCartItem carried in the ShoppingCart to an OrderProduct
	 * that will be saved in the system
	 */
	@Override
	public OrderProduct populate(ShoppingCartItem source, OrderProduct target,
			MerchantStore store, Language language) throws ConversionException {
		
		Validate.notNull(productService,"productService must be set");
		Validate.notNull(digitalProductService,"digitalProductService must be set");
		Validate.notNull(productAttributeService,"productAttributeService must be set");

		
		try {
			Product modelProduct = productService.getById(source.getProductId());
			if(modelProduct==null) {
				throw new ConversionException("Cannot get product with id (productId) " + source.getProductId());
			}
			
			if(modelProduct.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Invalid product id " + source.getProductId());
			}

			DigitalProduct digitalProduct = digitalProductService.getByProduct(store, modelProduct);
			
			if(digitalProduct!=null) {
				OrderProductDownload orderProductDownload = new OrderProductDownload();	
				orderProductDownload.setOrderProductFilename(digitalProduct.getProductFileName());
				orderProductDownload.setOrderProduct(target);
				orderProductDownload.setDownloadCount(0);
				orderProductDownload.setMaxdays(ApplicationConstants.MAX_DOWNLOAD_DAYS);
				target.getDownloads().add(orderProductDownload);
			}

			target.setOneTimeCharge(source.getItemPrice());	
			target.setProductName(source.getProduct().getDescriptions().iterator().next().getName());
			target.setProductQuantity(source.getQuantity());
			target.setSku(source.getProduct().getSku());
			
			FinalPrice finalPrice = source.getFinalPrice();
			if(finalPrice==null) {
				throw new ConversionException("Object final price not populated in shoppingCartItem (source)");
			}
			//Default price
			OrderProductPrice orderProductPrice = orderProductPrice(finalPrice);
			orderProductPrice.setOrderProduct(target);
			
			Set<OrderProductPrice> prices = new HashSet<OrderProductPrice>();
			prices.add(orderProductPrice);

			//Other prices
			List<FinalPrice> otherPrices = finalPrice.getAdditionalPrices();
			if(otherPrices!=null) {
				for(FinalPrice otherPrice : otherPrices) {
					OrderProductPrice other = orderProductPrice(otherPrice);
					other.setOrderProduct(target);
					prices.add(other);
				}
			}
			
			target.setPrices(prices);
			
			//OrderProductAttribute
			Set<ShoppingCartAttributeItem> attributeItems = source.getAttributes();
			if(!CollectionUtils.isEmpty(attributeItems)) {
				Set<OrderProductAttribute> attributes = new HashSet<OrderProductAttribute>();
				for(ShoppingCartAttributeItem attribute : attributeItems) {
					OrderProductAttribute orderProductAttribute = new OrderProductAttribute();
					orderProductAttribute.setOrderProduct(target);
					Long id = attribute.getProductAttributeId();
					ProductAttribute attr = productAttributeService.getById(id);
					if(attr==null) {
						throw new ConversionException("Attribute id " + id + " does not exists");
					}
					
					if(attr.getProduct().getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Attribute id " + id + " invalid for this store");
					}
					
					orderProductAttribute.setProductAttributeIsFree(attr.getProductAttributeIsFree());
					orderProductAttribute.setProductAttributeName(attr.getProductOption().getDescriptionsSettoList().get(0).getName());
					orderProductAttribute.setProductAttributeValueName(attr.getProductOptionValue().getDescriptionsSettoList().get(0).getName());
					orderProductAttribute.setProductAttributePrice(attr.getProductAttributePrice());
					orderProductAttribute.setProductAttributeWeight(attr.getProductAttributeWeight());
					orderProductAttribute.setProductOptionId(attr.getProductOption().getId());
					orderProductAttribute.setProductOptionValueId(attr.getProductOptionValue().getId());
					attributes.add(orderProductAttribute);
				}
				target.setOrderAttributes(attributes);
			}

			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		return target;
	}

	@Override
	protected OrderProduct createTarget() {
		return null;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		return productService;
	}
	
	private OrderProductPrice orderProductPrice(FinalPrice price) {
		
		OrderProductPrice orderProductPrice = new OrderProductPrice();
		
		ProductPrice productPrice = price.getProductPrice();
		
		orderProductPrice.setDefaultPrice(productPrice.isDefaultPrice());

		orderProductPrice.setProductPrice(price.getFinalPrice());
		orderProductPrice.setProductPriceCode(productPrice.getCode());
		if(productPrice.getDescriptions()!=null && productPrice.getDescriptions().size()>0) {
			orderProductPrice.setProductPriceName(productPrice.getDescriptions().iterator().next().getName());
		}
		if(price.isDiscounted()) {
			orderProductPrice.setProductPriceSpecial(productPrice.getProductPriceSpecialAmount());
			orderProductPrice.setProductPriceSpecialStartDate(productPrice.getProductPriceSpecialStartDate());
			orderProductPrice.setProductPriceSpecialEndDate(productPrice.getProductPriceSpecialEndDate());
		}
		
		return orderProductPrice;
	}


}
