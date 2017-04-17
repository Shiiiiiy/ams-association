package com.uws.association.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.association.dao.IAssociationDao;
import com.uws.association.service.IAssociationService;
import com.uws.association.util.AssociationConstants;
import com.uws.common.dao.ICommonRoleDao;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.AmsDateUtil;
import com.uws.common.util.CYLeagueUtil;
import com.uws.common.util.CYLeagueUtil.CYL_ROLES;
import com.uws.common.util.Constants;
import com.uws.core.base.BaseModel;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;
import com.uws.core.util.SpringBeanLocator;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.domain.association.AssociationAttacheModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.domain.association.AssociationHonorModel;
import com.uws.domain.association.AssociationMemberModel;
import com.uws.domain.association.AssociationTempUserModel;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;

@Service("com.uws.association.service.impl.AssociationServiceImpl")
public class AssociationServiceImpl implements IAssociationService {
	@Autowired
	private IBaseDataService baseDataService;
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private FileUtil fileUtil = FileFactory.getFileUtil();
	private SessionUtil sessionUtil = SessionFactory.getSession(AssociationConstants.NAMESPACE);
	
	@Autowired
	private IAssociationDao associationDao;

	@Autowired
	private ICommonRoleDao commonRoleDao;

	@Override
	public Page pageQueryAssociationApplyInfo(AssociationApplyModel  aam,int pageNo,int pageSize) {

		return this.associationDao.pageQueryAssociationApplyInfo(aam,pageNo,pageSize);
	}
	
	@Override
	public Page pageQueryAssociationApplyByAdvisor(AssociationApplyModel  aam,int pageNo,int pageSize) {
		
		return this.associationDao.pageQueryAssociationApplyByAdvisor(aam,pageNo,pageSize);
	}
	
	@Override
	public Page pageQueryAssociationApplyByMember(AssociationApplyModel  aam,int pageNo,int pageSize) {
		
		return this.associationDao.pageQueryAssociationApplyByMember(aam,pageNo,pageSize);
	}

	@Override
	public List<AssociationApplyModel> getAssociationApplyList(AssociationApplyModel aam) {
		
		return this.associationDao.getAssociationApplyList(aam);
	}

	@Override
	public void associationApplyHandler(AssociationBaseinfoModel abm) {
		
	}

	@Override
	public void associationApproveHandler(AssociationBaseinfoModel abm) {
		
	}

	@Override
	public Page pageQueryAssociationInfo(AssociationBaseinfoModel abm,int pageNo,int pageSize) {
		
		return this.associationDao.pageQueryAssociationBaseInfo(abm,pageNo,pageSize);
	}

	@Override
	public Page pageQueryReportAssociationInfo(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		
		return this.associationDao.pageQueryReportAssociationInfo(abm,pageNo,pageSize);
	}

	@Override
	public Page pageQueryAssociationByMember(AssociationMemberModel amm,int pageNo, int pageSize) {
		
		return this.associationDao.pageQueryAssociationByMember(amm,pageNo,pageSize);
	}
	
	@Override
	public Page pageQueryAssociationByManager(AssociationMemberModel amm,int pageNo, int pageSize) {
		
		return this.associationDao.pageQueryAssociationByManager(amm,pageNo,pageSize);
	}
	
	@Override
	public Page pageQueryAssociationByMember_(AssociationMemberModel amm,int pageNo, int pageSize) {
		
		return this.associationDao.pageQueryAssociationByMember_(amm,pageNo,pageSize);
	}

	@Override
	public Page pageQueryAssociationReportList(AssociationBaseinfoModel abm,int pageNo, int pageSize) {

		return new Page();
	}

