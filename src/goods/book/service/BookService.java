package goods.book.service;

import java.sql.SQLException;

import goods.book.dao.BookDao;
import goods.book.domain.Book;
import goods.page.PageBean;

public class BookService {
	private BookDao bookDao=new BookDao();
	
	public PageBean<Book> findByCategory(String cid, int currentPage) {
		try {
			return bookDao.findByCategory(cid, currentPage);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public PageBean<Book> findByBname(String bname, int currentPage) {
		try {
			return bookDao.findByBname(bname, currentPage);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按作者查
	 * @param author
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByAuthor(String author, int pc) {
		try {
			return bookDao.findByAuthor(author, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//按id查询加载图书
	public Book loadBook(String bid){
		try {
			return bookDao.findByBid(bid);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	
	/**
	 * 添加图书
	 * @param book
	 */
	public void add(Book book) {
		try {
			bookDao.add(book);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 删除图书
	 * @param bid
	 */
	public void delete(String bid) {
		try {
			bookDao.delete(bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改图书
	 * @param book
	 */
	public void edit(Book book) {
		try {
			bookDao.edit(book);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 返回当前分类下图书个数
	 * @param cid
	 * @return
	 */
	public int findBookCountByCategory(String cid) {
		try {
			return bookDao.findBookCountByCategory(cid);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 按出版社查
	 * @param press
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByPress(String press, int pc) {
		try {
			return bookDao.findByPress(press, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 多条件组合查询
	 * @param criteria
	 * @param pc
	 * @return
	 */
	public PageBean<Book> findByCombination(Book criteria, int pc) {
		try {
			return bookDao.findByCombination(criteria, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
