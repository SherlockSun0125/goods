package goods.category.dao;

import goods.category.domain.Category;
import goods.order.domain.Order;
import goods.order.domain.OrderItem;
import goods.page.Expression;
import goods.page.PageBean;
import goods.page.PageConstants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import tools.commons.CommonUtils;
import tools.jdbc.TxQueryRunner;


public class CategoryDao {
private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 通用查询方法
	 */
	private PageBean<Category> findByCriteria(List<Expression> exprList, int currentPage) throws SQLException{
		int pageSize = PageConstants.CATEGORY_PAGE_SIZE;//每页记录数
		StringBuilder whereSql = new StringBuilder(" where 1=1"); 
		List<Object> params = new ArrayList<Object>();//SQL中有问号，它是对应问号的值
		for(Expression expr : exprList) {
			whereSql.append(" and ").append(expr.getName())
				.append(" ").append(expr.getOperator()).append(" ");
			// where 1=1 and bid = ?
			if(!expr.getOperator().equals("is null")) {
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}
	
		String sql = "select count(*) from t_category" + whereSql;
		Number number = (Number)qr.query(sql, new ScalarHandler(), params.toArray());
		int totalRecords = number.intValue();//得到了总记录数
		sql = "select * from t_category" + whereSql + " order by orderBy limit ?,?";
		params.add((currentPage-1) * pageSize);//当前页首行记录的下标
		params.add(pageSize);//一共查询几行，就是每页记录数
		
		List<Category> beanList = qr.query(sql, new BeanListHandler<Category>(Category.class), params.toArray());
//		List<Category> children = new ArrayList<Category>();
		
		// 遍历每个一级分类，为其加载它的所有二级分类
		for(Category category : beanList) {
			//for(Category child:children){
//				children=loadCategoryChildren(category);
				category.setChildren(loadCategoryChildren(category));
//				System.out.println("pb============category.getCid："+category.getCid());
//				System.out.println("pb============child："+category.getChildren());
		//	}
		}
		
		PageBean<Category> pb = new PageBean<Category>();
		//pb.setBeanList(findByParent("1"));
		
		pb.setBeanList(beanList);
		pb.setCurrentPage(currentPage);
		pb.setPageSize(pageSize);
		pb.setTotalRecords(totalRecords);
		
//		System.out.println("pb============getCurrentPage："+pb.getCurrentPage());
//		System.out.println("pb============getPageCount："+pb.getPageCount());
//		System.out.println("pb============getTotalRecords："+pb.getTotalRecords());
//		System.out.println("pb============getUrl："+pb.getUrl());
//		System.out.println("pb============getPageSize："+pb.getPageSize());
//		System.out.println("pb============getBeanList："+pb.getBeanList());
		return pb;
	}
	
	/*
	 * 为指定的category载它的所有二级分类
	 */
	private List<Category> loadCategoryChildren(Category category) throws SQLException {
		String sql="select * from t_Category where pid=?";
		List<Map<String,Object>> mapList=qr.query(sql, new MapListHandler(), category.getCid()); 
		//List<OrderItem> categoryItemList=toCategoryItemList(mapList);
		//category.setCategoryItemList(categoryItemList);
//		List<Category> children = new ArrayList<Category>;
		List<Category> children=new ArrayList<Category>();
		for(Map<String,Object> maplist:mapList){
//			for(Category child:children){
				children.add(toCategory(maplist));
//				child.setCid(toCategory(maplist).getCid());
//				child=toCategory(maplist);
//				System.out.println("loadCategoryChildren============Children："+children);
//				System.out.println("toCategory(maplist)============toCategory(maplist).getCid："+toCategory(maplist).getCid());
//				}
		}
		return children;
	}
	
	/*
	 * 把一个Map中的数据映射到Category中
	 */
	private Category toCategory(Map<String,Object> map) {
		/*
		 * map {cid:xx, cname:xx, pid:xx, desc:xx, orderBy:xx}
		 * Category{cid:xx, cname:xx, parent:(cid=pid), desc:xx}
		 */
		Category category = CommonUtils.toBean(map, Category.class);
		String pid = (String)map.get("pid");// 如果是一级分类，那么pid是null
		if(pid != null) {//如果父分类ID不为空，
			/*
			 * 使用一个父分类对象来拦截pid
			 * 再把父分类设置给category
			 */
			Category parent = new Category();
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	
	/*
	 * 可以把多个Map(List<Map>)映射成多个Category(List<Category>)
	 */
	private List<Category> toCategoryList(List<Map<String,Object>> mapList) {
		List<Category> categoryList = new ArrayList<Category>();//创建一个空集合
		for(Map<String,Object> map : mapList) {//循环遍历每个Map
			Category c = toCategory(map);//把一个Map转换成一个Category
			categoryList.add(c);//添加到集合中
		}
		return categoryList;//返回集合
	}
	
	/**
	 * 返回所有分类
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findAll() throws SQLException {
		/*
		 * 1. 查询出所有一级分类
		 */
		String sql = "select * from t_category where pid is null order by orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		
		List<Category> parents = toCategoryList(mapList);
		
		/*
		 * 2. 循环遍历所有的一级分类，为每个一级分类加载它的二级分类 
		 */
		for(Category parent : parents) {
			// 查询出当前父分类的所有子分类
			List<Category> children = findByParent(parent.getCid());
			// 设置给父分类
			parent.setChildren(children);
		}
		return parents;
	}
	
	public PageBean<Category> findAllParents(int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		Expression exp=new Expression("pid","is",null);
		exprList.add(exp);
		
		return findByCriteria(exprList,pc);
	}
	
	/**
	 * 通过父分类查询子分类
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findByParent(String pid) throws SQLException {
		String sql = "select * from t_category where pid=? order by orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(), pid);
		return toCategoryList(mapList);
	}
	
	/**
	 * 添加分类
	 * @param category
	 * @throws SQLException 
	 */
	public void add(Category category) throws SQLException {
		String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";
		/*
		 * 因为一级分类，没有parent，而二级分类有！
		 * 我们这个方法，要兼容两次分类，所以需要判断
		 */
		String pid = null;//一级分类
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCid(), category.getCname(), pid, category.getDesc()};
		qr.update(sql, params);
	}
	
	/**
	 * 获取所有父分类，但不带子分类的！
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findParents() throws SQLException {
		/*
		 * 1. 查询出所有一级分类
		 */
		String sql = "select * from t_category where pid is null order by orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		
		return toCategoryList(mapList);
	}
	
