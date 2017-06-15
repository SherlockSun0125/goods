package goods.user.domain;

/**
 * 用户实体类
 * @author 14501_000
 */
public class User {
	private String uid;//用户id,主键
	private String loginname;//登录名
	private String loginpass;//密码
	private String phone;//邮箱
	
	//注册表单
	private String reloginpass;//确认密码
	private String verifyCode;//验证码
	
	//修改密码表单
	private String newloginpass;//新密码
	public String getReloginpass() {
		return reloginpass;
	}
	public void setReloginpass(String reloginpass) {
		this.reloginpass = reloginpass;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public String getNewloginpass() {
		return newloginpass;
	}
	public void setNewloginpass(String newloginpass) {
		this.newloginpass = newloginpass;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	public String getLoginpass() {
		return loginpass;
	}
	public void setLoginpass(String loginpass) {
		this.loginpass = loginpass;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "User [uid=" + uid + ", loginname=" + loginname + ", loginpass="
				+ loginpass + ", phone=" + phone + ", reloginpass="
				+ reloginpass + ", newloginpass=" + newloginpass + "]";
	}

}
