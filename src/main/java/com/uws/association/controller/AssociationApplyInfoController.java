package com.uws.association.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.apw.model.ApproveResult;
import com.uws.apw.model.Approver;
import com.uws.apw.service.IFlowInstanceService;
import com.uws.association.service.IAssociationApplyInfoService;
import com.uws.association.service.IAssociationService;
import com.uws.association.util.AssociationConstants;
import com.uws.association.util.AssociationUtils;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.ICommonRoleService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.CYLeagueUtil;
import com.uws.common.util.ChineseUtill;
import com.uws.common.util.Constants;
import com.uws.common.util.JsonUtils;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.session.UserSession;
import com.uws.core.util.DataUtil;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationApplyModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.domain.association.AssociationMemberModel;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.user.model.User;
import com.uws.util.ProjectConstants;

/**
 * 
* @ClassName: AssociationApplyInfoController 
* @Description: 社团申请controller  模块功能重构
* @author 联合永道
* @date 2016-1-12 下午4:37:28 
*
 */
@Controller
public class AssociationApplyInfoController extends BaseController
{
	@Autowired
	private IAssociationApplyInfoService associationApplyInfoService;
	@Autowired
  	private ICommonRoleService commonRoleService;
	@Autowired
	private IBaseDataService baseDataService;
	@Autowired
	private IStudentCommonService studentCommonServie;
	@Autowired
	private IAssociationService associationService;
	@Autowired
	private IFlowInstanceService flowInstanceService;

	//数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();
 	//附件工具类
 	private FileUtil fileUtil=FileFactory.getFileUtil();
	//session工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(AssociationConstants.NAMESPACE);
	
	//日期格式批量转换
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
	/**
	 * 
	 * @Title: getAssociationApplyList
	 * @Description: 申请查询 列表
	 * @param model
	 * @param request
	 * @param applyModel
	 * @return
	 * @throws
	 */
	@SuppressWarnings("unchecked")
    @RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList")
	public String getAssociationApplyList(ModelMap model,HttpServletRequest request,AssociationApplyModel  applyModel)
	{
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		String curUserId = this.sessionUtil.getCurrentUserId();
		boolean isAdvisor = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_TEACHER.toString());
		Page page = associationApplyInfoService.pageQueryAssociationApply(applyModel,pageNo,Page.DEFAULT_PAGE_SIZE,curUserId,isAdvisor);
		
		//判断当前登录知道老师的操作状态
		if(isAdvisor)
		{
			AssociationAdvisorModel associationAdvisorModel = null;
			String advisorStatus = "SAVE";
			List<AssociationApplyModel> result = (List<AssociationApplyModel>) page.getResult();
			for(AssociationApplyModel apply : result)
			{
				associationAdvisorModel =  associationApplyInfoService.getAssociationAdvisor(apply.getId(),curUserId);
				advisorStatus = null == associationAdvisorModel ? "SAVE" : associationAdvisorModel.getStatus();
				apply.setAdvisorStatus(advisorStatus);
			}
		}
		
		// 下拉列表 学院
		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
		model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
	    model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
		model.addAttribute("aam", applyModel);
		model.addAttribute("page", page);
		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
		model.addAttribute("registerApply", AssociationConstants.registerDic);
		model.addAttribute("changeApply", AssociationConstants.changeDic);
		model.addAttribute("managerSave", AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());
		model.addAttribute("advisorSave", AssociationConstants.OPERATE_STATUS.ADVISOR_SAVE.toString());
		model.addAttribute("cancelApply", AssociationConstants.cancelDic);
		model.addAttribute("isAdvisor", isAdvisor);
		model.addAttribute("refuseStatus","REJECT");
		
