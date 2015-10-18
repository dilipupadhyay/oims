package com.wms.web.shop.controller.customer;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wms.core.business.customer.model.Customer;
import com.wms.core.business.customer.model.attribute.CustomerAttribute;
import com.wms.core.business.customer.model.attribute.CustomerOptionType;
import com.wms.core.business.customer.service.CustomerService;
import com.wms.core.business.customer.service.attribute.CustomerAttributeService;
import com.wms.core.business.customer.service.attribute.CustomerOptionService;
import com.wms.core.business.customer.service.attribute.CustomerOptionSetService;
import com.wms.core.business.customer.service.attribute.CustomerOptionValueService;
import com.wms.core.business.generic.exception.ServiceException;
import com.wms.core.business.merchant.model.MerchantStore;
import com.wms.core.business.order.service.OrderService;
import com.wms.core.business.reference.country.model.Country;
import com.wms.core.business.reference.country.service.CountryService;
import com.wms.core.business.reference.language.model.Language;
import com.wms.core.business.reference.language.service.LanguageService;
import com.wms.core.business.reference.zone.service.ZoneService;
//import com.wms.web.shop.controller.data.CountryData;
import com.wms.core.utils.ajax.AjaxResponse;
import com.wms.web.constants.Constants;
import com.wms.web.entity.customer.Address;
import com.wms.web.entity.customer.CustomerEntity;
import com.wms.web.entity.customer.CustomerPassword;
import com.wms.web.shop.controller.AbstractController;
import com.wms.web.shop.controller.ControllerConstants;
import com.wms.web.shop.controller.customer.facade.CustomerFacade;
import com.wms.web.shop.controller.order.facade.OrderFacade;
import com.wms.web.utils.EmailTemplatesUtils;
import com.wms.web.utils.LabelUtils;
import com.wms.web.utils.LocaleUtils;

/**
 * Entry point for logged in customers
 * @author Carl Samson
 *
 */
@Controller
@RequestMapping("/shop/customer")
public class CustomerAccountController extends AbstractController {
	
