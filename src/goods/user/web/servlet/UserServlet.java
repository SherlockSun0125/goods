package goods.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tools.commons.CommonUtils;
import tools.servlet.BaseServlet;

import goods.user.domain.User;
import goods.user.service.UserService;
import goods.user.service.exception.UserException;

/**
 * 用户模块控制层
 * 
 * @author 14501_000
 */
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();

	// 用户名校验
	public String ajaxValidateLoginname(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		// 1.获取用户名
		String loginname = req.getParameter("loginname");

		// 2.service校验
		boolean b = userService.ajaxValidateLoginname(loginname);
		// 3.响应请求,发给客户端
		resp.getWriter().print(b);
		return null;
	}

	// phone校验
	public String ajaxValidatePhone(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		// 1.获取phone
		String email = req.getParameter("phone");

		// 2.service校验
		boolean b = userService.ajaxValidatePhone(email);

		// 3.响应请求,发给客户端
		resp.getWriter().print(b);
		return null;
	}

	// 验证码校验
	public String ajaxValidateVerifyCode(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		String verifyCode = req.getParameter("verifyCode");
		String vCode = (String) req.getSession().getAttribute("vCode");
		boolean b = verifyCode.equalsIgnoreCase(vCode);
		resp.getWriter().print(b);
		return null;
	}

	// 注册
	public String regist(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("----------开始注册-----------");
		// 1. 封装表单数据到User对象
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		System.out.println(formUser.toString());

		// 2.完整表单校验
		Map<String, String> errors = validateRegist(formUser, req.getSession());
		System.out.println("error:"+errors);
		if (errors.size() > 0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		// 3. 使用service完成业务
		userService.regist(formUser);
		

		// 4. 保存成功信息，转发到msg.jsp显示！
		req.setAttribute("code", "success");
		req.setAttribute("msg", "注册成功！");
		return "f:/jsps/msg.jsp";
	}

	/*
	 * 注册校验 对表单的字段进行逐个校验，如果有错误，使用当前字段名称为key，错误信息为value，保存到map中 返回map
	 */
	private Map<String, String> validateRegist(User formUser,
			HttpSession session) {
		Map<String, String> errors = new HashMap<String, String>();
		/*
		 * 1. 校验登录名
		 */
		String loginname = formUser.getLoginname();
		if (loginname == null || loginname.trim().isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		} else if (loginname.length() < 3 || loginname.length() > 20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		} else if (!userService.ajaxValidateLoginname(loginname)) {
			errors.put("loginname", "用户名已被注册！");
		}

		/*
		 * 2. 校验登录密码
		 */
		String loginpass = formUser.getLoginpass();
		if (loginpass == null || loginpass.trim().isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		} else if (loginpass.length() < 3 || loginpass.length() > 20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}

		/*
		 * 3. 确认密码校验
		 */
		String reloginpass = formUser.getReloginpass();
		if (reloginpass == null || reloginpass.trim().isEmpty()) {
			errors.put("reloginpass", "确认密码不能为空！");
		} else if (!reloginpass.equals(loginpass)) {
			errors.put("reloginpass", "两次输入不一致！");
		}

		/*
		 * 4. 校验phone
		 */
		String phone = formUser.getPhone();
		if (phone == null || phone.trim().isEmpty()) {
			errors.put("email", "Email不能为空！");
		} else if (!phone
				.matches("^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")) {
			errors.put("phone", "手机号码格式错误！");
		} else if (!userService.ajaxValidatePhone(phone)) {
			errors.put("phone", "该手机号已被注册！");
		}

		/*
		 * 5. 验证码校验
		 */
		String verifyCode = formUser.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if (verifyCode == null || verifyCode.trim().isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		} else if (!verifyCode.equalsIgnoreCase(vcode)) {
			errors.put("verifyCode", "验证码错误！");
		}

		return errors;
	}

	// 用户登录
	public String login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		Map<String, String> errors = validateLogin(formUser, req.getSession());
		if (errors.size() > 0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/login.jsp";
		}
		User user = userService.login(formUser);
		if (user == null) {
			req.setAttribute("msg", "用户名或密码错误！");
			req.setAttribute("user", formUser);
			return "f:/jsps/user/login.jsp";
		} else {
			
			HttpSession session=req.getSession();
			// 保存用户到session
			session.setAttribute("sessionUser", user);
			// 获取用户名保存到cookie中
			String loginname = user.getLoginname();
			loginname = URLEncoder.encode(loginname, "utf-8");
			Cookie cookie = new Cookie("loginname", loginname);
			cookie.setMaxAge(60 * 60 * 24 * 10);// 保存10天
			resp.addCookie(cookie);
			return "r:/index.jsp";// 重定向到主页
		}
	}

	/*
	 * 登录校验方法
	 */
	private Map<String, String> validateLogin(User formUser, HttpSession session) {
		Map<String, String> errors = new HashMap<String, String>();
		return errors;
	}
	
	/**
	 * 修改密码　
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		User user = (User)req.getSession().getAttribute("sessionUser");
		// 如果用户没有登录，返回到登录页面，显示错误信息
		if(user == null) {
			req.setAttribute("msg", "您还没有登录！");
			return "f:/jsps/user/login.jsp";
		}
		
		try {
			userService.updatePassword(user.getUid(), formUser.getReloginpass(), 
					formUser.getLoginpass());
			req.setAttribute("msg", "修改密码成功");
			req.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());//保存异常信息到request
			req.setAttribute("user", formUser);//为了回显
			return "f:/jsps/user/pwd.jsp";
		}
	}
	
	public String quit(HttpServletRequest req, HttpServletResponse resp){
		req.getSession().invalidate();//注销用户信息
		return "f:/jsps/main.jsp";
	}

}
