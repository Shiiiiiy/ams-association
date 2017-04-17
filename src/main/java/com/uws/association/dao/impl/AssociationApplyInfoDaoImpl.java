package com.uws.association.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.association.dao.IAssociationApplyInfoDao;
import com.uws.association.util.AssociationConstants;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/**
 * 
* @ClassName: AssociationApplyInfoDaoImpl 
* @Description:  社团申请
* @author 联合永道
* @date 2016-1-12 下午4:41:10 
*
 */
@Repository("com.uws.association.dao.impl.AssociationApplyInfoDaoImpl")
public class AssociationApplyInfoDaoImpl extends BaseDaoImpl implements IAssociationApplyInfoDao
{
	private DicUtil dicUtil = DicFactory.getDicUtil();
	/**
	 * 描述信息: 申请信息列表
	 * @param aam
	 * @param pageNo
	 * @param pageSize
	 * @param userId
	 * @param isAdvisor
	 * @return
	 * 2016-1-12 下午3:52:08
	 */
	@Override
    public Page pageQueryAssociationApply(AssociationApplyModel aam,int pageNo, int pageSize, String userId, boolean isAdvisor)
    {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationApplyModel where 1=1 and  deleteStatus = ? ");
		values.add(dicUtil.getStatusNormal());
		/**
		 * 过滤是指导人 还是社团负责人， 在申请中 将人员信息维护准确
		 */
		if(isAdvisor){
			hql.append(" and operateStatus != ?  and ( (orignAdvisorId like ? and  applyTypeDic != ?)  or ( changedAdvisorId like ? and  applyTypeDic = ? ) )");
			values.add(AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());
			values.add("%"+userId+",%");
			values.add(AssociationConstants.changeDic);
			values.add("%"+userId+",%");
			values.add(AssociationConstants.changeDic);
		}else{
			hql.append(" and orignManagerId like ? ");
			values.add("%"+userId+",%");
		}
		
		if(null!=aam)
		{
			if(null!=aam.getCollege() && !StringUtils.isEmpty(aam.getCollege().getId()))
			{
				hql.append(" and college.id = ? ");
				values.add(aam.getCollege().getId());
			}
			if(!StringUtils.isEmpty(aam.getChangedAssociationName()))
			{
				hql.append(" and orginAssociationName like ? ");
				if (HqlEscapeUtil.IsNeedEscape(aam.getOrignAssociationName())) {
					values.add("%" + HqlEscapeUtil.escape(aam.getOrignAssociationName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else
					values.add("%" + aam.getChangedAssociationName() + "%");
			}
			if(null!=aam.getOrignAssociationType() && !StringUtils.isEmpty(aam.getOrignAssociationType().getId()))
			{
				hql.append(" and changedAssociationType.id = ? ");
				values.add(aam.getOrignAssociationType().getId());
			}
			if(null!=aam.getApplyTypeDic() && !StringUtils.isEmpty(aam.getApplyTypeDic().getId()))
			{
				hql.append(" and applyTypeDic.id = ? ");
				values.add(aam.getApplyTypeDic().getId());
			}
		}
		
		//排序
		hql.append(" order by createTime ");
		
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
    }
	
	/**
	 * 
	 * @Description: 
	 * @author 根据当前社团申请的id查询指导老师对象
	 * @date 2016-1-22 下午4:39:38
	 */
	@SuppressWarnings("unchecked")
    @Override
	public List<AssociationAdvisorModel> getAssociationAdvisorByApplyId(String applyId)
	{
		return this.query("from AssociationAdvisorModel aam where aam.associationApplyModel.id=? and aam.deleteStatus.id = ? ", new Object[] {applyId,this.dicUtil.getStatusNormal().getId()});
	}
	
	/**
	 * 描述信息: 指导老师查询
	 * @param applyId
	 * @param teacherId
	 * @return
	 * 2016-1-25 下午5:12:12
	 */
	@Override
    public AssociationAdvisorModel getAssociationAdvisor(String applyId, String teacherId)
    {
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(teacherId)){
			String hql=" from AssociationAdvisorModel aam where aam.associationApplyModel.id=? and aam.advisorPo.id=?";
			return (AssociationAdvisorModel)this.queryUnique(hql, new Object[]{applyId,teacherId});
		}
		return null;
    }

	@Override
    public void updateAdvisorStatusByAssociationId(String associationId, Dic status)
    {
		if(!StringUtils.isEmpty(associationId) && null != status)
		{
			this.executeHql("update AssociationAdvisorModel set deleteStatus = ?  where associationPo.id = ? ", new Object[]{status,associationId});
		}
    }
	
	

	/**
	 * 描述信息: 删除注册指导人信息
	 * @param applyId
	 * 2016-1-28 上午10:32:39
	 */
	@Override
    public void deleteRegisterAdvisor(String applyId)
    {
		if(!StringUtils.isEmpty(applyId))
		{
			this.executeHql("delete from AssociationAdvisorModel where associationApplyModel.id = ? ", new Object[]{applyId});
		}
    }
	
	@Override
	public void updateAdvisorByApplyId(String[] advisorIds, String applyId,String associationPo)
	{
		if(!StringUtils.isEmpty(applyId) && !ArrayUtils.isEmpty(advisorIds))
		{
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("associationPoId", associationPo);
			param.put("applyId", applyId);
			param.put("advisorIds", advisorIds);
			this.executeHql("update AssociationAdvisorModel set associationPo.id = :associationPoId  where associationApplyModel.id = :applyId and advisorPo.id in (:advisorIds)", param);
		}
	}
}
