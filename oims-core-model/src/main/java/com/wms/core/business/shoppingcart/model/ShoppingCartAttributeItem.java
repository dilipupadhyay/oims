package com.wms.core.business.shoppingcart.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import com.wms.core.business.shoppingcart.model.ShoppingCartAttributeItem;
import com.wms.core.business.shoppingcart.model.ShoppingCartItem;
import com.wms.core.business.catalog.product.model.attribute.ProductAttribute;
import com.wms.core.business.common.model.audit.AuditListener;
import com.wms.core.business.common.model.audit.AuditSection;
import com.wms.core.business.common.model.audit.Auditable;
import com.wms.core.business.generic.model.SalesManagerEntity;
import com.wms.core.constants.SchemaConstant;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "SHOPPING_CART_ATTR_ITEM")
public class ShoppingCartAttributeItem extends SalesManagerEntity<Long, ShoppingCartAttributeItem> implements Auditable {


	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SHP_CART_ATTR_ITEM_ID", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "SHP_CRT_ATTR_ITM_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;

	@Embedded
	private AuditSection auditSection = new AuditSection();
	

	
	@Column(name="PRODUCT_ATTR_ID", nullable=false)
	private Long productAttributeId;
	
	@Transient
	private ProductAttribute productAttribute;
	

	
	@ManyToOne(targetEntity = ShoppingCartItem.class)
	@JoinColumn(name = "SHP_CART_ITEM_ID", nullable = false)
	private ShoppingCartItem shoppingCartItem;
	
	public ShoppingCartAttributeItem(ShoppingCartItem shoppingCartItem, ProductAttribute productAttribute) {
		this.shoppingCartItem = shoppingCartItem;
		this.productAttribute = productAttribute;
		this.productAttributeId = productAttribute.getId();
	}
	
	public ShoppingCartAttributeItem() {

	}
	
	

	public ShoppingCartItem getShoppingCartItem() {
		return shoppingCartItem;
	}

	public void setShoppingCartItem(ShoppingCartItem shoppingCartItem) {
		this.shoppingCartItem = shoppingCartItem;
	}

	@Override
	public AuditSection getAuditSection() {
		return auditSection;
	}

	@Override
	public void setAuditSection(AuditSection audit) {
		this.auditSection = audit;
		
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
		
	}


	public void setProductAttributeId(Long productAttributeId) {
		this.productAttributeId = productAttributeId;
	}

	public Long getProductAttributeId() {
		return productAttributeId;
	}

	public void setProductAttribute(ProductAttribute productAttribute) {
		this.productAttribute = productAttribute;
	}

	public ProductAttribute getProductAttribute() {
		return productAttribute;
	}


}
