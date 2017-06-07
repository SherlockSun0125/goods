package goods.cart.service;

import java.sql.SQLException;
import java.util.List;
import cn.itcast.commons.CommonUtils;
import goods.cart.dao.CartItemDao;
import goods.cart.domain.CartItem;

public class CartItemService {
	private CartItemDao cartItemDao=new CartItemDao();
	
	public void batchDelete(String cartItemIds){
		try {
			cartItemDao.batchDelete(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加购物车条目
	 * @param cartItem
	 */
	public void add(CartItem cartItem) {
		try {
			CartItem _cartItem = cartItemDao.findByUidAndBid(cartItem.getUser().getUid(),cartItem.getBook().getBid());
			if(_cartItem == null) {//未有
				cartItem.setCartItemId(CommonUtils.uuid());
				cartItemDao.addCartItem(cartItem);
			} else {//已有
				int quantity = cartItem.getQuantity() + _cartItem.getQuantity();
				// 修改这个老条目的数量
				cartItemDao.updateQuantity(_cartItem.getCartItemId(), quantity);
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//我的购物车内容
	public List<CartItem> myCart(String uid){
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
}