package cn.com.cis.module.drgsgroup.entity;


public enum GroupState {

	UNGROUP("0", "待分组"), BEGINGROUP("1", "正在分组"), FINISHEDGROUP("2", "分组完成"), ERRORGROUP("3", "分组异常");

	private String state;
	private String message;

	private GroupState(String state, String message) {
		this.state = state;
		this.message = message;
	}

	public String valueOfState() {
		return state;
	}

	public String valueOfMessage() {
		return message;
	}
	
	public String valueOfMessage(String message) {
		return message;
	}
	
	public static void main(String[] args) {
		System.out.println(GroupState.ERRORGROUP.valueOfMessage("ddsfds"));
	}
	
}
