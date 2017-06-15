package goods.admin.admin.service;

import goods.admin.admin.dao.AdminDao;
import goods.admin.admin.domain.Admin;

import java.sql.SQLException;

public class AdminService {
	private AdminDao adminDao = new AdminDao();
	
	/**
	 * 登录功能
	 */
	public Admin login(Admin admin){
		try{
			return adminDao.find(admin.getAdminname(), admin.getAdminpwd());
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
}
