package com.lmp.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Strings;
import com.lmp.app.entity.ShoppingWishList;
import com.lmp.app.entity.ShoppingWishList.WishItem;
import com.lmp.app.exceptions.CartNotFoundException;
import com.lmp.app.exceptions.ItemNotFoundException;
import com.lmp.app.exceptions.MuliplteStoreInCartException;
import com.lmp.app.model.ShoppingWishListRequest;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.ShoppingWishListEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.db.repository.ShoppingWishListRepository;
import com.lmp.db.repository.StoreInventoryRepository;

@Service
public class ShoppingWishService {
	 private final Logger logger = LoggerFactory.getLogger(this.getClass());
	  @Autowired
	  StoreInventoryRepository sirepo;
	  @Autowired
	  private ShoppingWishListRepository repo;
	  @Autowired
	  private StoreInventoryService storeItemService;
	  @Autowired
	  ItemService itemService;

	  public ShoppingWishList getCart(ShoppingWishListRequest cartRequest) {
	    if(cartRequest == null || cartRequest.getUserId() == null) {
	      return null;
	    }
	    // user id is cart's id
	    return getCart(cartRequest.getUserId());
	  }

	  public ShoppingWishList getCart(String id) {
		  System.out.println("getting cart for "+id);
	    if(Strings.isNullOrEmpty(id)) {
	      return null;
	    }
	    // user id is cart's id
	    Optional<ShoppingWishListEntity> cart = repo.findById(id);
	    if(!cart.isPresent()) { // return an empty cart
	      return ShoppingWishList.forUser(id);
	    }
	    return ShoppingWishList.fromEntity(cart.get());
	  }

	  /* clear items in cart not the items marked as saved for later*/
	  public boolean clear(ShoppingWishList cart) {
	    ListIterator<WishItem> lit = cart.getItems().listIterator();
	    while(lit.hasNext()) {
	      if(!lit.next().isSaveForLater()) {
	        lit.remove();
	      }
	    }
	    return true;
	  }

	  public boolean clear(String cartId) {
	    if(Strings.isNullOrEmpty(cartId)) {
	      return false;
	    }
	    ShoppingWishList wishList = getCart(cartId);
	    clear(wishList);
	    repo.save(ShoppingWishListEntity.toEntity(wishList));
	    return true;
	  }

	  public ShoppingWishList add(ShoppingWishListRequest cartRequest) {
	    ShoppingWishList cart = null;
	    try {
	     cart = getCart(cartRequest);
	    } catch(CartNotFoundException e) {
	      //create new cart for user
	      cart = ShoppingWishList.forUser(cartRequest.getUserId());
	    }
	    
	  //  WishItem item = cart.get(cartRequest.getItemId());
	    Optional<StoreItemEntity> storeItem = storeItemService.findByid(cartRequest.getItemId());
	   // if(item == null) {
	    	if(storeItem == null) {
	    		logger.info("no store item id: {}", cartRequest.getItemId());
	    		throw new ItemNotFoundException();
	    	} else {
	    		System.out.println("cart item "+cart.getItems().size());
	    		if(cart.getItems().size()>0){
	    			List<String> list = new ArrayList<>();
	    			for (WishItem ie : cart.getItems()) {
						list.add(ie.getItem().getId());
					}
	    			System.out.println("list "+storeItem.get().getItem());
	    			if(list.contains(storeItem.get().getItem().getId())){
	    				
	    			} else {
	    				List<WishItem> allItems = cart.getItems();
	    				WishItem item = new WishItem();
	    				item.setId(cartRequest.getItemId());
	    				item.setItem(storeItem.get().getItem());
	    				item.setSaveForLater(false);
	    				cart.addToWish(item, cartRequest.getQuantity());
	    				repo.save(ShoppingWishListEntity.toEntity(cart));
	    			}
	    		}else {
	    			WishItem item = new WishItem();
	    			item.setId(cartRequest.getItemId());
    				item.setItem(storeItem.get().getItem());
    				item.setSaveForLater(false);
    				cart.addToWish(item, cartRequest.getQuantity());
    				repo.save(ShoppingWishListEntity.toEntity(cart));
	    		}
	    		
	    		
	    	}
//	    }else {
//	    	if(storeItem == null) {
//	    		logger.info("no store item id: {}", cartRequest.getItemId());
//	    		throw new ItemNotFoundException();
//	    	} else {
//	    		System.out.println("already exists "+cart.getItem().contains(storeItem));
//	    	}
//	    }
	    return getCart(cartRequest);
	  }

	  public ShoppingWishList remove(ShoppingWishListRequest cartRequest) {
	    ShoppingWishList cart = getCart(cartRequest);
	    if(cart == null) {
	      return ShoppingWishList.forUser(cartRequest.getUserId());
	    }
	    System.out.println("delete "+cartRequest.getItemId());
	    System.out.println("before "+cart.getItems().size());
	    cart.getItems().removeIf(p -> p.getId().equals(cartRequest.getItemId()));
	    System.out.println("after "+cart.getItems().size());
	    //cart.remove(cartRequest.getItemId());
	    repo.save(ShoppingWishListEntity.toEntity(cart));
	    
	    return getCart(cartRequest);
	  }

	  public ShoppingWishList update(ShoppingWishListRequest cartRequest) {
	    ShoppingWishList cart = getCart(cartRequest);
	    if(cart == null) {
	      return ShoppingWishList.forUser(cartRequest.getUserId());
	    }
	    cart.update(cartRequest.getItemId(), cartRequest.getQuantity());
	    repo.save(ShoppingWishListEntity.toEntity(cart));
	    return getCart(cartRequest);
	  }

	  public ShoppingWishList moveToList(ShoppingWishListRequest cartRequest) {
	    ShoppingWishList cart = getCart(cartRequest);
	    if(cart == null) {
	      return ShoppingWishList.forUser(cartRequest.getUserId());
	    }
	    for(WishItem ci : cart.getItems()) {
	      if(ci.getItem().getId().equals(cartRequest.getItemId())) {
	        ci.setSaveForLater(true);
	      }
	    }
	    repo.save(ShoppingWishListEntity.toEntity(cart));
	    return getCart(cartRequest);
	  }
	  public double getTotal(String cartId) {
	    return 0;
	  }

}
