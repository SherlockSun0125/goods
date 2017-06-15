package goods.page;

import java.util.List;

/**
 * 分页Bean
 */
public class PageBean<T> {
	private int currentPage;//当前页码
	private int totalRecords;//总记录数
	private int pageSize;//每页记录数
	private String url;//请求路径和参数
	private List<T> beanList;
	
	//计算总页数
	public int getPageCount(){
		int tp=totalRecords/pageSize;
		return totalRecords%pageSize==0?tp:tp+1;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<T> getBeanList() {
		return beanList;
	}

	public void setBeanList(List<T> beanList) {
		this.beanList = beanList;
	}

	
}