	@Override
	public AssociationBaseinfoModel getAssociationInfo(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			AssociationBaseinfoModel abm =  this.associationDao.getAssociationInfo(associationId);
			if(DataUtil.isNotNull(abm)){
				return abm;
			}else{
				return new AssociationBaseinfoModel();
			}
		}
		return new AssociationBaseinfoModel();
	}

	@Override
	public void createAssociationInfo(AssociationBaseinfoModel abm) {
		
	}

	@Override
	public void modifyAssociationInfo(AssociationBaseinfoModel abm) {
		
	}

	@Override
	public void deleteAssociationInfo(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			this.associationDao.deleteAssociationInfo(associationId);
		}
	}

	@Override
	public void deprecatedAssociationInfo(String associationId) {
		
	}

	@Override
	public List<AssociationAdvisorModel> getAssociationAdvisors(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			List<AssociationAdvisorModel> aamList = this.associationDao.getAssociationAdvisors(associationId);
			if(DataUtil.isNotNull(aamList)){
				return aamList;
			}else{
				return new ArrayList<AssociationAdvisorModel>();
			}
		}else{
			return new ArrayList<AssociationAdvisorModel>();
		}
	}

	@Override
	public Page pageQueryAssociationAdvisor(String associationId,String teacherIdsConditon,int pageNo,int pageSize) {
		if(DataUtil.isNotNull(associationId)){
			
			return this.associationDao.pageQueryAssociationAdvisor(associationId,teacherIdsConditon,pageNo,pageSize);
		}else{
			
			return new Page();
		}
	}

	@Override
	public Page pageQueryAssociationAdvisor(String associationId, int pageNo,int pageSize) {
		if(DataUtil.isNotNull(associationId)){
			
			return this.associationDao.pageQueryAssociationAdvisor(associationId,pageNo,pageSize);
		}else{
			
			return new Page();
		}
	}
	
	@Override
	public Page pageQueryAssociationByAdvisor(AssociationAdvisorModel aam,int pageNo, int pageSize) {
		
		return this.associationDao.pageQueryAssociationByAdvisor(aam, pageNo, pageSize);
	}
	
	@Override
	public List<AssociationMemberModel> getAssociationMembers(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			return this.associationDao.getAssociationMembers(associationId);
		}else{
			return new ArrayList<AssociationMemberModel>();
		}
	}
	
	@Override
	public List<AssociationMemberModel> getAssociationManagers(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			return 	this.associationDao.getAssociationManager(associationId);
		}else{
			return new ArrayList<AssociationMemberModel>();
		}
	}

	@Override
	public Page pageQueryAssociationMember(AssociationBaseinfoModel abm,String managerIdsConditon,int pageNo,int pageSize) {
		if(DataUtil.isNotNull(abm)){
			return this.associationDao.pageQueryAssociationMember(abm,managerIdsConditon,pageNo,pageSize);
		}else{
			return new Page();
		}
	}

	@Override
	public Page pageQueryAssociationMember(AssociationBaseinfoModel abm, int pageNo, int pageSize) {
		if(DataUtil.isNotNull(abm)){
			return this.associationDao.pageQueryAssociationMember(abm,pageNo,pageSize);
		}else{
			return new Page();
		}
	}
	
	@Override
	public Page pageQueryAssociationMember_(AssociationMemberModel amm, int pageNo, int pageSize) {
		if(DataUtil.isNotNull(amm)){
			return this.associationDao.pageQueryAssociationMember_(amm,pageNo,pageSize);
		}else{
			return new Page();
		}
	}

	@Override
	public void addAssociationMember(AssociationMemberModel amm) {
		this.associationDao.addAssociationMember(amm);
	}

	@Override
	public void delAssociationMember(AssociationMemberModel amm) {
		
	}

	@Override
	public void modifyAssociationMember(AssociationMemberModel amm) {
		
	}

	@Override
	public List<AssociationHonorModel> getMemberHonorList(String memberId) {
		return null;
	}

	@Override
	public Page pageQueryAssociationHonor(AssociationMemberModel am,AssociationHonorModel honor,int pageNo,int pageSize) {
		if(DataUtil.isNotNull(am)){
			return this.associationDao.pageQueryAssociationHonor(am,honor,pageNo,pageSize);
		}else{
			return new Page();
		}
	}

	@Override
	public void addAssociationHonor(AssociationHonorModel ahm) {
		
	}

	@Override
	public void modifyAssociationHonor(AssociationHonorModel ahm) {
		
	}

	@Override
	public void deleteAssociationHonor(AssociationHonorModel ahm) {
		
	}

	@Override
	public String getAssociationAdvisors(AssociationApplyModel aam) {
		StringBuffer advisors = new StringBuffer();
		if(DataUtil.isNotNull(aam)&&DataUtil.isNotNull(aam.getAssociationPo())){
			List<AssociationAdvisorModel> aamList =  this.associationDao.getAssociationAdvisors(aam.getAssociationPo().getId());
			for(int i=0;i<aamList.size();i++){
				AssociationAdvisorModel param = aamList.get(i);
				if(i==aamList.size()-1){
					advisors.append(param.getAdvisorPo().getName());
				}else{
					advisors.append(param.getAdvisorPo().getName()).append("，");
				}
			}
			
			return advisors.toString();
		}
		return "";
	}
	
	@Override
	public String getAssociationAdvisorName(String associationId) {
		StringBuffer advisors = new StringBuffer();
		if(DataUtil.isNotNull(associationId)){
			List<AssociationAdvisorModel> aamList =  this.associationDao.getAssociationAdvisors(associationId);
			for(int i=0;i<aamList.size();i++){
				AssociationAdvisorModel param = aamList.get(i);
				if(i==aamList.size()-1){
					advisors.append(param.getAdvisorPo().getName());
				}else{
					advisors.append(param.getAdvisorPo().getName()).append("，");
				}
			}
			
			return advisors.toString();
		}
		return "";
	}

	@Override
	public int getAssociationMemberNums(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			List<AssociationMemberModel> aamList =  this.associationDao.getAssociationMembers(associationId);
			return aamList.size();
		}
		return 0;
	}

	@Override
	public AssociationApplyModel getAssociationApplyInfo(AssociationApplyModel aam) {
		return this.associationDao.getAssociationApplyInfo(aam);
	}

	@Override
	public AssociationApplyModel getAssociationApplyInfo(String applyId) {
		return this.associationDao.getAssociationApplyInfo(applyId);
	}

	@Override
	public void deleteAssociationTeachers(String associationId) {
		this.associationDao.deleteAssociationTeachers(associationId);
	}

	@Override
	public void addAssociationAdvisor(AssociationAdvisorModel aam) {
		this.associationDao.addAssociationAdvisor(aam);
	}

	@Override
	public void deleteAssociationManagers(String associationId,AssociationMemberModel proprieter) {
		//社长用户id
		String proprieterId = (DataUtil.isNotNull(proprieter)&&
													 DataUtil.isNotNull(proprieter.getMemberPo()))?proprieter.getMemberPo().getId():"";
		this.resetManager(associationId,proprieter);
		this.resetManagerRole(associationId,proprieterId);
	}

	/**
	 * 重置社团负责人角色
	 * @param associationId	社团主键
	 * @param proprieterId	社长用户id
	 */
	private void resetManagerRole(String associationId,String proprieterId) {
		//获取社团负责人列表
		List<AssociationMemberModel> ammList = this.associationDao.getAssociationManager(associationId);
		for(AssociationMemberModel amm:ammList){
			//删除基础信息中用户的“社团负责人”角色
			this.commonRoleDao.deleteUserRole(amm.getMemberPo().getId(), CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
		}
		
		if(DataUtil.isNotNull(proprieterId)){//恢复社长角色
			this.commonRoleDao.saveUserRole(proprieterId, CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
		}
	}

	/**
	 * 重置社团负责人
	 * @param associationId			社团主键
	 * @param proprieter				社长实体类
	 */
	private void resetManager(String associationId,AssociationMemberModel proprieter) {
		AssociationMemberModel newProprieter = new AssociationMemberModel();
		if(DataUtil.isNotNull(proprieter)){
			BeanUtils.copyProperties(proprieter,newProprieter);
		}
		//删除社团负责人
		this.associationDao.deleteAssociationManagers(associationId);
		if(DataUtil.isNotNull(newProprieter)&&DataUtil.isNotNull(newProprieter.getId())){//恢复社长信息
			this.associationDao.save(newProprieter);
		}
	}

	@Override
	public void addAssociationManager(AssociationMemberModel amm) {
		String associationId = (DataUtil.isNotNull(amm.getAssociationPo()))?amm.getAssociationPo().getId():"";
		String memberId = (DataUtil.isNotNull(amm.getMemberPo()))?amm.getMemberPo().getId():"";
		AssociationMemberModel existAmm = this.associationDao.getAssociationMemberByUserId(associationId,memberId);
		if(!DataUtil.isNotNull(existAmm) ||( DataUtil.isNotNull(existAmm) && DataUtil.isNull(existAmm.getId()))){//如果当前成员不存在，新增
			this.associationDao.addAssociationMember(amm);
		}
		String userId = amm.getMemberPo().getId();
		boolean  curUserIsAm = this.commonRoleDao.checkUserIsExist(userId, CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
		if(!curUserIsAm){//初始化“社团负责人的角色”
			this.commonRoleDao.saveUserRole(userId, CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
		}
	}

	@Override
	public AssociationMemberModel getAssociationMemberPo(String pk) {
		return this.associationDao.getAssociationManagerPo(pk);
	}

	@Override
	public void setMemberPosition(String pk, String associationPosition) {
		if(DataUtil.isNotNull(pk)){
			AssociationMemberModel amm = this.associationDao.getAssociationManagerPo(pk);
			this.saveMemberPosition(amm,associationPosition);
		}
	}

	/**
	 * 保存成员社团职务
	 * @param amm								社团成员对象
	 * @param associationPosition	社团职务
	 */
	private void saveMemberPosition(AssociationMemberModel amm,String associationPosition) {
		this.associationDao.saveMemberPosition(associationPosition,amm.getId());
	}

	/**
	 * 设置社团负责人
	 * @param amm								社团成员对象
	 * @param associationPosition	社团职务
	 */
	private void setAssociationProprieter(AssociationMemberModel amm,String associationPosition) {
		String manager = AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER.getId();
		String dbPosition = (DataUtil.isNotNull(amm.getLeaguePosition()))?amm.getLeaguePosition().getId():"";
		AssociationBaseinfoModel associationPo = this.associationDao.getAssociationInfo(amm.getAssociationPo().getId());
		if(manager.equals(associationPosition) || manager.equals(dbPosition)){//第一次设置社长
			String proprieter = (manager.equals(associationPosition))?amm.getMemberPo().getId():"";
			this.associationDao.setAssociationPropreter(amm.getAssociationPo().getId(),proprieter);
		}
	}

	@Override
	public boolean isAssociationProprieter(String associationId) {
		boolean flag=false;
		AssociationMemberModel amm=null;
		if(DataUtil.isNotNull(associationId))
			amm = this.associationDao.getAssociationProprieter(associationId);
		if(DataUtil.isNotNull(amm) && DataUtil.isNotNull(amm.getId()))
			flag =  true;
		
		return flag;
	}

	@Override
	public boolean getAssociationMemberByUserId(String associationId,String currentUserId) {
		boolean flag=false;
		AssociationMemberModel amm=null;
		if(DataUtil.isNotNull(currentUserId))
			amm = this.associationDao.getAssociationManagerPo_(associationId,currentUserId);
		if(DataUtil.isNotNull(amm) && DataUtil.isNotNull(amm.getId()))
			flag =  true;
		
		return flag;
	}
	
	@Override
	public AssociationMemberModel getAssociationMember_(String associationId,String memberId) {
		AssociationMemberModel amm = this.associationDao.getAssociationMember(associationId,memberId);
		return amm;
	}
	
	@Override
	public boolean getAssociationMember(String associationId,String currentUserId){
		boolean flag=false;
		AssociationMemberModel amm=null;
		if(DataUtil.isNotNull(currentUserId))
			amm = this.associationDao.getAssociationMember(associationId,currentUserId);
		if(DataUtil.isNotNull(amm) && DataUtil.isNotNull(amm.getId()))
			flag =  true;
		
		return flag;
	}
	

	@Override
	public boolean isAssociationTemMember(String associationId, String currentUserId) {
		boolean flag=false;
		AssociationMemberModel amm=null;
		if(DataUtil.isNotNull(currentUserId))
			amm = this.associationDao.getAssociationTempMember(associationId,currentUserId);
		if(DataUtil.isNotNull(amm) && DataUtil.isNotNull(amm.getId()))
			flag =  true;
		
		return flag;
	}

	@Override
	public boolean isAssociationConfirmMember(String associationId, String curUserId) {
		boolean flag=false;
		AssociationMemberModel amm=null;
		if(DataUtil.isNotNull(curUserId))
			amm = this.associationDao.getAssociationConfirmMember(associationId,curUserId);
		if(DataUtil.isNotNull(amm) && DataUtil.isNotNull(amm.getId()))
			flag =  true;
		
		return flag;
	}

	@Override
	public AssociationMemberModel getAssociationProprieter(String associationId) {
		return this.associationDao.getAssociationProprieter(associationId);
	}

	@Override
	public AssociationAdvisorModel getAssociationAdvisor(String aamId) {
		
		return this.associationDao.getAssociationAdvisor(aamId);
	}

	@Override
	public boolean isCurAssociationAdvisor(String associationId,String currentUserId) {
		AssociationAdvisorModel aam = this.associationDao.getCurAssociationAdvisor(associationId,currentUserId);
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getId())){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void updateAdvisor(AssociationAdvisorModel newAam) {
		if(DataUtil.isNotNull(newAam)){
			this.associationDao.update(newAam);
		}
	}

	@Override
	public void addAssociationBaseInfo(AssociationBaseinfoModel associationPo) {
		if(DataUtil.isNotNull(associationPo)){
			this.associationDao.save(associationPo);
		}
	}

	@Override
	public void truncateAdvisorInfo(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			this.associationDao.rollbackAssociationAdvisor(associationId);
		}
	}
	
	@Override
	public void truncateManagerInfo(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			this.associationDao.rollbackAssociationManager(associationId);
		}
	}

	@Override
	public void saveAttacheMent(String applyId, String[] fileId) {
		if(DataUtil.isNotNull(applyId)){
			//上传的附件进行处理
			if (ArrayUtils.isEmpty(fileId))
				fileId = new String[0];
			List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(applyId);
			for (UploadFileRef ufr : list) {
				if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId())){
					this.fileUtil.deleteFormalFile(ufr);
				}
			}
			for (String id : fileId){
				this.fileUtil.updateFormalFileTempTag(id, applyId);
			}
		}
	}

	@Override
	public void addAssociationApplyInfo(AssociationApplyModel aam,String applyType,String operateStatus) {
		if(DataUtil.isNotNull(aam)&&DataUtil.isNotNull(aam.getId())){
			AssociationBaseinfoModel newPo = aam.getAssociationPo();
			AssociationBaseinfoModel associationPo = this.associationDao.getAssociationInfo(newPo.getId());
			BeanUtils.copyProperties(newPo, associationPo, new String[]{"id","isValid","creator","createTime","deleteStatus","isTopten","honorRating"});
			//修改社团基本信息
			this.associationDao.update(associationPo);
			aam.setOperateStatus(operateStatus);
			AssociationApplyModel newAam = this.associationDao.getAssociationApplyInfo(aam);
			if("MANAGER_SAVE".equals(operateStatus)){
				BeanUtils.copyProperties(aam, newAam, new String[]{"associationPo","applyTypeDic","applyStatus","initiator","creator","createTime","deleteStatus","processstatus"});
			}else if("MANAGER_SUBMIT".equals(operateStatus)){
				BeanUtils.copyProperties(aam, newAam, new String[]{"associationPo","applyTypeDic","applyStatus","initiator","creator","createTime","deleteStatus"});

			}
			//修改社团申请信息
			this.associationDao.update(newAam);
		}else{
			AssociationApplyModel newAam=this.formateAssociationApplyInfo(aam,applyType,operateStatus);
			this.associationDao.save(newAam);
		}
	}

	/**
	 * 封装新增的社团申请信息
	 * @param aam					社团申请对象
	 * @param applyType		社团申请类型
	 * @param operateStatus	操作类型【提交、保存】
	 */
	private AssociationApplyModel formateAssociationApplyInfo(AssociationApplyModel aam,String applyType,String operateStatus) {
		//保存社团基本信息
		AssociationBaseinfoModel associationPo = new AssociationBaseinfoModel();
		if(AssociationConstants.APPLY_STATUS.REGISTER.toString().equals(applyType)){
			associationPo = this.saveAssociationBaseInfo(aam);
		}else{
			associationPo = this.updateAssociationBaseInfo(aam,applyType);
		}
		
		//保存社团申请
		aam.setAssociationPo(associationPo);
		aam.setCreator(new User(this.sessionUtil.getCurrentUserId()));
		aam.setDeleteStatus(this.dicUtil.getStatusNormal());
		aam.setOperateStatus(operateStatus);
		return aam;
	}

	/**
	 * 获取当前登陆人的操作状态
	 * @param associationId		社团id
	 * @param currentUserId    当前用户
	 * @param operateType		操作类型
	 */
	private String getOperateStatus(String associationId,String currentUserId,String operateType) {
		String returnValue="";
		//当前用户是否社团指导老师
		boolean curUserIsAdvisor = this.isCurAssociationAdvisor(associationId,currentUserId);
		if(operateType.equals(Constants.OPERATE_STATUS.SAVE.toString())){
			if(curUserIsAdvisor){
				returnValue = AssociationConstants.OPERATE_STATUS.ADVISOR_SAVE.toString();
			}else{
				returnValue = AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString();
			}
		}else if(operateType.equals(Constants.OPERATE_STATUS.SUBMIT.toString())){
			if(curUserIsAdvisor){
				returnValue = AssociationConstants.OPERATE_STATUS.ADVISOR_SUBMIT.toString();
			}else{
				returnValue = AssociationConstants.OPERATE_STATUS.MANAGER_SUBMIT.toString();
			}
		}
		
		return returnValue;
	}

	/**
	 * 更新社团基本信息
	 * @param aam				社团申请对象
	 * @param applyType	社团申请类型
	 * @return	社团对象
	 */
	private AssociationBaseinfoModel updateAssociationBaseInfo(AssociationApplyModel aam,String applyType) {
		AssociationBaseinfoModel newPo = aam.getAssociationPo();
		AssociationBaseinfoModel associationPo = this.associationDao.getAssociationInfo(newPo.getId());
		if(AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(applyType)){
			BeanUtils.copyProperties(newPo, associationPo, new String[]{"id","college","isValid","creator","createTime","deleteStatus"});
		}else if(AssociationConstants.APPLY_STATUS.CANCEL.toString().equals(applyType)){
			associationPo.setIsCancel(this.dicUtil.getDicInfo("Y&N", "Y"));
		}
		//修改社团基本信息
		this.associationDao.update(associationPo);
		return associationPo;
	}

	/**
	 * 新增社团信息
	 * @param aam	社团申请对象
	 * @return	社团对象
	 */
	private AssociationBaseinfoModel saveAssociationBaseInfo(AssociationApplyModel aam) {
		//保存社团基本信息
		AssociationBaseinfoModel associationPo = aam.getAssociationPo();
		String associationId = associationPo.getId();
		associationPo.setId(associationId);
		//设置社团注册名称
		associationPo.setAssociationRegisterName(associationPo.getAssociationName());
		//原社团类型
		associationPo.setAssociationRegisterType(associationPo.getAssociationType());
		//设置注册时的社团性质
		associationPo.setIsMajorRegister(associationPo.getIsMajor());
		//设置社团是否注销【新注册是未：N】
		associationPo.setIsCancel(this.dicUtil.getDicInfo("Y&N", "N"));
		//设置社团删除状态
		associationPo.setDeleteStatus(this.dicUtil.getStatusNormal());
		//设置申请社团的负责人(社长)
		associationPo.setProprieter(associationPo.getProprieter());
		//设置注册时的社长
		associationPo.setProprieterRegister(associationPo.getProprieter());
		//初始社团为无效状态
		associationPo.setIsValid(this.dicUtil.getDicInfo("Y&N", "N"));
		//创建人当前登录人
		associationPo.setCreator(new User(this.sessionUtil.getCurrentUserId()));
		//创建时间
		associationPo.setCreateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
		//保存社团信息
		this.associationDao.save(associationPo);
		return associationPo;
	}

	@Override
	public void modifyAssociationApplyInfo(AssociationApplyModel aam) {
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getId())){
			this.associationDao.update(aam);
		}
	}

	@Override
	public List<AssociationApplyModel> getAssociationApplyByIds(String applyIds) {
		if(DataUtil.isNotNull(applyIds)){
			
			return this.associationDao.getAssociationApplyByIds(applyIds);
		}else{
			return new ArrayList<AssociationApplyModel>();
		}
	}

	@Override
	public void updateAssociationInfo(AssociationBaseinfoModel associationPo) {
		if(DataUtil.isNotNull(associationPo)){
			this.associationDao.update(associationPo);
		}
	}

	@Override
	public AssociationBaseinfoModel getAssociationInfoByName(String associationName) {
		if(DataUtil.isNotNull(associationName)){
			return this.associationDao.getAssociationInfoByName(associationName);
		}
		return new AssociationBaseinfoModel();
	}

	@Override
	public AssociationBaseinfoModel getAssociationInfoByCode(String associationCode) {
		if(DataUtil.isNotNull(associationCode)){
			return this.associationDao.getAssociationInfoByCode(associationCode);
		}
		return new AssociationBaseinfoModel();
	}

	@Override
	public boolean isCurAssociationMember(String associationId, String curUserId) {
		AssociationMemberModel amm = this.associationDao.getAssociationMemberByUserId(associationId, curUserId);
		if(DataUtil.isNotNull(amm) && DataUtil.isNotNull(amm.getId())){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void createAssociationMember(String associationId,String currentUserId) {
		AssociationMemberModel existAmm = this.associationDao.getAssociationMember(associationId, currentUserId);
		if(DataUtil.isNotNull(existAmm) && DataUtil.isNotNull(existAmm.getId())){
			existAmm.setMemberStatus(CYLeagueUtil.APPROVE_NOT_APPROVE);
			this.associationDao.update(existAmm);
		}else{
			AssociationMemberModel amm = this.newAssociationMember(associationId);
			this.associationDao.save(amm);
		}
	}
	
	/**
	 * 封装社团成员对象
	 * @param associationId
	 */
   private AssociationMemberModel newAssociationMember(String associationId) {
		AssociationBaseinfoModel abm = new AssociationBaseinfoModel();
		abm.setId(associationId);
		AssociationMemberModel amm = new AssociationMemberModel();
		amm.setAssociationPo(abm);
		amm.setCreateTime(DateUtil.getDate());
		amm.setUpdateTime(DateUtil.getDate());
		amm.setRegisterForm(this.dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_ONLINE"));
		amm.setIsManager(this.dicUtil.getDicInfo("Y&N", "N"));
		amm.setMemberStatus(CYLeagueUtil.APPROVE_NOT_APPROVE);
		amm.setDeleteStatus(this.dicUtil.getStatusNormal());
		StudentInfoModel sim = new StudentInfoModel();
		sim.setId(this.sessionUtil.getCurrentUserId());
		amm.setMemberPo(sim);
		return amm;
	}

	@Override
	public boolean isTopTen(String associationId) {
		AssociationBaseinfoModel associationPo = this.associationDao.getTopTenAssociation(associationId);
		if(DataUtil.isNotNull(associationPo) && DataUtil.isNotNull(associationPo.getId())){
			return true;
		}
		return false;
	}

	@Override
	public boolean isMemberExist(String associationId, String memberId) {
		AssociationMemberModel existAmm = this.associationDao.getAssociationMember(associationId, memberId);
		return DataUtil.isNotNull(existAmm);
	}

	@Override
	public void saveAssociationMember(AssociationMemberModel amm) {
		this.associationDao.save(amm);
	}

	@Override
	public void deleteAssociationMember(String associationId, String memberId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(memberId)){
			this.associationDao.deleteAssociationMember(associationId,memberId);
		}
	}

	@Override
	public void updateAssociationMember(AssociationMemberModel amm) {
		if(DataUtil.isNotNull(amm)){
			this.associationDao.update(amm);
		}
	}

	@Override
	public Page pageQueryAssociationService(AssociationMemberModel amm, int pageNo, int pageSize, String userId) {
		return this.associationDao.pageQueryAssociationService(amm, pageNo, pageSize, userId);	
	}

	@Override
	public String importAssociationMember(List<AssociationMemberModel> list,
			 String[] compareId, String associationId)
			throws OfficeXmlFileException, IOException, IllegalAccessException,
			ExcelException, InstantiationException, ClassNotFoundException,
			Exception {
		Map map=new HashMap();
		if(compareId!=null){
			for(int i=0;i<compareId.length;i++){
				map.put(compareId[i], compareId[i]);
			}
		}
		
		// 错误信息
		String message = "";
		if (list != null && list.size() > 0){
				// 把导入的数据保存到数据库中
				String stuNumberText = "";
				for (AssociationMemberModel associationMember : list) { 
					//取出学号
					stuNumberText=associationMember.getStuNumberText();
					if (stuNumberText != null && !"".equals(stuNumberText)){
						
						IStudentCommonService studentCommonService = (IStudentCommonService)SpringBeanLocator.getBean("com.uws.common.service.impl.StudentCommonServiceImpl");
						//通过学号查询学生信息是否存在
						StudentInfoModel studentInfo=studentCommonService.queryStudentByStudentNo(stuNumberText);
						if(studentInfo!=null){
							//通过学号查询学生信息表中是否存在该学号存在则update
							AssociationMemberModel associationMemberPo=this.associationDao.getAssociationMemberByUserId(associationId, stuNumberText);
							if(associationMemberPo!=null){
								if(!map.containsKey(associationMemberPo.getId()) && associationMemberPo.getMemberStatus()!=null && associationMemberPo.getMemberStatus().getId()!=null && !associationMemberPo.getMemberStatus().getId().equals(CYLeagueUtil.APPROVE_PASS.getId())){
									associationMember.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
									associationMember.setJoinTime(new Date());
									BeanUtils.copyProperties(associationMember,associationMemberPo,new String[]{"id","associationPo","isManager","memberPo","registerForm","createTime","deleteStatus"});
									this.updateAssociationMember(associationMemberPo);
								}
							}else{
									this.addImportMember(associationMember,studentInfo,associationId);
							}
							
						}else{
							message ="学号"+stuNumberText+"的学生在系统中不存在，请确认后再上传！";	
						}
					}
				}
		} 
		return message;		
	}

	

	/**
	 * 封装导入成员对象
	 * @param associationMember		社团成员临时对象
	 * @param studentInfo						学生对象
	 * @param associationId					社团主键
	 */
	private void addImportMember(AssociationMemberModel associationMember,StudentInfoModel studentInfo,String associationId) {
		associationMember.setMemberPo(studentInfo);
		//设置审核通过
		associationMember.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
		associationMember.setIsManager(Constants.STATUS_NO);
		associationMember.setRegisterForm(dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_OFFLINE"));
		AssociationBaseinfoModel abmPo=this.getAssociationInfo(associationId);
		associationMember.setJoinTime(new Date());
		associationMember.setAssociationPo(abmPo);
		associationMember.setDeleteStatus(Constants.STATUS_NORMAL);
		this.saveAssociationMember(associationMember);
	}
	
	/**
	 * 导入数据比较
	 * @param list
	 * @return
	 */
	@Override
	public List<Object[]> compareData(List<AssociationMemberModel> list,String associationPoId) {
		List compareList = new ArrayList();
	    Object[] array = (Object[])null;
	    long count =this.associationDao.getAssociationMembers(associationPoId).size();
	    if (count != 0L) {
		      for (int i = 0; i < count / Constants.DEFAULT_EXCEL_MAX_COUNT + 1L; i++) {
		    	   AssociationMemberModel amm= new AssociationMemberModel();
		    	   AssociationBaseinfoModel abmPo=this.getAssociationInfo(associationPoId);
		    	   amm.setAssociationPo(abmPo);
		    	   Page page=this.associationDao.pageQueryAssociationMember_(amm,i+1,10);
			       List<AssociationMemberModel> memberList = (List<AssociationMemberModel>)page.getResult();
			        for(AssociationMemberModel member : memberList) {
						for(AssociationMemberModel xls : list) {
							if((member.getMemberPo().getId()).equals(xls.getStuNumberText())) {
								array = new Object[]{member,xls};
								compareList.add(array);
							}
						}
					}
		
		      }
	     
	    }
	    return compareList;
	}


	/**
	 * 同步社团成员人数
	 * @param associationId		社团主键
	 * @param counter					变动社团人数
	 * @param flag							变动标识[PLUS: 加，MINUS: 减]
	 */
	@Override
	public void synAssociationMemberNums(String associationId, int counter,String operator) {
		AssociationBaseinfoModel abm = this.getAssociationInfo(associationId);
		if(DataUtil.isNotNull(abm) && DataUtil.isNotNull(abm.getId())){
			if(CYLeagueUtil.OPERATOR_FLAG.PLUS.toString().equals(operator)){
				abm.setMemberNums(abm.getMemberNums()+counter);
			}else if(CYLeagueUtil.OPERATOR_FLAG.MINUS.toString().equals(operator)){
				abm.setMemberNums(abm.getMemberNums()-counter);
			}
			this.updateAssociationInfo(abm);
		}
	}

	@Override
	public void deleteUserRole(String userId,String roleCode) {
		this.commonRoleDao.deleteUserRole(userId, roleCode);
	}

	@Override
	public void saveUserRole(String userId, String roleCode) {
		this.commonRoleDao.saveUserRole(userId, roleCode);
	}

	@Override
	public boolean checkADvisorChange(String associationId,String advisorCondition,String advisors) {
		List<AssociationAdvisorModel> existAamList = 
				this.associationDao.getAssociationAdvisorList(associationId,advisorCondition);
		String [] advisorArray = (DataUtil.isNotNull(advisors))?advisors.split(","):new String[]{};
		if(advisorArray.length==existAamList.size()){
			return false;
		}
		return true;
	}

	@Override
	public boolean checkManagerChange(String associationId,String managerCondition,String managers) {
		List<AssociationMemberModel> existAmmList = 
		this.associationDao.getAssociationManagerList(associationId,managerCondition);
		String [] managerArray = (DataUtil.isNotNull(managers))?managers.split(","):new String[]{};
		if(managerArray.length==existAmmList.size()){
			return false;
		}
		return true;
	}

	@Override
	public void truncateManagerInfo(String associationId, String managerId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(managerId)){
			this.associationDao.truncateManagerInfo(associationId, managerId);
			this.deleteUserRole(managerId, CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
		}
	}

	@Override
	public void deleteAssociationApplyInfo(String applyId,String applyType) {
		AssociationApplyModel aam = this.associationDao.getAssociationApplyInfo(applyId);
		
		String register = AssociationConstants.APPLY_STATUS.REGISTER.toString();
		String cancel = AssociationConstants.APPLY_STATUS.CANCEL.toString();
		if(DataUtil.isNotNull(applyType) && applyType.equals(register)){
			this.deleteAssociationCascade(aam);
		}else if(DataUtil.isNotNull(applyType) && applyType.equals(cancel)){
			this.resetAssociationCancelStatus(aam);
		}
		
		//删除社团申请
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(applyType)){
			this.associationDao.deleteAssociationApplyInfo(applyId);
			this.associationDao.deleteAssociationAttachCascade(applyId,applyType);
		}
	}

	/**
	 * 重置社团注销标志
	 * @param aam		社团申请对象
	 */
	private void resetAssociationCancelStatus(AssociationApplyModel aam) {
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getAssociationPo())){
			String associationId = aam.getAssociationPo().getId();
			AssociationBaseinfoModel abm = this.associationDao.getAssociationInfo(associationId);
			abm.setIsCancel(Constants.STATUS_NO);
			this.updateAssociationInfo(abm);
		}
	}

	/**
	 * 级联删除社团信息
	 * @param aam		社团申请对象
	 */
	private void deleteAssociationCascade(AssociationApplyModel aam) {
		if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getAssociationPo())){
			String associationId = aam.getAssociationPo().getId();
			//级联删除社团指导老师
			this.associationDao.deleteAssociationTeachers(associationId);
			//级联删除社团负责人角色
			this.deleteMemberRole(associationId);
			//级联删除社团负责人
			this.associationDao.deleteAssociationMember_(associationId);
			//级联删除社团信息
			this.associationDao.deleteAssociationInfo(associationId);
		}
	}

	@Override
	public void deleteAssociationMember_(String associationId) {
		this.associationDao.deleteAssociationMember(associationId);
	}

	/**
	 * 删除社团负责人角色
	 * @param associationId
	 */
	private void deleteMemberRole(String associationId) {
		List<AssociationMemberModel> ammList = this.getAssociationManagers(associationId);
		for(AssociationMemberModel amm:ammList){
			//查看当前负责人是否参加多个社团
			String memberId =(DataUtil.isNotNull(amm.getMemberPo()))?amm.getMemberPo().getId():"";
			List<AssociationBaseinfoModel> abmList = this.associationDao.getAssociationByMember(memberId);
			if(abmList!=null && abmList.size()==1){
				this.deleteUserRole(memberId, CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
			}
		}
	}

	@Override
	public List<AssociationApplyModel> getApprovingApply(String associationId,String applyType) {

		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(applyType))
			
			 return this.associationDao.getApproveingApply(associationId,applyType);
			
			 return new ArrayList<AssociationApplyModel>();
	}
	/** 
	* @Title: updateActivityMembers 
	* @Description: 修改活动参与人员的审核状态
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	@Override
	public void updateAssociationMemberHonor(String associationId, String ids, String approveStatus) {
		AssociationBaseinfoModel association =(AssociationBaseinfoModel)this.associationDao.get(AssociationBaseinfoModel.class,associationId);
		if(association!=null){
			//审核通过的参与人员id
			String honorIds [] = ids.split(",");
			for(int i=0;i<honorIds.length;i++){
				String honorId = honorIds[i];
				//删除活动id为activityId的参与人员id为memberId的信息
				this.associationDao.updateAssociationMemberHonor(associationId,honorId,approveStatus);
			}
		}
	}
	@Override
	public AssociationHonorModel getAssociationHonorById(String id) {
		return (AssociationHonorModel)this.associationDao.get(AssociationHonorModel.class, id);
	}

	@Override
	public void saveHonor(AssociationHonorModel honor, String[] fileId) {
		honor.setDeleteStatus(Constants.STATUS_NORMAL);
		this.associationDao.save(honor);
		//上传的附件进行处理
		 if (ArrayUtils.isEmpty(fileId)) {
		       return;
		    }
		 for (String id : fileId){
			 this.fileUtil.updateFormalFileTempTag(id, honor.getId());
		 }
		
	}

	@Override
	public void updateHonor(AssociationHonorModel honor, String[] fileId) {
		AssociationHonorModel honorPo=this.getAssociationHonorById(honor.getId());
		if(Constants.OPERATE_STATUS_SAVE.getId().equals(honor.getOperateStatus().getId())){
			BeanUtils.copyProperties(honor, honorPo, new String[]{"id","deleteStatus","createTime","approveResult"});
		}else{
			BeanUtils.copyProperties(honor, honorPo, new String[]{"id","deleteStatus","createTime"});
		}
		this.associationDao.update(honorPo);
		 //上传的附件进行处理
		 if (ArrayUtils.isEmpty(fileId))
			 fileId = new String[0];
		     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(honorPo.getId());
		     for (UploadFileRef ufr : list) {
		       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId())){
		    	   this.fileUtil.deleteFormalFile(ufr);
		       }
		     }
		     for (String id : fileId){
		       this.fileUtil.updateFormalFileTempTag(id, honorPo.getId());
		     }
		
	}
	@Override
	public void delAssociationHonor(AssociationHonorModel honor) {
		this.associationDao.delete(honor);
	}
	@Override
	public AssociationMemberModel findAssociationMember(String assocaiatioId,String memberId) {
		if(DataUtil.isNotNull(assocaiatioId) && DataUtil.isNotNull(memberId)){
			return this.associationDao.getAssociationConfirmMember(assocaiatioId,memberId);
		}
		return new AssociationMemberModel();
	}

	@Override
	public List<UploadFileRef> getAssociationAttache(String applyId,String attacheType) {
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(attacheType)){
			return this.associationDao.getAssociationAttache(applyId,attacheType);
		}
		
		return new ArrayList<UploadFileRef>();
	}

	@Override
	public AssociationAttacheModel getAssociationAttacheByFileId(String applyId, String fileId) {
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(fileId)){
			return this.associationDao.getAssociationAttacheByFileId(applyId,fileId);
		}
		return new AssociationAttacheModel();
	}

	@Override
	public UploadFileRef getFileUploadRef(String applyId, String fileId) {
		if(DataUtil.isNotNull(applyId) && DataUtil.isNotNull(fileId)){
			return this.associationDao.getFileUploadRef(applyId, fileId);
		}
		return new UploadFileRef();
	}

	@Override
	public void saveAssociationAttach(AssociationAttacheModel aam_) {
		if(DataUtil.isNotNull(aam_))
			this.associationDao.save(aam_);
	}

	@Override
	public void deleteAssociationAttach(String applyId, String applyType) {
		if(DataUtil.isNotNull(applyId)&&DataUtil.isNotNull(applyType)){
			this.associationDao.deleteAssociationAttach(applyId, applyType);
		}
	}

	@Override
	public void deleteAdvisorInfo(String id) {
		if(DataUtil.isNotNull(id)){
			this.associationDao.deleteAdvisorInfo(id);
		}
	}

	@Override
	public void deleteAssociationAdvisor(String associationId, String advisorId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(advisorId)){
			this.associationDao.deleteAssociationAdvisor(associationId,advisorId);
		}
	}
	
	@Override
	public void deleteAssociationMemberInfo(String id) {
		if(DataUtil.isNotNull(id)){
			this.associationDao.deleteAssociationMemberInfo(id);
		}
	}

	@Override
	public String getHiddenAssociationAdvisor(String associationId) {
		List<AssociationAdvisorModel> aamList = this.associationDao.getAssociationAdvisors(associationId);
		StringBuffer advisors= new StringBuffer();
		for(int i=0;i<aamList.size();i++){
			AssociationAdvisorModel aam=aamList.get(i);
			if(i==aamList.size()-1){
				advisors.append(aam.getAdvisorPo().getCode());
			}else{
				advisors.append(aam.getAdvisorPo().getCode()).append(",");
			}
		}
		return advisors.toString();
	}

	@Override
	public String getHiddenAssociationAdvisors(String associationId) {
		List<AssociationMemberModel> ammList = this.associationDao.getAssociationManager(associationId);
		StringBuffer managers= new StringBuffer();
		for(int i=0;i<ammList.size();i++){
			AssociationMemberModel amm=ammList.get(i);
			if(i==ammList.size()-1){
				managers.append(amm.getMemberPo().getId());
			}else{
				managers.append(amm.getMemberPo().getId()).append(",");
			}
		}
		return managers.toString();
	}

	@Override
	public void saveAtumpo(AssociationTempUserModel atumPo) {
		if(DataUtil.isNotNull(atumPo)){
			this.associationDao.save(atumPo);
		}
	}

	@Override
	public void deleteAssociationTempUser(String associationId, String userType) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(userType)){
			this.associationDao.deleteAssociationTempUser(associationId, userType);
		}
	}

	@Override
	public List<AssociationTempUserModel> getTempUserInfo(String associationId, String userType) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(userType)){
			return this.associationDao.getTempUserInfo(associationId, userType);
		}else{
			return new ArrayList<AssociationTempUserModel>();
		}
	}

	@Override
	public AssociationAdvisorModel findAssociationAdvisor(String associationId,String teacherId) {
		if(DataUtil.isNotNull(associationId)&&DataUtil.isNotNull(teacherId)){
			
			return this.associationDao.findAssociationAdvisor(associationId,teacherId);
		}
		return new AssociationAdvisorModel();
	}

	@Override
	public AssociationTempUserModel getAssociationTempUser(String associationId, String userId, String userType) {
		if(DataUtil.isNotNull(associationId)&&
			 DataUtil.isNotNull(userId)&&
			 DataUtil.isNotNull(userType)){
			
			return this.associationDao.getAssociationTempUser(associationId,userId,userType);
		}
		return new AssociationTempUserModel();
	}

	@Override
	public void updateAssociationTempUser(AssociationTempUserModel atumPo) {
		if(DataUtil.isNotNull(atumPo))
			this.associationDao.update(atumPo);
	}

	@Override
	public AssociationApplyModel getAssociationCurApply(String applyId) {
		return this.getAssociationApplyInfo(applyId);
	}
	
	/**
	 * 更新对象
	 * @param obj
	 */
	@Override
	public void updateObject(BaseModel obj) {
		this.associationDao.update(obj);
		
	}

	@Override
	public AssociationAdvisorModel getAssociationAdvisor(String associationId,String teacherId) {
		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(teacherId)){
			return this.associationDao.getAssociationAdvisor(associationId,teacherId);
		}
		return new AssociationAdvisorModel();
	}

	@Override
	public void deleteAssociationTempUser(String associationId) {
		if(DataUtil.isNotNull(associationId)){
			this.associationDao.deleteAssociationTempUser(associationId);
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
	public Page pageQueryAssociationBaseInfo_(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		return this.associationDao.pageQueryAssociationBaseInfo_(abm,pageNo,pageSize);
	}
	
	/**
	 * 分页获取社团基本信息--【负责人】
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	@Override
	public Page pageQueryAssociationBaseInfoByManager(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		return this.associationDao.pageQueryAssociationBaseInfoByManager(abm,pageNo,pageSize);
	}
	
	/**
	 * 分页获取社团基本信息--【指导老师】
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	@Override
	public Page pageQueryAssociationBaseInfoByAdvisor(AssociationBaseinfoModel abm,int pageNo, int pageSize) {
		return this.associationDao.pageQueryAssociationBaseInfoByAdvisor(abm,pageNo,pageSize);
	}

	@Override
	public boolean curUserIsModifyAdvisor(String associationId,String currentUserId) {
		String userType=AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString();
		AssociationTempUserModel atum = this.associationDao.getAssociationTempUser(associationId, currentUserId, userType);
		if(DataUtil.isNotNull(atum) && DataUtil.isNotNull(atum.getId()))
			return true;
		return false;
	}

	@Override
	public String getCurApplyAdvisors(String associationId, String userType) {
		List<AssociationTempUserModel> atumList = this.getTempUserInfo(associationId, userType);
		StringBuffer advisors=new StringBuffer();
		for(int i=0;i<atumList.size();i++){
			AssociationTempUserModel atum = atumList.get(i);
			BaseTeacherModel btm = this.baseDataService.findTeacherById(atum.getUserId());
			if(i==atumList.size()-1){
				advisors.append(btm.getName());
			}else{
				advisors.append(btm.getName()).append("，");
			}
		}
		return advisors.toString();
	}
	
	
	@Override
	public boolean isAssociationNameRepeat(String associationId,String associationName)
	{
	    return this.associationDao.isAssociationNameRepeat(associationId,associationName);
	}

	/**
	 * 描述信息: 学院中社团的个数
	 * @param collegeId
	 * @return
	 * 2016-1-8 上午10:53:53
	 */
	@Override
    public int getAssociationTotalCountByCollege(String collegeId)
    {
		return associationDao.getAssociationTotalCountByCollege(collegeId);
    }

	/**
	 * 描述信息: 社员荣誉列表审核通过的
	 * @param assoicaiotnId
	 * @param userId
	 * @return
	 * 2016-2-3 下午3:40:42
	 */
	@Override
    public List<AssociationHonorModel> getMemberHonorList(String assoicaiotnId,String memberId)
    {
		return associationDao.getMemberHonorList(assoicaiotnId,memberId);
    }

	@Override
    public Page pageQueryAssociationApprovedHonor(String associationId, AssociationHonorModel honor, int pageNo, int pageSize)
    {
		return this.associationDao.pageQueryAssociationApprovedHonor(associationId,honor,pageNo,pageSize);
    }
}
