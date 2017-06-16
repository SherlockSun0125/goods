package goods.user.dao;

import goods.order.domain.Order;
import goods.order.domain.OrderItem;
import goods.page.Expression;
import goods.page.PageBean;
import goods.page.PageConstants;
import goods.user.domain.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import tools.jdbc.TxQueryRunner;

/**
 * 用户数据持久层
 * @author 14501_000
 */
public class UserDao {
	private QueryRunner qr=new TxQueryRunner();
	
	/**
	 * 校验用户名是否已存在
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean ajaxValidateLoginname(String loginname) throws SQLException{
		String sql = "select count(1) from t_user where loginname=?";
		Number number = (Number)qr.query(sql, new ScalarHandler(), loginname);
		return number.intValue() == 0;
	}

	/**
	 * email存在校验
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public boolean ajaxValidatePhone(String phone) throws SQLException{
		String sql = "select count(1) from t_user where phone=?";
		Number number = (Number)qr.query(sql, new ScalarHandler(), phone);
		return number.intValue() == 0;
	}
	
	/**
	 * 添加用户
	 * @param user
	 * @throws SQLException 
	 */
	public void add(User user) throws SQLException {
		String sql = "insert into t_user values(?,?,?,?)";
		Object[] params = {user.getUid(), user.getLoginname(), user.getLoginpass(),
				user.getPhone()};
		qr.update(sql, params);
	}
	
	public User findByLoginnameAndLoginpass(String loginname,String loginpass) throws SQLException{
		String sql = "select * from t_user where loginname=? and loginpass=?";
		return qr.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
	}
	
	/**
	 * 按uid和password查询
	 * @param uid
	 * @param password
	 * @return
	 * @throws SQLException 
	 */
	public boolean findByUidAndPassword(String uid, String password) throws SQLException {
		String sql = "select count(*) from t_user where uid=? and loginpass=?";
		Number number = (Number)qr.query(sql, new ScalarHandler(), uid, password);
		return number.intValue() > 0;
	}
	
	/**
	 * 修改密码
	 * @param uid
	 * @param password
	 * @throws SQLException
	 */
	public void updatePassword(String uid, String password) throws SQLException {
		String sql = "update t_user set loginpass=? where uid=?";
		qr.update(sql, password, uid);
	}
	
	/**
	 * 通用查询方法
	 */
	private PageBean<User> findByCriteria(List<Expression> exprList, int currentPage) throws SQLException{
		int pageSize = PageConstants.USER_PAGE_SIZE;//每页记录数
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

		String sql = "select count(*) from t_user" + whereSql;
		Number number = (Number)qr.query(sql, new ScalarHandler(), params.toArray());
		int totalRecords = number.intValue();//得到了总记录数
		sql = "select * from t_user" + whereSql + " order by uid limit ?,?";
		params.add((currentPage-1) * pageSize);//当前页首行记录的下标
		params.add(pageSize);//一共查询几行，就是每页记录数
		
		List<User> beanList = qr.query(sql, new BeanListHandler<User>(User.class), params.toArray());
		// 遍历每个订单，为其加载它的所有订单条目
//		for(User user : beanList) {
//			loadAllUser(user);
//		}
		PageBean<User> pb = new PageBean<User>();
		pb.setBeanList(beanList);
		pb.setCurrentPage(currentPage);
		pb.setPageSize(pageSize);
		pb.setTotalRecords(totalRecords);
		
		return pb;
	}
	
	/*
	 * 为指定的order载它的所有OrderItem
	 */
//	private PageBean<User> loadAllUser(int currentPage) throws SQLException {
//			List<Expression> exprList = new ArrayList<Expression>();
//			return findByCriteria(exprList, currentPage);
//	}

	
	/**
	 * 查询所有
	 */
	public PageBean<User> findAll(int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		return findByCriteria(exprList, pc);
	}
	
}
