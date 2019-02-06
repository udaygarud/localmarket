package com.lmp.app.service;

import java.util.ListIterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.lmp.app.entity.ShoppingCart;
import com.lmp.app.entity.ShoppingCart.CartItem;
import com.lmp.app.exceptions.CartNotFoundException;
import com.lmp.app.exceptions.ItemNotFoundException;
import com.lmp.app.exceptions.MuliplteStoreInCartException;
import com.lmp.app.model.ShoppingCartRequest;
import com.lmp.db.pojo.ShoppingCartEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.db.repository.ShoppingCartRepository;

@Service
public class ShoppingCartService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ShoppingCartRepository repo;
  @Autowired
  private StoreInventoryService storeItemService;

  public ShoppingCart getCart(ShoppingCartRequest cartRequest) {
    if(cartRequest == null || cartRequest.getUserId() == null) {
      return null;
    }
    // user id is cart's id
    return getCart(cartRequest.getUserId());
  }

  public ShoppingCart getCart(String id) {
    if(Strings.isNullOrEmpty(id)) {
      return null;
    }
    // user id is cart's id
    Optional<ShoppingCartEntity> cart = repo.findById(id);
    if(!cart.isPresent()) { // return an empty cart
      return ShoppingCart.forUser(id);
    }
    return ShoppingCart.fromEntity(cart.get());
  }

  /* clear items in cart not the items marked as saved for later*/
  public boolean clear(ShoppingCart cart) {
    ListIterator<CartItem> lit = cart.getItems().listIterator();
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
    ShoppingCart cart = getCart(cartId);
    clear(cart);
    repo.save(ShoppingCartEntity.toEntity(cart));
    return true;
  }

  public ShoppingCart add(ShoppingCartRequest cartRequest) {
    ShoppingCart cart = null;
    try {
     cart = getCart(cartRequest);
    } catch(CartNotFoundException e) {
      //create new cart for user
      cart = ShoppingCart.forUser(cartRequest.getUserId());
    }
    Optional<StoreItemEntity> storeItem = storeItemService.findStoreItemById(cartRequest.getItemId());
    CartItem item = cart.get(cartRequest.getItemId());
    if(item == null) {
      item = storeItemService.findById(cartRequest.getItemId());
    	
      if(item == null) {
        logger.info("no store item id: {}", cartRequest.getItemId());
        throw new ItemNotFoundException();
      }
    }
    // requested item from different store
    if(cart.getStoreId() != null && !cart.getStoreId().equals(item.getStoreId())) {
      // if force flag is true then clear the current cart and add incoming item
      if(cartRequest.isClearFirst()) {
        clear(cart);
      } else {
        throw new MuliplteStoreInCartException();
      }
    }
    cart.setStoreId(storeItem.get().getStoreId());
    cart.addToCart(item, cartRequest.getQuantity(),storeItem.get().getItem());
    repo.save(ShoppingCartEntity.toEntity(cart));
    return getCart(cartRequest);
  }

  public ShoppingCart remove(ShoppingCartRequest cartRequest) {
    ShoppingCart cart = getCart(cartRequest);
    if(cart == null) {
      return ShoppingCart.forUser(cartRequest.getUserId());
    }
    cart.remove(cartRequest.getItemId());
    repo.save(ShoppingCartEntity.toEntity(cart));
    return getCart(cartRequest);
  }

  public ShoppingCart update(ShoppingCartRequest cartRequest) {
    ShoppingCart cart = getCart(cartRequest);
    if(cart == null) {
      return ShoppingCart.forUser(cartRequest.getUserId());
    }
    cart.update(cartRequest.getItemId(), cartRequest.getQuantity());
    repo.save(ShoppingCartEntity.toEntity(cart));
    return getCart(cartRequest);
  }

  public ShoppingCart moveToList(ShoppingCartRequest cartRequest) {
    ShoppingCart cart = getCart(cartRequest);
    if(cart == null) {
      return ShoppingCart.forUser(cartRequest.getUserId());
    }
    for(CartItem ci : cart.getItems()) {
      if(ci.getId().equals(cartRequest.getItemId())) {
        ci.setSaveForLater(true);
      }
    }
    repo.save(ShoppingCartEntity.toEntity(cart));
    return getCart(cartRequest);
  }
  public double getTotal(String cartId) {
    return 0;
  }
}
