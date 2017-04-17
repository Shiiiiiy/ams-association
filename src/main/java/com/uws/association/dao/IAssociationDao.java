package com.uws.association.dao;

import java.util.List;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.domain.association.AssociationAttacheModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.domain.association.AssociationHonorModel;
import com.uws.domain.association.AssociationMemberModel;
import com.uws.domain.association.AssociationTempUserModel;
import com.uws.sys.model.UploadFileRef;

public interface IAssociationDao extends IBaseDao{

	/**
	 * 分页获取社团基本信息
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	public Page pageQueryAssociationBaseInfo(AssociationBaseinfoModel abm,int pageNo, int pageSize);
	
	/**
	 * 获取可报名的社团列表
	 * @param abm			查询条件对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 */
	public Page pageQueryReportAssociationInfo(AssociationBaseinfoModel abm,int pageNo, int pageSize);

	/**
	 * 获取社团负责人参加的社团信息
	 * @param amm			社团负责人对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页对象
	 */
	public Page pageQueryAssociationByMember(AssociationMemberModel amm,int pageNo, int pageSize);
	
	/**
	 * 获取社团管理员参加的社团信息
	 * @param amm			社团负责人对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页对象
	 */
	public Page pageQueryAssociationByManager(AssociationMemberModel amm,int pageNo, int pageSize);
	
	/**
	 * 获取社团成员参加的社团信息
	 * @param amm			社团负责人对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页对象
	 */
	public Page pageQueryAssociationByMember_(AssociationMemberModel amm,int pageNo, int pageSize);
	

	/**
	 * 根据成员获取其参加的社团列表
	 * @param memberId		成员id
	 */
	public List<AssociationBaseinfoModel> getAssociationByMember(String memberId);
	
	/**
	 * 分页获取社团申请信息【审批人】
	 * @param aam			社团申请对象 
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页信息
	 */
	public Page pageQueryAssociationApplyInfo(AssociationApplyModel aam, int pageNo,int pageSize);
	
	/**
	 * 分页获取社团申请信息【指导老师】
	 * @param aam			社团申请对象 
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页信息
	 */
	public Page pageQueryAssociationApplyByAdvisor(AssociationApplyModel aam, int pageNo,int pageSize);
	
	/**
	 * 分页获取社团申请信息【社团负责人】
	 * @param aam			社团申请对象 
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页信息
	 */
	public Page pageQueryAssociationApplyByMember(AssociationApplyModel aam, int pageNo,int pageSize);
	
	/**
	 * 查询发起的申请列表
	 * @param aam		页面查询条件
	 * @return	发起的申请列表
	 */
	public List<AssociationApplyModel> getAssociationApplyList(AssociationApplyModel aam);

	/**
	 * 获得社团指导老师异步展现的分页信息
	 * @param associationId					社团主键
	 * @param teacherIdsConditon		分页查询条件
	 * @param pageNo								当前页码
	 * @param pageSize							分页大小
	 * @return												分页信息
	 */
	public Page pageQueryAssociationAdvisor(String associationId,String teacherIdsConditon,int pageNo, int pageSize);

	/**
	 *  获得社团指导老师异步展现的分页信息
	 * @param associationId			社团主键
	 * @param pageNo						当前页码
	 * @param pageSize					分页大小
	 * @return										分页信息
	 */
	public Page pageQueryAssociationAdvisor(String associationId, int pageNo,int pageSize);
	
	/**
	 * 根据指导老师获取社团信息
	 * @param aam			指导老师对象
	 * @param pageNo		当前页
	 * @param pageSize	分页大小
	 * @return	分页信息
	 */
	public Page pageQueryAssociationByAdvisor(AssociationAdvisorModel aam, int pageNo,int pageSize);

	/**
	 * 获得社团成员异步展现的分页信息
	 * @param abm										社团对象 
	 * @param managerIdsConditon		分页查询条件
	 * @param pageNo									当前页码
	 * @param pageSize								分页大小
	 * @return													分页信息
	 */
	public Page pageQueryAssociationMember(AssociationBaseinfoModel abm,String managerIdsConditon,int pageNo, int pageSize);

	/**
	 * 分页获取社团负责人信息
	 * @param abm			社团基本信息对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页信息
	 */
	public Page pageQueryAssociationMember(AssociationBaseinfoModel abm, int pageNo, int pageSize);
	
