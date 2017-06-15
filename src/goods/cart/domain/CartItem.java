package goods.cart.domain;

import java.math.BigDecimal;

import goods.book.domain.Book;
import goods.user.domain.User;

public class CartItem {
	private String cartItemId;//购物项id
	private int quantity;//购买数量
	private Book book;//购买的书籍
	private User user;
	
	//购物项小计
	public double getSubTotal(){
		BigDecimal b1=new BigDecimal(book.getCurrPrice()+"");
		BigDecimal b2=new BigDecimal(quantity+"");
		BigDecimal b3=b1.multiply(b2);
		return b3.doubleValue();
	}
	
	
	public String getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(String cartItemId) {
		this.cartItemId = cartItemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}


	@Override
	public String toString() {
		return "CartItem [cartItemId=" + cartItemId + ", quantity=" + quantity
				+ ", book=" + book + ", user=" + user + "]";
	}
	
	

}
