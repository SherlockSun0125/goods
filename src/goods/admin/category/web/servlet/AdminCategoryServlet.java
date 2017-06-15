package goods.admin.category.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.commons.CommonUtils;
import tools.servlet.BaseServlet;

import goods.book.service.BookService;
import goods.category.domain.Category;
import goods.category.service.CategoryService;

public class AdminCategoryServlet extends BaseServlet{
	private CategoryService categoryService =new CategoryService();	
	private BookService bookService = new BookService();
	
	/**
	 * 查询所有分类
	 */
	public String findAll(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException, IOException{
		List<Category> list=categoryService.findAll();
		req.setAttribute("parents",list);
		return "f:/adminjsps/admin/category/list.jsp";
	}
	
	/**
	 * 添加一级分类
	 * 1、封装表单数据到category中
	 * 2、调用service的add()方法完成添加
	 * 3调用findAll(),返回list.jsp显示所有分类
	 */
	public String addParent(HttpServletRequest req,HttpServletResponse resp)
		throws ServletException, IOException{
		Category parent=CommonUtils.toBean(req.getParameterMap(), Category.class);
		parent.setCid(CommonUtils.uuid());//设置cid
		categoryService.add(parent);
		return findAll(req,resp);
	}
	
	/**
	 * 1.封装表单数据到category中
	 * 2.需要手动的把表单中的pid映射到child对象中
	 * 3.调用service的add（）方法完成添加
	 * 4.调用findAll(),返回list.jsp显示所有分类
	 */
	public String addChild(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		
		Category child=CommonUtils.toBean(req.getParameterMap(), Category.class);
		child.setCid(CommonUtils.uuid());//设置cid
		
		//手动映射pid
		String pid=req.getParameter("pid");
		Category parent =new Category();
		parent.setCid(pid);
		child.setParent(parent);
		
		categoryService.add(child);
		return findAll(req,resp);
	}
	
	/**
	 * 添加二级分类：第一步
	 */
	public String addChildPre(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		String pid = req.getParameter("pid");//当前点击的父分类id
		List<Category> parents=categoryService.findParents();
		req.setAttribute("pid", pid);
		req.setAttribute("parents", parents);
		/*
		 * 测试是否成功连接数据库
		 * List<Category> list= parents;
		 * for(Category out: parents){
		 * System.out.println(pid+"================"+out);
		 * System.out.println();
		 * }
		 */
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	
	/*
	 * 修改一级分类：第一步
	 * 1.获取连接中过的cid
	 * 2.使用cid加载category
	 * 3.保存Category
	 * 4.转发到edit.jsp页面显示Category
	 */
	public String editParentPre(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		
		String cid = req.getParameter("cid");
		Category parent = categoryService.load(cid);
		req.setAttribute("parent", parent);
		return "f:/adminjsps/admin/category/edit.jsp";
	}
	/*
	 * 修改一级分类：第二步
	 * 1.封装表单数据到category中
	 * 2.调用service方法完成修改
	 * 3.转发list.jsp显示所有分类(return fidAll();)
	 */
	public String editParent(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		categoryService.edit(parent);
		return findAll(req,resp);
	}
	
	/**
	 * 修改二级分类：第一步
	 * 1.获取链接参数cid,通过cid加载category，并保存
	 * 2.查询所有1级分类并保存
	 * 3.转发到edit2.jsp
	 */
	public String editChildPre(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		String cid=req.getParameter("cid");
		Category child=categoryService.load(cid);
		req.setAttribute("child", child);
		req.setAttribute("parents", categoryService.findParents());
		
		return "f:/adminjsps/admin/category/edit2.jsp";
	}
	
	/**
	 * 修改二级分类：第二步
	 * 1.封装表单参数到Category child
	 * 2.把表单中的pid封装到child
	 * 3.调用service.edit()完成修改
	 * 4.返回到list.jsp
	 */
	public String editChild(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		String pid = req.getParameter("pid");
		Category parent=new Category();
		parent.setCid(pid);
		child.setParent(parent);
		
		categoryService.edit(child);
		return findAll(req,resp);
	}
	
	/**
	 * 删除一级分类
	 */
	public String deleteParent(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		String cid=req.getParameter("cid");//获取一级分类id
		int cnt = categoryService.findChildrenCountByParent(cid);//获取该父分类下子分类的个数
		if(cnt>0){
			req.setAttribute("msg", "改分类下还有子类，不能删除！");//如果大于零，保存错误信息，转发到msg.jsp
			return "f:/adminjsps/msg.jsp";
		}else{
			categoryService.delete(cid);
			return findAll(req,resp);//如果等于0，删除，并返回list.jsp
		}	
	}
	
	public String deleteChild(HttpServletRequest req,HttpServletResponse resp)
			throws ServletException,IOException{
		String cid=req.getParameter("cid");//获取一级分类id
		//int cnt = bookService.findBookCountByCategory(cid);//获取该父分类下子分类的个数
		int cnt=bookService.findBookCountByCategory(cid);
		//System.out.println(cnt+"++++++++++");
		if(cnt>0){
			//System.out.println("nothing"+"=================");
			req.setAttribute("msg", "改分类下还有图书，不能删除！");//如果大于零，保存错误信息，转发到msg.jsp
			return "f:/adminjsps/msg.jsp";
		}else{
			//System.out.println(cid+"=================");
			categoryService.delete(cid);
			//return null;
			return findAll(req,resp);//如果等于0，删除，并返回list.jsp
		}	
	}

}