		return AssociationConstants.NAMESPACE_APPLY+"/associationApplyList";
	}
	
	/**
	 * 
	 * @Title: viewApply
	 * @Description: 查看申请信息
	 * @param model
	 * @param request
	 * @param applyId
	 * @return
	 * @throws
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-view/viewApply"})
	public String viewApply(ModelMap model,HttpServletRequest request,String applyId)
	{
		if(!StringUtils.isEmpty(applyId))
		{
			AssociationApplyModel  applyModel = associationApplyInfoService.getApplyModelById(applyId);
			//不同的申请查看不同的页面
			if(applyModel.getApplyTypeDic().getId().equals(AssociationConstants.registerDic.getId()))
			{
				this.setViewRegisterValue(model, request, applyModel);
				return AssociationConstants.NAMESPACE_APPLY+"/registerApplyView";
			}else if(applyModel.getApplyTypeDic().getId().equals(AssociationConstants.changeDic.getId()))
			{
				this.setViewChangeValue(model, request, applyModel);
				return AssociationConstants.NAMESPACE_APPLY+"/modifyApplyView";
			}else if(applyModel.getApplyTypeDic().getId().equals(AssociationConstants.cancelDic.getId()))
			{
				this.setViewCancelValue(model, request, applyModel);
				return AssociationConstants.NAMESPACE_APPLY+"/cancelApplyView";
			}
		}
		return "";
	}
	
	/**
	 * 
	 * @Title: setViewRegisterValue
	 * @Description: 注册申请查看信息封装
	 * @param model
	 * @param request
	 * @param applyModel
	 * @throws
	 */
	private void setViewRegisterValue(ModelMap model,HttpServletRequest request , AssociationApplyModel  applyModel)
	{
		model.addAttribute("fileList",fileUtil.getFileRefsByObjectId(applyModel.getId()));
		List<StudentInfoModel> studentList = new ArrayList<StudentInfoModel>();
		String curUserId = sessionUtil.getCurrentUserId();
		studentList.add(studentCommonServie.queryStudentById(curUserId));
		String managerIds = applyModel.getMemberId();
		if (!StringUtils.isEmpty(managerIds))
		{
			for (String id : managerIds.split(","))
				studentList.add(studentCommonServie.queryStudentById(id));
		}
		model.addAttribute("curUserId", curUserId);
		model.addAttribute("studentList", studentList);
		model.addAttribute("logicYesNo", dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("associationKind", dicUtil.getDicInfoList("ASSOCIATION_PROPERTY"));
	    model.addAttribute("openScope", dicUtil.getDicInfoList("ASSOCIATION_SCOPE"));
	    model.addAttribute("majorScope", dicUtil.getDicInfo("ASSOCIATION_SCOPE","MAJOR"));
		model.addAttribute("applyModel", applyModel);
		//封装指导老师
		String teacherId = applyModel.getOrignAdvisorId();
		if(!StringUtils.isEmpty(teacherId))
		{
			String[] teacherIds = teacherId.split(",");
			String[] teacherNames = applyModel.getOrignAdvisorName().split(";");
			List<AssociationAdvisorModel> advisorList = new ArrayList<AssociationAdvisorModel>();
			BaseTeacherModel teacher = null;
			for(int i=0;i<teacherIds.length;i++)
			{
				AssociationAdvisorModel advisor = new AssociationAdvisorModel();
				advisor.setAssociationApplyModel(applyModel);
				teacher = new BaseTeacherModel();
				teacher.setId(teacherIds[i]);
				teacher.setName(teacherNames[i]);
				advisor.setAdvisorPo(teacher);
				advisorList.add(advisor);
			}
			model.addAttribute("advisorList", advisorList);
		}
	}
	
	/**
	 * 
	 * @Title: setViewCancelValue
	 * @Description: 变更申请查看信息封装
	 * @param model
	 * @param request
	 * @param applyModel
	 * @throws
	 */
	private void setViewChangeValue(ModelMap model,HttpServletRequest request , AssociationApplyModel  applyModel)
	{
		model.addAttribute("fileList",fileUtil.getFileRefsByObjectId(applyModel.getId()));
		model.addAttribute("modifyItemMap", AssociationConstants.getApproveModifyItemMap());
      	model.addAttribute("applyModel", applyModel);
//    	List<AssociationAdvisorModel> advisorList = associationService.getAssociationAdvisors(applyModel.getAssociationPo().getId());
//    	model.addAttribute("advisorList", advisorList);
	}
	
	/**
	 * 
	 * @Title: setViewCancelValue
	 * @Description: 注销申请查看信息封装
	 * @param model
	 * @param request
	 * @param applyModel
	 * @throws
	 */
	private void setViewCancelValue(ModelMap model,HttpServletRequest request , AssociationApplyModel  applyModel)
	{
		model.addAttribute("fileList",fileUtil.getFileRefsByObjectId(applyModel.getId()));
		model.addAttribute("logicYesNo", dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("associationKind", dicUtil.getDicInfoList("ASSOCIATION_PROPERTY"));
        model.addAttribute("openScope", dicUtil.getDicInfoList("ASSOCIATION_SCOPE"));
     	 model.addAttribute("majorScope", dicUtil.getDicInfo("ASSOCIATION_SCOPE","MAJOR"));
      	model.addAttribute("applyModel", applyModel);
    	List<AssociationAdvisorModel> advisorList = associationService.getAssociationAdvisors(applyModel.getAssociationPo().getId());
    	model.addAttribute("advisorList", advisorList);
	}
	
	/**
	 * 编辑社团注册申请
	 * @param model			页面数据加载器
	 * @param request			页面请求
	 * @param applyId			业务主键
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-add/addAssociationRegister",AssociationConstants.NAMESPACE_APPLY+"/opt-edit/editAssociationRegister"})
	public String editAssociationRegisterApply(ModelMap model,HttpServletRequest request,String applyId)
	{
		AssociationApplyModel applyModel = null;
		if(!StringUtils.isEmpty(applyId)){
			applyModel = associationApplyInfoService.getApplyModelById(applyId);
			model.addAttribute("fileList",fileUtil.getFileRefsByObjectId(applyId));
			List<StudentInfoModel> studentList = new ArrayList<StudentInfoModel>();
			String curUserId = sessionUtil.getCurrentUserId();
			studentList.add(studentCommonServie.queryStudentById(curUserId));
			String managerIds = applyModel.getMemberId();
			if (!StringUtils.isEmpty(managerIds))
			{
				for (String id : managerIds.split(","))
				{
					if(!id.equals(curUserId))
						studentList.add(studentCommonServie.queryStudentById(id));
				}
			}
			model.addAttribute("curUserId", curUserId);
			model.addAttribute("studentList", studentList);
		}else
			applyModel = new AssociationApplyModel();
	   model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
       model.addAttribute("collegeList", baseDataService.listBaseAcademy());
       model.addAttribute("logicYesNo", dicUtil.getDicInfoList("Y&N"));
       model.addAttribute("associationKind", dicUtil.getDicInfoList("ASSOCIATION_PROPERTY"));
       model.addAttribute("openScope", dicUtil.getDicInfoList("ASSOCIATION_SCOPE"));
       model.addAttribute("allScope", dicUtil.getDicInfo("ASSOCIATION_SCOPE","COLLEGE"));
       model.addAttribute("applyModel", applyModel);
	   model.addAttribute("nowDate", new Date());
	   return AssociationConstants.NAMESPACE_APPLY+"/associationRegisterApplyEdit";
	}
	
	/**
	 * 
	 * @Title: saveAssociationRegisterApply
	 * @Description: 负责人提交保存注册申请信息
	 * @param model
	 * @param request
	 * @param applyModel
	 * @return
	 * @throws
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-save/saveAssociationRegister"})
	public String saveAssociationRegisterApply(ModelMap model,HttpServletRequest request,AssociationApplyModel applyModel,String[] applyFileId)
	{
		UserSession userSession = (UserSession) request.getSession().getAttribute("user_key");
		String submitStatus = request.getParameter("submitStatus");
		if("20".equals(submitStatus))
		{
			//applyModel.setApplyStatus(AssociationConstants.OPERATE_STATUS.ADVISOR_SAVE.toString());
			applyModel.setOperateStatus(AssociationConstants.OPERATE_STATUS.ADVISOR_SAVE.toString());
		}
		else
		{
			applyModel.setApplyStatus(AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());
			applyModel.setOperateStatus(AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());
		}
		//申请人，社长
		applyModel.setOrignManagerId(userSession.getUserId()+",");
		applyModel.setOrignManagerName(userSession.getUserName());
		
		associationApplyInfoService.saveOrUpdateRegister(applyModel, applyFileId ,submitStatus);
		return "redirect:"+AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList.do";
	}
	
	
	/**
	 * @Description: 编辑社团注销申请
	 * @param model
	 * @param request
	 * @param applyId
	 * @param associationId
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-edit/addCancelApply",AssociationConstants.NAMESPACE_APPLY+"/opt-edit/editCancelApply"})
	public String editCancelApply(ModelMap model,HttpServletRequest request,String applyId,String associationId)
	{
		AssociationApplyModel applyModel = new AssociationApplyModel();
		if(!StringUtils.isEmpty(applyId))
		{
			applyModel = associationApplyInfoService.getApplyModelById(applyId);
			model.addAttribute("fileList",fileUtil.getFileRefsByObjectId(applyId));
		}else{
			AssociationBaseinfoModel baseAssociationModel =  associationService.getAssociationInfo(associationId);
			copyBaseInfoToApplyModel(baseAssociationModel,applyModel);
		}
      	model.addAttribute("logicYesNo", dicUtil.getDicInfoList("Y&N"));
      	model.addAttribute("associationKind", dicUtil.getDicInfoList("ASSOCIATION_PROPERTY"));
      	model.addAttribute("openScope", dicUtil.getDicInfoList("ASSOCIATION_SCOPE"));
      	 model.addAttribute("majorScope", dicUtil.getDicInfo("ASSOCIATION_SCOPE","MAJOR"));
      	model.addAttribute("applyModel", applyModel);
    	List<AssociationAdvisorModel> advisorList = associationService.getAssociationAdvisors(associationId);
    	model.addAttribute("advisorList", advisorList);
		
		return AssociationConstants.NAMESPACE_APPLY+"/cancelApplyEdit";
	}
	
	/**
	 * 
	 * @Title: saveCancelApply
	 * @Description: 负责人 注销申请信息保存
	 * @param model
	 * @param request
	 * @param applyModelVo
	 * @param financeFileId
	 * @return
	 * @throws
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-edit/SaveCancelApply"})
	public String saveCancelApply(ModelMap model,HttpServletRequest request,AssociationApplyModel applyModelVo,String[] financeFileId)
	{
		String status = request.getParameter("status");
		String applyModelId = applyModelVo.getId();
		String associationId =  request.getParameter("associationPo.id");
		if("20".equals(status))
			applyModelVo.setOperateStatus(AssociationConstants.OPERATE_STATUS.ADVISOR_SAVE.toString());
		else
			applyModelVo.setOperateStatus(AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());
		
		//新增的时候 赋值
		if(StringUtils.isEmpty(applyModelId))
		{
			AssociationBaseinfoModel baseAssociationModel =  associationService.getAssociationInfo(associationId);
			copyBaseInfoToApplyModel(baseAssociationModel,applyModelVo);
		}
		
		associationApplyInfoService.saveOrUpdateCancelApply(applyModelVo, financeFileId ,status, associationId);
      	return "redirect:"+AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList.do";
	}
	
	/**
	 * 
	 * @Title: editModifyApply
	 * @Description: 负责人 编辑 修改 变更申请信息
	 * @param model
	 * @param request
	 * @param applyId
	 * @param associationId
	 * @return
	 * @throws
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-edit/addModifyApply",AssociationConstants.NAMESPACE_APPLY+"/opt-edit/editModifyApply"})
	public String editModifyApply(ModelMap model,HttpServletRequest request,String id,String associationId)
	{
		AssociationApplyModel applyModel = new AssociationApplyModel();
		if(!StringUtils.isEmpty(id))
		{
			applyModel = associationApplyInfoService.getApplyModelById(id);
			model.addAttribute("fileList",fileUtil.getFileRefsByObjectId(id));
		}else{
			AssociationBaseinfoModel baseAssociationModel =  associationService.getAssociationInfo(associationId);
			copyBaseInfoToApplyModel(baseAssociationModel,applyModel);
		}
      	model.addAttribute("logicYesNo", dicUtil.getDicInfoList("Y&N"));
      	model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
      	model.addAttribute("associationKind", dicUtil.getDicInfoList("ASSOCIATION_PROPERTY"));
        model.addAttribute("openScope", dicUtil.getDicInfoList("ASSOCIATION_SCOPE"));
      	model.addAttribute("applyModel", applyModel);
//    	List<AssociationAdvisorModel> advisorList = associationService.getAssociationAdvisors(associationId);
//    	model.addAttribute("advisorList", advisorList);
    	model.addAttribute("modifyItemMap", AssociationConstants.getApproveModifyItemMap());
    	//变更的具体项目
    	model.addAttribute("associatioName", AssociationConstants.MODIFY_TYPE.ASSOCIATION_NAME.toString());
    	model.addAttribute("associatioAdvisor", AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR.toString());
    	model.addAttribute("associatioManager", AssociationConstants.MODIFY_TYPE.ASSOCIATION_MANAGER.toString());
    	model.addAttribute("changeType", AssociationConstants.MODIFY_TYPE.ASSOCIATION_TYPE.toString());
    	model.addAttribute("associatioIsMajor", AssociationConstants.MODIFY_TYPE.IS_MAJOR.toString());
    	model.addAttribute("others", AssociationConstants.MODIFY_TYPE.OTHERS.toString());
    	
		return AssociationConstants.NAMESPACE_APPLY+"/modifyApplyEdit";
	}
	
	/**
	 * 
	 * @Title: saveCancelApply
	 * @Description: 负责人 变更申请信息保存
	 * @param model
	 * @param request
	 * @param applyId
	 * @param associationId
	 * @return
	 * @throws
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-edit/saveChangeApply"})
	public String saveChangeApply(ModelMap model,HttpServletRequest request,AssociationApplyModel applyModelVo ,String associationId,String[] financeFileId)
	{
		String status = request.getParameter("status");
		if("20".equals(status)){
			applyModelVo.setOperateStatus(AssociationConstants.OPERATE_STATUS.ADVISOR_SAVE.toString());
		}else{
			applyModelVo.setOperateStatus(AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());
		}
		associationApplyInfoService.saveOrUpdateChange(applyModelVo, financeFileId, status);
		
      	return "redirect:"+AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList.do";
	}
	
	
	/**
	 * 
	 * @Title: copyBaseInfoModelToModifyApplyModel
	 * @Description: 复制基础信息到修改申请实体中
	 * @param baseAssociationModel
	 * @param applyModel
	 * @throws
	 */
	private void copyBaseInfoToApplyModel(AssociationBaseinfoModel baseAssociationModel,AssociationApplyModel applyModel)
	{
		applyModel.setAssociationAim(baseAssociationModel.getAssociationAim());
		applyModel.setAssociationFee(baseAssociationModel.getAssociationFee());
		applyModel.setAssociationPo(baseAssociationModel);
		applyModel.setOrignAssociationName(baseAssociationModel.getAssociationName());
		applyModel.setCollege(baseAssociationModel.getCollege());
		applyModel.setOrignAssociationType(baseAssociationModel.getAssociationType());
		applyModel.setOrignIsMajor(baseAssociationModel.getIsMajor());
		List<AssociationAdvisorModel> advisorList = associationService.getAssociationAdvisors(baseAssociationModel.getId());
		String advisorIds = "";
		String advisorNames = "";
		for(AssociationAdvisorModel advisor : advisorList )
		{
			advisorIds+=advisor.getAdvisorPo().getId()+",";
			advisorNames+=advisor.getAdvisorPo().getName()+",";
		}
		if(advisorNames.lastIndexOf(",")!=-1)
			applyModel.setOrignAdvisorName(advisorNames.substring(0,advisorNames.lastIndexOf(",")));
		else
			applyModel.setOrignAdvisorName(advisorNames);
		applyModel.setOrignAdvisorId(advisorIds);
		applyModel.setApplyDate(new Date());
		applyModel.setOrignManagerId(baseAssociationModel.getProprieter().getId()+",");
		applyModel.setOrignManagerName(baseAssociationModel.getProprieter().getName());
	}
	
	
	/**
	 * 
	 * @Title: asynPackageManagerList
	 * @Description: 社团成员列表查询显示
	 * @param model
	 * @param managerIds
	 * @return
	 * @throws
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/nsm/registerManagerList"})
	public String asynPackageManagerList(ModelMap model,String managerIds)
	{
		List<StudentInfoModel> studentList = new ArrayList<StudentInfoModel>();
		String curUserId = sessionUtil.getCurrentUserId();
		StudentInfoModel student = studentCommonServie.queryStudentById(curUserId);
		if(null!= student)
			studentList.add(student);
		if (!StringUtils.isEmpty(managerIds))
		{
			for (String id : managerIds.split(","))
			{
				if(!id.equals(curUserId))
					studentList.add(studentCommonServie.queryStudentById(id));
			}
		}
		model.addAttribute("curUserId", curUserId);
		model.addAttribute("studentList", studentList);
		model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
		model.addAttribute("associationManager", AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER);
			
		return "/association/memberSelectedList";
	}
	
	/**
	 * 
	 * @Title: deleteApply
	 * @Description: 删除申请信息
	 * @param model
	 * @param request
	 * @param response
	 * @param applyId
	 * @return
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-delete/deleteApply"},produces={"text/plain;charset=UTF-8"})
	public String deleteApply(ModelMap model,HttpServletRequest request,HttpServletResponse response,String applyId){
		try {
			this.associationApplyInfoService.deleteAssociationApplyInfo(applyId);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	
	/**
     * 获取社团信息列表
	 * @param model			页面数据加载器
	 * @param request			页面请求
     * @param amm				社团申请对象
     * @param applyType	申请类型【注册、变更、注销】
	 * @return							指定视图
    */
   @RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/nsm/getAssociationRadioList"})
   public String getAssociationRadioList(ModelMap model,HttpServletRequest request,AssociationMemberModel amm,String applyType)
   {
	   int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
	   Page page = this.associationService.pageQueryAssociationByMember(amm,pageNo,AssociationConstants.DEFALT_PAGE_SIZE);
	   // 下拉列表 学院
	   List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
	   model.addAttribute("amm", amm);
	   model.addAttribute("page", page);
	   model.addAttribute("page", page);
	   model.addAttribute("collegeList", collegeList);
	   model.addAttribute("applyType", applyType);
	   model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
	   model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
	   return AssociationConstants.NAMESPACE_MAINTAIN+"/queryAssocitionRadio4CurAM";
   }
   /**
    * 获取社团信息列表
	* @param model					页面数据加载器
	* @param request				页面请求
    * @param associationId	社团主键
    * @param applyType		申请类型【注册、变更、注销】
    */
   @ResponseBody
   @RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/isModifyApplyFinish"},produces={"text/plain;charset=UTF-8"})
   public String isModifyApplyFinish(ModelMap model,HttpServletRequest request,String associationId,String applyType)
	{
		try
		{
			List<AssociationApplyModel> aamList = this.associationService.getApprovingApply(associationId, applyType);
			if (DataUtil.isNotNull(aamList) && aamList.size() > 0)
			{
				return "{\"flag\":\"no\"}";
			} else{
				return "{\"flag\":\"yes\"}";
			}
		} catch (Exception e){
			e.printStackTrace();
			return "{\"flag\":\"error\"}";
		}
   }
	
	/**
	 * 
	 * @Title: advisorCommentView
	 * @Description: 指导人简介信息查看
	 * @param model
	 * @param request
	 * @param applyId
	 * @param associationId
	 * @param advisorId
	 * @return
	 * @throws
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/nsm/viewAdvisor"})
	public String advisorCommentView(ModelMap model,HttpServletRequest request,String id,String applyId,String teacherId)
	{
		AssociationAdvisorModel advisor = null;
		if(!StringUtils.isEmpty(id))
			advisor = associationService.getAssociationAdvisor(id);
		else if(!StringUtils.isEmpty(applyId) && !StringUtils.isEmpty(teacherId))
		{
			advisor = associationApplyInfoService.getAssociationAdvisor(applyId, teacherId);
			if(null == advisor || StringUtils.isEmpty(advisor.getId()))
			{
				advisor = new AssociationAdvisorModel();
				BaseTeacherModel advisorPo = baseDataService.findTeacherById(teacherId);
				advisor.setAdvisorPo(advisorPo);
			}
		}
		model.addAttribute("advisor", advisor);
		return AssociationConstants.NAMESPACE_APPLY+"/advisorCommentView";
	}
	
	
	/**
	 * 
	 * @Title: advisorCommentView
	 * @Description: 社团成员列表查看
	 * @param model
	 * @param request
	 * @param applyId
	 * @param associationId
	 * @param advisorId
	 * @return
	 * @throws
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/nsm/viewMember"})
	public String advisorAssociationMember(ModelMap model,HttpServletRequest request,String id)
	{
		List<AssociationMemberModel> memberList = null;
		if(!StringUtils.isEmpty(id))
			memberList = associationService.getAssociationMembers(id);
		model.addAttribute("memberList", memberList);
		return AssociationConstants.NAMESPACE_APPLY+"/memberListView";
	}
	
	
	/**
	 * 
	 * @Title: viewRegisterMember
	 * @Description: 注册信息中成员的列表
	 * @param model
	 * @param request
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/nsm/viewRegisterMember"})
	public String viewRegisterMember(ModelMap model,HttpServletRequest request,String id)
	{
		List<StudentInfoModel> studentList = new ArrayList<StudentInfoModel>();
		String managerId = "";
		if(!StringUtils.isEmpty(id))
		{
			AssociationApplyModel apply = associationService.getAssociationCurApply(id);
			managerId = apply.getOrignManagerId();
			if(!StringUtils.isEmpty(managerId))
			{
				managerId=managerId.substring(0, managerId.lastIndexOf(","));
				studentList.add(studentCommonServie.queryStudentById(managerId));
			}
			String managerIds = apply.getMemberId();
			if (!StringUtils.isEmpty(managerIds))
			{
				for (String studentId : managerIds.split(","))
				{
					if(!studentId.equals(managerId))
						studentList.add(studentCommonServie.queryStudentById(studentId));
				}
			}
		}
		model.addAttribute("curUserId", managerId);
		model.addAttribute("studentList", studentList);
		return "/association/memberSelectedList";
	}
	
	/**
	 * 
	 * @Title: advisorEditApply
	 * @Description: 指导老师编辑方法
	 * @param model
	 * @param request
	 * @param id
	 * @return
	 * @throws
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-edit/advisorEditApply"})
	public String advisorEditApply(ModelMap model,HttpServletRequest request,String id)
	{
		if(!StringUtils.isEmpty(id))
		{
			AssociationApplyModel  applyModel = associationApplyInfoService.getApplyModelById(id);
			model.addAttribute("allScope", dicUtil.getDicInfo("ASSOCIATION_SCOPE","COLLEGE"));
			//指导老师根据不同的类型进入不同的页面
			if(applyModel.getApplyTypeDic().getId().equals(AssociationConstants.registerDic.getId()))
			{
				this.setViewRegisterValue(model, request, applyModel);
				String curUserId = sessionUtil.getCurrentUserId();
				AssociationAdvisorModel associationAdvisorModel =  associationApplyInfoService.getAssociationAdvisor(id,curUserId);
				model.addAttribute("advisor", associationAdvisorModel);
				return AssociationConstants.NAMESPACE_APPLY+"/registerApplyAdvisor";
			}else if(applyModel.getApplyTypeDic().getId().equals(AssociationConstants.changeDic.getId()))
			{
				this.setViewChangeValue(model, request, applyModel);
				String curUserId = sessionUtil.getCurrentUserId();
				AssociationAdvisorModel associationAdvisorModel =  associationApplyInfoService.getAssociationAdvisor(id,curUserId);
				model.addAttribute("advisor", associationAdvisorModel);
				return AssociationConstants.NAMESPACE_APPLY+"/modifyApplyAdvisor";
			}else if(applyModel.getApplyTypeDic().getId().equals(AssociationConstants.cancelDic.getId()))
			{
				this.setViewCancelValue(model, request, applyModel);
				String curUserId = sessionUtil.getCurrentUserId();
				AssociationAdvisorModel associationAdvisorModel =  associationApplyInfoService.getAssociationAdvisor(id,curUserId);
				model.addAttribute("advisor", associationAdvisorModel);
				return AssociationConstants.NAMESPACE_APPLY+"/cancelApplyAdvisor";
			}
		}
		return null;
	}
	
	/**
	 * 
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-save/advisorSaveApplyInfo"})
	public String advisorSaveApply(ModelMap model,HttpServletRequest request)
	{   
		String id = request.getParameter("id");
		//简历
		String comments = request.getParameter("comments");
		if(!StringUtils.isEmpty(id))
		{
			AssociationAdvisorModel associationAdvisorPo = associationApplyInfoService.getAssociationAdvisorById(id);
			associationAdvisorPo.setComments(comments);
			this.associationApplyInfoService.updateAdvisor(associationAdvisorPo);
	    }
		return "redirect:"+AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList.do";
	}
	
	/**
	 * 
	 * @Title: AssociationApplyInfoController.java 
	 * @Package com.uws.association.controller 
	 * @Description: 判断是2个指导老师是否已提交了简历
	 * @author LiuChen 
	 * @date 2016-1-26 下午5:46:05
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/checkSubmitApplyInfo"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String checkSubmitApplyInfo(String advisorId,String applyId,String comments){
		//当前登录人
		String flag = "success";
		String curUserId = sessionUtil.getCurrentUserId();
		String advisorIdStr = "";
		AssociationApplyModel  applyModel = associationApplyInfoService.getApplyModelById(applyId);
		if(AssociationConstants.registerDic.getId().equals(applyModel.getApplyTypeDic().getId()))
		{
			advisorIdStr = applyModel.getOrignAdvisorId();
		}
		else if(AssociationConstants.changeDic.getId().equals(applyModel.getApplyTypeDic().getId()))
		{
			advisorIdStr = applyModel.getChangedAdvisorId();
		}else if(AssociationConstants.cancelDic.getId().equals(applyModel.getApplyTypeDic().getId()))
		{
			advisorIdStr = applyModel.getOrignAdvisorId();
		}
		String[] advisorIdArr = advisorIdStr.split(",");
		AssociationAdvisorModel associationAdvisorModel = null;
		if(advisorIdArr !=null && advisorIdArr.length>0)
		{
			for(String aId :advisorIdArr)
			{
				if(!aId.equals(curUserId))
				{
					associationAdvisorModel =  associationApplyInfoService.getAssociationAdvisor(applyId,aId);
					if(null==associationAdvisorModel)
					{
						flag = "fail";
						break;
					}else{
						String status = associationAdvisorModel.getStatus();
						if("SAVE".equals(status))
						{
							flag = "fail";
							break;
						}
					}
				}
			}
		}
		return flag;
	}

	
	/**
	 * 提交社团申请【指导老师】
	 * @param model							页面数据加载器
	 * @param request							页面请求
	 * @param aam								社团申请实体
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-save/advisorSubmitApplyInfo"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String submitAssociationApply(ModelMap model,HttpServletRequest request){
		String advisorId = request.getParameter("id");
		//简历
		String result= "success";
		String comments = request.getParameter("comments");
		if(!StringUtils.isEmpty(advisorId))
		{
			AssociationAdvisorModel associationAdvisorPo = associationApplyInfoService.getAssociationAdvisorById(advisorId);
			//个人简历
			associationAdvisorPo.setComments(comments);
			//提交操作
			associationAdvisorPo.setStatus(AssociationConstants.STATUS_SUBMIT_STRING);
			this.associationApplyInfoService.updateAdvisor(associationAdvisorPo);
	    }
		return result;
	}
	
	
	/**
	 * 发起流程后，回写业务中的流程信息【流程状态、下一节点办理人】
	 * @param model							页面数据加载器
	 * @param request							页面请求
	 * @param objectId						业务主键
	 * @param nextApproverId		下一节点办理人
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-init/initCurrentProcess"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String initCurrentProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId){
		ApproveResult result = new ApproveResult();
		result.setResultFlag("success");
		if(ProjectConstants.IS_APPROVE_ENABLE){
			try {
				User initiator = new User(this.sessionUtil.getCurrentUserId());
				User nextApprover = new User(nextApproverId);
				result = this.flowInstanceService.initProcessInstance(objectId,"ASSOCIATION_APPLY_APPROVE", 
						initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
				this.saveProcessInfo(objectId,nextApprover);
			} catch (Exception e) {
				result.setResultFlag("error");
			}
		}else{
			result.setResultFlag("deprecated");
	    }
		JSONObject json=JsonUtils.getJsonObject(result);
		return JsonUtils.jsonObject2Json(json);
	}
	
	
	/**
	 * 回写提交后返回的流程信息
	 * @param objectId					业务主键
	 * @param nextApproverId	下一节点办理人
	 */
	private void saveProcessInfo(String objectId,User nextApprover) {
		AssociationApplyModel newAam = this.associationService.getAssociationApplyInfo(objectId);
		if(DataUtil.isNotNull(newAam)){
			newAam.setApplyStatus(AssociationConstants.OPERATE_STATUS.ADVISOR_SUBMIT.toString());
			newAam.setNextapprover(nextApprover);
			newAam.setInitiator(new User(this.sessionUtil.getCurrentUserId()));
			newAam.setApproveresult("审核中");
			newAam.setProcessstatus("APPROVING");
			this.associationService.modifyAssociationApplyInfo(newAam);
		}
	}
	
	
	/**
	 * 获得社团审核列表
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param aam					社团申请对象
	 * @return								指定页面
	 */
	@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-query/getAssociationApproveList")
	public String getAssociationApproveList(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		aam.setApplyStatus(AssociationConstants.OPERATE_STATUS.ADVISOR_SUBMIT.toString());
		Page page = this.associationService.pageQueryAssociationApplyInfo(aam,pageNo,Page.DEFAULT_PAGE_SIZE);
		// 下拉列表 学院
		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
		model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
	    model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
		model.addAttribute("page", page);
		model.addAttribute("aam", aam);
		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
		return AssociationConstants.NAMESPACE_APPROVE+"/associationApproveList";
	}
	
	
		
		/**
		 * 社团注册审核页面
		 * @param model				页面数据加载器
		 * @param request				页面请求
		 * @param aam					社团申请对象
		 * @param applyType		申请类型【注册、变更、注销】
		 * @return								指定视图
		 */
		@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-edit/editAssociationApprove")
		public String editAssociationApprove(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam,String applyType,String id){
			AssociationApplyModel  applyModel = associationApplyInfoService.getApplyModelById(id);
			//不同的申请查看不同的页面
			if(applyModel.getApplyTypeDic().getId().equals(AssociationConstants.registerDic.getId()))
			{
				this.setViewRegisterValue(model, request, applyModel);
				String curUserId = sessionUtil.getCurrentUserId();
				AssociationAdvisorModel associationAdvisorModel =  associationApplyInfoService.getAssociationAdvisor(id,curUserId);
				model.addAttribute("advisor", associationAdvisorModel);
			}
		   String url = this.getApproveEditPage(model,applyType,applyModel);
		   return url;
		}
		
		/**
		 * 获取审批返回页面
		 * @param applyType	申请类型
		 * @return	审批返回页面
		 */
		private String getApproveEditPage(ModelMap model,String applyType,AssociationApplyModel applyModel) {
			   String url="";
			   String register_ = AssociationConstants.APPLY_STATUS.REGISTER.toString();
			   String modify_ = AssociationConstants.APPLY_STATUS.MODIFY.toString();
			   String cancel_ = AssociationConstants.APPLY_STATUS.CANCEL.toString();
			   model.addAttribute("fileList",fileUtil.getFileRefsByObjectId(applyModel.getId()));
			   model.addAttribute("logicYesNo", dicUtil.getDicInfoList("Y&N"));
			   model.addAttribute("associationKind", dicUtil.getDicInfoList("ASSOCIATION_PROPERTY"));
		       model.addAttribute("openScope", dicUtil.getDicInfoList("ASSOCIATION_SCOPE"));
		       model.addAttribute("applyModel", applyModel);
		       model.addAttribute("allScope", dicUtil.getDicInfo("ASSOCIATION_SCOPE","COLLEGE"));
			   if(DataUtil.isNotNull(applyType) && register_.equals(applyType)){
				   model.addAttribute("applyType", applyType);
				   url = AssociationConstants.NAMESPACE_APPROVE+"/associationRegisterApproveEdit";
			   }else if(DataUtil.isNotNull(applyType) && modify_.equals(applyType)){
				   List<AssociationAdvisorModel> advisorList = associationService.getAssociationAdvisors(applyModel.getAssociationPo().getId());
				   model.addAttribute("advisorList", advisorList);
				   model.addAttribute("modifyItemMap", AssociationConstants.getApproveModifyItemMap());
				   model.addAttribute("applyType", applyType);
				   url =  AssociationConstants.NAMESPACE_APPROVE+"/associationModifyApproveEdit";
			   }else if(DataUtil.isNotNull(applyType) && cancel_.equals(applyType)){
				   model.addAttribute("applyType", applyType);
				   url =  AssociationConstants.NAMESPACE_APPROVE+"/associationCancelApproveEdit";
			   }
			   
			   return url;
		}
		
		
		/**
		 * 审批社团申请
		 * @param model								页面数据加载器
		 * @param request								页面请求
		 * @param applyType						申请类型【注册、变更、注销】
		 * @param objectId							业务主键
		 * @param nextApproverId			下一节点办理人
		 * @param approveStatus				流程审核状态
		 * @param processStatusCode		审核状态编码
		 * @param approveKey					审核操作【通过，拒绝】
		 * @return												指定视图
		 */
		@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-apporve/approveAssociationApply")
		public String approveAssociationApply(ModelMap model,HttpServletRequest request,
			String objectId,String nextApproverId,String approveStatus,String processStatusCode,String approveKey){
			approveStatus = ChineseUtill.toChinese(approveStatus);
			String applyType = request.getParameter("applyType");
			AssociationApplyModel newAam = this.associationService.getAssociationApplyInfo(objectId);
			boolean isFinalTask = this.flowInstanceService.isFinalTask_(newAam.getId(),this.sessionUtil.getCurrentUserId());
			if(DataUtil.isNotNull(newAam)){
				if(DataUtil.isNotNull(approveKey) && approveKey.equals("REJECT")){
					newAam.setProcessstatus(approveKey);
					newAam.setApproveresult("审核拒绝");
					//newAam.setNextapprover(null);
				}else if(DataUtil.isNotNull(approveKey) && approveKey.equals("PASS")){
					newAam.setNextapprover(new User(nextApproverId));
					if(isFinalTask)
						activiteAssociation(applyType,newAam,approveKey,isFinalTask, request);
				}
				this.associationService.modifyAssociationApplyInfo(newAam);
			}
			return "redirect:"+AssociationConstants.NAMESPACE_APPROVE+"/opt-query/getAssociationApproveList.do";
		}
		
		/**
		 *  获取批量审批社团申请
		 * @param model					页面数据加载器
		 * @param request					页面请求
		 * @param selectedBox			批量审核主键集合
		 * @return									指定视图
		 */
		@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-apporve/getMulAAPage")
		public String getMulAAPage(ModelMap model,HttpServletRequest request,String selectedBox){

			String applyIds = CYLeagueUtil.getCondition(selectedBox);
//			List<AssociationApplyModel> selecteAamList = new ArrayList<AssociationApplyModel>();
			List<AssociationApplyModel> aamList = this.associationService.getAssociationApplyByIds(applyIds);
//			for(AssociationApplyModel param:aamList){
//				//关联获取指导老师
//				String advisors = this.associationService.getAssociationAdvisors(param);
//				param.getAssociationPo().setAdvisors(advisors);
//				selecteAamList.add(param);
//			}
			model.addAttribute("aamList", aamList);
		    model.addAttribute("objectIds", selectedBox);
		    model.addAttribute("changeApply", AssociationConstants.changeDic);
			return AssociationConstants.NAMESPACE_APPROVE+"/mulAssociationApprove";
		}
		
		/**
		 * 批量审批社团申请
		 * @param model					页面数据加载器
		 * @param request					页面请求
		 * @param mulResults			批量审核结果集
		 * @return									指定视图
		 */
		@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-apporve/mulAssociationApprove")
		public String mulAssociationApprove(ModelMap model,HttpServletRequest request,String mulResults){
			
			List<ApproveResult> list  = this.flowInstanceService.getFormatedResult(mulResults,ProjectConstants.IS_APPROVE_ENABLE);
			if(DataUtil.isNotNull(list) && list.size()>0){
				for(ApproveResult result:list){
					String objectId = result.getObjectId();
					String approveKey = result.getApproveKey();
					
					Approver nextApprover = (result.getNextApproverList()!=null && result.getNextApproverList().size()>0)?
																		    result.getNextApproverList().get(0):null;
					String nextApproverId = (DataUtil.isNotNull(nextApprover))?nextApprover.getUserId():"";
			        User nextUser = new User(nextApproverId);
			        String processStatusCode = result.getProcessStatusCode();
			        this.saveMulApproveResult(objectId,approveKey,nextUser,processStatusCode, request);
				}
			}
			return "redirect:"+AssociationConstants.NAMESPACE_APPROVE+"/opt-query/getAssociationApproveList.do";
		}
		
		/**
		 * 保存批量审批结果到业务表
		 * @param objectId						业务主键
		 * @param approveKey				审批操作【PASS、NOT_PASS、REJECT】
		 * @param nextUser						下一节点审核人
		 * @param processStatusCode	 当前节点审批结果
		 */
		private void saveMulApproveResult(String objectId, String approveKey,
			User nextUser, String processStatusCode,HttpServletRequest request) {
			AssociationApplyModel newAam = this.associationService.getAssociationApplyInfo(objectId);
			boolean isFinalTask = this.flowInstanceService.isFinalTask_(newAam.getId(),this.sessionUtil.getCurrentUserId());
			if(DataUtil.isNotNull(newAam)){
				if(DataUtil.isNotNull(approveKey) && approveKey.equals("REJECT")){
					newAam.setApplyStatus(Constants.OPERATE_STATUS.SAVE.toString());
					newAam.setNextapprover(newAam.getInitiator());
					newAam.setProcessstatus(approveKey);
					newAam.setApproveresult("审核拒绝");
					//this.rejectCurApply(newAam,isFinalTask);
				}else if(DataUtil.isNotNull(approveKey) && approveKey.equals("PASS")){
					newAam.setNextapprover(nextUser);
					String applyType = (newAam.getApplyTypeDic()!=null)?newAam.getApplyTypeDic().getCode():"";
					if(isFinalTask)
					   this.activiteAssociation(applyType,newAam,approveKey,isFinalTask, request);
				}
				this.associationService.modifyAssociationApplyInfo(newAam);
			}
		}
		
		
		/**
		 * 社团申请后审批通过，设置社团标志
		 * @param applyType		社团申请类型
		 * @param newAam			社团申请对象
		 * @param approveKey	社团申请审批标识
		 */
	private void activiteAssociation(String applyType,AssociationApplyModel newAam ,String approveKey, boolean isFinalTask, HttpServletRequest request)
	{
		
		String associationId = (DataUtil.isNotNull(newAam.getAssociationPo())) ? newAam.getAssociationPo().getId() : "";
		AssociationBaseinfoModel associationPo = this.associationService.getAssociationInfo(associationId);
		if (isFinalTask){
			if (applyType.equals(AssociationConstants.APPLY_STATUS.REGISTER.toString())){// 社团注册成功
				if(null == associationPo)	
					associationPo = new AssociationBaseinfoModel();
				// 获取社团编码
				String collegeId = newAam.getCollege().getId();
				Dic associationType = newAam.getOrignAssociationType();
				Dic isMajor = newAam.getOrignIsMajor();
				String associationCode = AssociationUtils.generateAssociationCode(collegeId, associationType, isMajor);
				
				//社团基本信息赋值保存
				associationPo.setAssociationCode(associationCode);
				associationPo.setIsValid(this.dicUtil.getDicInfo("Y&N","Y"));
				associationPo.setIsCancel(this.dicUtil.getDicInfo("Y&N","N"));
				associationPo.setMajorIds(newAam.getMajorId());
				associationPo.setMajorNames(newAam.getMajorName());
				associationPo.setAssociationName(newAam.getOrignAssociationName());
				associationPo.setAssociationAim(newAam.getAssociationAim());
				associationPo.setCollege(newAam.getCollege());
				associationPo.setIsMajor(newAam.getOrignIsMajor());
				//判断开发范围是给全校老师还是学生
				if(null !=newAam.getOpenScope() && newAam.getOpenScope().getId().equals(dicUtil.getDicInfo("ASSOCIATION_SCOPE","MAJOR").getId()))
				{
					associationPo.setOpenScope(dicUtil.getDicInfo("ASSOCIATION_SCOPE","MAJOR"));
					associationPo.setMajorIds(newAam.getMajorId());
					associationPo.setMajorNames(newAam.getMajorName());
				}
				associationPo.setOpenScope(dicUtil.getDicInfo("ASSOCIATION_SCOPE","COLLEGE"));
				if(newAam.getAssociationFee() >0)
					associationPo.setIsPay(dicUtil.getDicInfo("Y&N","Y"));
				else
					associationPo.setIsPay(dicUtil.getDicInfo("Y&N","N"));
				associationPo.setOpenScope(newAam.getOpenScope());
				String managerId = newAam.getOrignManagerId();
				if(!StringUtils.isEmpty(managerId))
				{
					managerId = managerId.substring(0, managerId.lastIndexOf(","));
					StudentInfoModel student = new StudentInfoModel();
					student.setId(managerId);
					associationPo.setProprieter(student);
				}
				
				associationPo.setDeleteStatus(dicUtil.getStatusNormal());
				associationPo.setApplyTime(newAam.getApplyDate());
				associationPo.setAssociationFee(newAam.getAssociationFee());
				
				String memberId = newAam.getMemberId();
				if(!memberId.contains(managerId+","))
					memberId = managerId +","+ memberId;
				String[] memberIs = null;
				if(!StringUtils.isEmpty(memberId))
					memberIs = memberId.split(",");
				
				int memberNum = null == memberIs ? 0 : memberIs.length;
				associationPo.setMemberNums(memberNum);
				
				associationPo.setAssociationType(newAam.getOrignAssociationType());
				associationApplyInfoService.saveBaseinfoModel(associationPo);
				
				//社团成员信息保存
				if(!ArrayUtils.isEmpty(memberIs))
				{
					StudentInfoModel student = null;
					AssociationMemberModel member = null;
					for(String id:memberIs)
					{
						member = new AssociationMemberModel();
						student = new StudentInfoModel();
						student.setId(id);
						member.setAssociationPo(associationPo);
						member.setMemberPo(student);
						member.setJoinTime(new Date());
						member.setMemberStatus(dicUtil.getDicInfo("APPLY_APPROVE", "PASS"));
						member.setDeleteStatus(dicUtil.getStatusNormal());
						if(managerId.equals(id))
						{
							member.setIsManager(dicUtil.getDicInfo("Y&N", "Y"));
							member.setLeaguePosition(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER);
							//社长角色保存和判断
							if(!commonRoleService.checkUserIsExist(id, CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString()))
								commonRoleService.saveUserRole(id,  CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
						}
						else
						{
							member.setIsManager(dicUtil.getDicInfo("Y&N", "N"));
							member.setLeaguePosition(AssociationConstants.ASSOCIATION_MEMBER);
						}
						associationService.saveAssociationMember(member);
					}
				}
				
				//指导人信息更新
				List<AssociationAdvisorModel> advisorList = associationApplyInfoService.getAssociationAdvisorByApplyId(newAam.getId());
				for(AssociationAdvisorModel advisor:advisorList)
				{
					advisor.setAssociationPo(associationPo);
					associationService.updateAdvisor(advisor);
				}
				//社团申请信息赋值
				newAam.setProcessstatus(approveKey);
				newAam.setApproveresult("审核通过");
				newAam.setAssociationPo(associationPo);
			} else if (applyType.equals(AssociationConstants.APPLY_STATUS.MODIFY.toString())){// 社团变更成功
				//获取变更项
				String modifyItem = newAam.getModifyItem();
				if(modifyItem.contains(AssociationConstants.MODIFY_TYPE.ASSOCIATION_NAME.toString()))//变更社团名字
				{  
					associationPo.setAssociationName(newAam.getChangedAssociationName());
				}
				if(modifyItem.contains(AssociationConstants.MODIFY_TYPE.IS_MAJOR.toString()))//社团性质
				{
					associationPo.setIsMajor(newAam.getChangedIsMajor());
				}
				if(modifyItem.contains(AssociationConstants.MODIFY_TYPE.ASSOCIATION_TYPE.toString()))//社团类型
				{
					associationPo.setAssociationType(newAam.getChangedAssociationType());
				}
				if(modifyItem.contains(AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR.toString()))//指导老师
				{
					//1、更新原来的指导人状态为删除
					associationApplyInfoService.updateAdvisorStatusByAssociationId(associationId,dicUtil.getStatusDeleted());
					//2、将变更后的指导人信息保存
					String changedAdvisorId = newAam.getChangedAdvisorId();
					String[] advisorIds = changedAdvisorId.split(",");
					associationApplyInfoService.updateAdvisorByApplyId(advisorIds,newAam.getId(),associationPo.getId());
				}
				if(modifyItem.contains(AssociationConstants.MODIFY_TYPE.ASSOCIATION_MANAGER.toString()))//社团负责人
				{
					String managerId = newAam.getChangedManagerId();
					AssociationMemberModel proprieter = associationService.getAssociationProprieter(associationId);
					if(!StringUtils.isEmpty(managerId))
					{
						managerId = managerId.substring(0, managerId.lastIndexOf(","));
						StudentInfoModel student = new StudentInfoModel();
						student.setId(managerId);
						AssociationMemberModel chengeProprieter = associationService.getAssociationMember_(associationId, managerId);
						if(StringUtils.isEmpty(chengeProprieter.getId()))//不存在
						{
							chengeProprieter.setMemberPo(student);
							chengeProprieter.setAssociationPo(associationPo);
							chengeProprieter.setLeaguePosition(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER);
							chengeProprieter.setIsManager(dicUtil.getDicInfo("Y&N", "Y"));
							associationService.saveAssociationMember(chengeProprieter);
							associationPo.setMemberNums(associationPo.getMemberNums()+1);
						}else{//存在
							chengeProprieter.setLeaguePosition(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER);
							chengeProprieter.setIsManager(dicUtil.getDicInfo("Y&N", "Y"));
							associationService.updateAssociationMember(chengeProprieter);
						}
						proprieter.setLeaguePosition(AssociationConstants.ASSOCIATION_MEMBER);
						proprieter.setIsManager(dicUtil.getDicInfo("Y&N", "N"));
						associationService.updateAssociationMember(proprieter);
						
						associationPo.setProprieter(student);
						
						//角色控制 社长角色保存和判断
						if(!commonRoleService.checkUserIsExist(managerId, CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString()))
							commonRoleService.saveUserRole(managerId, CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
					}
				}
				this.associationService.updateAssociationInfo(associationPo);
				//社团申请信息赋值
				newAam.setProcessstatus(approveKey);
				newAam.setApproveresult("审核通过");
				newAam.setAssociationPo(associationPo);
			} else if (applyType.equals(AssociationConstants.APPLY_STATUS.CANCEL.toString())){// 社团注销成功
				//社团申请信息赋值
				newAam.setProcessstatus(approveKey);
				newAam.setApproveresult("审核通过");
				associationPo.setIsValid(this.dicUtil.getDicInfo("Y&N", "N"));
				associationPo.setIsCancel(this.dicUtil.getDicInfo("Y&N", "Y"));
				this.associationService.updateAssociationInfo(associationPo);
			}
		}
	}
	
	
}