	/**
	 * 分页获取社团基本信息信息
	 * @param abm			社团基本信息对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页信息
	 */
	public Page pageQueryAssociationMember_(AssociationMemberModel amm, int pageNo, int pageSize);

	/**
	 * 分页获取社团成员荣誉信息
	 * @param lmim			社团成员荣誉对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页信息
	 */
	public Page pageQueryAssociationHonor(AssociationMemberModel am,AssociationHonorModel honor,int pageNo, int pageSize);

	/**
	 * 或缺的社团指导老师列表
	 * @param associationId 社团主键
	 * @return 社团指导老师列表
	 */
	public List<AssociationAdvisorModel> getAssociationAdvisors(String associationId);

	/**
	 * 获得社团成员列表
	 * @param associationId 社团主键
	 * @return 社团成员列表
	 */
	public List<AssociationMemberModel> getAssociationMembers(String associationId);

	/**
	 * 获取社团负责人【除社长之外】
	 * @param associationId		社团主键
	 * @return									社团负责人列表
	 */
	public List<AssociationMemberModel> getAssociationManager(String associationId);
	
	/**
	 * 获得社团申请对象
	 * @param aam	社团申请对象
	 * @return	社团申请对象
	 */
	public AssociationApplyModel getAssociationApplyInfo(AssociationApplyModel aam);
	
	/**
	 * 获取社团申请对象
	 * @param applyId		社团申请主键
	 * @return 社团申请对象
	 */
	public AssociationApplyModel getAssociationApplyInfo(String applyId);
	
	/**
	 * 删除社团指导老师
	 * @param associationId	社团主键
	 */
	public void deleteAssociationTeachers(String associationId);

	/**
	 * 添加社团指导老师
	 * @param aam	社团指导老师对象
	 */
	public void addAssociationAdvisor(AssociationAdvisorModel aam);

	/**
	 * 获取社团对象
	 * @param associationId	社团主键
	 * @return	社团对象
	 */
	public AssociationBaseinfoModel getAssociationInfo(String associationId);
	
	/**
	 * 添加社团成员
	 * @param amm	社团成员对象
	 */
	public void addAssociationMember(AssociationMemberModel amm);

	/**
	 * 删除社团指导老师
	 * @param associationId	社团主键
	 */
	public void deleteAssociationManagers(String associationId);
	
	/**
	 * 删除社团成员
	 * @param associationId	社团主键
	 */
	public void deleteAssociationMember(String associationId);
	
	/**
	 * 删除社团所有成员
	 * @param associationId	社团主键
	 */
	public void deleteAssociationMember_(String associationId);

	/**
	 * 获得社团负责人
	 * @param memberId		社团成员id
	 */
	public AssociationMemberModel getAssociationManagerPo(String memberId);
	
	/**
	 * 获取社团负责人
	 * @param associationId		社团主键
	 * @param userId					用户id
	 * @return
	 */
	public AssociationMemberModel getAssociationManagerPo_(String associationId,String userId);

	/**
	 * 获取社团社长
	 * @param associationId	社团主键
	 * @return	 [true,false]
	 */
	public AssociationMemberModel getAssociationProprieter(String associationId);
	
	/**
	 * 获取社团成员【审批通过】
	 * @param currentUserId	当前用户id
	 * @param associationId		社团主键
	 */
	public AssociationMemberModel getAssociationMemberByUserId(String associationId,String currentUserId);
	
	/**
	 * 获取社团成员
	 * @param associationId		当前用户id
	 * @param currentUserId	社团主键
	 */
	public AssociationMemberModel getAssociationMember(String associationId,String currentUserId);

	/**
	 * 获取社团正式成员
	 * @param associationId	社团主键
	 * @param curUserId		当前用户id
	 * @return
	 */
	public AssociationMemberModel getAssociationConfirmMember(String associationId, String curUserId);
	
	/**
	 * 获取社团成员
	 * @param amm	社团成员对象
	 * @return	社团成员
	 */
	public AssociationMemberModel getAssociationMember(AssociationMemberModel amm);	
	
	/**
	 * 当前用户是否社团的临时成员
	 * @param associationId	 	社团主键
	 * @param currentUserId	当前用户
	 */
	public AssociationMemberModel getAssociationTempMember(String associationId, String currentUserId);

