package com.uws.association.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

import com.uws.core.base.BaseModel;
import com.uws.core.base.IBaseService;
import com.uws.core.excel.ExcelException;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.domain.association.AssociationAttacheModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.domain.association.AssociationHonorModel;
import com.uws.domain.association.AssociationMemberModel;
import com.uws.domain.association.AssociationTempUserModel;
import com.uws.sys.model.UploadFileRef;

public  interface IAssociationService extends IBaseService{
	
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
	 * 获取社团【注册、变更、注销】申请列表
	 * @param aam	查询条件对象
	 * @return	【注册、变更、注销】List
	 */
	public List<AssociationApplyModel> getAssociationApplyList(AssociationApplyModel  aam);

	/**
	 * 社团【注册、变更、注销】申请
	 * @param abm	社团基础信息实例
	 */
	public void associationApplyHandler(AssociationBaseinfoModel  abm);
	
	/**
	 * 社团【注册、变更、注销】审批
	 * @param abm
	 */
	public void associationApproveHandler(AssociationBaseinfoModel  abm);
	
	/**
	 * 获取社团分页信息
	 * @param abm	查询条件对象
	 * @return	分页对象
	 */
	public Page pageQueryAssociationInfo(AssociationBaseinfoModel abm,int pageNo,int pageSize);

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
	public Page pageQueryAssociationByMember(AssociationMemberModel amm,int pageNo,int pageSize);
	
	/**
	 * 获取社团管理员参加的社团信息
	 * @param amm			社团负责人对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页对象
	 */
	public Page pageQueryAssociationByManager(AssociationMemberModel amm,int pageNo,int pageSize);
	
	/**
	 * 获取社团成员参加的社团信息
	 * @param amm			社团负责人对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页对象
	 */
	public Page pageQueryAssociationByMember_(AssociationMemberModel amm,int pageNo,int pageSize);
	
	/**
	 * 获取可报名社团分页信息
	 * @param abm			查询条件对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return	分页对象
	 */
	public Page pageQueryAssociationReportList(AssociationBaseinfoModel abm,int pageNo, int pageSize);
	
	/**
	 * 获取单个社团对象
	 * @param abm	社团基础信息
	 * @return	社团对象
	 */
	public AssociationBaseinfoModel getAssociationInfo(String associationId);
	
	/**
	 * 创建社团信息
	 * @param abm	社团基础信息
	 */
	public void createAssociationInfo(AssociationBaseinfoModel abm);
	
	/**
	 * 修改社团信息
	 * @param abm	社团基础信息
	 */
	public void modifyAssociationInfo(AssociationBaseinfoModel abm);
	
	/**
	 * 删除社团信息【物理删除】
	 * @param abm	社团基础信息
	 */
	public void deleteAssociationInfo(String associationId);

	/**
	 * 废弃社团信息【逻辑删除】
	 * @param abm	社团基础信息
	 */
	public void deprecatedAssociationInfo(String associationId);
	
	/**
	 * 获取社团的指导老师
	 * @param associationId	社团主键
	 * @return 社团指导老师列表
	 */
	public List<AssociationAdvisorModel> getAssociationAdvisors(String associationId);
	
	/**
	 * 分页获取社团指导老师
	 * @param associationId  社团ID
	 * @param pageNo				当前页码
	 * @param pageSize			分页大小
	 * @return	社团指导老师分页信息
	 */
	public Page pageQueryAssociationAdvisor(String associationId,String teacherIdsConditon,int pageNo,int pageSize);
	
	/**
	 * 分页查询社团指导老师
	 * @param associationId	社团主键
	 * @param pageNo				当前页码
	 * @param pageSize			分页大小
	 * @return 社团指导老师分页信息
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
	 * 获取社团成员
	 * @param associationId	社团主键
	 * @return 社团成员列表
	 */
	public List<AssociationMemberModel> getAssociationMembers(String associationId);
	
	/**
	 * 获取社团负责人
	 * @param associationId	社团主键
	 * @return 社团负责人列表
	 */
	public List<AssociationMemberModel> getAssociationManagers(String associationId);
	
	/**
	 * 分页获取社团负责人【异步列表】
	 * @param abm					社团对象
	 * @param pageNo				当前页码
	 * @param pageSize			分页大小
	 * @return	社团成员分页信息
	 */
	public Page pageQueryAssociationMember(AssociationBaseinfoModel abm,String managerIdsConditon,int pageNo,int pageSize);
	