	/**
	 * 加载分类
	 * 即可加载一级分类，也可加载二级分类
	 * @param cid
	 * @return
	 * @throws SQLException 
	 */
	public Category load(String cid) throws SQLException {
		String sql = "select * from t_category where cid=?";
		return toCategory(qr.query(sql, new MapHandler(), cid));
	}
	
	/**
	 * 修改分类
	 * 即可修改一级分类，也可修改二级分类
	 * @param category
	 * @throws SQLException 
	 */
	public void edit(Category category) throws SQLException {
		String sql = "update t_category set cname=?, pid=?, `desc`=? where cid=?";
		String pid = null;
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCname(), pid, category.getDesc(), category.getCid()};
		qr.update(sql, params);
	}
	
	/**
	 * 查询指定父分类下子分类的个数
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public int findChildrenCountByParent(String pid) throws SQLException {
		String sql = "select count(*) from t_category where pid=?";
		Number cnt = (Number)qr.query(sql, new ScalarHandler(), pid);
		return cnt == null ? 0 : cnt.intValue();
	}
	
	/**
	 * 删除分类
	 * @param cid
	 * @throws SQLException 
	 */
	public void delete(String cid) throws SQLException {
		String sql = "delete from t_category where cid=?";
		qr.update(sql, cid);
	}
}
