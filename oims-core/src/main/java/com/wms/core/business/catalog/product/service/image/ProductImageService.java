package com.wms.core.business.catalog.product.service.image;

import java.util.List;

import com.wms.core.business.catalog.product.model.Product;
import com.wms.core.business.catalog.product.model.file.ProductImageSize;
import com.wms.core.business.catalog.product.model.image.ProductImage;
import com.wms.core.business.content.model.ImageContentFile;
import com.wms.core.business.content.model.OutputContentFile;
import com.wms.core.business.generic.exception.ServiceException;
import com.wms.core.business.generic.service.SalesManagerEntityService;


public interface ProductImageService extends SalesManagerEntityService<Long, ProductImage> {
	
	
	
	/**
	 * Add a ProductImage to the persistence and an entry to the CMS
	 * @param product
	 * @param productImage
	 * @param file
	 * @throws ServiceException
	 */
	void addProductImage(Product product, ProductImage productImage, ImageContentFile inputImage)
			throws ServiceException;

	/**
	 * Get the image ByteArrayOutputStream and content description from CMS
	 * @param productImage
	 * @return
	 * @throws ServiceException
	 */
	OutputContentFile getProductImage(ProductImage productImage, ProductImageSize size)
			throws ServiceException;

	/**
	 * Returns all Images for a given product
	 * @param product
	 * @return
	 * @throws ServiceException
	 */
	List<OutputContentFile> getProductImages(Product product)
			throws ServiceException;

	void removeProductImage(ProductImage productImage) throws ServiceException;

	void saveOrUpdate(ProductImage productImage) throws ServiceException;

	/**
	 * Returns an image file from required identifier. This method is
	 * used by the image servlet
	 * @param store
	 * @param product
	 * @param fileName
	 * @param size
	 * @return
	 * @throws ServiceException
	 */
	OutputContentFile getProductImage(String storeCode, String productCode,
			String fileName, final ProductImageSize size) throws ServiceException;

	void addProductImages(Product product, List<ProductImage> productImages)
			throws ServiceException;
	
}
