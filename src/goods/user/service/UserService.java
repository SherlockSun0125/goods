package goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import tools.commons.CommonUtils;
import goods.user.service.exception.UserException;

import goods.user.dao.UserDao;
import goods.user.domain.User;

/**
 * 用户模块业务层
 * @author 14501_000
 */
public class UserService {
	private UserDao userDao=new UserDao();
	
	//用户名校验
	public boolean ajaxValidateLoginname(String loginname) {
		try {
			return userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	//email校验
	public boolean ajaxValidatePhone(String phone){
		try {
			return userDao.ajaxValidatePhone(phone);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	
	/**
	 * 注册功能
	 * @param user
	 */
	public void regist(User user) {
		/*
		 * 1. 数据的补齐
		 */
		user.setUid(CommonUtils.uuid());
		/*
		 * 2. 向数据库插入
		 */
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public User login(User user) {
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改密码
	 * @param uid
	 * @param newPass
	 * @param oldPass
	 * @throws UserException 
	 */
	public void updatePassword(String uid, String newPass, String oldPass) throws UserException{

		try {
			//
			boolean bool = userDao.findByUidAndPassword(uid, oldPass);
			if(!bool) {//如果老密码错误
				throw new UserException("老密码错误！");
			}
			//2. 修改密码
			userDao.updatePassword(uid, newPass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
