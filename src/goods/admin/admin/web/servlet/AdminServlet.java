package goods.admin.admin.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.commons.CommonUtils;
import tools.servlet.BaseServlet;

import goods.admin.admin.domain.Admin;
import goods.admin.admin.service.AdminService;

public class AdminServlet extends BaseServlet {
	private AdminService adminService = new AdminService();
	
	/**
	 * 登录功能
	 */
	public String login(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		/**
		 * 封装表单数据到Admin
		 */
		Admin form=CommonUtils.toBean(req.getParameterMap(), Admin.class);
		Admin admin=adminService.login(form);
		if(admin==null){
			req.setAttribute("msg","用户名或密码错误！");
			return "/adminjsps/login.jsp";
		}
		req.getSession().setAttribute("admin", admin);
		return "r:/adminjsps/admin/index.jsp";
	}
}