	/**
	 * 分页获取社团成员信息
	 * @param abm			社团基本信息对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @return						分页信息
	 */
	public Page pageQueryAssociationMember_(AssociationMemberModel amm, int pageNo, int pageSize);
	
	/**
	 * 分页获取社团负责人【同步列表】
	 * @param abm					社团对象
	 * @param pageNo				当前页码
	 * @param pageSize			分页大小
	 * @return	社团成员分页信息
	 */
	public Page pageQueryAssociationMember(AssociationBaseinfoModel associationPo, int pageNo, int pageSize);
	
	/**
	 * 新增社团成员
	 * @param amm	社团成员对象
	 */
	public void addAssociationMember(AssociationMemberModel amm);
	
	/**
	 * 删除社团成员
	 * @param amm	社团成员对象
	 */
	public void delAssociationMember(AssociationMemberModel amm);
	
	/**
	 * 编辑社团成员信息
	 * @param amm	社团成员对象
	 */
	public void modifyAssociationMember(AssociationMemberModel amm);
	
//	/**
//	 * 导入社团成员信息
//	 * @param filePath			附件路劲
//	 * @param importId		导入信息配置ID
//	 * @param dataMap		数据源集合
//	 * @param class_				导入信息实体类
//	 */
//	public void importAssociationMember(String filePath, String importId, Map dataMap,Class class_);
	
	/**
	 * 获取社团成员荣誉列表
	 * @param memberId	社团主键
	 * @return	社团成员荣誉列表
	 */
	public List<AssociationHonorModel> getMemberHonorList(String memberId);
	
	/**
	 * 分页获取社团成员荣誉列表
	 * @param am					社团成员对象
	 * @param pageNo			当前页码
	 * @param pageSize		分页大小
	 * @return	社团成员荣誉分页信息
	 */
	public Page pageQueryAssociationHonor(AssociationMemberModel am,AssociationHonorModel honor,int pageNo,int pageSize);
	
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

	/**
	 * 新增社团成员荣誉
	 * @param ahm				社团成员荣誉对象
	 */
	public void addAssociationHonor(AssociationHonorModel ahm);
	
	/**
	 * 编辑社团成员荣誉
	 * @param ahm				社团成员荣誉对象
	 */
	public void modifyAssociationHonor(AssociationHonorModel ahm);
	
	/**
	 * 删除社团成员荣誉信息
	 * @param ahm				社团成员荣誉对象
	 */
	public void deleteAssociationHonor(AssociationHonorModel ahm);

	/**
	 * 获得社团指导老师名称
	 * @param aam	社团申请对象
	 * @return
	 */
	public String getAssociationAdvisors(AssociationApplyModel aam);
	
	/**
	 * 获得社团指导老师名称
	 * @param associationId	社团主键
	 * @return	指导老师名称
	 */
	public String getAssociationAdvisorName(String associationId);

	/**
	 * 获取社团成员人数
	 * @param associationId	社团主键
	 * @return								社团人数
	 */
	public int getAssociationMemberNums(String associationId);

	/**
	 * 获取社团申请信息
	 * @param associationId		社团id
	 * @return	社团申请对象								
	 */
	public AssociationApplyModel getAssociationApplyInfo(AssociationApplyModel aam);

	/**
	 * 获取社团申请对象
	 * @param objectId	业务主键
	 * @return 社团申请对
	 */
	public AssociationApplyModel getAssociationApplyInfo(String objectId);
	
	/**
	 * 删除社团指导老师
	 * @param associationId	社团主键
	 */
	public void deleteAssociationTeachers(String associationId);

	/**
	 * 添加社团指导老师
	 * @param aam	指导老师对象
	 */
	public void addAssociationAdvisor(AssociationAdvisorModel aam);

	/**
	 * 删除社团负责人【社长除外】
	 * @param associationId	社团主键
	 * @param proprieter		社团负责人
	 */
	public void deleteAssociationManagers(String associationId,AssociationMemberModel proprieter);
	
	/**
	 * 添加社团成员
	 * @param amm	社团成员对象
	 */
	public void addAssociationManager(AssociationMemberModel amm);

	/**
	 * 获取单个社团负责人
	 * @param pk	社团成员主键
	 * @return			社团成员对象
	 */
	public AssociationMemberModel getAssociationMemberPo(String pk);

