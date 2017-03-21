package com.shinemo.publish.constants;

public class ProjectConstants {

	/** 项目发布类型，war/静态资源 **/
	public static int PROJECT_TYPE_JAVA = 0;
	public static int PROJECT_TYPE_RESOURCE = 1;
	public static int PROJECT_TYPE_GULP_RESOURCE = 2;

	/**** 项目中的用户类型 普通用户/管理员 ****/
	public static int PROJECT_USER_TYPE_USER = 0;
	public static int PROJECT_USER_TYPE_ADMIN = 1;

	/**
	 * 发布单状态
	 */
	public static int APPLY_FLAG_DEL = -1;
	public static int APPLY_FLAG_UNAUDITED = 0;
	public static int APPLY_FLAG_NORMAL = 1;
	public static int APPLY_FLAG_AUDIT_NO_PASS = 2;

	/**
	 * 发布单step 具体到哪一步
	 */
	public final static int APPLY_STATUS_START = 0;
	public final static int APPLY_STATUS_GIT = 1;
	public final static int APPLY_STATUS_BUILD_OK = 2;
	public final static int APPLY_STATUS_BUILD_FAILED = 3;
	public final static int APPLY_STATUS_SYNC_PRE_OK = 4;
	public final static int APPLY_STATUS_SYNC_PRE_FAILED = 5;
	public final static int APPLY_STATUS_RESTART_PRE_OK = 6;
	public final static int APPLY_STATUS_RESTART_PRE_FAILED = 7;
	public final static int APPLY_STATUS_SYNC_ONLINE_READY = 8;	//需要审核
	
	public final static int APPLY_STATUS_BUILD_ONLINE_OK = 10;
	public final static int APPLY_STATUS_BUILD_ONLINE_FAILED = 11;
	public final static int APPLY_STATUS_SYNC_ONLINE_ING = 12;
	public final static int APPLY_STATUS_PUB_ONLINE_OK = 13;
	public final static int APPLY_STATUS_FINISH = 15;
	public final static int APPLY_STATUS_ROLLBACK = 17;

	
	/*** 用户属性 ****/
	public static int USER_FLAG_DISABLE = 0;
	public static int USER_FLAG_ENABLE = 1;
	public static int USER_FLAG_DEL = -1;
	
	public static int USER_TYPE_USER = 0;
	public static int USER_TYPE_ADMIN = 1;
	public static int USER_TYPE_SUPERADMIN = 2;
	
	
	
	public static String parseStatus(int status){
		switch (status) {
		case APPLY_STATUS_START:
			return "创建成功";
		case APPLY_STATUS_GIT:
			return "代码更新";
		case APPLY_STATUS_BUILD_OK:
			return "预发编译成功";
		case APPLY_STATUS_BUILD_FAILED:
			return "预发编译失败";
		case APPLY_STATUS_SYNC_PRE_OK:
			return "预发同步成功";
		case APPLY_STATUS_RESTART_PRE_OK:
			return "预发重启成功";
		case APPLY_STATUS_RESTART_PRE_FAILED:
			return "预发重启失败";
		case APPLY_STATUS_SYNC_PRE_FAILED:
			return "预发同步失败";
		case APPLY_STATUS_SYNC_ONLINE_READY:
			return "准备发布";
		case APPLY_STATUS_BUILD_ONLINE_OK:
			return "线上编译成功";
		case APPLY_STATUS_BUILD_ONLINE_FAILED:
			return "线上编译失败";
		case APPLY_STATUS_SYNC_ONLINE_ING:
			return "线上发布中";	
		case APPLY_STATUS_PUB_ONLINE_OK:
			return "线上同步完成";
		case APPLY_STATUS_FINISH:
			return "发布已完成";
		case APPLY_STATUS_ROLLBACK:
			return "发布单已回滚";
		default:
			return "未知";
		}
	}

}