	private static final String CUSTOMER_ID_PARAMETER = "customer";
    private static final String BILLING_SECTION="/shop/customer//billing.html";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerAccountController.class);
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CustomerOptionService customerOptionService;
	
	@Autowired
	private CustomerOptionValueService customerOptionValueService;
	
	@Autowired
	private CustomerOptionSetService customerOptionSetService;
	
	@Autowired
	private CustomerAttributeService customerAttributeService;
	
    @Autowired
    private LanguageService languageService;
    
	@Autowired
	private PasswordEncoder passwordEncoder;


    @Autowired
    private CountryService countryService;
    
	@Autowired
	private EmailTemplatesUtils emailTemplatesUtils;

    
    @Autowired
    private ZoneService zoneService;
    
    @Autowired
    private CustomerFacade customerFacade;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderFacade orderFacade;
    
	@Autowired
	private LabelUtils messages;


	
	/**
	 * Dedicated customer logon page
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/customLogon.html", method=RequestMethod.GET)
	public String displayLogon(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);


		//dispatch to dedicated customer logon
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.customerLogon).append(".").append(store.getStoreTemplate());

		return template.toString();
		
	}
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/account.html", method=RequestMethod.GET)
	public String displayCustomerAccount(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);

		
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.customer).append(".").append(store.getStoreTemplate());

		return template.toString();
		
	}
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/password.html", method=RequestMethod.GET)
	public String displayCustomerChangePassword(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);

		CustomerPassword customerPassword = new CustomerPassword();
		model.addAttribute("password", customerPassword);
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.changePassword).append(".").append(store.getStoreTemplate());

		return template.toString();
		
	}
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/changePassword.html", method=RequestMethod.POST)
	public String changePassword(@Valid @ModelAttribute(value="password") CustomerPassword password, BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
	    
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.changePassword).append(".").append(store.getStoreTemplate());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	if(customer==null) {
    		return "redirect:/"+Constants.SHOP_URI;
    	}
    	
    	String currentPassword = password.getCurrentPassword();
    	String encodedCurrentPassword = passwordEncoder.encodePassword(currentPassword, null);
    	
    	if(!StringUtils.equals(encodedCurrentPassword, customer.getPassword())) {
			FieldError error = new FieldError("password","password",messages.getMessage("message.invalidpassword", locale));
        	bindingResult.addError(error);
    	}

    	
        if ( bindingResult.hasErrors() )
        {
            LOGGER.info( "found {} validation error while validating customer password",
                         bindingResult.getErrorCount() );
    		return template.toString();

        }
    	
		CustomerPassword customerPassword = new CustomerPassword();
		model.addAttribute("password", customerPassword);
		
		String newPassword = password.getPassword();
		String encodedPassword = passwordEncoder.encodePassword(newPassword, null);
		
		customer.setPassword(encodedPassword);
		
		customerService.saveOrUpdate(customer);
		
		emailTemplatesUtils.changePasswordNotificationEmail(customer, store, LocaleUtils.getLocale(customer.getDefaultLanguage()), request.getContextPath());
		
		model.addAttribute("success", "success");

		return template.toString();
		
	}
	

	
	/**
	 * Manage the edition of customer attributes
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value={"/attributes/save.html"}, method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String saveCustomerAttributes(HttpServletRequest request, Locale locale) throws Exception {
		

		AjaxResponse resp = new AjaxResponse();
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		
		//1=1&2=on&3=eeee&4=on&customer=1

		@SuppressWarnings("rawtypes")
		Enumeration parameterNames = request.getParameterNames();
		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	if(customer==null) {
    		LOGGER.error("Customer id [customer] is not defined in the parameters");
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			return resp.toJSONString();
    	}
		
		

		
		if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			LOGGER.error("Customer id does not belong to current store");
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			return resp.toJSONString();
		}
		
		List<CustomerAttribute> customerAttributes = customerAttributeService.getByCustomer(store, customer);
		Map<Long,CustomerAttribute> customerAttributesMap = new HashMap<Long,CustomerAttribute>();
		
		for(CustomerAttribute attr : customerAttributes) {
			customerAttributesMap.put(attr.getCustomerOption().getId(), attr);
		}

		parameterNames = request.getParameterNames();
		
		while(parameterNames.hasMoreElements()) {
			
			String parameterName = (String)parameterNames.nextElement();
			String parameterValue = request.getParameter(parameterName);
			try {
				
				String[] parameterKey = parameterName.split("-");
				com.wms.core.business.customer.model.attribute.CustomerOption customerOption = null;
				com.wms.core.business.customer.model.attribute.CustomerOptionValue customerOptionValue = null;

				
				if(CUSTOMER_ID_PARAMETER.equals(parameterName)) {
					continue;
				}
				
					if(parameterKey.length>1) {
						//parse key - value
						String key = parameterKey[0];
						String value = parameterKey[1];
						//should be on
						customerOption = customerOptionService.getById(new Long(key));
						customerOptionValue = customerOptionValueService.getById(new Long(value));
						

						
					} else {
						customerOption = customerOptionService.getById(new Long(parameterName));
						customerOptionValue = customerOptionValueService.getById(new Long(parameterValue));

					}
					
					//get the attribute
					//CustomerAttribute attribute = customerAttributeService.getByCustomerOptionId(store, customer.getId(), customerOption.getId());
					CustomerAttribute attribute = customerAttributesMap.get(customerOption.getId());
					if(attribute==null) {
						attribute = new CustomerAttribute();
						attribute.setCustomer(customer);
						attribute.setCustomerOption(customerOption);
					} else {
						customerAttributes.remove(attribute);
					}
					
					if(customerOption.getCustomerOptionType().equals(CustomerOptionType.Text.name())) {
						if(!StringUtils.isBlank(parameterValue)) {
							attribute.setCustomerOptionValue(customerOptionValue);
							attribute.setTextValue(parameterValue);
						}  else {
							attribute.setTextValue(null);
						}
					} else {
						attribute.setCustomerOptionValue(customerOptionValue);
					}
					
					
					if(attribute.getId()!=null && attribute.getId().longValue()>0) {
						if(attribute.getCustomerOptionValue()==null){
							customerAttributeService.delete(attribute);
						} else {
							customerAttributeService.update(attribute);
						}
					} else {
						customerAttributeService.save(attribute);
					}
					


			} catch (Exception e) {
				LOGGER.error("Cannot get parameter information " + parameterName,e);
			}
			
		}
		
		//and now the remaining to be removed
		for(CustomerAttribute attr : customerAttributes) {
			customerAttributeService.delete(attr);
		}
		
		//refresh customer
		Customer c = customerService.getById(customer.getId());
		super.setSessionAttribute(Constants.CUSTOMER, c, request);
		
		resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
		return resp.toJSONString();
		

	}

	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/billing.html", method=RequestMethod.GET)
    public String displayCustomerBillingAddress(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        

        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        Language language = getSessionAttribute(Constants.LANGUAGE, request);
    
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	if(customer==null) {
    		return "redirect:/"+Constants.SHOP_URI;
    	}
        
        
        CustomerEntity customerEntity = customerFacade.getCustomerDataByUserName( customer.getNick(), store, language );
        if(customer !=null){
           model.addAttribute( "customer",  customerEntity);
        }
        
        
        /** template **/
        StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.Billing).append(".").append(store.getStoreTemplate());

        return template.toString();
        
    }
    
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
    @RequestMapping(value="/editAddress.html", method={RequestMethod.GET,RequestMethod.POST})
    public String editAddress(final Model model, final HttpServletRequest request,
                              @RequestParam(value = "billingAddress", required = false) Boolean billingAddress) throws Exception {
        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	if(customer==null) {
    		return "redirect:/"+Constants.SHOP_URI;
    	}
        
        
        
        Address address=customerFacade.getAddress( customer.getId(), store, billingAddress );
        model.addAttribute( "address", address);
        model.addAttribute( "customerId", customer.getId() );
        StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.EditAddress).append(".").append(store.getStoreTemplate());
        return template.toString();
    }
    
    
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
    @RequestMapping(value="/updateAddress.html", method={RequestMethod.GET,RequestMethod.POST})
    public String updateCustomerAddress(@Valid
                                        @ModelAttribute("address") Address address,BindingResult bindingResult,final Model model, final HttpServletRequest request,
                              @RequestParam(value = "billingAddress", required = false) Boolean billingAddress) throws Exception {
       
        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.EditAddress).append(".").append(store.getStoreTemplate());
    	
    	if(customer==null) {
    		return "redirect:/"+Constants.SHOP_URI;
    	}
    	
    	model.addAttribute( "address", address);
        model.addAttribute( "customerId", customer.getId() );
        
        
        if(bindingResult.hasErrors()){
            LOGGER.info( "found {} error(s) while validating  customer address ",
                         bindingResult.getErrorCount() );
            return template.toString();
        }
        

        Language language = getSessionAttribute(Constants.LANGUAGE, request);
        customerFacade.updateAddress( customer.getId(), store, address, language);
        
        Customer c = customerService.getById(customer.getId());
		super.setSessionAttribute(Constants.CUSTOMER, c, request);
        
        model.addAttribute("success", "success");
        
        return template.toString();

    }
    
    
	@ModelAttribute("countries")
	protected List<Country> getCountries(final HttpServletRequest request){
	    
        Language language = (Language) request.getAttribute( "LANGUAGE" );
        try
        {
            if ( language == null )
            {
                language = (Language) request.getAttribute( "LANGUAGE" );
            }

            if ( language == null )
            {
                language = languageService.getByCode( Constants.DEFAULT_LANGUAGE );
            }
            
            List<Country> countryList=countryService.getCountries( language );
            return countryList;
        }
        catch ( ServiceException e )
        {
            LOGGER.error( "Error while fetching country list ", e );

        }
        return Collections.emptyList();
    }

    //@ModelAttribute("zones")
    //public List<Zone> getZones(final HttpServletRequest request){
    //    return zoneService.list();
    //}
 




}
