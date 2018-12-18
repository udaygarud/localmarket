package com.lmp.app.model.validator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.google.common.base.Strings;
import com.lmp.app.model.ShoppingWishListRequest;

@Component
public class WishListRequestValidator implements Validator{
	public boolean supports(Class clazz) {
	    return ShoppingWishListRequest.class.equals(clazz);
	  }

	  public void validate(Object obj, Errors e) {
		  ShoppingWishListRequest sRequest = (ShoppingWishListRequest) obj;
	    if(Strings.isNullOrEmpty(sRequest.getUserId())) {
	      e.reject("userId.required", "userId is required");
	    }
	    if(Strings.isNullOrEmpty(sRequest.getItemId())) {
	      e.reject("itemId.required", "itemId is required");
	    }
	  }

  
  public static void validateQuantity(ShoppingWishListRequest sRequest, Errors e) {
	    if(sRequest.getQuantity() <= 0) {
	      e.reject("quantity.invalid", "quantity should be greater than 0");
	    }
	  }


}
