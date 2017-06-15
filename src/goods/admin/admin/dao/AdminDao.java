package goods.admin.admin.dao;

import goods.admin.admin.domain.Admin;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import tools.jdbc.TxQueryRunner;

public class AdminDao {
	private QueryRunner qr = new TxQueryRunner();
	/**
	 * 通过管理员登录名和密码进行查询
	 */
	public Admin find(String adminname,String adminpwd)throws SQLException{
		String sql="select * from t_admin where adminname=? and adminpwd=?";
		return qr.query(sql,new BeanHandler<Admin>(Admin.class),adminname,adminpwd);
	
	}
}
