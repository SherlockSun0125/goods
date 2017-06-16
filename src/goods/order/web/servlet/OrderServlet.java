package goods.order.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.commons.CommonUtils;
import tools.servlet.BaseServlet;

import goods.cart.domain.CartItem;
import goods.cart.service.CartItemService;
import goods.order.domain.Order;
import goods.order.domain.OrderItem;
import goods.order.service.OrderService;
import goods.page.PageBean;
import goods.user.domain.User;

public class OrderServlet extends BaseServlet {
	private OrderService orderService=new OrderService();
	private CartItemService cartItemService=new CartItemService();
	/**
	 * 获取当前页码
	 */
	private int getPc(HttpServletRequest req) {
		int pc = 1;
		String param = req.getParameter("currentPage");
		System.out.println("param="+param);
		if(param != null && !param.trim().isEmpty()) {
			try {
				pc = Integer.parseInt(param);
			} catch(RuntimeException e) {}
		}
		return pc;
	}
	
	/**
	 * 截取url，页面中的分页导航中需要使用它做为超链接的目标！
	 * /goods/BookServlet + methed=findByCategory&cid=xxx&currentPage=3
	 */
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		System.out.println("url="+url);
		//如果url中存在pc参数，截取掉
		int index = url.lastIndexOf("&currentPage=");
		if(index != -1) {
			url = url.substring(0, index);
		}
		return url;
	}
	
	/**
	 * 我的订单
	 */
	public String myOrders(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int currentPage = getPc(req);
		String url = getUrl(req);
		User user = (User)req.getSession().getAttribute("sessionUser");
		PageBean<Order> pb = orderService.myOrders(user.getUid(), currentPage);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/order/list.jsp";
	}
	
	public String createOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cartItemIds=req.getParameter("cartItemIds");
		List<CartItem> cartItemList=cartItemService.loadCartItems(cartItemIds);
		
		Order order=new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrderTime(String.format("%tF %<tT", new Date()));
		order.setStatus(1);
		order.setAddress(req.getParameter("address"));
		User owner = (User)req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);
		
		BigDecimal total=new BigDecimal("0");
		for(CartItem cartItem:cartItemList){
			total=total.add(new BigDecimal(cartItem.getSubTotal()+""));
		}
		order.setTotal(total.doubleValue());
		
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(CartItem cartItem : cartItemList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());//设置主键
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSubtotal(cartItem.getSubTotal());
			orderItem.setBook(cartItem.getBook());
			orderItem.setOrder(order);
			orderItemList.add(orderItem);
		}
		order.setOrderItemList(orderItemList);
		
		orderService.createOrder(order);//创建订单
		cartItemService.batchDelete(cartItemIds);//删除购物车条目
		req.setAttribute("order", order);
		return "f:/jsps/order/ordersucc.jsp";
	}
	
	/**
	 * 加载订单详情
	 */
	public String loadOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid=req.getParameter("oid");
		Order order=orderService.loadOrder(oid);
		req.setAttribute("order", order);
		String btn=req.getParameter("btn");//btn值代表超链接访问类别
		req.setAttribute("btn", btn);
//		List<OrderItem> list=order.getOrderItemList();
//		for(OrderItem orderItem:list){
//			System.out.println(orderItem.getBook().getBname().substring(0,10)+":"+orderItem.getSubtotal());
//		}
		return "f:/jsps/order/desc.jsp";
	}
	
	/**
	 * 取消订单
	 */
	public String cancelOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid=req.getParameter("oid");
		int status=orderService.findOrderStatus(oid);
		if(status!=1){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "订单状态错误，不能取消！");
			return "f:/jsps/order/msg.jsp";
		}
		orderService.updateStatus(oid, 5);//设置状态为取消！
		req.setAttribute("code", "success");
		req.setAttribute("msg", "您的订单已取消，您不后悔吗！");
		return "f:/jsps/order/msg.jsp";		
	}
	
	/**
	 * 确认收货
	 */
	public  String confirmOrder(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid=req.getParameter("oid");
		int status=orderService.findOrderStatus(oid);
		if(status!=3){
			req.setAttribute("code", "error");
			req.setAttribute("msg", "订单状态错误，不能确认收货！");
			return "f:/jsps/order/msg.jsp";
		}
		orderService.updateStatus(oid, 4);//设置状态为交易成功
		req.setAttribute("code", "success");
		req.setAttribute("msg", "恭喜您,交易成功");
		return "f:/jsps/order/msg.jsp";	
		
	}
	
	public String paymentPre(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("order", orderService.loadOrder(req.getParameter("oid")));
		return "f:/jsps/order/pay.jsp";
	}
	
	/**
	 * 订单支付完成
	 */
	public String payment(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid=req.getParameter("oid");
		orderService.updateStatus(oid, 2);//设置状态为等待确认！
		req.setAttribute("code", "success");
		req.setAttribute("msg", "支付成功");
		return "f:/jsps/order/msg.jsp";	
	}
	
	/**
	 * 我的订单
	 */
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int currentPage = getPc(req);
		String url = getUrl(req);
		User user = (User)req.getSession().getAttribute("sessionUser");
		PageBean<Order> pb = orderService.myOrders(user.getUid(), currentPage);
		pb.setUrl(url);
		int status = Integer.parseInt(req.getParameter("status"));
		List<Order> orderList=pb.getBeanList();
		List<Order> myOrderList=new ArrayList<Order>();
		if(status==123){
			for(Order order:orderList){
				if(order.getStatus()==1||order.getStatus()==2||order.getStatus()==3){
					myOrderList.add(order);
				}
			}
		}else{
			for(Order order:orderList){
				if(order.getStatus()==status){
					myOrderList.add(order);
				}
			}
		}
		pb.setBeanList(myOrderList);
		req.setAttribute("pb", pb);
		return "f:/jsps/order/list.jsp";
	}
}
