package com.uws.association.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.association.dao.IAssociationDao;
import com.uws.association.util.AssociationConstants;
import com.uws.common.util.CYLeagueUtil;
import com.uws.common.util.Constants;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.domain.association.AssociationAttacheModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.domain.association.AssociationHonorModel;
import com.uws.domain.association.AssociationMemberModel;
import com.uws.domain.association.AssociationTempUserModel;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

@Repository("associationDao")
@SuppressWarnings("all")
public class AssociationDaoImpl  extends BaseDaoImpl implements IAssociationDao {

	/**
	 * 数据字典工具类
	 */
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	/**
	 * session工具类
	 */
	private SessionUtil sessionUtil = SessionFactory.getSession(AssociationConstants.NAMESPACE);

	@Override
	public Page pageQueryAssociationBaseInfo(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationBaseinfoModel abm where 1=1 ");
		
	     //学院查询
	     if(DataUtil.isNotNull(abm.getCollege()) && DataUtil.isNotNull(abm.getCollege().getId())){
	    	 hql.append(" and abm.college.id=?");
	    	 values.add(abm.getCollege().getId());
	     }
	     
	     //社团编号查询
	     if(DataUtil.isNotNull(abm.getAssociationCode())){
			hql.append(" and abm.associationCode like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationCode()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(abm.getAssociationCode()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" +abm.getAssociationCode() + "%");
	         }
	     }
		
	     //社团名称查询
	     if (!StringUtils.isEmpty(abm.getAssociationName())) {
	         hql.append(" and abm.associationName like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationName()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(abm.getAssociationName()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" + abm.getAssociationName() + "%");
	         }
	       }

		//社团类型查询
		if(DataUtil.isNotNull(abm.getAssociationType()) && DataUtil.isNotNull(abm.getAssociationType().getId())){
			hql.append(" and abm.associationType.id=?");
			values.add(abm.getAssociationType().getId());
		}
		
		//社团注销状态
		if(DataUtil.isNotNull(abm.getIsCancel()) && DataUtil.isNotNull(abm.getIsCancel().getId())){
			hql.append(" and abm.isCancel = ?");
			values.add(abm.getIsCancel());
		}
		
		//社团注销状态
//		hql.append(" and abm.isCancel.id=?");
//		values.add(this.dicUtil.getDicInfo("Y&N", "N").getId());
		