	/**
	 * 设置社团成员的社团职务
	 * @param ammId	社团成员主键
	 * @param associationPosition	社团职务
	 */
	public void saveMemberPosition(String associationPosition,String ammId);

	/**
	 * 设置社团负责人
	 * @param associationId		社团主键
	 * @param proprieter			社团负责人
	 */
	public void setAssociationPropreter(String associationId, String proprieter);

	/**
	 * 获取社团指导老师信息
	 * @param aamId	社团指导老师主键
	 */
	public AssociationAdvisorModel getAssociationAdvisor(String aamId);

	/**
	 * 验证用户是否本社团指导老师
	 * @param associationId		社团主键
	 * @param currentUserId	用户id
	 */
	public AssociationAdvisorModel getCurAssociationAdvisor(String associationId, String currentUserId);

	/**
	 * 回滚社团指导老师
	 * @param associationId	社团主键
	 */
	public void rollbackAssociationAdvisor(String associationId);

	/**
	 * 回滚社团负责人
	 * @param associationId	社团主键
	 */
	public void rollbackAssociationManager(String associationId);

	/**
	 * 获取勾选的社团申请列表
	 * @param applyIds	查询条件
	 * @return	社团申请列表
	 */
	public List<AssociationApplyModel> getAssociationApplyByIds(String applyIds);

	/**
	 * 根据社团名称获取社团对象
	 * @param associationName	社团名称
	 * @return	社团对象
	 */
	public AssociationBaseinfoModel getAssociationInfoByName(String associationName);

	/**
	 * 根据社团编号获取社团对象
	 * @param associationName	社团名称
	 * @return	社团基本信息
	 */
	public AssociationBaseinfoModel getAssociationInfoByCode(String associationCode);

	/**
	 * 获取十佳社团
	 * @param associationId	社团主键
	 * @return	十佳社团
	 */
	public AssociationBaseinfoModel getTopTenAssociation(String associationId);

	/**
	 * 删除社团成员
	 * @param associationId	社团id
	 * @param memberId		成员id
	 */
	public void deleteAssociationMember(String associationId, String memberId);
	
	/**
	 * 社员服务列表
	 * @param 
	 */
	public Page pageQueryAssociationService(AssociationMemberModel amm, int pageNo,int pageSize,String userId);

	/**
	 * 查询选中的社团指导老师
	 * @param associationId				社团主键
	 * @param advisorCondition		查询条件
	 * @return	指导老师列表
	 */
	public List<AssociationAdvisorModel> getAssociationAdvisorList(String associationId, String advisorCondition);

	/**
	 * 查询选中的社团负责人
	 * @param associationId				社团主键
	 * @param managerCondition	查询条件
	 * @return	社团负责人列表
	 */
	public List<AssociationMemberModel> getAssociationManagerList(String associationId, String managerCondition);

	/**
	 * 删除社团指定用户
	 * @param associationId			社团主键
	 * @param managerId				成员id
	 */
	public void truncateManagerInfo(String associationId, String managerId);

	/**
	 * 删除社团申请信息
	 * @param applyId		社团申请id
	 */
	public void deleteAssociationApplyInfo(String applyId);

	/**
	 * 删除社团信息
	 * @param associationId
	 */
	public void deleteAssociationInfo(String associationId);

	/**
	 * 获取未完成的社团申请
	 * @param associationId	社团主键
	 * @param applyType		申请类型
	 */
	public List<AssociationApplyModel> getApproveingApply(String associationId,String applyType);
	
	/** 
	* @Title: updateActivityMembers 
	* @Description: 修改活动参与人员的审核状态
	* @param  @param activity    
	* @return void    
	* @throws 
	*/
	public void updateAssociationMemberHonor(String associationId, String honorId, String approveStatus);

	/**
	 * 获取社团附件列表
	 * @param applyId				业务主键【社团申请id】
	 * @param attacheType	附件类型
	 * @return 附件列表
	 */
	public List<UploadFileRef> getAssociationAttache(String applyId,String attacheType);

	/**
	 * 获取社团附件列表
	 * @param applyId				业务主键【社团申请id】
	 * @param fileId					附件类型id
	 * @return 附件对象
	 */
	public AssociationAttacheModel getAssociationAttacheByFileId(String applyId, String fileId);

