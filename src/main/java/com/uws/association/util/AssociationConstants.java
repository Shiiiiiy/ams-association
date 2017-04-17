package com.uws.association.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

public class AssociationConstants {
	
	/**
	 * 数据字典工具类
	 */
	private static DicUtil dicUtil=DicFactory.getDicUtil();
	
	/**
	 * 默认分页大小
	 */
	public static int DEFALT_PAGE_SIZE=5;
	
	/**
	 * 社团负责人数【默认】
	 */
	public static int associationMembers=3;

	public static final String NAMESPACE = "/association";
	/**
	 * 社团注册申请
	 */
	public static final String NAMESPACE_APPLY = "/association/apply";
	/**
	 * 社团注册审批
	 */
	public static final String NAMESPACE_APPROVE = "/association/approve";
	/**
	 * 社团维护
	 */
	public static final String NAMESPACE_MAINTAIN = "/association/maintain";
	/**
	 * 社团报名【申请】
	 */
	public static final String NAMESPACE_REPORT = "/association/report";
	/**
	 * 社员服务
	 */
	public static final String NAMESPACE_SERVICE = "/association/service";
	/**
	 * 社团监管分析
	 */
	public static final String NAMESPACE_STATISTIC = "/association/statistic";
	
	public static enum APPLY_STATUS{
		/**
		 * 新增注册
		 */
		REGISTER,
		/**
		 * 变更注册
		 */
		MODIFY,
		/**
		 * 注销注册
		 */
		CANCEL
	}
	
	public static enum ASSOCIATION_USER_TYPE{
		/**
		 * 社团负责人
		 */
		MANAGER,
		/**
		 * 指导老师
		 */
		ADVISOR
		
	}
	
	public static enum OPERATE_STATUS{
		/**
		 * 负责人保存
		 */
		MANAGER_SAVE,
		/**
		 * 负责人提交
		 */
		MANAGER_SUBMIT,
		/**
		 * 指导老师保存
		 */
		ADVISOR_SAVE,
		/**
		 * 指导老师提交
		 */
		ADVISOR_SUBMIT
	}
	
	/**
	 * 社团附件枚举
	 */
	public static enum ATTACHE_TYPE{
		/**
		 * 新增附件
		 */
		REGISTER,
		/**
		 * 变更附件
		 */
		MODIFY,
		/**
		 * 注销附件
		 */
		CANCEL,
		/**
		 * 财务附件
		 */
		FINANCE
	}
	
	/**
	 * 社团申请类型
	 */
	public static List<Dic> applyTypeList = dicUtil.getDicInfoList("ASSOCIATION_APPLY_TYPE");
	
	/**
	 * 社团申请类型-注册申请
	 */
	public static final Dic registerDic = dicUtil.getDicInfo("ASSOCIATION_APPLY_TYPE", "REGISTER");
	
	/**
	 * 变更申请
	 */
	public static final Dic changeDic = dicUtil.getDicInfo("ASSOCIATION_APPLY_TYPE", "MODIFY");
	
	/**
	 * 注销申请
	 */
	public static final Dic cancelDic = dicUtil.getDicInfo("ASSOCIATION_APPLY_TYPE", "CANCEL");
	
	/**
	 * 社团类型
	 */
	public static final List<Dic> associationTypeList = dicUtil.getDicInfoList("ASSOCIATION_TYPE");
	
	/**
	 * 社团类型
	 */
	public static enum ASSOCIATION_TYPE_ENUM{
		/**
		 * 理论学习
		 */
		THEORY_LEARNING,
		/**
		 * 兴趣爱好
		 */
		INTEREST_HOBBY,
		/**
		 * 学习科学
		 */
		SCIENCE_LEARNING,
		/**
		 * 文娱体育
		 */
		RECREATION,
		/**
		 * 志愿服务
		 */
		VOLUNTEERING,
		/**
		 * 社会实践
		 */
		SOCIAL_PRACTICE
	}
	
	/**
	 * 分页列表类型
	 */
	public static enum LIST_STYPE{
		/**
		 * 指导老师
		 */
		LIST_ADVISOR,
		/**
		 * 负责人
		 */
		LIST_MANAGER
	}
	
	/**
	 * 社团负责人
	 */
	public static final List<Dic> associationManagerList = dicUtil.getDicInfoList("ASSOCIATION_MANAGER");
	
	/**
	 * 社团负责人-社长
	 */
	public static final Dic ASSOCIATION_MANAGER_PROPRIETER = dicUtil.getDicInfo("ASSOCIATION_MANAGER", "PROPRIETER");
	
	/**
	 * 社团成员
	 */
	public static final Dic ASSOCIATION_MEMBER = dicUtil.getDicInfo("ASSOCIATION_MANAGER", "MEMBER");
	
	/**
	 * 社团负责人-副社长
	 */
	public static final Dic ASSOCIATION_MANAGER_PROPRIETER_DEPUTY = dicUtil.getDicInfo("ASSOCIATION_MANAGER", "PROPRIETER_DEPUTY");
	
	/**
	 * 业务操作类型
	 */
	public static enum OPT_TYPE{
		/**
		 * 新增
		 */
		ADD,
		/**
		 * 修改
		 */
		UPDATE,
		/**
		 * 回滚
		 */
		ROLLBACK
	}
	
	public static enum MODIFY_TYPE{
		/**
		 * 社团名称
		 */
		ASSOCIATION_NAME,
		/**
		 * 社团性质
		 */
		IS_MAJOR,
		/**
		 * 社团类型
		 */
		ASSOCIATION_TYPE,
		/**
		 * 指导老师
		 */
		ASSOCIATION_ADVISOR,
		/**
		 * 负责人
		 */
		ASSOCIATION_MANAGER,
		/**
		 * 其他
		 */
		OTHERS
	}
	
	/**
	 * 获取报名审批状态
	 */
	public static final Map<String,String> getApproveModifyItemMap(){
		Map<String,String> map = new HashMap<String,String>();
		map.put(MODIFY_TYPE.ASSOCIATION_NAME.toString(),"社团名称");
		map.put(MODIFY_TYPE.IS_MAJOR.toString(),"社团性质");
		map.put(MODIFY_TYPE.ASSOCIATION_TYPE.toString(),"社团类型");
		map.put(MODIFY_TYPE.ASSOCIATION_ADVISOR.toString(),"指导老师");
		map.put(MODIFY_TYPE.ASSOCIATION_MANAGER.toString(),"负责人");
		map.put(MODIFY_TYPE.OTHERS.toString(),"其他");
		return  map;
	}

	/**
	 * 状态  保存和 提交对应的 常量 
	 */
	public static final String STATUS_SAVE_STRING = "SAVE";
	public static final String STATUS_SUBMIT_STRING = "SUBMIT";
	
}
