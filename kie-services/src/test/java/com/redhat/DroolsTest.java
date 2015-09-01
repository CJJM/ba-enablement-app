package com.redhat;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class DroolsTest {

	private StatelessDecisionService service = BrmsHelper.newStatelessDecisionServiceBuilder().auditLogName("audit").build();

	@Test
	public void helloWorldTest() {
		// given
		Collection<Object> facts = new ArrayList<Object>();
		Business business = new Business();
		business.setName("test");
		facts.add(business);

		// when
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		// then
		Assert.assertNotNull(response);
		Assert.assertNotNull(response.getBusiness());
		Assert.assertEquals("test", response.getBusiness().getName());
	}
	
	@Test
	public void shouldFilterOutAllRequestsFromKansas(){
		// scenario: business from Kansas are handled by another system - filter them out
		// given a business from Kansas
		// when I apply the filtering rules
		// then the business should be filtered
		// and the reason message should be "business filtered from Kansas
		
		Collection<Object> facts = new ArrayList<Object>();
		Business business = new Business();
		business.setStateCode("KS");
		facts.add(business);
		
		business.setName("Google");
		business.setAddressLine1("10 Main Street");
		business.setAddressLine2("20 Main Street");
		business.setCity("Compton");
		business.setZipCode("99999");
		business.setPhoneNumber("800-867-6509");
		business.setFederalTaxId("TaxId");
		
		// when I apply the filtering rules
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);
		
		// then the business should be filtered
		Assert.assertNotNull(response);
		//Assert.assertNotNull(response.getResponseCode());
		Assert.assertEquals("filtered", response.getResponseCode());
		
		// and the reason message should be "business filtered from Kansas"
		boolean found = false;
		for (Reason reason : response.getReasons()){
			if ( reason.getReasonMessage().equals( "business filtered from Kansas not found") ){
				found = true;
			}
		}
		
	}
	
	@Test
	public void shouldProcessAllBusinessesNotFromKansas(){
		// scenario: we are responsible for all businesses not from Kansas
		// given a business from New York
		// when I apply the filtering rules
		// then the business should be not be filtered
		// and the validation rules should be applied to the business
		
		Collection<Object> facts = new ArrayList<Object>();
		Business business = new Business();
		business.setStateCode("NY");
		facts.add(business);
		
		// when I apply the filtering rules
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);
		
		// then the business should be not be filtered
		Assert.assertNotNull(response);
		Assert.assertNotEquals("filtered", response.getResponseCode());
		
		// and the validation rules should be applied to the business
		Assert.assertNotNull(response.getReasons());
		
	}
	
	@Test
	public void shouldCreateValidationErrorsForAnyFieldThatAreEmptyOrNull(){
		// scenario: all fields must have values. 
		// given a business 
		// and the business' zipcode is empty
		// and the business' address line 1 is null
		// when I apply the validation rules
		// then the business should be return a validation error
		// and a message should say the zipcode is empty
		// and a message should say the address is null
		
		Collection<Object> facts = new ArrayList<Object>();
		Business business = new Business();
		facts.add(business);
		
		RuleResponse response = service.runRules(facts, "VerifySupplier", RuleResponse.class);

		Assert.assertNotNull(response);
		Assert.assertEquals("error", response.getResponseCode());

		boolean found = false;
		for (Reason reason : response.getReasons()){
			if ( reason.getReasonMessage().equals( "name field is empty") ){
				found = true;
				Assert.assertTrue( "name field is empty", found);
				found = false;
		
			if ( reason.getReasonMessage().equals( "addressLine1 field is empty") ){
				found = true;
				Assert.assertTrue( "addressLine1 field is empty", found);
				found = false;
				
			}
			if ( reason.getReasonMessage().equals( "addressLine2 field is empty") ){
				found = true;
				Assert.assertTrue( "addressLine2 field is empty", found);
				found = false;
				
			}
			if ( reason.getReasonMessage().equals( "phoneNumber field is empty") ){
				found = true;
				Assert.assertTrue( "phoneNumber field is empty", found);
				found = false;
				
			}
			if ( reason.getReasonMessage().equals( "city field is empty") ){
				found = true;
				Assert.assertTrue( "city field is empty", found);
				found = false;
				
			}
			if ( reason.getReasonMessage().equals( "stateCode field is empty") ){
				found = true;
				Assert.assertTrue( "stateCode field is empty", found);
				found = false;
				
			}
			if ( reason.getReasonMessage().equals( "zipCode field is empty") ){
				found = true;
				Assert.assertTrue( "zipCode field is empty", found);
				found = false;
				
			}
			if ( reason.getReasonMessage().equals( "federalTaxId field is empty") ){
				found = true;
				Assert.assertTrue( "federalTaxId field is empty", found);
				found = false;
				
			}
		}
	}
		
	}
	
	public void shouldEnrichTheTaxIdWithZipCode(){
		// scenario: we need to enrich the taxId with the zipcode for system XYZ
		// given a business 
		// and the business' zipcode is 10002
		// and the business' taxId is 98765
		// when I apply the enrichment rules
		// then the business' taxId should be enriched to 98765-10002
	}
	
}