//		//社团有效状态【注销成功后的社团为无效状态】
//		hql.append(" and abm.isValid.id=?");
//		values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
		
		//社团删除状态
		hql.append(" and abm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by abm.updateTime desc");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	

	@Override
	public Page pageQueryReportAssociationInfo(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationBaseinfoModel abm where 1=1 ");
		
	     //学院查询
	     if(DataUtil.isNotNull(abm.getCollege()) && DataUtil.isNotNull(abm.getCollege().getId())){
	    	 hql.append(" and abm.college.id=?");
	    	 values.add(abm.getCollege().getId());
	     }
	     
	     //社团编号查询
	     if(DataUtil.isNotNull(abm.getAssociationCode())){
			hql.append(" and abm.associationCode like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationCode()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(abm.getAssociationCode()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" +abm.getAssociationCode() + "%");
	         }
	     }
		
	     //社团名称查询
	     if (!StringUtils.isEmpty(abm.getAssociationName())) {
	         hql.append(" and abm.associationName like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationName()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(abm.getAssociationName()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" + abm.getAssociationName() + "%");
	         }
	       }

		//社团类型查询
		if(DataUtil.isNotNull(abm.getAssociationType()) && DataUtil.isNotNull(abm.getAssociationType().getId())){
			hql.append(" and abm.associationType.id=?");
			values.add(abm.getAssociationType().getId());
		}
		//社团状态为有效状态
		hql.append(" and abm.isValid.id=?");
		values.add(Constants.STATUS_YES.getId());
		//社团注销状态
		hql.append(" and abm.isCancel.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "N").getId());
		
		//社团删除状态
		hql.append(" and abm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by abm.updateTime desc");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}

	/**
	 * 查询社长参加的社团
	 */
	@Override
	public Page pageQueryAssociationByMember(AssociationMemberModel amm,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select amm.associationPo from AssociationMemberModel amm where 1=1 ");
		
	     //社团名称查询
	     if (DataUtil.isNotNull(amm.getAssociationPo()) && 
	    	   DataUtil.isNotNull(amm.getAssociationPo().getAssociationName())) {
	         hql.append(" and amm.associationPo.associationName like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(amm.getAssociationPo().getAssociationName()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(amm.getAssociationPo().getAssociationName()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" + amm.getAssociationPo().getAssociationName() + "%");
	         }
	       }

		//社团类型查询
		if(DataUtil.isNotNull(amm.getAssociationPo()) && 
			 DataUtil.isNotNull(amm.getAssociationPo().getAssociationType()) && 
			 DataUtil.isNotNull(amm.getAssociationPo().getAssociationType().getId())){
			hql.append(" and amm.associationPo.associationType.id=?");
			values.add(amm.getAssociationPo().getAssociationType().getId());
		}
		
		//当前用户是社团成员
		hql.append(" and amm.memberPo.id=?");
		values.add(this.sessionUtil.getCurrentUserId());
		
		//当前社团成员是社团负责人
		hql.append(" and amm.isManager.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
		
		//当前社团成员为社长
		hql.append(" and amm.leaguePosition.id=?");
		values.add(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER.getId());
		
		//未注销的社团
		hql.append(" and amm.associationPo.isCancel.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "N").getId());
		
		//社团有效状态为“有效”
		hql.append(" and amm.associationPo.isValid.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
		
		//社团删除状态为未删除
		hql.append(" and amm.associationPo.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by amm.associationPo.updateTime desc");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	/**
	 * 查询负责人参加的社团
	 */
	@Override
	public Page pageQueryAssociationByManager(AssociationMemberModel amm,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select amm.associationPo from AssociationMemberModel amm where 1=1 ");
		
		//社团名称查询
		if (DataUtil.isNotNull(amm.getAssociationPo()) && 
				DataUtil.isNotNull(amm.getAssociationPo().getAssociationName())) {
			hql.append(" and amm.associationPo.associationName like ? ");
			if (HqlEscapeUtil.IsNeedEscape(amm.getAssociationPo().getAssociationName()))
			{
				values.add("%" + HqlEscapeUtil.escape(amm.getAssociationPo().getAssociationName()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" + amm.getAssociationPo().getAssociationName() + "%");
			}
		}
		
		//社团类型查询
		if(DataUtil.isNotNull(amm.getAssociationPo()) && 
				DataUtil.isNotNull(amm.getAssociationPo().getAssociationType()) && 
				DataUtil.isNotNull(amm.getAssociationPo().getAssociationType().getId())){
			hql.append(" and amm.associationPo.associationType.id=?");
			values.add(amm.getAssociationPo().getAssociationType().getId());
		}
		
		//当前用户是社团成员
		hql.append(" and amm.memberPo.id=?");
		values.add(this.sessionUtil.getCurrentUserId());
		
		//当前社团成员是社团负责人
		hql.append(" and amm.isManager.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
		
		//未注销的社团
		hql.append(" and amm.associationPo.isCancel.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "N").getId());
		
		//社团有效状态为“有效”
		hql.append(" and amm.associationPo.isValid.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
		
		//社团删除状态为未删除
		hql.append(" and amm.associationPo.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by amm.associationPo.updateTime desc");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	/**
	 * 查询社员参加的社团
	 */
	@Override
	public Page pageQueryAssociationByMember_(AssociationMemberModel amm,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select amm.associationPo from AssociationMemberModel amm where 1=1 ");
		
		//当前用户是社团成员
		hql.append(" and amm.memberPo.id=?");
		values.add(this.sessionUtil.getCurrentUserId());
		
		//未注销的社团
		hql.append(" and amm.associationPo.isCancel.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "N").getId());
		
		//社团有效状态为“有效”
		hql.append(" and amm.associationPo.isValid.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
		
		//社团删除状态为未删除
		hql.append(" and amm.associationPo.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by amm.associationPo.updateTime desc");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	@Override
	public List<AssociationBaseinfoModel> getAssociationByMember(String memberId){
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select amm.associationPo from AssociationMemberModel amm where 1=1 ");
		
		//当前用户是社团成员
		hql.append(" and amm.memberPo.id=?");
		values.add(memberId);
		
		//未注销的社团
		hql.append(" and amm.associationPo.isCancel.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "N").getId());
		
		//社团有效状态为“有效”
		hql.append(" and amm.associationPo.isValid.id=?");
		values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
		
		//社团删除状态为未删除
		hql.append(" and amm.associationPo.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by amm.associationPo.updateTime desc");
		
		return this.query(hql.toString(), values.toArray());
	}
	
	@Override
	public Page pageQueryAssociationApplyInfo(AssociationApplyModel aam,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationApplyModel aam where 1=1 ");
		
		//学院查询条件
		if(DataUtil.isNotNull(aam) && 
			DataUtil.isNotNull(aam.getCollege())&& 
			DataUtil.isNotNull(aam.getCollege().getId())){
			hql.append(" and aam.college.id=?");
			values.add(aam.getAssociationPo().getCollege().getId());
		}
		
		//申请类型查询条件
		if(DataUtil.isNotNull(aam) && 
			 DataUtil.isNotNull(aam.getApplyTypeDic())&& 
			 DataUtil.isNotNull(aam.getApplyTypeDic().getId())){
			hql.append(" and aam.applyTypeDic.id=?");
			values.add(aam.getApplyTypeDic().getId());
		}
		
		//社团类型查询条件
		if(DataUtil.isNotNull(aam) && 
				DataUtil.isNotNull(aam.getOrignAssociationType())&& 
				DataUtil.isNotNull(aam.getOrignAssociationType().getId())){
				hql.append(" and aam.orignAssociationType.id=?");
				values.add(aam.getOrignAssociationType().getId());
			}
		
	    /* //社团名称查询
	     if (DataUtil.isNotNull(aam.getAssociationPo()) && 
	    	   DataUtil.isNotNull(aam.getAssociationPo().getAssociationName())) {
	         hql.append(" and aam.associationPo.associationName like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationName()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationName()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" + aam.getAssociationPo().getAssociationName() + "%");
	         }
	       }
	     
	     //社团编号查询
	     if(DataUtil.isNotNull(aam.getAssociationPo()) && 
	    	  DataUtil.isNotNull(aam.getAssociationPo().getAssociationCode())){
			 hql.append(" and aam.associationPo.associationCode like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationCode()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationCode()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" +aam.getAssociationPo().getAssociationCode()+ "%");
	         }
	     }
*/
		//申请状态查询条件
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getApplyStatus())){
			hql.append(" and aam.applyStatus=?");
			values.add(aam.getApplyStatus());
		}
		
		hql.append(" and aam.nextapprover.id='"+this.sessionUtil.getCurrentUserId()+"'");
		
		hql.append(" order by aam.updateTime desc,aam.applyTypeDic.code");

		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	@Override
	public Page pageQueryAssociationApplyByAdvisor(AssociationApplyModel aam,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		String curUserId = this.sessionUtil.getCurrentUserId();
		StringBuffer hql = new StringBuffer("select distinct aam from AssociationApplyModel aam ,AssociationAdvisorModel aamr,AssociationTempUserModel atum where 1=1 ");
		hql.append(" and aam.associationPo.id=aamr.associationPo.id");
		hql.append(" and ((aam.applyTypeDic.code='"+AssociationConstants.APPLY_STATUS.MODIFY.toString()+"' " );
		hql.append(" 	 and aam.modifyItem like '%"+AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR+"%' ");
		hql.append(" 	 and aam.associationPo.id=atum.associationPo.id and atum.userId='"+curUserId+"') or");
		hql.append(" 	 aamr.advisorPo.code='"+curUserId+"')");
		/*StringBuffer hql = new StringBuffer("select distinct aam from AssociationApplyModel aam ,AssociationAdvisorModel aamr where 1=1 ");
		hql.append(" and aam.associationPo.id=aamr.associationPo.id");
		hql.append(" and ((aam.applyTypeDic.code='"+AssociationConstants.APPLY_STATUS.MODIFY.toString()+"' " );
		hql.append(" 	 and aam.modifyItem like '%"+AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR+"%' ");
		hql.append(" 	 and aam.associationPo.id=atum.associationPo.id and atum.userId='"+curUserId+"') or");
		hql.append(" and	 aamr.advisorPo.code='"+curUserId+"')");*/
		
		
		//学院查询条件
		if(DataUtil.isNotNull(aam) && 
			DataUtil.isNotNull(aam.getAssociationPo()) && 
			DataUtil.isNotNull(aam.getAssociationPo().getCollege())&& 
			DataUtil.isNotNull(aam.getAssociationPo().getCollege().getId())){
			hql.append(" and aam.associationPo.college.id=?");
			values.add(aam.getAssociationPo().getCollege().getId());
		}
		
		//申请类型查询条件
		if(DataUtil.isNotNull(aam) && 
			 DataUtil.isNotNull(aam.getApplyTypeDic())&& 
			 DataUtil.isNotNull(aam.getApplyTypeDic().getId())){
			hql.append(" and aam.applyTypeDic.id=?");
			values.add(aam.getApplyTypeDic().getId());
		}
		
		//社团类型查询条件
		if(DataUtil.isNotNull(aam) && 
				DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationType())&& 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationType().getId())){
				hql.append(" and aam.associationPo.associationType.id=?");
				values.add(aam.getAssociationPo().getAssociationType().getId());
			}
		
	     //社团名称查询
	     if (DataUtil.isNotNull(aam.getAssociationPo()) && 
	    	   DataUtil.isNotNull(aam.getAssociationPo().getAssociationName())) {
	         hql.append(" and aam.associationPo.associationName like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationName()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationName()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" + aam.getAssociationPo().getAssociationName() + "%");
	         }
	       }
	     
	     //社团编号查询
	     if(DataUtil.isNotNull(aam.getAssociationPo()) && 
	    	  DataUtil.isNotNull(aam.getAssociationPo().getAssociationCode())){
			 hql.append(" and aam.associationPo.associationCode like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationCode()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationCode()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" +aam.getAssociationPo().getAssociationCode()+ "%");
	         }
	     }

		//申请状态查询条件
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getApplyStatus())){
			hql.append(" and aam.applyStatus=?");
			values.add(aam.getApplyStatus());
		}
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	@Override
	public Page pageQueryAssociationApplyByMember(AssociationApplyModel aam,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		String curUserId = this.sessionUtil.getCurrentUserId();
		StringBuffer hql = new StringBuffer("select aam from AssociationApplyModel aam , AssociationMemberModel amm where 1=1 ");
		hql.append(" and aam.associationPo.id=amm.associationPo.id");
		hql.append(" and amm.memberPo.id='"+curUserId+"'");
		hql.append(" and amm.leaguePosition.code='PROPRIETER'");
		
		//学院查询条件
		if(DataUtil.isNotNull(aam) && 
				DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getCollege())&& 
				DataUtil.isNotNull(aam.getAssociationPo().getCollege().getId())){
			hql.append(" and aam.associationPo.college.id=?");
			values.add(aam.getAssociationPo().getCollege().getId());
		}
		
		//申请类型查询条件
		if(DataUtil.isNotNull(aam) && 
				DataUtil.isNotNull(aam.getApplyTypeDic())&& 
				DataUtil.isNotNull(aam.getApplyTypeDic().getId())){
			hql.append(" and aam.applyTypeDic.id=?");
			values.add(aam.getApplyTypeDic().getId());
		}
		
		//社团类型查询条件
		if(DataUtil.isNotNull(aam) && 
				DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationType())&& 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationType().getId())){
			hql.append(" and aam.associationPo.associationType.id=?");
			values.add(aam.getAssociationPo().getAssociationType().getId());
		}
		
		//社团名称查询
		if (DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationName())) {
			hql.append(" and aam.associationPo.associationName like ? ");
			if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationName()))
			{
				values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationName()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" + aam.getAssociationPo().getAssociationName() + "%");
			}
		}
		
		//社团编号查询
		if(DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationCode())){
			hql.append(" and aam.associationPo.associationCode like ? ");
			if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationCode()))
			{
				values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationCode()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" +aam.getAssociationPo().getAssociationCode()+ "%");
			}
		}
		
		//申请状态查询条件
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getApplyStatus())){
			hql.append(" and aam.applyStatus=?");
			values.add(aam.getApplyStatus());
		}
		
		
		hql.append(" order by aam.updateTime desc,aam.associationPo.associationCode asc,aam.applyTypeDic.code");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	@Override
	public List<AssociationApplyModel> getAssociationApplyList(AssociationApplyModel aam) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationApplyModel aam where 1=1 ");
		
		//学院查询条件
		if(DataUtil.isNotNull(aam) && 
				DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getCollege())&& 
				DataUtil.isNotNull(aam.getAssociationPo().getCollege().getId())){
			hql.append(" and aam.associationPo.college.id=?");
			values.add(aam.getAssociationPo().getCollege().getId());
		}
		
		//申请类型查询条件
		if(DataUtil.isNotNull(aam) && 
				DataUtil.isNotNull(aam.getApplyTypeDic())&& 
				DataUtil.isNotNull(aam.getApplyTypeDic().getId())){
			hql.append(" and aam.applyTypeDic.id=?");
			values.add(aam.getApplyTypeDic().getId());
		}
		
		//社团类型查询条件
		if(DataUtil.isNotNull(aam) && 
				DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationType())&& 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationType().getId())){
			hql.append(" and aam.associationPo.associationType.id=?");
			values.add(aam.getAssociationPo().getAssociationType().getId());
		}
		
		//社团名称查询
		if (DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationName())) {
			hql.append(" and aam.associationPo.associationName like ? ");
			if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationName()))
			{
				values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationName()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" + aam.getAssociationPo().getAssociationName() + "%");
			}
		}
		
		//社团编号查询
		if(DataUtil.isNotNull(aam.getAssociationPo()) && 
				DataUtil.isNotNull(aam.getAssociationPo().getAssociationCode())){
			hql.append(" and aam.associationPo.associationCode like ? ");
			if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationCode()))
			{
				values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationCode()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" +aam.getAssociationPo().getAssociationCode()+ "%");
			}
		}
		
		//申请状态查询条件
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getApplyStatus())){
			hql.append(" and aam.applyStatus=?");
			values.add(aam.getApplyStatus());
		}
		
		hql.append(" order by aam.associationPo.associationCode asc,aam.applyTypeDic.code, aam.updateTime desc");
		
		return this.query(hql.toString(), values.toArray());
	}
	
	@Override
	public Page pageQueryAssociationAdvisor(String associationId,String teacherIdsConditon,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationAdvisorModel aam where 1=1 ");
		
		if(DataUtil.isNotNull(associationId)){
			hql.append(" and aam.associationPo.id=?");
			values.add(associationId);
		}
		
		if(DataUtil.isNotNull(teacherIdsConditon) && !teacherIdsConditon.equals("()")){
			hql.append(" and aam.advisorPo.id in "+teacherIdsConditon);
		}
		
		hql.append(" and aam.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by aam.advisorPo.code asc");
		
		if(DataUtil.isNotNull(teacherIdsConditon)){
			if (values.size() == 0){
				return this.pagedQuery(hql.toString(), pageNo, pageSize);
			}else{
				return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
			}
		}else{
			return new Page();
		}
	}

	@Override
	public Page pageQueryAssociationAdvisor(String associationId, int pageNo,int pageSize) {
		if(DataUtil.isNotNull(associationId)){
			List<Object> values = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer("from AssociationAdvisorModel aam where 1=1 ");
			
			if(DataUtil.isNotNull(associationId)){
				hql.append(" and aam.associationPo.id=?");
				values.add(associationId);
			}
			
			hql.append(" and aam.deleteStatus.id=?");
			values.add(this.dicUtil.getStatusNormal().getId());
			
			hql.append(" order by aam.advisorPo.code asc");
			
			if (values.size() == 0){
				return this.pagedQuery(hql.toString(), pageNo, pageSize);
			}else{
				return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
			}
		}else{
			return new Page();
		}
	}
	
	@Override
	public Page pageQueryAssociationByAdvisor(AssociationAdvisorModel aam, int pageNo,int pageSize) {
			List<Object> values = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer("seelct aam.associationPo from AssociationAdvisorModel aam where 1=1 ");
			
			//社团名称查询
			if (DataUtil.isNotNull(aam.getAssociationPo()) && 
					DataUtil.isNotNull(aam.getAssociationPo().getAssociationName())) {
				hql.append(" and aam.associationPo.associationName like ? ");
				if (HqlEscapeUtil.IsNeedEscape(aam.getAssociationPo().getAssociationName()))
				{
					values.add("%" + HqlEscapeUtil.escape(aam.getAssociationPo().getAssociationName()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				}else {
					values.add("%" + aam.getAssociationPo().getAssociationName() + "%");
				}
			}
			
			//社团类型查询
			if(DataUtil.isNotNull(aam.getAssociationPo()) && 
					DataUtil.isNotNull(aam.getAssociationPo().getAssociationType()) && 
					DataUtil.isNotNull(aam.getAssociationPo().getAssociationType().getId())){
				hql.append(" and aam.associationPo.associationType.id=?");
				values.add(aam.getAssociationPo().getAssociationType().getId());
			}
			
			//当前用户是指导老师
			hql.append(" and aam.advisorPo.code=?");
			values.add(this.sessionUtil.getCurrentUserId());
			
			//有效的指导老师
			hql.append(" and aam.deleteStatus.id=?");
			values.add(this.dicUtil.getStatusNormal().getId());
			
			//未注销的社团
			hql.append(" and aam.associationPo.isCancel.id=?");
			values.add(this.dicUtil.getDicInfo("Y&N", "N").getId());
			
			//社团有效状态为“有效”
			hql.append(" and aam.associationPo.isValid.id=?");
			values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
			
			//社团删除状态为未删除
			hql.append(" and aam.associationPo.deleteStatus.id=?");
			values.add(this.dicUtil.getStatusNormal().getId());
			
			hql.append(" order by aam.associationPo.updateTime desc");

			if (values.size() == 0){
				return this.pagedQuery(hql.toString(), pageNo, pageSize);
			}else{
				return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
			}
	}
	
	@Override
	public Page pageQueryAssociationMember(AssociationBaseinfoModel abm,String managerIdsConditon,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationMemberModel amm where 1=1 ");
		
		if(DataUtil.isNotNull(abm) && DataUtil.isNotNull(abm.getId())){
			hql.append(" and amm.associationPo.id=?");
			values.add(abm.getId());
		}
		
		if(DataUtil.isNotNull(managerIdsConditon)&& !managerIdsConditon.equals("()")){
			hql.append(" and amm.memberPo.id in"+managerIdsConditon);
		}
		
		hql.append(" and amm.isManager.id=?");
		values.add(Constants.STATUS_YES.getId());
		
		hql.append(" and amm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by amm.leaguePosition.id,amm.memberPo.stuNumber asc");
		
		if(DataUtil.isNotNull(managerIdsConditon)){
			if (values.size() == 0){
				return this.pagedQuery(hql.toString(), pageNo, pageSize);
			}else{
				return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
			}
		}else{
			return new Page();
		}
	}

	@Override
	public Page pageQueryAssociationMember(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		if(DataUtil.isNotNull(abm) && DataUtil.isNotNull(abm.getId())){
			List<Object> values = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer("from AssociationMemberModel amm where 1=1 ");
			
			if(DataUtil.isNotNull(abm) && DataUtil.isNotNull(abm.getId())){
				hql.append(" and amm.associationPo.id=?");
				values.add(abm.getId());
			}
			
			hql.append(" and amm.isManager.id=?");
			values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
			
			hql.append(" and amm.deleteStatus.id=?");
			values.add(this.dicUtil.getStatusNormal().getId());
			
			hql.append(" order by amm.leaguePosition.id,amm.memberPo.stuNumber asc");
			if (values.size() == 0){
				return this.pagedQuery(hql.toString(), pageNo, pageSize);
			}else{
				return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
			}
		}else{
			return new Page();
		}
	}
	

	@Override
	public Page pageQueryAssociationMember_(AssociationMemberModel amm,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationMemberModel amm where 1=1 ");
		
		//社团查询条件
		if(DataUtil.isNotNull(amm) && 
			 DataUtil.isNotNull(amm.getAssociationPo())&&
			 DataUtil.isNotNull(amm.getAssociationPo().getId())){
			 hql.append(" and amm.associationPo.id=?");
			 values.add(amm.getAssociationPo().getId());
		}
		
		//学院查询条件
		if(DataUtil.isNotNull(amm) && 
				DataUtil.isNotNull(amm.getAssociationPo()) && 
				DataUtil.isNotNull(amm.getAssociationPo().getCollege())&& 
				DataUtil.isNotNull(amm.getAssociationPo().getCollege().getId())){
				hql.append(" and amm.associationPo.college.id=?");
				values.add(amm.getAssociationPo().getCollege().getId());
			}
		
		//姓名查询条件
	     if (DataUtil.isNotNull(amm.getMemberPo())&&
	    	   DataUtil.isNotNull(amm.getMemberPo().getName())) {
	         hql.append(" and amm.memberPo.name like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(amm.getMemberPo().getName()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(amm.getMemberPo().getName()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" + amm.getMemberPo().getName() + "%");
	         }
	       }
	     
	     //学号查询条件
	     if(DataUtil.isNotNull(amm.getMemberPo())&&
		      DataUtil.isNotNull(amm.getMemberPo().getStuNumber())){
	          hql.append(" and amm.memberPo.stuNumber=? ");
	          values.add(amm.getMemberPo().getStuNumber().trim());
	     }
	     
	     //成员状态
	     if(DataUtil.isNotNull(amm.getMemberStatus())&&
	    		 DataUtil.isNotNull(amm.getMemberStatus().getCode())){
	    	 hql.append(" and amm.memberStatus.code=? ");
	    	 values.add(amm.getMemberStatus().getCode());
	     }
	     
	     //性别查询条件
	     if(DataUtil.isNotNull(amm.getMemberPo())&&
	    	  DataUtil.isNotNull(amm.getMemberPo().getGenderDic())&&
	    	  DataUtil.isNotNull(amm.getMemberPo().getGenderDic().getId())){
	    	 hql.append(" and amm.memberPo.genderDic.id=? ");
	    	 values.add(amm.getMemberPo().getGenderDic().getId());
	     }

		hql.append(" and amm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by amm.isManager.id,amm.leaguePosition.id,amm.memberPo.stuNumber asc");
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	@Override
	public Page pageQueryAssociationHonor(AssociationMemberModel am,AssociationHonorModel honor,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationHonorModel ahm where 1=1 ");
		
		if(DataUtil.isNotNull(am)&&
			 DataUtil.isNotNull(am.getAssociationPo())&&
			 DataUtil.isNotNull(am.getAssociationPo().getId())){
			 hql.append(" and ahm.associationPo.id=?");
			 values.add(am.getAssociationPo().getId());
		}

		if(DataUtil.isNotNull(am) && 
			 DataUtil.isNotNull(am.getMemberPo())&&
			 DataUtil.isNotNull(am.getMemberPo().getId())){
			 hql.append(" and ahm.member.memberPo.id=?");
			 values.add(am.getMemberPo().getId());
		}
		if(DataUtil.isNotNull(honor)){
			//学年
			if(DataUtil.isNotNull(honor.getHonorYear())&&DataUtil.isNotNull(honor.getHonorYear().getId())){
				 hql.append(" and ahm.honorYear.id=?");
				 values.add(honor.getHonorYear().getId());
			}
			//学期
			if(DataUtil.isNotNull(honor.getHonorTerm())&&DataUtil.isNotNull(honor.getHonorTerm().getId())){
				 hql.append(" and ahm.honorTerm.id=?");
				 values.add(honor.getHonorTerm().getId());
			}
			//荣誉名称
			if(DataUtil.isNotNull(honor.getHonorName())){
				 hql.append(" and ahm.honorName like ?");
				 values.add("%" +HqlEscapeUtil.escape( honor.getHonorName() )+ "%");
			}
			//荣誉级别
			if(DataUtil.isNotNull(honor.getHonorLevel())&&DataUtil.isNotNull(honor.getHonorLevel().getId())){
				 hql.append(" and ahm.honorLevel.id=?");
				 values.add(honor.getHonorLevel().getId());
			}
			//状态
			if(DataUtil.isNotNull(honor.getOperateStatus())&&DataUtil.isNotNull(honor.getOperateStatus().getId())){
				 hql.append(" and ahm.operateStatus.id=?");
				 values.add(honor.getOperateStatus().getId());
			}
			//审核状态
			if(DataUtil.isNotNull(honor.getApproveResult())&&DataUtil.isNotNull(honor.getApproveResult().getId())){
				 hql.append(" and ahm.approveResult.id=?");
				 values.add(honor.getApproveResult().getId());
			}
		}
		hql.append(" and ahm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by ahm.honorYear.name,ahm.honorTerm.name,ahm.honorTime asc");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	@Override
	public List<AssociationAdvisorModel> getAssociationAdvisors(String associationId) {
		List<Object> values = new ArrayList<Object>();
		if(DataUtil.isNotNull(associationId)){
			StringBuffer hql = new StringBuffer("from AssociationAdvisorModel aam where aam.associationPo.id=? and  aam.deleteStatus = ? ");
			values.add(associationId);
			values.add(dicUtil.getStatusNormal());
			hql.append(" order by aam.advisorPo.code asc");
			return this.query(hql.toString(), values.toArray());
		}
		
		return new ArrayList<AssociationAdvisorModel>();
	}
	
	@Override
	public List<AssociationMemberModel> getAssociationMembers(String associationId) {
		List<Object> values = new ArrayList<Object>();
		if(DataUtil.isNotNull(associationId)){
			StringBuffer hql = new StringBuffer("from AssociationMemberModel amm where amm.associationPo.id=? ");
			values.add(associationId);
			hql.append(" order by amm.memberPo.stuNumber asc");
			return this.query(hql.toString(), values.toArray());
		}
		return new ArrayList<AssociationMemberModel>();
	}
	
	@Override
	public List<AssociationMemberModel> getAssociationManager(String associationId) {
		List<Object> values = new ArrayList<Object>();
		if(DataUtil.isNotNull(associationId)){
			StringBuffer hql = new StringBuffer("from AssociationMemberModel amm where amm.associationPo.id=? ");
			values.add(associationId);
			hql.append(" and amm.isManager.id=?");
			values.add(Constants.STATUS_YES.getId());
			hql.append(" order by amm.memberPo.stuNumber asc");
			return this.query(hql.toString(), values.toArray());
		}
		return new ArrayList<AssociationMemberModel>();
	}
	
	@Override
	public AssociationApplyModel getAssociationApplyInfo(AssociationApplyModel aam) {
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getId())){
			String sql=" from AssociationApplyModel aam where aam.id=?";
			return (AssociationApplyModel)this.queryUnique(sql, new Object[]{aam.getId()});
		}
		return new AssociationApplyModel();
	}

	@Override
	public AssociationApplyModel getAssociationApplyInfo(String applyId) {
		if(DataUtil.isNotNull(applyId)){
			String sql=" from AssociationApplyModel aam where aam.id=?";
			return (AssociationApplyModel)this.queryUnique(sql, new Object[]{applyId});
		}
		return new AssociationApplyModel();
	}
	
	@Override
	public void deleteAssociationTeachers(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			this.executeHql("delete AssociationAdvisorModel where associationPo.id=?", new Object[]{associationId});
		}
	}
	
	@Override
	public void addAssociationAdvisor(AssociationAdvisorModel aam) {
		if(DataUtil.isNotNull(aam))
			this.save(aam);
	}

	@Override
	public AssociationBaseinfoModel getAssociationInfo(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			List<String> values=new ArrayList<String>();
			String hql="from AssociationBaseinfoModel abm where abm.id=? and abm.deleteStatus.id=?";
			values.add(associationId);
			values.add(this.dicUtil.getStatusNormal().getId());
			return (AssociationBaseinfoModel)this.queryUnique(hql, values.toArray());
		}
		
		return new AssociationBaseinfoModel();
	}

	@Override
	public AssociationBaseinfoModel getTopTenAssociation(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			List<String> values=new ArrayList<String>();
			String hql="from AssociationBaseinfoModel abm where abm.id=? and abm.isTopten.id=? and abm.deleteStatus.id=?";
			values.add(associationId);
			values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
			values.add(this.dicUtil.getStatusNormal().getId());
			return (AssociationBaseinfoModel)this.queryUnique(hql, values.toArray());
		}
		
		return new AssociationBaseinfoModel();
	}

	@Override
	public void addAssociationMember(AssociationMemberModel amm) {
		if(DataUtil.isNotNull(amm) && DataUtil.isNotNull(amm.getMemberPo()) && DataUtil.isNotNull(amm.getMemberPo().getId()))
			this.save(amm);
	}
	
	@Override
	public void deleteAssociationManagers(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			List<String> values=new ArrayList<String>();
			StringBuffer sql = new StringBuffer("delete AssociationMemberModel amm where amm.associationPo.id=? and amm.isManager.id=?");
			values.add(associationId);
			values.add(Constants.STATUS_YES.getId());
			this.executeHql(sql.toString(),values.toArray());
		}
	}

	@Override
	public void deleteAssociationMember(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			String sql = "delete AssociationMemberModel where associationPo.id=? and isManager.id=?";
			this.executeHql(sql, new Object[]{associationId,Constants.STATUS_NO.getId()});
		}
	}
	
	@Override
	public void deleteAssociationMember_(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			String sql = "delete AssociationMemberModel where associationPo.id=?";
			this.executeHql(sql, new Object[]{associationId});
		}
	}

	@Override
	public void truncateManagerInfo(String associationId, String managerId) {
		if(DataUtil.isNotNull(associationId)){
			String sql = "delete AssociationMemberModel where associationPo.id=? and memberPo.id=? and  isManager.id=?";
			this.executeHql(sql, new Object[]{associationId,managerId,Constants.STATUS_YES.getId()});
		}
	}

	@Override
	public AssociationMemberModel getAssociationManagerPo(String memberId) {
		if(DataUtil.isNotNull(memberId)){
			String sql=" from AssociationMemberModel amm where amm.id=? and amm.isManager.id=? and amm.deleteStatus.id=?";
			return (AssociationMemberModel) this.queryUnique(sql, new Object[]{memberId,Constants.STATUS_YES.getId(),this.dicUtil.getStatusNormal().getId()});
		}
		return new AssociationMemberModel();
	}
	
	@Override
	public AssociationMemberModel getAssociationManagerPo_(String associationId,String userId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(userId)){
			String sql=" from AssociationMemberModel amm where amm.associationPo.id=? " +
								 " and amm.memberPo.id=? and amm.isManager.id=? and amm.leaguePosition.id=?" +
								 " and amm.deleteStatus.id=?";
			Object [] vlueArray = new Object[]{associationId,userId,Constants.STATUS_YES.getId(),
			AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER.getId(),this.dicUtil.getStatusNormal().getId()};
			return (AssociationMemberModel) this.queryUnique(sql, vlueArray);
		}
		return new AssociationMemberModel();
	}
	

	@Override
	public AssociationMemberModel getAssociationProprieter(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			String sql=" from AssociationMemberModel amm where amm.associationPo.id=? and amm.leaguePosition.id=? and amm.deleteStatus.id=?";
			Object [] values = new Object[]{associationId,
												AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER.getId(),
												this.dicUtil.getStatusNormal().getId()};
			return (AssociationMemberModel) this.queryUnique(sql, values);
		}
		return new AssociationMemberModel();
	}

	@Override
	public AssociationMemberModel getAssociationMemberByUserId(String associationId,String currentUserId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(currentUserId)){
			String sql=" from AssociationMemberModel amm where amm.associationPo.id=? " +
								 " and amm.memberPo.id=? " +
								 " and amm.memberStatus.id=? " +
								 " and amm.deleteStatus.id=?";
			Object [] values = new Object[]{associationId,currentUserId,CYLeagueUtil.APPROVE_PASS.getId(),this.dicUtil.getStatusNormal().getId()};
			return (AssociationMemberModel) this.queryUnique(sql, values);
		}
		return new AssociationMemberModel();
	}

	@Override
	public AssociationMemberModel getAssociationMember(AssociationMemberModel amm) {
		if(DataUtil.isNotNull(amm) &&
			 DataUtil.isNotNull(amm.getId()) && 
			 DataUtil.isNotNull(amm.getMemberPo()) && 
			 DataUtil.isNotNull(amm.getMemberPo().getId())){
			String sql=" from AssociationMemberModel amm where amm.associationPo.id=? " +
					" and amm.memberPo.id=? " +
					" and amm.deleteStatus.id=?";
			Object [] values = new Object[]{amm.getId(),amm.getMemberPo().getId(),this.dicUtil.getStatusNormal().getId()};
			return (AssociationMemberModel) this.queryUnique(sql, values);
		}
		return new AssociationMemberModel();
	}
	
	@Override
	public AssociationMemberModel getAssociationMember(String associationId,String currentUserId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(currentUserId)){
			String sql=" from AssociationMemberModel amm where amm.associationPo.id=? " +
					" and amm.memberPo.id=? " +
					" and amm.deleteStatus.id=?";
			Object [] values = new Object[]{associationId,currentUserId,this.dicUtil.getStatusNormal().getId()};
			return (AssociationMemberModel) this.queryUnique(sql, values);
		}
		return new AssociationMemberModel();
	}
	
	@Override
	public AssociationAdvisorModel findAssociationAdvisor(String associationId,String teacherId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(teacherId)){
			String sql=" from AssociationAdvisorModel amm where amm.associationPo.id=? " +
					" and amm.advisorPo.code=? " +
					" and amm.deleteStatus.id=?";
			Object [] values = new Object[]{associationId,teacherId,this.dicUtil.getStatusNormal().getId()};
			return (AssociationAdvisorModel) this.queryUnique(sql, values);
		}
		return new AssociationAdvisorModel();
	}

	@Override
	public AssociationMemberModel getAssociationTempMember(String associationId, String currentUserId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(currentUserId)){
			String sql=" from AssociationMemberModel amm where amm.associationPo.id=? " +
					" and amm.memberPo.id=? " +
					" and amm.memberStatus.id=?" +
					" and amm.deleteStatus.id=?";
			Object [] values = new Object[]{associationId,currentUserId,CYLeagueUtil.APPROVE_NOT_APPROVE.getId(),this.dicUtil.getStatusNormal().getId()};
			return (AssociationMemberModel) this.queryUnique(sql, values);
		}
		return new AssociationMemberModel();
	}
	


	@Override
	public AssociationMemberModel getAssociationConfirmMember(String associationId, String curUserId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(curUserId)){
			String sql=" from AssociationMemberModel amm where amm.associationPo.id=? " +
					" and amm.memberPo.id=? " +
					" and amm.memberStatus.id=?" +
					" and amm.deleteStatus.id=?";
			Object [] values = new Object[]{associationId,curUserId,CYLeagueUtil.APPROVE_PASS.getId(),this.dicUtil.getStatusNormal().getId()};
			return (AssociationMemberModel) this.queryUnique(sql, values);
		}
		return new AssociationMemberModel();
	}


	@Override
	public void saveMemberPosition(String associationPosition,String ammId) {
			associationPosition = DataUtil.isNotNull(associationPosition)?associationPosition:"";
			String sql = "update AssociationMemberModel set leaguePosition.id=? where id=?";
			this.executeHql(sql, new Object[]{associationPosition,ammId});
	}

	@Override
	public void setAssociationPropreter(String associationId, String proprieter) {
		String sql = "update AssociationBaseinfoModel  set proprieter.id=? where id=?";
		this.executeHql(sql, new Object[]{proprieter,associationId});
	}

	@Override
	public AssociationAdvisorModel getAssociationAdvisor(String aamId) {

		if(DataUtil.isNotNull(aamId)){
			List<String> values = new ArrayList<String>();
			String sql = "from AssociationAdvisorModel where id=? and deleteStatus.id=?";
			values.add(aamId);
			values.add(this.dicUtil.getStatusNormal().getId());
			return (AssociationAdvisorModel)this.queryUnique(sql, values.toArray());
		}
		return new AssociationAdvisorModel();
	}

	@Override
	public AssociationAdvisorModel getCurAssociationAdvisor(
			String associationId, String currentUserId) {

		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(currentUserId)){
			List<String> values = new ArrayList<String>();
			String sql = "from AssociationAdvisorModel where associationPo.id=? and  advisorPo.id=?  and deleteStatus.id=?";
			values.add(associationId);
			values.add(currentUserId);
			values.add(this.dicUtil.getStatusNormal().getId());
			return (AssociationAdvisorModel)this.queryUnique(sql, values.toArray());
		}
		return new AssociationAdvisorModel();
	}

	@Override
	public void rollbackAssociationAdvisor(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			this.executeHql("delete AssociationAdvisorModel where associationPo.id=?", new Object[]{associationId});
		}
	}

	@Override
	public void rollbackAssociationManager(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			String sql = "delete AssociationMemberModel where associationPo.id=? and isManager.id=?";
			this.executeHql(sql, new Object[]{associationId,Constants.STATUS_YES.getId()});
		}
	}

	@Override
	public List<AssociationApplyModel> getAssociationApplyByIds(String applyIds) {
		if(DataUtil.isNotNull(applyIds)){
			String sql=" from AssociationApplyModel aam where aam.id in"+applyIds+" order by aam.applyTypeDic.code,aam.updateTime desc";
			return this.query(sql, new Object[]{});
		}
		return new ArrayList<AssociationApplyModel>();
	}

	@Override
	public AssociationBaseinfoModel getAssociationInfoByName(String associationName) {
		if(DataUtil.isNotNull(associationName)){
			List<String> values = new ArrayList<String>();
			StringBuffer hql = new StringBuffer("from AssociationBaseinfoModel abm where 1=1 ");
			hql.append(" and abm.associationName=?");
			 if (HqlEscapeUtil.IsNeedEscape(associationName))
	         {
	           values.add(HqlEscapeUtil.escape(associationName));
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add(associationName);
	         }
			return (AssociationBaseinfoModel)this.queryUnique(hql.toString(), values.toArray());
		}
		return new AssociationBaseinfoModel();
	}

	@Override
	public AssociationBaseinfoModel getAssociationInfoByCode(String associationCode) {
		if(DataUtil.isNotNull(associationCode)){
			List<String> values = new ArrayList<String>();
			StringBuffer hql = new StringBuffer("from AssociationBaseinfoModel abm where 1=1 ");
			hql.append(" and abm.associationCode=?");
	         if (HqlEscapeUtil.IsNeedEscape(associationCode))
	         {
	           values.add(HqlEscapeUtil.escape(associationCode));
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add(associationCode);
	         }
			return (AssociationBaseinfoModel)this.queryUnique(hql.toString(), values.toArray());
		}
		return new AssociationBaseinfoModel();
	}


	@Override
	public void deleteAssociationMember(String associationId, String memberId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(memberId)){
			String hql = "delete AssociationMemberModel amm where amm.associationPo.id=? and amm.memberPo.id=?";
			this.executeHql(hql, new Object[]{associationId,memberId});
		}
	}


	@Override
	public Page pageQueryAssociationService(AssociationMemberModel amm, int pageNo, int pageSize, String userId) {
		if(DataUtil.isNotNull(userId)){
			List<Object> values = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer("from AssociationMemberModel amm where 1=1 ");
			hql.append(" and amm.isManager.id=?");
			values.add(this.dicUtil.getDicInfo("Y&N", "Y").getId());
			hql.append(" and amm.memberPo.id=?");
			values.add(userId);
			hql.append(" and amm.deleteStatus.id=?");
			values.add(this.dicUtil.getStatusNormal().getId());
			if (values.size() == 0){
				return this.pagedQuery(hql.toString(), pageNo, pageSize);
			}else{
				return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
			}
		}else{
			return new Page();
		}
	}


	@Override
	public List<AssociationAdvisorModel> getAssociationAdvisorList(String associationId, String advisorCondition) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationAdvisorModel aam where 1=1 ");
		
		if(DataUtil.isNotNull(associationId)){
			hql.append(" and aam.associationPo.id=?");
			values.add(associationId);
		}
		
		if(DataUtil.isNotNull(advisorCondition)){
			hql.append(" and aam.advisorPo.id in "+advisorCondition);
		}
		
		hql.append(" and aam.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by aam.advisorPo.code asc");
		
		if(DataUtil.isNotNull(advisorCondition)){
			
			return this.query(hql.toString(), values.toArray());
		}else{
			
			return new ArrayList<AssociationAdvisorModel>();
		}
	}


	@Override
	public List<AssociationMemberModel> getAssociationManagerList(
			String associationId, String managerCondition) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationMemberModel amm where 1=1 ");
		
		if(DataUtil.isNotNull(associationId)){
			hql.append(" and amm.associationPo.id=?");
			values.add(associationId);
		}
		
		if(DataUtil.isNotNull(managerCondition)){
			hql.append(" and amm.memberPo.id in"+managerCondition);
		}
		
		hql.append(" and amm.isManager.id=?");
		values.add(Constants.STATUS_YES.getId());
		
		hql.append(" and amm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by amm.memberPo.stuNumber asc");
		
		if(DataUtil.isNotNull(managerCondition)){

			return this.query(hql.toString(), values.toArray());
		}else{
			return new ArrayList<AssociationMemberModel>();
		}
	}


	@Override
	public void deleteAssociationApplyInfo(String applyId) {
		if(DataUtil.isNotNull(applyId)){
			String hql = "delete AssociationApplyModel aam where aam.id=?";
			this.executeHql(hql, new Object[]{applyId});
		}
	}


	@Override
	public void deleteAssociationInfo(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			String hql = "delete AssociationBaseinfoModel abm where abm.id=?";
			this.executeHql(hql, new Object[]{associationId});
		}
	}


	@Override
	public List<AssociationApplyModel> getApproveingApply(String associationId,String applyType) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(applyType)){
			List<Object> values = new ArrayList<Object>();
			StringBuffer sql=new StringBuffer("from AssociationApplyModel aam where  1=1 and deleteStatus =? ");
			values.add(dicUtil.getStatusNormal());
			//社团主键
			sql.append(" and aam.associationPo.id=?");
			values.add(associationId);
			
			//申请类型
//			sql.append(" and aam.applyTypeDic.code=?");
//			values.add(AssociationConstants.APPLY_STATUS.MODIFY.toString());
			
			//审批流状态
			sql.append(" and (aam.processstatus in ('CURRENT_APPROVE','APPROVING') or aam.processstatus is null)");
			
			return this.query(sql.toString(), values.toArray());
		}
		
		return new ArrayList<AssociationApplyModel>();
	}


	@Override
	public void updateAssociationMemberHonor(String associationId, String honorId, String approveStatus) {
		
		String sql = "update AssociationHonorModel a set a.approveResult.id=? where a.associationPo.id=? and a.id=?";
		this.executeHql(sql, new Object[]{approveStatus,associationId,honorId});
	}


	@Override
	public List<UploadFileRef> getAssociationAttache(String applyId,String attacheType) {
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(attacheType)){
			List<Object> values = new ArrayList<Object>();
			String sql="select  aam.attachePo  from AssociationAttacheModel aam where 1=1 " +
								 " and aam.applyPo.id=? and aam.attacheType=? and aam.deleteStatus.id=?";
			return this.query(sql, new Object[]{applyId,attacheType,Constants.STATUS_NORMAL.getId()});
		}
		return new ArrayList<UploadFileRef>();
	}


	@Override
	public AssociationAttacheModel getAssociationAttacheByFileId(String applyId, String fileId) {
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(fileId)){
			List<Object> values = new ArrayList<Object>();
			String sql="select  aam.attachePo  from AssociationAttacheModel aam where 1=1 " +
								 " and aam.applyPo.id=? and aam.attachePo.uploadFile.id=? and aam.deleteStatus.id=?";
			return (AssociationAttacheModel)this.queryUnique(sql, new Object[]{applyId,fileId,Constants.STATUS_NORMAL.getId()});
		}
		return new AssociationAttacheModel();
	}


	@Override
	public UploadFileRef getFileUploadRef(String applyId, String fileId) {
		String  sql = "from UploadFileRef ref where  ref.objectId=? and ref.uploadFile.id = ?";
		return (UploadFileRef)this.queryUnique(sql, new Object[]{applyId,fileId});
	}


	@Override
	public void deleteAssociationAttachCascade(String applyId, String applyType) {
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(applyType)){
			List<Object> values = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer("delete AssociationAttacheModel aam where 1=1 ");
			hql.append(" and aam.applyPo.id=?");
			values.add(applyId);
			if(applyType.equals(AssociationConstants.APPLY_STATUS.CANCEL.toString())){
				hql.append(" and aam.attacheType in('"+applyType+"','"+AssociationConstants.ATTACHE_TYPE.FINANCE.toString()+"')");
			}else{
				hql.append(" and aam.attacheType=?");
				values.add(applyType);
			}
			this.executeHql(hql.toString(), values.toArray());
		}
	}


	@Override
	public void deleteAssociationAttach(String applyId, String applyType) {
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(applyType)){
			List<Object> values = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer("delete AssociationAttacheModel aam where 1=1 ");
			hql.append(" and aam.applyPo.id=?");
			hql.append(" and aam.attacheType=?");
			values.add(applyId);
			values.add(applyType);
			this.executeHql(hql.toString(), values.toArray());
		}
	}

	@Override
	public void deleteAdvisorInfo(String id) {
		if(DataUtil.isNotNull(id)){
			String hql = "delete AssociationAdvisorModel amm where amm.id=? ";
			this.executeHql(hql, new Object[]{id});
		}
	}

	@Override
	public void deleteAssociationAdvisor(String associationId, String advisorId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(advisorId)){
			String hql = "delete AssociationAdvisorModel amm where amm.associationPo.id=? and amm.advisorPo.id=?";
			this.executeHql(hql, new Object[]{associationId,advisorId});
		}
	}

	@Override
	public AssociationApplyModel getAssociationCurApply(String associationId,String applyType) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(applyType)){
			String sql=" from AssociationApplyModel aam where aam.associationPo.id=? and aam.applyTypeDic.code=? and aam.applyStatus=?";
			return (AssociationApplyModel)this.queryUnique(sql, new Object[]{associationId,applyType,Constants.OPERATE_STATUS.SAVE.toString()});
		}
		return new AssociationApplyModel();
	}

	@Override
	public AssociationAdvisorModel getAssociationAdvisor(String associationId,String teacherId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(teacherId)){
			String hql=" from AssociationAdvisorModel aam where aam.associationPo.id=? and aam.advisorPo.code=?";
			return (AssociationAdvisorModel)this.queryUnique(hql, new Object[]{associationId,teacherId});
		}
		return new AssociationAdvisorModel();
	}
	
	
	@Override
	public void deleteAssociationMemberInfo(String id) {
		if(DataUtil.isNotNull(id)){
			String hql = "delete AssociationMemberModel amm where amm.id=? ";
			this.executeHql(hql, new Object[]{id});
		}
	}


	@Override
	public void deleteAssociationTempUser(String associationId, String userType) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(userType)){
			String hql = "delete AssociationTempUserModel atum where atum.associationPo.id=?  and atum.userType=?";
			this.executeHql(hql, new Object[]{associationId,userType});
		}
	}


	@Override
	public List<AssociationTempUserModel> getTempUserInfo(String associationId, String userType) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(userType)){
			String hql = "from AssociationTempUserModel atum where atum.associationPo.id=?  and atum.userType=?";
			return this.query(hql, new Object[]{associationId,userType});
		}else{
			return new ArrayList<AssociationTempUserModel>();
		}
	}

	@Override
	public AssociationTempUserModel getAssociationTempUser(
			String associationId, String userId, String userType) {
		if(DataUtil.isNotNull(associationId)&&
				 DataUtil.isNotNull(userId)&&
				 DataUtil.isNotNull(userType)){
			String hql = "from AssociationTempUserModel atum where atum.associationPo.id=?  and atum.userId=? and atum.userType=?";
			return (AssociationTempUserModel)this.queryUnique(hql, new Object[]{associationId,userId,userType});
		}
		return new AssociationTempUserModel();
	}

	@Override
	public void deleteAssociationTempUser(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			String hql = "delete AssociationTempUserModel atum where atum.associationPo.id=?";
			this.executeHql(hql, new Object[]{associationId});
		}
	}

	/**
	 * 分页获取社团基本信息--zhangmx
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	@Override
	public Page pageQueryAssociationBaseInfo_(AssociationBaseinfoModel abm,
			int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationBaseinfoModel abm where 1=1 ");
		
	     //学院查询
	     if(DataUtil.isNotNull(abm.getCollege()) && DataUtil.isNotNull(abm.getCollege().getId())){
	    	 hql.append(" and abm.college.id=?");
	    	 values.add(abm.getCollege().getId());
	     }
	     
	     //社团编号查询
	     if(DataUtil.isNotNull(abm.getAssociationCode())){
			hql.append(" and abm.associationCode like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationCode()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(abm.getAssociationCode()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" +abm.getAssociationCode() + "%");
	         }
	     }
		
	     //社团名称查询
	     if (!StringUtils.isEmpty(abm.getAssociationName())) {
	         hql.append(" and abm.associationName like ? ");
	         if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationName()))
	         {
	           values.add("%" + HqlEscapeUtil.escape(abm.getAssociationName()) + "%");
	           hql.append(HqlEscapeUtil.HQL_ESCAPE);
	         }else {
	           values.add("%" + abm.getAssociationName() + "%");
	         }
	       }

		//社团类型查询
		if(DataUtil.isNotNull(abm.getAssociationType()) && DataUtil.isNotNull(abm.getAssociationType().getId())){
			hql.append(" and abm.associationType.id=?");
			values.add(abm.getAssociationType().getId());
		}
		
		if(DataUtil.isNotNull(abm.getIsCancel()) && DataUtil.isNotNull(abm.getIsCancel().getId())){
			hql.append(" and abm.isCancel.id=?");
			values.add(abm.getIsCancel().getId());
		}
		
		//社团删除状态
		hql.append(" and abm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by abm.isCancel desc,abm.updateTime desc");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	/**
	 * 分页获取社团基本信息【负责人】
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	@Override
	public Page pageQueryAssociationBaseInfoByManager(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		String curUserId = this.sessionUtil.getCurrentUserId();
		StringBuffer hql = new StringBuffer("select distinct  abm  from AssociationBaseinfoModel abm,AssociationMemberModel amm where 1=1 ");
		hql.append(" and abm.id=amm.associationPo.id");
		hql.append(" and amm.memberPo.id='"+curUserId+"'");
		hql.append(" and amm.leaguePosition.code='"+AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER.getCode()+"'");
		
		//学院查询
		if(DataUtil.isNotNull(abm.getCollege()) && DataUtil.isNotNull(abm.getCollege().getId())){
			hql.append(" and abm.college.id=?");
			values.add(abm.getCollege().getId());
		}
		
		//社团编号查询
		if(DataUtil.isNotNull(abm.getAssociationCode())){
			hql.append(" and abm.associationCode like ? ");
			if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationCode()))
			{
				values.add("%" + HqlEscapeUtil.escape(abm.getAssociationCode()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" +abm.getAssociationCode() + "%");
			}
		}
		
		//社团名称查询
		if (!StringUtils.isEmpty(abm.getAssociationName())) {
			hql.append(" and abm.associationName like ? ");
			if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationName()))
			{
				values.add("%" + HqlEscapeUtil.escape(abm.getAssociationName()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" + abm.getAssociationName() + "%");
			}
		}
		
		//社团类型查询
		if(DataUtil.isNotNull(abm.getAssociationType()) && DataUtil.isNotNull(abm.getAssociationType().getId())){
			hql.append(" and abm.associationType.id=?");
			values.add(abm.getAssociationType().getId());
		}
		
		if(DataUtil.isNotNull(abm.getIsCancel()) && DataUtil.isNotNull(abm.getIsCancel().getId())){
			hql.append(" and abm.isCancel.id=?");
			values.add(abm.getIsCancel().getId());
		}
		
		//社团删除状态
		hql.append(" and abm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		hql.append(" order by abm.isCancel desc,abm.updateTime desc");
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	/**
	 * 分页获取社团基本信息【指导老师】
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	@Override
	public Page pageQueryAssociationBaseInfoByAdvisor(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		String curUserId = this.sessionUtil.getCurrentUserId();
		StringBuffer hql = new StringBuffer("select distinct abm from AssociationBaseinfoModel abm ,AssociationAdvisorModel aamr  where 1=1 ");
		hql.append(" and abm.id=aamr.associationPo.id");
		hql.append(" and aamr.advisorPo.code='"+curUserId+"'");
		
		//学院查询
		if(DataUtil.isNotNull(abm.getCollege()) && DataUtil.isNotNull(abm.getCollege().getId())){
			hql.append(" and abm.college.id=?");
			values.add(abm.getCollege().getId());
		}
		
		//社团编号查询
		if(DataUtil.isNotNull(abm.getAssociationCode())){
			hql.append(" and abm.associationCode like ? ");
			if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationCode()))
			{
				values.add("%" + HqlEscapeUtil.escape(abm.getAssociationCode()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" +abm.getAssociationCode() + "%");
			}
		}
		
		//社团名称查询
		if (!StringUtils.isEmpty(abm.getAssociationName())) {
			hql.append(" and abm.associationName like ? ");
			if (HqlEscapeUtil.IsNeedEscape(abm.getAssociationName()))
			{
				values.add("%" + HqlEscapeUtil.escape(abm.getAssociationName()) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			}else {
				values.add("%" + abm.getAssociationName() + "%");
			}
		}
		
		//社团类型查询
		if(DataUtil.isNotNull(abm.getAssociationType()) && DataUtil.isNotNull(abm.getAssociationType().getId())){
			hql.append(" and abm.associationType.id=?");
			values.add(abm.getAssociationType().getId());
		}
		
		if(DataUtil.isNotNull(abm.getIsCancel()) && DataUtil.isNotNull(abm.getIsCancel().getId())){
			hql.append(" and abm.isCancel.id=?");
			values.add(abm.getIsCancel().getId());
		}
		
		//社团删除状态
		hql.append(" and abm.deleteStatus.id=?");
		values.add(this.dicUtil.getStatusNormal().getId());
		
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
	}
	
	@Override
	public boolean isAssociationNameRepeat(String associationId,String associationName)
	{
		List<AssociationBaseinfoModel> list = query("from AssociationBaseinfoModel abm where 1=1 and abm.associationName=? ", new Object[] {associationName});
	     boolean b = false;
	     if ((list != null) && (list.size() > 0)) {
	       for (AssociationBaseinfoModel associationBaseinfoModel : list) {
	         if (!associationBaseinfoModel.getId().equals(associationId)) {
	           b = true;
	         }
	       }
	     }
	     return b;
	}


	/**
	 * 描述信息: 学院社团的个数
	 * @param collegeId
	 * @return
	 * 2016-1-8 上午10:55:51
	 */
	@Override
    public int getAssociationTotalCountByCollege(String collegeId)
    {
		List<AssociationBaseinfoModel> list = query("from AssociationBaseinfoModel abm where college.id=? and associationCode is not null ", new Object[] {collegeId});
		return null == list? 0 : list.size();
    }


	/**
	 * 描述信息: 社员荣誉列表 审核通过的
	 * @param assoicaiotnId
	 * @param userId
	 * @return
	 * 2016-2-3 下午3:47:17
	 */
	@Override
    public List<AssociationHonorModel> getMemberHonorList(String assoicaiotnId, String memberId)
    {
		StringBuffer hql = new StringBuffer("from AssociationHonorModel a where a.associationPo.id=? and a.member.id=? and a.deleteStatus=? and a.approveResult = ? ");
		return this.query(hql.toString(), new Object[]{assoicaiotnId,memberId,dicUtil.getStatusNormal(),dicUtil.getDicInfo("APPLY_APPROVE","PASS")});
    }


	/**
	 * 描述信息: 审核通过的荣誉信息
	 * @param am
	 * @param honor
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * 2016-2-3 下午4:43:44
	 */
	@Override
    public Page pageQueryAssociationApprovedHonor(String associationId, AssociationHonorModel honor,int pageNo, int pageSize)
    {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from AssociationHonorModel ahm where 1=1 ");
		if(DataUtil.isNotNull(honor)){
			//学年
			if(DataUtil.isNotNull(honor.getHonorYear())&&DataUtil.isNotNull(honor.getHonorYear().getId())){
				 hql.append(" and ahm.honorYear.id=?");
				 values.add(honor.getHonorYear().getId());
			}
			//学期
			if(DataUtil.isNotNull(honor.getHonorTerm())&&DataUtil.isNotNull(honor.getHonorTerm().getId())){
				 hql.append(" and ahm.honorTerm.id=?");
				 values.add(honor.getHonorTerm().getId());
			}
			//荣誉名称
			if(DataUtil.isNotNull(honor.getHonorName())){
				 hql.append(" and ahm.honorName like ?");
				 values.add("%" +HqlEscapeUtil.escape( honor.getHonorName() )+ "%");
			}
			//荣誉级别
			if(DataUtil.isNotNull(honor.getHonorLevel())&&DataUtil.isNotNull(honor.getHonorLevel().getId())){
				 hql.append(" and ahm.honorLevel.id=?");
				 values.add(honor.getHonorLevel().getId());
			}
		}
		hql.append("  and ahm.approveResult= ? and ahm.deleteStatus.id=? and associationPo.id = ? ");
		values.add(dicUtil.getDicInfo("APPLY_APPROVE","PASS"));
		values.add(dicUtil.getStatusNormal().getId());
		values.add(associationId);
		
		hql.append(" order by ahm.honorYear.name,ahm.honorTerm.name,ahm.honorTime asc");
		if (values.size() == 0){
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		}else{
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());	
		}
    }

}
