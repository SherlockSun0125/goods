package goods.book.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;

import tools.servlet.BaseServlet;

import goods.book.domain.Book;
import goods.book.service.BookService;
import goods.page.PageBean;

public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();

	// 按照目录查询
	public String findByCategory(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		int pc = getCurrentPage(req);
		String url = getUrl(req);
		String cid = req.getParameter("cid");
		PageBean<Book> pb = bookService.findByCategory(cid, pc);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}

	// 得到当前页
	private int getCurrentPage(HttpServletRequest req) {
		int currentPage = 1;
		String param = req.getParameter("currentPage");
		if (param != null && !param.trim().isEmpty()) {
			try {
				currentPage = Integer.parseInt(param);
			} catch (RuntimeException e) {
			}
		}
		return currentPage;
	}

	// 获取URL
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		// 如果url中存在currentPage参数，截取掉，如果不存在那就不用截取。
		int index = url.lastIndexOf("&currentPage=");
		if (index != -1) {
			url = url.substring(0, index);
		}
		return url;
	}

	// 按照书名模糊查询
	public String findByBName(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int currentPage = getCurrentPage(req);
		String url = getUrl(req);
		String bname = req.getParameter("bname");
		PageBean<Book> pb = bookService.findByBname(bname, currentPage);
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}

	// 加载图书详细信息
	public String loadBook(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String bid=req.getParameter("bid");
		Book book=bookService.loadBook(bid);
		req.setAttribute("book",book);
		return "f:/jsps/book/desc.jsp";
	}
}