	/**
	 * 设置社团成员团内职务
	 * @param pk		主键
	 * @param associationPosition	职务
	 */
	public void setMemberPosition(String pk, String associationPosition);
	
	/**
	 * 获取社团社长
	 * @param associationId	社团主键
	 * @return	 [true,false]
	 */
	public boolean isAssociationProprieter(String associationId);

	/**
	 * 判断当前登录人是否社团负责人
	 * @param currentUserId	当前用户id
	 * @param associationId		社团主键
	 */
	public boolean getAssociationMemberByUserId(String associationId,String currentUserId);
	
	/**
	 * 获取系统中存在的成员【正式成员，临时成员】
	 * @param associationId		社团id
	 * @param memberId			成员id
	 * @return									社团成员id
	 */
	public AssociationMemberModel getAssociationMember_(String associationId, String memberId);
	
	/**
	 * 获取社团成员
	 * @param associationId		当前用户id
	 * @param currentUserId	社团主键
	 * @return	[true/false]
	 */
	public boolean getAssociationMember(String associationId,String currentUserId);
	
	/**
	 * 当前用户是否社团的临时成员
	 * @param associationId	 	社团主键
	 * @param currentUserId	当前用户
	 */
	public boolean isAssociationTemMember(String associationId, String currentUserId);
	
	/**
	 * 当前用户是否社团的正式成员
	 * @param associationId	 	社团主键
	 * @param currentUserId	当前用户
	 */
	public boolean isAssociationConfirmMember(String associationId, String curUserId);

	/**
	 * 获取社团负责人
	 * @param associationId	社团主键
	 * @return	社团负责人对象
	 */
	public AssociationMemberModel getAssociationProprieter(String associationId);

	/**
	 * 获取社团指导老师信息
	 * @param aamId	社团指导老师主键O
	 */
	public AssociationAdvisorModel getAssociationAdvisor(String aamId);

	/**
	 * 判断当前用户是否社团指导老师
	 * @param associationId		社团主键
	 * @param currentUserId	当前登陆用户
	 * @return 【true,false】
	 */
	public boolean isCurAssociationAdvisor(String associationId,String currentUserId);

	/**
	 * 更新指导老师简介
	 * @param newAam	指导老师对象
	 */
	public void updateAdvisor(AssociationAdvisorModel newAam);

	/**
	 * 创建社团基本信息
	 * @param associationPo	社团基本信息对象
	 */
	public void addAssociationBaseInfo(AssociationBaseinfoModel associationPo);

	/**
	 * 清除【指导老师】信息
	 * @param associationId	社团主键
	 */
	public void truncateAdvisorInfo(String associationId);
	
	/**
	 * 清除【社团负责人】信息
	 * @param associationId	社团主键
	 */
	public void truncateManagerInfo(String associationId);

	/**
	 * 保存附件
	 * @param associationId	社团主键
	 * @param fileId	附件id列表
	 */
	public void saveAttacheMent(String associationId, String[] fileId);

	/**
	 * 保存社团申请信息
	 * @param aam					社团申请对象
	 * @param applyType		社团申请类型
	 * @param operateStatus	操作类型【负责人、指导老师】【保存、提交】
	 */
	public void addAssociationApplyInfo(AssociationApplyModel aam,String applyType,String operateStatus);

	/**
	 * 修改社团申请信息
	 * @param aam
	 */
	public void modifyAssociationApplyInfo(AssociationApplyModel aam);

	/**
	 * 获取勾选的社团申请列表
	 * @param applyIds	查询条件
	 */
	public List<AssociationApplyModel> getAssociationApplyByIds(String applyIds);

	/**
	 * 更新社团基本信息对象
	 * @param associationPo		社团基本信息
	 */
	public void updateAssociationInfo(AssociationBaseinfoModel associationPo);

	/**
	 * 根据社团名称获取社团对象
	 * @param associationName	社团名称
	 * @return	社团基本信息
	 */
	public AssociationBaseinfoModel getAssociationInfoByName(String associationName);

	/**
	 * 根据社团编号获取社团对象
	 * @param associationName	社团编号
	 * @return	社团基本信息
	 */
	public AssociationBaseinfoModel getAssociationInfoByCode(String associationCode);

