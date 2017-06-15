package goods.order.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tools.jdbc.JdbcUtils;

import goods.order.dao.OrderDao;
import goods.order.domain.Order;
import goods.page.Expression;
import goods.page.PageBean;

public class OrderService {
	OrderDao orderDao=new OrderDao();
	
	public Order loadOrder(String oid){
		try {
			JdbcUtils.beginTransaction();
			Order order=orderDao.loadOrder(oid);
			JdbcUtils.commitTransaction();
			return order;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	
	//生成订单
	public void createOrder(Order order){
		try {
			JdbcUtils.beginTransaction();
			orderDao.add(order);
			JdbcUtils.commitTransaction();
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
		
	}
	
	
	public PageBean<Order> myOrders(String uid,int currentPage){
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb=orderDao.findByUser(uid, currentPage);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 查询订单状态
	 * @param oid
	 */
	public int findOrderStatus(String oid){
		try {
			return orderDao.findOrderStatus(oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 更新订单状态
	 * @param oid
	 * @param status
	 */
	public void updateStatus(String oid,int status){
		try {
			orderDao.updateOrderStatus(oid, status);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 查询所有
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findAll(int pc) {
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = orderDao.findAll(pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按状态查询
	 * @param status
	 * @param pc
	 * @return
	 */
	public PageBean<Order> findByStatus(int status, int pc) {
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = orderDao.findByStatus(status, pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}


}
