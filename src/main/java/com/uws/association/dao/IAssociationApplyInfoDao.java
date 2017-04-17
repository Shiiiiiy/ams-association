package com.uws.association.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.sys.model.Dic;

/**
 * 
* @ClassName: IAssociationApplyInfoDao 
* @Description: 社团申请dao接口
* @author 联合永道
* @date 2016-1-12 下午4:40:20 
*
 */
public interface IAssociationApplyInfoDao extends IBaseDao
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
	 * @Title: IAssociationApplyInfoDao.java 
	 * @Package com.uws.association.dao 
	 * @Description:根据当前登录的指导老师和社团申请的id查询指导老师对象
	 * @author LiuChen 
	 * @date 2016-1-22 下午4:33:53
	 */
	public List<AssociationAdvisorModel> getAssociationAdvisorByApplyId(String applyId);

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
     * @Title: 指导人更新状态
     * @Package com.uws.association.service 
     * @Description: TODO
     * @author LiuChen 
     * @date 2016-1-28 上午10:16:56
     */
	public void updateAdvisorStatusByAssociationId(String associationId,Dic status);

	public void updateAdvisorByApplyId(String[] advisorIds, String applyId,String associationPo);
	
	/**
	 * 
	 * @Title: deleteRegisterAdvisor
	 * @Description: 删除注册指导人信息， 物理删除
	 * @param applyId
	 * @throws
	 */
	public void deleteRegisterAdvisor(String applyId);
}
