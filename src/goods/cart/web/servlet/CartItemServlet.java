package goods.cart.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tools.commons.CommonUtils;
import tools.servlet.BaseServlet;
import goods.book.domain.Book;
import goods.book.service.BookService;
import goods.cart.domain.CartItem;
import goods.cart.service.CartItemService;
import goods.user.domain.User;

public class CartItemServlet extends BaseServlet{
	private CartItemService cartItemService=new CartItemService();
	private BookService bookService=new BookService();
	
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cartItemIds = req.getParameter("cartItemIds");
		double total = Double.parseDouble(req.getParameter("total"));
		HttpSession session=req.getSession();
		Object user=session.getAttribute("sessionUser");
		if(user==null){
			req.setAttribute("code", "error");//为了显示X图片
			req.setAttribute("msg", "您还没有登录，不能访问本资源");
			return "f:/jsps/msg.jsp";
		}
		List<CartItem> cartItemList=cartItemService.loadCartItems(cartItemIds);
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("total", total);
		req.setAttribute("cartItemIds", cartItemIds);
		return "f:/jsps/cart/showitem.jsp";
	}
//	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		User user = (User)req.getSession().getAttribute("sessionUser");
//		String uid = user.getUid();
//		double total = Double.parseDouble(req.getParameter("total"));
//		List<CartItem> cartItemList = cartItemService.myCart(uid);
//		req.setAttribute("cartItemList", cartItemList);
//		req.setAttribute("total", total);
//		return "f:/jsps/cart/showitem.jsp";
//	}
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cartItemId=req.getParameter("cartItemId");
		int quantity=Integer.parseInt(req.getParameter("quantity"));
		HttpSession session=req.getSession();
		Object user = session.getAttribute("sessionUser");
		if(user==null){
			List<CartItem> list=(List<CartItem>) session.getAttribute("cartItemList");
			for(CartItem cartItem:list){
				if(cartItem.getCartItemId().equals(cartItemId)){
					cartItem.setQuantity(quantity);
					//给客户端返回一个json对象
					StringBuilder sb=new StringBuilder("{");
					sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
					sb.append(",");
					sb.append("\"subtotal\"").append(":").append(cartItem.getSubTotal());
					sb.append("}");
					resp.getWriter().print(sb);
					break;
				}
			}
		}else{
			CartItem cartItem=cartItemService.updateQuantity(cartItemId, quantity);
			//给客户端返回一个json对象
			StringBuilder sb=new StringBuilder("{");
			sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
			sb.append(",");
			sb.append("\"subtotal\"").append(":").append(cartItem.getSubTotal());
			sb.append("}");
			resp.getWriter().print(sb);
		}
		
	
		return null;
	}
	
	public String batchDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cartItemIds=req.getParameter("cartItemIds");
		String[] cart=cartItemIds.split(",");
		HttpSession session=req.getSession();
		Object user=session.getAttribute("sessionUser");
		if(user==null){//用户未登录
			List<CartItem> list=(List<CartItem>) session.getAttribute("cartItemList");
			if(cart.length==1){
				for(CartItem cartItem:list){
					if(cartItem.getCartItemId().equals(cartItemIds)){
						list.remove(cartItem);
						break;
					}
				}
			}else{
				list.removeAll(list);
			}
			session.setAttribute("cartItemList", list);
		}else{
			cartItemService.batchDelete(cartItemIds);
		}
		return myCart(req,resp);
	}
	
	@SuppressWarnings("unchecked")
	public String add(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Map map=req.getParameterMap();
		CartItem cartItem=CommonUtils.toBean(map,CartItem.class);
		Book book=CommonUtils.toBean(map,Book.class);
		
		HttpSession session=req.getSession();
		Object user = session.getAttribute("sessionUser");
		List<CartItem> cartItemList=(List<CartItem>) session.getAttribute("cartItemList");
		if(user==null){//用户未登录
			if(cartItemList==null){
				cartItemList=new ArrayList<CartItem>();
			}
			book=bookService.loadBook(book.getBid());
			cartItem.setBook(book);
			cartItem.setCartItemId(CommonUtils.uuid());
			cartItemList.add(cartItem);
			session.setAttribute("cartItemList", cartItemList);//将未登录购物数据加入session
		}else{//用户已登录则直接将购物车加入数据库
			User myUser=(User) user;
			cartItem.setBook(book);
			cartItem.setUser(myUser);
			cartItemService.add(cartItem);
		}
		return myCart(req,resp);
	}
	
	public String myCart(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession();
		Object user = session.getAttribute("sessionUser");
		User myUser=(User) user;
		List<CartItem> noUserCart=(List<CartItem>) session.getAttribute("cartItemList");
		if(user==null){
			req.setAttribute("cartItemList", noUserCart);
		}else{
			String uid = myUser.getUid();
			if(noUserCart!=null){
				for(CartItem cartItem:noUserCart){
					cartItem.setUser(myUser);
					cartItem.setCartItemId("");
					cartItemService.add(cartItem);
				}
				noUserCart.removeAll(noUserCart);
				session.setAttribute("cartItemList", noUserCart);
			}
			
			List<CartItem> cartitemList = cartItemService.myCart(uid);
			req.setAttribute("cartItemList", cartitemList);
		}
		
		return "f:/jsps/cart/list.jsp";
	}
	
	
}
