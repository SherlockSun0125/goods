package goods.category.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servlet.BaseServlet;

import goods.category.domain.Category;
import goods.category.service.CategoryService;

@SuppressWarnings("serial")
public class CategoryServlet extends BaseServlet{
	CategoryService categoryService=new CategoryService();
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
	 * 查询所有分类
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 通过service得到所有的分类
		 * 2. 保存到request中，转发到left.jsp
		 */
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return "f:/jsps/left.jsp";
	}

}
