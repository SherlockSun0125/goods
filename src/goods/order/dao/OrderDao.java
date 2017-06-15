package goods.order.dao;

import goods.book.domain.Book;
import goods.category.domain.Category;
import goods.order.domain.Order;
import goods.order.domain.OrderItem;
import goods.page.Expression;
import goods.page.PageBean;
import goods.page.PageConstants;
import goods.user.dao.UserDao;
import goods.user.domain.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import tools.commons.CommonUtils;
import tools.jdbc.TxQueryRunner;
public class OrderDao {
	QueryRunner qr=new TxQueryRunner();
	private UserDao userDao=new UserDao();
	/**
	 * 通用查询方法
	 */
	private PageBean<Order> findByCriteria(List<Expression> exprList, int currentPage) throws SQLException{
		int pageSize = PageConstants.ORDER_PAGE_SIZE;//每页记录数
		StringBuilder whereSql = new StringBuilder(" where 1=1"); 
		List<Object> params = new ArrayList<Object>();//SQL中有问号，它是对应问号的值
		for(Expression expr : exprList) {
			whereSql.append(" and ").append(expr.getName())
				.append(" ").append(expr.getOperator()).append(" ");
			// where 1=1 and bid = ?
			if(!expr.getOperator().equals("is null")) {
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}

		String sql = "select count(*) from t_order" + whereSql;
		Number number = (Number)qr.query(sql, new ScalarHandler(), params.toArray());
		int totalRecords = number.intValue();//得到了总记录数
		sql = "select * from t_order" + whereSql + " order by ordertime desc limit ?,?";
		params.add((currentPage-1) * pageSize);//当前页首行记录的下标
		params.add(pageSize);//一共查询几行，就是每页记录数
		
		List<Order> beanList = qr.query(sql, new BeanListHandler<Order>(Order.class), params.toArray());
		// 遍历每个订单，为其加载它的所有订单条目
		for(Order order : beanList) {
			loadOrderItem(order);
		}
		PageBean<Order> pb = new PageBean<Order>();
		pb.setBeanList(beanList);
		pb.setCurrentPage(currentPage);
		pb.setPageSize(pageSize);
		pb.setTotalRecords(totalRecords);
		
		return pb;
	}
	
	/*
	 * 为指定的order载它的所有OrderItem
	 */
	private void loadOrderItem(Order order) throws SQLException {
		String sql="select * from t_orderitem where oid=?";
		List<Map<String,Object>> mapList=qr.query(sql, new MapListHandler(), order.getOid()); 
		List<OrderItem> orderItemList=toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
	}

	/**
	 * 把多个Map转换成多个OrderItem
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList=new ArrayList<OrderItem>();
		for(Map<String,Object> map:mapList){
			OrderItem orderItem=toOrderItem(map);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}

	/**
	 * 将一个Map转化为一个OrderItem
	 * @param map
	 * @return
	 */
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem=CommonUtils.toBean(map, OrderItem.class);
		Book book=CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}

	/**
	 * 按用户uid查询
	 */
	public PageBean<Order> findByUser(String uid,int currentPage) throws SQLException {
		ArrayList<Expression> exprList=new ArrayList<Expression>();
		Expression exp=new Expression("uid","=",uid);
		exprList.add(exp);
		return findByCriteria(exprList,currentPage);
	}
	
	
	public void add(Order order) throws SQLException{
		/**
		 * 1.插入订单
		 */
		String sql="insert into t_order values(?,?,?,?,?,?)";
		Object[] params={order.getOid(),order.getOrderTime(),order.getTotal(),
				order.getStatus(),order.getAddress(),order.getOwner().getUid()};
		qr.update(sql, params);
		
		/**
		 * 插入订单条目
		 */
		sql="insert into t_orderitem values(?,?,?,?,?,?,?,?)";
		int len=order.getOrderItemList().size();
		Object[][] obj=new Object[len][];
		for(int i=0;i<len;i++){
			OrderItem orderItem=order.getOrderItemList().get(i);
			obj[i]=new Object[]{orderItem.getOrderItemId(),orderItem.getQuantity(),
					orderItem.getSubtotal(),orderItem.getBook().getBid(),
					orderItem.getBook().getBname(),orderItem.getBook().getCurrPrice(),
					orderItem.getBook().getImage_b(),order.getOid()};
		}
		qr.batch(sql, obj);//执行批处理
	}

	/**
	 * 加载订单详情
	 * @throws SQLException 
	 */
	public Order loadOrder(String oid) throws SQLException{
		String sql="select * from t_order where oid=?";
		Map<String, Object> map = qr.query(sql, new MapHandler(), oid);
		Order order = CommonUtils.toBean(map, Order.class);
		Order order2 = qr.query(sql, new BeanHandler<Order>(Order.class), oid);
		order.setOrderTime(order2.getOrderTime());
		order2=null;
		User user = CommonUtils.toBean(map, User.class);
		user=userDao.load(user.getUid());
		order.setOwner(user);
		loadOrderItem(order);//加载所有订单条目
		return order;	
	}
	
	/**
	 * 查询订单状态
	 * @throws SQLException 
	 */
	public int findOrderStatus(String oid) throws SQLException{
		String sql="select status from t_order where oid=?";
		Number num=(Number)qr.query(sql,new ScalarHandler(),oid);
		return num.intValue();
	}
	
	public void updateOrderStatus(String oid,int status) throws SQLException{
		String sql="update t_order set status=? where oid=?";
		qr.update(sql, status,oid);
	}
	
	/**
	 * 查询所有
	 */
	public PageBean<Order> findAll(int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * 按状态查询
	 * @param status
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findByStatus(int status, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("status", "=", status + ""));
		return findByCriteria(exprList, pc);
	}
	
//	public void deleteOrder(String oid) throws SQLException{
//		String sql="delete from t_order where oid=?";
//		qr.update(sql, oid);
//	}
	
}