	/**
	 * 验证当前用户是否本社团成员
	 * @param associationId	社团id
	 * @param curUserId		当前用户id
	 * @return	[true、false]
	 */
	public boolean isCurAssociationMember(String associationId, String curUserId);

	/**
	 * 创建社团成员对象
	 * @param associationId		社团主键
	 * @param currentUserId	当前用户
	 */
	public void createAssociationMember(String associationId,String currentUserId);

	/**
	 * 验证当前社团是否十佳社团
	 * @param associationId		社团主键
	 * @return	[true,false]
	 */
	public boolean isTopTen(String associationId);

	/**
	 * 验证社团是否存在该成员
	 * @param associationId		社团主键
	 * @param memberId			成员id
	 * @return [true,false]
	 */
	public boolean isMemberExist(String associationId, String memberId);

	/**
	 * 新增社团成员
	 * @param amm		社团成员对象
	 */
	public void saveAssociationMember(AssociationMemberModel amm);

	/**
	 * 删除社团成员
	 * @param associationId	社团主键
	 * @param memberId		成员id
	 */
	public void deleteAssociationMember(String associationId, String memberId);

	/**
	 * 修改社团成员
	 * @param amm		社团成员对象
	 */
	public void updateAssociationMember(AssociationMemberModel amm);
	
	/**
	 * 社员服务列表
	 * @param amm			社团成员对象
	 * @param pageNo		当前页码
	 * @param pageSize	分页大小
	 * @param userId		用户id
	 * @return						 分页对象
	 */
	public Page pageQueryAssociationService(AssociationMemberModel amm, int pageNo,int pageSize,String userId);

	/**
	 * 导入比较的数据
	 * @param list	社团成员集合	
	 * @param compareId	业务主键
	 * @return 	错误提示信息
	 */
	public String importAssociationMember(List<AssociationMemberModel> list, String[] compareId,String associationPoId) throws OfficeXmlFileException, IOException, IllegalAccessException, ExcelException, InstantiationException, ClassNotFoundException, Exception;

	
	/**
	 * 导入数据比较
	 * @param list	社团成员列表
	 * @return			重复数据集合
	 */
	public List<Object[]> compareData(List<AssociationMemberModel> list,String associationPoId);

	/**
	 * 同步社团成员数目
	 * @param associationId	社团主键
	 * @param counter				成员变化数目
	 * @param operator			变化类型【加，减】
	 */
	public void synAssociationMemberNums(String associationId, int counter,String operator);

	/**
	 * 删除用户角色
	 * @param userId 用户id
	 * @param roleCode 角色编码
	 */
	public void deleteUserRole(String userId,String roleCode);

	/**
	 * 新增用户角色
	 * @param userId 用户id
	 * @param roleCode 角色编码
	 */
	public void saveUserRole(String userId, String roleCode);

	/**
	 * 判断社团指导老师是否变动
	 * @param associationId				社团主键
	 * @param advisorCondition		查询条件
	 * @param advisors						选择的指导老师
	 * @return											[true、false]
	 */
	public boolean checkADvisorChange(String associationId,String advisorCondition,String advisors);

	/**
	 * 判断社团指导老师是否变动
	 * @param associationId				社团主键
	 * @param managerCondition	查询条件
	 * @param managers						选择的社团负责人
	 * @return											[true、false]
	 */
	public boolean checkManagerChange(String associationId,String managerCondition,String managers);

	/**
	 * 删除指定社团成员
	 * @param associationId				社团主键
	 * @param managerId					成员id
	 */
	public void truncateManagerInfo(String associationId, String managerId);
	
	/**
	 * 删除社团下的所有成员
	 * @param associationId
	 */
	public void deleteAssociationMember_(String associationId);

	/**
	 * 删除社团申请信息
	 * @param applyId			社团申请id
	 * @param applyType	社团申请类型
	 */
	public void deleteAssociationApplyInfo(String applyId,String applyType);

	/**
	 * 获取未完成的社团申请
	 * @param associationId	社团主键
	 * @param applyType		申请类型
	 */
	public List<AssociationApplyModel> getApprovingApply(String associationId, String applyType);
	/**
	 * 社员荣誉审核
	 * @param associationId	社团主键
	 * @param memberIds		申请类型
	 * @param approveStatus		申请类型
	 */
	public void updateAssociationMemberHonor(String associationId, String memberIds, String approveStatus);

