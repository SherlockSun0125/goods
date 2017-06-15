package goods.user.dao;

import goods.user.domain.User;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import tools.commons.CommonUtils;
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

	public User load(String uid) throws SQLException{
		String sql="select * from t_user where uid=?";
		return toUser(qr.query(sql, new MapHandler(), uid));
	}

	public User toUser(Map<String, Object> map) {
		User user = CommonUtils.toBean(map, User.class);
		return user;
	}

}
