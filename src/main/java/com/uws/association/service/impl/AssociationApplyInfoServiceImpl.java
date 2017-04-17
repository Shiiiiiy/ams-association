package com.uws.association.service.impl;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.association.dao.IAssociationApplyInfoDao;
import com.uws.association.dao.IAssociationDao;
import com.uws.association.service.IAssociationApplyInfoService;
import com.uws.association.util.AssociationConstants;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.IdUtil;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;

/**
 * 
* @ClassName: AssociationApplyInfoServiceImpl 
* @Description: 社团申请serive实现类型
* @author 联合永道
* @date 2016-1-12 下午4:39:18 
*
 */
@Service("com.uws.association.service.impl.AssociationApplyInfoServiceImpl")
public class AssociationApplyInfoServiceImpl extends BaseServiceImpl implements IAssociationApplyInfoService
{
	@Autowired
	private IAssociationApplyInfoDao associationApplyInfoDao;
	
	@Autowired
	private IAssociationDao associationApplyDao;
	
	private DicUtil dicUtil = DicFactory.getDicUtil();
	private FileUtil fileUtil = FileFactory.getFileUtil();
	//session工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(AssociationConstants.NAMESPACE);
	
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
		return associationApplyInfoDao.pageQueryAssociationApply(aam,pageNo,pageSize,userId,isAdvisor);
    }

	/**
	 * 描述信息: ID查询
	 * @param applyId
	 * @return
	 * 2016-1-12 下午5:03:05
	 */
	@Override
    public AssociationApplyModel getApplyModelById(String applyId)
    {
	    if(!StringUtils.isEmpty(applyId))
	    	return (AssociationApplyModel) associationApplyInfoDao.get(AssociationApplyModel.class, applyId);
	    return null;
    }

	/**
	 * 描述信息: 注册申请的保存修改 
	 * @param applyModel
	 * @param fileId
	 * @param advisorIds
	 * @param memberIds
	 * @param status
	 * 2016-1-13 下午12:03:14
	 */
	@Override
    public void saveOrUpdateRegister(AssociationApplyModel applyModel, String[] fileId,String status)
    {
		String applyId = applyModel.getId();
		if(!StringUtils.isEmpty(applyId))
		{
			AssociationApplyModel applyModelPo = this.getApplyModelById(applyId);
			BeanUtils.copyProperties(applyModel, applyModelPo, new String[]{"applyTypeDic","deleteStatus","creator"});		
			//上传的附件进行处理
			if (ArrayUtils.isEmpty(fileId))
			       fileId = new String[0];
			     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(applyModelPo.getId());
			     for (UploadFileRef ufr : list) {
			       if (!ArrayUtils.contains(fileId, ufr.getUploadFile().getId()))
			         this.fileUtil.deleteFormalFile(ufr);
			    }
			     for (String id : fileId){
			       this.fileUtil.updateFormalFileTempTag(id, applyModelPo.getId());
			  }
		}else{
			User creator = new User();
			creator.setId(sessionUtil.getCurrentUserId());
			applyModel.setCreator(creator);
			applyModel.setDeleteStatus(dicUtil.getStatusNormal());
			applyModel.setApplyTypeDic(AssociationConstants.registerDic);
			
			associationApplyInfoDao.save(applyModel);
			// 上传的附件进行处理
			if (!ArrayUtils.isEmpty(fileId))
			{
				for (String id : fileId)
					this.fileUtil.updateFormalFileTempTag(id,applyModel.getId());
			}
		}
		
		//指导老师处理
		if("20".equals(status))
		{
			associationApplyInfoDao.deleteRegisterAdvisor(applyModel.getId());
			String advisorId = applyModel.getOrignAdvisorId();
			if(!"".equals(advisorId))
			{
				String[] advisorIds = advisorId.split(",");
				AssociationAdvisorModel advisor = null;
				BaseTeacherModel teahcer = null;
				for(String id : advisorIds)
				{
					advisor = new AssociationAdvisorModel();
					teahcer = new BaseTeacherModel();
					advisor.setDeleteStatus(dicUtil.getStatusNormal());
					teahcer.setId(id);
					advisor.setAdvisorPo(teahcer);
					advisor.setAssociationApplyModel(applyModel);
					advisor.setStatus(AssociationConstants.STATUS_SAVE_STRING);
					associationApplyInfoDao.save(advisor);
				}
			}
		}
    }

	/**
	 * @param applyId
	 * 注释,根据不同的类型在更新其他的业务逻辑
	 * 2016-1-13 下午1:19:48
	 */
	@Override
    public void deleteAssociationApplyInfo(String applyId)
    {
	    if(!StringUtils.isEmpty(applyId))
	    {
	    	AssociationApplyModel applyModel = this.getApplyModelById(applyId);
	    	if(null!=applyModel)
	    	{
	    		applyModel.setDeleteStatus(dicUtil.getStatusDeleted());
	    		associationApplyInfoDao.update(applyModel);
	    	}
	    }
	    
    }

	@Override
    public void update(AssociationApplyModel applyModel)
    {
	    if(null!=applyModel)
	    {
	    	associationApplyInfoDao.update(applyModel);
	    }
    }

	/**
	 * 描述信息: 保存注销申请信息
	 * @param applyModel
	 * @param fileId
	 * 2016-1-21 下午5:19:47
	 */
	@Override
    public void saveOrUpdateCancelApply(AssociationApplyModel applyModelVo,String[] financeFileId,String status,String associationId)
    {
		String applyId = applyModelVo.getId();
		if(!StringUtils.isEmpty(applyId))
		{
			AssociationApplyModel applyModel = this.getApplyModelById(applyId);
			applyModel.setApplyReason(applyModelVo.getApplyReason());
			applyModel.setFinancialComments(applyModelVo.getFinancialComments());
			applyModel.setApplyStatus(applyModelVo.getApplyStatus());
			applyModel.setOperateStatus(applyModelVo.getOperateStatus());
			associationApplyInfoDao.update(applyModel);
			//上传的附件进行处理
			if (ArrayUtils.isEmpty(financeFileId))
				financeFileId = new String[0];
			     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(applyId);
			     for (UploadFileRef ufr : list) {
			       if (!ArrayUtils.contains(financeFileId, ufr.getUploadFile().getId()))
			         this.fileUtil.deleteFormalFile(ufr);
			    }
			     for (String id : financeFileId){
			       this.fileUtil.updateFormalFileTempTag(id,applyId);
			  }
		}else{
			applyModelVo.setApplyTypeDic(AssociationConstants.cancelDic);
			User creator = new User();
			creator.setId(sessionUtil.getCurrentUserId());
			applyModelVo.setCreator(creator);
			applyModelVo.setDeleteStatus(dicUtil.getStatusNormal());
			associationApplyInfoDao.save(applyModelVo);
			// 上传的附件进行处理
			if (!ArrayUtils.isEmpty(financeFileId))
			{
				for (String id : financeFileId)
					this.fileUtil.updateFormalFileTempTag(id,applyModelVo.getId());
			}
		}
		
		//指导老师处理
		if("20".equals(status))
		{
			List<AssociationAdvisorModel> advisorList = associationApplyDao.getAssociationAdvisors(associationId);
			
			for(AssociationAdvisorModel advisor : advisorList )
			{
				AssociationAdvisorModel advisorVo = new AssociationAdvisorModel();
				BeanUtils.copyProperties(advisor, advisorVo, new String[]{"id","associationPo","createTime","updateTime","status"});
				advisorVo.setAssociationApplyModel(applyModelVo);
				advisorVo.setStatus(AssociationConstants.STATUS_SAVE_STRING);
				associationApplyInfoDao.save(advisorVo);
			}
		}
		
    }

	/**
	 * 描述信息: 指导老师查询
	 * @param applyId
	 * @param teacherId
	 * @return
	 * 2016-1-25 下午5:11:09
	 */
	@Override
    public AssociationAdvisorModel getAssociationAdvisor(String applyId,
            String teacherId)
    {
		return associationApplyInfoDao.getAssociationAdvisor(applyId,teacherId);
    }
	
	/**
	 * 
	 * @Description:根据当前登录的指导老师和社团申请的id查询指导老师对象
	 * @author LiuChen  
	 * @date 2016-1-22 下午4:31:42
	 */
	@Override
	public List<AssociationAdvisorModel> getAssociationAdvisorByApplyId(String applyId)
	{
		return this.associationApplyInfoDao.getAssociationAdvisorByApplyId(applyId);
	}
	
	/**
	 * 
	 * @Description:根据id查询指导老师对象
	 * @author LiuChen  
	 * @date 2016-1-22 下午5:39:09
	 */
	@Override
	public AssociationAdvisorModel getAssociationAdvisorById(String id)
	{
	    return (AssociationAdvisorModel)this.associationApplyInfoDao.get(AssociationAdvisorModel.class, id);
	}
	
	/**
	 * 
	 * @Description: 修改指导老师信息
	 * @author LiuChen  
	 * @date 2016-1-22 下午5:39:25
	 */
	@Override
	public void updateAdvisor(AssociationAdvisorModel associationAdvisorPo)
	{
	    this.associationApplyInfoDao.update(associationAdvisorPo);
	}
	
	/**
	 * 
	 * @Description:保存社团基本信息
	 * @author LiuChen  
	 * @date 2016-1-25 下午5:19:25
	 */
	@Override
	public void saveBaseinfoModel(AssociationBaseinfoModel baseAssociationModel)
	{   
		if(baseAssociationModel.getId()==null || StringUtils.isEmpty(baseAssociationModel.getId()))
		{
			baseAssociationModel.setId(IdUtil.getUUIDHEXStr());
			this.associationApplyInfoDao.save(baseAssociationModel);
		}
	}
	
	
	@Override
	public void updateBaseinfoModel(AssociationBaseinfoModel associationPo)
	{
	    this.associationApplyInfoDao.update(associationPo);
	}
	
	@Override
	public void updateApplyModel(AssociationApplyModel associationApplyModel)
	{
		this.associationApplyInfoDao.update(associationApplyModel);
	}

	/**
	 * 
	 * @Description: 
	 * @author LiuChen  
	 * @date 2016-1-27 下午4:53:01
	 */
	@Override
    public void saveOrUpdateChange(AssociationApplyModel applyModelVo, String[] financeFileId, String status)
    {
		String applyId = applyModelVo.getId();
		//变更指导人的处理
		String changeAdvisorId = applyModelVo.getChangedAdvisorId();
		if(StringUtils.isEmpty(changeAdvisorId))
		{
			applyModelVo.setChangedAdvisorId(applyModelVo.getOrignAdvisorId());
			applyModelVo.setChangedAdvisorName(applyModelVo.getOrignAdvisorName());
		}
		if(!StringUtils.isEmpty(applyId))
		{
			AssociationApplyModel applyModelPo = this.getApplyModelById(applyId);
			BeanUtils.copyProperties(applyModelVo, applyModelPo, new String[]{"id","associationPo","financialComments","suggest","processstatus","approveresult","initiator","nextapprover","creator","deleteStatus","associationFee","associationAim","college","memberId","openScope","isOpen","applyTypeDic"});
			this.update(applyModelPo);
			//上传的附件进行处理
			if (ArrayUtils.isEmpty(financeFileId))
				financeFileId = new String[0];
			     List<UploadFileRef> list = this.fileUtil.getFileRefsByObjectId(applyId);
			     for (UploadFileRef ufr : list) {
			       if (!ArrayUtils.contains(financeFileId, ufr.getUploadFile().getId()))
			         this.fileUtil.deleteFormalFile(ufr);
			    }
		     for (String id : financeFileId){
		       this.fileUtil.updateFormalFileTempTag(id,applyId);
		     }
		}else{
			User creator = new User();
			creator.setId(sessionUtil.getCurrentUserId());
			applyModelVo.setCreator(creator);
			applyModelVo.setDeleteStatus(dicUtil.getStatusNormal());
			applyModelVo.setApplyTypeDic(AssociationConstants.changeDic);
			
			associationApplyInfoDao.save(applyModelVo);
			// 上传的附件进行处理
			if (!ArrayUtils.isEmpty(financeFileId))
			{
				for (String id : financeFileId)
					this.fileUtil.updateFormalFileTempTag(id,applyModelVo.getId());
			}
		}
		
		//指导老师处理
		if("20".equals(status))
		{
			String advisorId = applyModelVo.getChangedAdvisorId();
			if(StringUtils.isEmpty(advisorId))
				advisorId = applyModelVo.getOrignAdvisorId();
			if(!"".equals(advisorId))
			{
				String[] advisorIds = advisorId.split(",");
				AssociationAdvisorModel advisor = null;
				BaseTeacherModel teahcer = null;
				String comments = "";
				AssociationAdvisorModel advisorPo = null;
				for(String id : advisorIds)
				{
					advisorPo = associationApplyDao.getAssociationAdvisor(applyModelVo.getAssociationPo().getId(), id);
					advisor = new AssociationAdvisorModel();
					teahcer = new BaseTeacherModel();
					advisor.setDeleteStatus(dicUtil.getStatusNormal());
					teahcer.setId(id);
					advisor.setAdvisorPo(teahcer);
					comments = null == advisorPo ? "" : advisorPo.getComments();
					advisor.setComments(comments);
					advisor.setAssociationApplyModel(applyModelVo);
					advisor.setStatus(AssociationConstants.STATUS_SAVE_STRING);
					associationApplyInfoDao.save(advisor);
				}
			}
		}
    }

	@Override
    public void updateAdvisorStatusByAssociationId(String associationId,
            Dic status)
    {
		associationApplyInfoDao.updateAdvisorStatusByAssociationId(associationId,status);
    }
	
	@Override
	public void updateAdvisorByApplyId(String[] advisorIds, String applyId,String associationPo)
	{
		associationApplyInfoDao.updateAdvisorByApplyId(advisorIds,applyId,associationPo);
	}

}