	/**
	 * 根据id 查找社员荣誉
	 * @param id					业务主键
	 * @return						社团荣誉对象
	 */
	public AssociationHonorModel getAssociationHonorById(String id);
	
	/**
	 * 保存荣誉
	 * @param honor		社团荣誉对象
	 * @param fileId			附件集合
	 */
	public void saveHonor(AssociationHonorModel honor,String[] fileId);
	
	/**
	 * 更新荣誉
	 * @param honor		社团荣誉对象
	 * @param fileId			附件集合
	 */
	public void updateHonor(AssociationHonorModel honor,String[] fileId);
	
	/**
	 * 删除荣誉
	 * @param honor		社团荣誉对象
	 */
	public void delAssociationHonor(AssociationHonorModel honor);
	
	/**
	 * 获取社团指定的成员对象
	 * @param assocaiatioId	社团主键
	 * @param memberId		成员id
	 * @return 社团成员对象
	 */
	public AssociationMemberModel findAssociationMember(String assocaiatioId,String memberId);

	/**
	 * 获取社团附件
	 * @param applyId				社团申请id
	 * @param attacheType	附件类型
	 * @return	附件列表
	 */
	public List<UploadFileRef> getAssociationAttache(String applyId,String attacheType);

	/**
	 * 获取社团附件
	 * @param applyId				社团申请id
	 * @param id							附件id
	 * @return 附件业务对象
	 */
	public AssociationAttacheModel getAssociationAttacheByFileId(String applyId, String id);

	/**
	 * 获取附件关系实体类
	 * @param applyId		业务主键
	 * @param fileId			附件id
	 */
	public UploadFileRef getFileUploadRef(String applyId, String fileId);

	/**
	 * 保存社团管理附件
	 * @param aam_		社团管理附件对象
	 */
	public void saveAssociationAttach(AssociationAttacheModel aam_);

	/**
	 * 删除指定类型附件
	 * @param applyId				业务主键
	 * @param applyType		附件类型
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
	 * 封装社团指导老师的隐藏域 
	 * @param associationId		社团主键
	 */
	public String getHiddenAssociationAdvisor(String associationId);

	/**
	 * 封装社团负责人的隐藏域
	 * @param associationId		社团主键
	 */
	public String getHiddenAssociationAdvisors(String associationId);

	/**
	 * 保存社团临时用户对象
	 * @param atumPo			社团临时用户对象
	 */
	public void saveAtumpo(AssociationTempUserModel atumPo);

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
	 * @return 指导老师对象
	 */
	public AssociationAdvisorModel findAssociationAdvisor(String associationId,String teacherId);

	/**
	 * 获取社团临时用户信息
	 * @param associationId		社团id
	 * @param userId					用户id
	 * @param userType				用户类型
	 * @return	社团临时用户
	 */
	public AssociationTempUserModel getAssociationTempUser(String associationId, String userId,String userType);

	/**
	 * 修改社团临时用户
	 * @param atumPo		社团临时用户对象
	 */
	public void updateAssociationTempUser(AssociationTempUserModel atumPo);

	/**
	 * 获取当前处理中的申请
	 * @param applyId			申请id
	 * @return 当前申请实体对象
	 */
	public AssociationApplyModel getAssociationCurApply(String applyId);
	
	/**
	 * 更新对象
	 * @param obj
	 */
	public void updateObject(BaseModel obj);

	/**
	 * 获取社团指导老师对象
	 * @param associationId	社团主键
	 * @param teacherId			用户id
	 * @return	知道老师对象
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

	/**
	 * 当前用户是否变更列表中的指导老师
	 * @param associationId			社团id
	 * @param currentUserId		当前用户
	 * @return	[true/false]
	 */
	public boolean curUserIsModifyAdvisor(String associationId,String currentUserId);

	/**
	 * 获取变更中的指导老师名称
	 * @param associationId		社团主键
	 * @param userType				用户类型
	 * @return	指导老师名称
	 */
	public String getCurApplyAdvisors(String associationId, String userType);
    
	/**
	 * 
	 * @Title: IAssociationService.java 
	 * @Package com.uws.association.service 
	 * @Description:验证社团名称重复
	 * @author LiuChen 
	 * @date 2015-12-25 上午11:42:59
	 */
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
	public List<AssociationHonorModel> getMemberHonorList(String assoicaiotnId,String userId);
}
