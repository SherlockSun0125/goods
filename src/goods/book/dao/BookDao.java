package goods.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import goods.book.domain.Book;
import goods.category.dao.CategoryDao;
import goods.category.domain.Category;
import goods.page.Expression;
import goods.page.PageBean;
import goods.page.PageConstants;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import tools.commons.CommonUtils;
import tools.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	private CategoryDao categoryDao=new CategoryDao();

	/**
	 * 通用查询方法
	 */
	private PageBean<Book> findByCriteria(List<Expression> exprList,
			int currentPage) throws SQLException {
		int pageSize = PageConstants.BOOK_PAGE_SIZE;// 每页记录数
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		List<Object> params = new ArrayList<Object>();// SQL中有问号，它是对应问号的值
		for (Expression expr : exprList) {
			whereSql.append(" and ").append(expr.getName()).append(" ")
					.append(expr.getOperator()).append(" ");
			// where 1=1 and bid = ?
			if (!expr.getOperator().equals("is null")) {
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}

		String sql = "select count(*) from t_book" + whereSql;
		Number number = (Number) qr.query(sql, new ScalarHandler(),
				params.toArray());
		int totalRecords = number.intValue();// 得到了总记录数
		sql = "select * from t_book" + whereSql + " order by orderBy limit ?,?";
		params.add((currentPage - 1) * pageSize);// 当前页首行记录的下标
		params.add(pageSize);// 一共查询几行，就是每页记录数

		List<Book> beanList = qr.query(sql, new BeanListHandler<Book>(
				Book.class), params.toArray());
		PageBean<Book> pb = new PageBean<Book>();
		pb.setBeanList(beanList);
		pb.setCurrentPage(currentPage);
		pb.setPageSize(pageSize);
		pb.setTotalRecords(totalRecords);

		return pb;
	}

	/**
	 * 按照目录查询
	 * 
	 * @param cid
	 * @param currentPage
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCategory(String cid, int currentPage)
			throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("cid", "=", cid));
		return findByCriteria(exprList, currentPage);
	}

	/**
	 * 按书名模糊查询
	 * 
	 * @param bname
	 * @param currentPage
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname, int currentPage)
			throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname", "like", "%" + bname + "%"));
		return findByCriteria(exprList, currentPage);
	}

	/**
	 * 按bid查询
	 * 
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public Book findByBid(String bid) throws SQLException {
		String sql = "select * from t_book where bid=?";
		Map<String, Object> map = qr.query(sql, new MapHandler(), bid);
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		category=categoryDao.load(category.getCid());
		book.setCategory(category);
		// 把pid获取出来，创建一个Category parnet，把pid赋给它，然后再把parent赋给category
		if (map.get("pid") != null) {
			Category parent = new Category();
			parent.setCid((String) map.get("pid"));
			category.setParent(parent);
		}
		return book;
	}

	/**
	 * 添加图书
	 * 
	 * @param book
	 * @throws SQLException
	 */
	public void add(Book book) throws SQLException {
		String sql = "insert into t_book(bid,bname,author,price,currPrice,"
				+ "discount,press,publishtime,edition,pageNum,wordNum,printtime,"
				+ "booksize,paper,cid,image_w,image_b)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = { book.getBid(), book.getBname(), book.getAuthor(),
				book.getPrice(), book.getCurrPrice(), book.getDiscount(),
				book.getPress(), book.getPublishtime(), book.getEdition(),
				book.getPageNum(), book.getWordNum(), book.getPrinttime(),
				book.getBooksize(), book.getPaper(),
				book.getCategory().getCid(), book.getImage_w(),
				book.getImage_b() };
		qr.update(sql, params);
	}

	public void edit(Book book) throws SQLException {
		String sql = "update t_book set bname=?,author=?,price=?,currPrice=?,"
				+ "discount=?,press=?,publishtime=?,edition=?,pageNum=?,wordNum=?,"
				+ "printtime=?,booksize=?,paper=?,cid=? where bid=?";
		Object[] params = { book.getBname(), book.getAuthor(), book.getPrice(),
				book.getCurrPrice(), book.getDiscount(), book.getPress(),
				book.getPublishtime(), book.getEdition(), book.getPageNum(),
				book.getWordNum(), book.getPrinttime(), book.getBooksize(),
				book.getPaper(), book.getCategory().getCid(), book.getBid() };
		qr.update(sql, params);
	}

	/**
	 * 删除图书
	 * 
	 * @param bid
	 * @throws SQLException
	 */
	public void delete(String bid) throws SQLException {
		String sql = "delete from t_book where bid=?";
		qr.update(sql, bid);
	}

	/**
	 * 查询指定分类下图书的个数
	 * 
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public int findBookCountByCategory(String cid) throws SQLException {
		String sql = "select count(*) from t_book where cid=?";
		Number cnt = (Number) qr.query(sql, new ScalarHandler(), cid);
		return cnt == null ? 0 : cnt.intValue();
	}

	/**
	 * 按作者查
	 * 
	 * @param bname
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author, int pc)
			throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("author", "like", "%" + author + "%"));
		return findByCriteria(exprList, pc);
	}

	/**
	 * 按出版社查
	 * 
	 * @param press
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("press", "like", "%" + press + "%"));
		return findByCriteria(exprList, pc);
	}

	/**
	 * 多条件组合查询
	 */
	public PageBean<Book> findByCombination(Book criteria, int pc)
			throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname", "like", "%" + criteria.getBname()
				+ "%"));
		exprList.add(new Expression("author", "like", "%"
				+ criteria.getAuthor() + "%"));
		exprList.add(new Expression("press", "like", "%" + criteria.getPress()
				+ "%"));
		return findByCriteria(exprList, pc);
	}

}
