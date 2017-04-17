package com.uws.association.service;

import java.util.List;

import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.sys.model.Dic;

/**
 * 
* @ClassName: IAssociationApplyInfoService 
* @Description: 社团申请service接口
* @author 联合永道
* @date 2016-1-12 下午4:38:28 
*
 */
public interface IAssociationApplyInfoService extends IBaseService
{
	/**
	 * 
	 * @Title: pageQueryAssociationApply
	 * @Description: 社团信息的分页查询
	 * @param aam
	 * @param pageNo
	 * @param pageSize
	 * @param userId
	 * @param isAdvisor
	 * @return
	 * @throws
	 */
	public Page pageQueryAssociationApply(AssociationApplyModel aam, int pageNo,int pageSize,String userId,boolean isAdvisor);

	/**
	 * 
	 * @Title: getApplyModelById
	 * @Description: 按照主键查找
	 * @param applyId
	 * @return
	 * @throws
	 */
	public AssociationApplyModel getApplyModelById(String applyId);
	
	
	/**
	 * 
	 * @Title: saveOrUpdateRegister
	 * @Description: 注册信息修改
	 * @param applyModel
	 * @param fileId
	 * @param advisorIds
	 * @param memberIds
	 * @throws
	 */
	public void saveOrUpdateRegister(AssociationApplyModel applyModel,String[] fileId,String status);
	
	
	/**
	 * 
	 * @Title: 变更信息保存 提交
	 * @Package com.uws.association.service 
	 * @Description: TODO
	 * @author LiuChen 
	 * @date 2016-1-27 下午4:49:36
	 */
	public void saveOrUpdateChange(AssociationApplyModel applyModel,String[] fileId,String status);
	
	/**
	 * 
	 * @Title: deleteAssociationApplyInfo
	 * @Description:删除
	 * @param applyId
	 * @throws
	 */
	public void deleteAssociationApplyInfo(String applyId);
	
	/**
	 * 
	 * @Title: update
	 * @Description: 更新
	 * @param applyModel
	 * @throws
	 */
	public void update(AssociationApplyModel applyModel);
	
	/**
	 * 
	 * @Title: saveApplyModelByType
	 * @Description: 申请保存
	 * @param applyModel
	 * @param fileId
	 * @throws
	 */
	public void saveOrUpdateCancelApply(AssociationApplyModel applyModel,String[] fileId,String status,String associationId);
    
	/**
	 * 
	 * @Title: IAssociationApplyInfoService.java 
	 * @Package com.uws.association.service 
	 * @Description:根据当前登录的指导老师和社团申请的id查询指导老师对象
	 * @author LiuChen 
	 * @date 2016-1-22 下午4:30:20
	 */
	public List<AssociationAdvisorModel> getAssociationAdvisorByApplyId(String applyId);
    
	/**
	 * 
	 * @Title: IAssociationApplyInfoService.java 
	 * @Package com.uws.association.service 
	 * @Description: 根据id查询指导老师对象
	 * @author LiuChen 
	 * @date 2016-1-22 下午5:24:34
	 */
	public AssociationAdvisorModel getAssociationAdvisorById(String id);
    
	/**
	 * 
	 * @Title: IAssociationApplyInfoService.java 
	 * @Package com.uws.association.service 
	 * @Description:保存指导老师信息（简历）
	 * @author LiuChen 
	 * @date 2016-1-22 下午5:38:21
	 */
	public void updateAdvisor(AssociationAdvisorModel associationAdvisorPo);
    
	/**
	 * 
	 * @Title: IAssociationApplyInfoService.java 
	 * @Package com.uws.association.service 
	 * @Description: 保存社团基本信息
	 * @author LiuChen 
	 * @date 2016-1-27 下午6:48:55
	 */
	public void saveBaseinfoModel(AssociationBaseinfoModel baseAssociationModel);
	
	/**
	 * 
	 * @Title: IAssociationApplyInfoService.java 
	 * @Package com.uws.association.service 
	 * @Description: 修改社团基本信息
	 * @author LiuChen 
	 * @date 2016-1-27 下午6:49:14
	 */
	public void updateBaseinfoModel(AssociationBaseinfoModel associationPo);
	
	/**
	 * 
	 * @Title: getAssociationAdvisor
	 * @Description: 查询指导老师信息
	 * @param applyId
	 * @param teacherId
	 * @return
	 * @throws
	 */
	public AssociationAdvisorModel getAssociationAdvisor(String applyId,String teacherId);
    
	/**
	 * 
	 * @Title: IAssociationApplyInfoService.java 
	 * @Package com.uws.association.service 
	 * @Description: 保存申请信息
	 * @author LiuChen 
	 * @date 2016-1-26 上午11:32:25
	 */
	public void updateApplyModel(AssociationApplyModel associationApplyModel);
	
     /**
      * 
      * @Title: 指导人更新状态
      * @Package com.uws.association.service 
      * @Description: TODO
      * @author LiuChen 
      * @date 2016-1-28 上午10:16:56
      */
	public void updateAdvisorStatusByAssociationId(String associationId,Dic status);
    
	/**
	 * 指导人跟新社团
	 * @Title: IAssociationApplyInfoService.java 
	 * @Package com.uws.association.service 
	 * @Description: TODO
	 * @author LiuChen 
	 * @date 2016-1-28 上午10:43:28
	 */
	public void updateAdvisorByApplyId(String[] advisorIds, String id,String associationPo);
	
}
