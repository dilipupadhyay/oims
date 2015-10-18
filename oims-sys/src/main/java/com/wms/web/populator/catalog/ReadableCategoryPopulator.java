package com.wms.web.populator.catalog;

import com.wms.core.business.catalog.category.model.Category;
import com.wms.core.business.catalog.category.model.CategoryDescription;
import com.wms.core.business.generic.exception.ConversionException;
import com.wms.core.business.merchant.model.MerchantStore;
import com.wms.core.business.reference.language.model.Language;
import com.wms.core.utils.AbstractDataPopulator;
import com.wms.web.entity.catalog.category.ReadableCategory;

public class ReadableCategoryPopulator extends
		AbstractDataPopulator<Category, ReadableCategory> {

	@Override
	public ReadableCategory populate(Category source, ReadableCategory target,
			MerchantStore store, Language language) throws ConversionException {
		
		
		target.setLineage(source.getLineage());
		if(source.getDescriptions()!=null && source.getDescriptions().size()>0) {
			
			CategoryDescription description = source.getDescription();
			if(source.getDescriptions().size()>1) {
				for(CategoryDescription desc : source.getDescriptions()) {
					if(desc.getLanguage().getCode().equals(language.getCode())) {
						description = desc;
						break;
					}
				}
			}
		
		
		
			if(description!=null) {
				com.wms.web.entity.catalog.category.CategoryDescription desc = new com.wms.web.entity.catalog.category.CategoryDescription();
				desc.setFriendlyUrl(description.getSeUrl());
				desc.setName(description.getName());
				desc.setDescription(description.getName());
				desc.setKeyWords(description.getMetatagKeywords());
				desc.setHighlights(description.getCategoryHighlight());
				desc.setTitle(description.getMetatagTitle());
				desc.setMetaDescription(description.getMetatagDescription());
				
				target.setDescription(desc);
			}
		
		}
		
		target.setCode(source.getCode());
		target.setId(source.getId());

		
		return target;
		
	}

	@Override
	protected ReadableCategory createTarget() {
		return null;
	}

}