	/**
	 * 获取附件关系实体类
	 * @param applyId		业务主键
	 * @param fileId			附件id
	 */
	public UploadFileRef getFileUploadRef(String applyId, String fileId);

	/**
	 * 级联删除社团申请附件
	 * @param applyId				社团申请id
	 * @param applyType		社团申请类型
	 */
	public void deleteAssociationAttachCascade(String applyId, String applyType);

	/**
	 * 删除社团申请附件
	 * @param applyId				社团申请id
	 * @param applyType		社团申请类型
	 */
	public void deleteAssociationAttach(String applyId, String applyType);

	/**
	 * 删除社团指导老师
	 * @param id		指导老师实体主键
	 */
	public void deleteAdvisorInfo(String id);
	
	/**
	 * 删除社团指导老师
	 * @param associationId	社团id
	 * @param advisorId			指导老师id
	 */
	public void deleteAssociationAdvisor(String associationId, String advisorId);
	
	/**
	 * 删除社团成员
	 * @param id		社团成员实体主键
	 */
	public void deleteAssociationMemberInfo(String id);

	/**
	 * 删除社团临时用户对象
	 * @param associationId		社团主键
	 * @param userType				用户类型
	 */
	public void deleteAssociationTempUser(String associationId, String userType);

	/**
	 * 获取社团临时用户列表
	 * @param associationId		社团id
	 * @param userType				用户类型
	 * @return	社团临时成员列表
	 */
	public List<AssociationTempUserModel> getTempUserInfo(String associationId, String userType);

	/**
	 * 获取社团指定指导老师
	 * @param associationId	社团id
	 * @param teacherId			指导老师id
	 * @return	指导老师对象
	 */
	public AssociationAdvisorModel findAssociationAdvisor(String associationId,String teacherId);

	/**
	 * 获取社团临时用户信息
	 * @param associationId		社团id
	 * @param userId					用户id
	 * @param userType				用户类型
	 * @return	社团临时用户
	 */
	public AssociationTempUserModel getAssociationTempUser(String associationId, String userId, String userType);

	/**
	 * 获取当前处理中的申请
	 * @param associationId		社团id
	 * @param applyType			申请类型
	 * @return	申请对象
	 */
	public AssociationApplyModel getAssociationCurApply(String associationId,String applyType);

	/**
	 * 获取社团指定指导老师对象
	 * @param associationId	社团id
	 * @param teacherId			用户id
	 * @return	指导老师对象
	 */
	public AssociationAdvisorModel getAssociationAdvisor(String associationId,String teacherId);

	/**
	 * 清除当前社团的临时用户信息
	 * @param associationId		社团id
	 */
	public void deleteAssociationTempUser(String associationId);
	
	/**
	 * 分页获取社团基本信息--zhangmx
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	public Page pageQueryAssociationBaseInfo_(AssociationBaseinfoModel abm,int pageNo, int pageSize);
	
	/**
	 * 分页获取社团基本信息--【负责人】
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	public Page pageQueryAssociationBaseInfoByManager(AssociationBaseinfoModel abm,int pageNo, int pageSize);
	
	/**
	 * 分页获取社团基本信息--【指导老师】
	 * @param abm				社团基本信息对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return							分页信息
	 */
	public Page pageQueryAssociationBaseInfoByAdvisor(AssociationBaseinfoModel abm,int pageNo, int pageSize);

	public boolean isAssociationNameRepeat(String associationId,String associationName);
	
	/**
	 * 
	 * @Title: getAssociationTotalCountByCollege
	 * @Description: 学院社团的个数
	 * @param collegeId
	 * @return
	 * @throws
	 */
	public int getAssociationTotalCountByCollege(String collegeId);
	
	/**
	 * 
	 * @Title: getMemberHonorList
	 * @Description: 社员所在社团的荣誉列表
	 * @param assoicaiotnId
	 * @param userId
	 * @return
	 * @throws
	 */
	public List<AssociationHonorModel> getMemberHonorList(String assoicaiotnId,String memberId);
	
	/**
	 * 
	 * @Title: pageQueryAssociationApprovedHonor
	 * @Description: 荣誉列别 审核通过的
	 * @param am
	 * @param honor
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws
	 */
	public Page pageQueryAssociationApprovedHonor(String associationId,AssociationHonorModel honor,int pageNo,int pageSize);

}
