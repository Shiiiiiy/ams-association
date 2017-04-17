package com.uws.association.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uws.association.service.IAssociationService;
import com.uws.association.util.AssociationConstants;
import com.uws.common.service.IBaseDataService;
import com.uws.common.service.ICommonApproveService;
import com.uws.common.service.ICommonRoleService;
import com.uws.common.service.IStudentCommonService;
import com.uws.common.util.AmsDateUtil;
import com.uws.common.util.CYLeagueUtil;
import com.uws.common.util.CYLeagueUtil.APPLY_APPROVE_STATUS;
import com.uws.common.util.Constants;
import com.uws.core.base.BaseController;
import com.uws.core.excel.ExcelException;
import com.uws.core.excel.ImportUtil;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.core.util.DateUtil;
import com.uws.domain.association.AssociationAdvisorModel;
import com.uws.domain.association.AssociationBaseinfoModel;
import com.uws.domain.association.AssociationHonorModel;
import com.uws.domain.association.AssociationMemberModel;
import com.uws.domain.base.BaseAcademyModel;
import com.uws.domain.common.CommonApproveComments;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.sys.model.Dic;
import com.uws.sys.model.UploadFileRef;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.FileUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.sys.service.impl.FileFactory;
import com.uws.sys.util.MultipartFileValidator;
import com.uws.user.service.IUserService;
import com.uws.util.ProjectSessionUtils;

/** 
* AssociationApplyController
* @Description:社团基础业务控制类Controller
* @author liuyang
* @date	   2015-12-02
*/
@Controller
public class AssociationBaseController extends BaseController{
	
	@Autowired
	private IAssociationService   associationService;
	
  	@Autowired
	private IUserService userService;
	
	@Autowired
	private IBaseDataService baseDataService;
	
	@Autowired
	private IStudentCommonService stuService;
	
  	@Autowired
	private ICommonApproveService commonApproveService;
  	
  	@Autowired
  	private ICommonRoleService commonRoleService;
  	

	 // 数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();
	
	//session工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(AssociationConstants.NAMESPACE);
	
	//日志工具
	private Logger logger = new LoggerFactory(AssociationBaseController.class);
  	
	 //附件工具类
  	private FileUtil fileUtil=FileFactory.getFileUtil();
  	
  	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
	
   /**
    * 获取社团信息列表
	 * @param model		页面数据加载器
	 * @param request		页面请求
    * @param abm				社团基础信息实体
	 * @return						指定视图
    */
   @RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/opt-query/getAssociationList"})
   public String getAssociationList(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm){
	   String curUserId = this.sessionUtil.getCurrentUserId();
	   
	   //设置查询条件：默认未注销状态
//	    if(abm.getIsCancel()==null||"".equals(abm.getIsCancel())){
//			abm.setIsCancel(Constants.STATUS_NO);
//		}
	    
		//校社联领导角色判断
		String  role1 = CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_UNION_LEADER.toString();
		boolean  isHHUL = this.commonRoleService.checkUserIsExist(curUserId, role1);
		
		//校团委
		String  role2 = CYLeagueUtil.CYL_ROLES.HKY_SCHOOL_LEAGUE_LEADER.toString();
		boolean isHSLL = this.commonRoleService.checkUserIsExist(curUserId, role2);
		
		//学院团委
		String  role3 = CYLeagueUtil.CYL_ROLES.HKY_COLLEGE_LEAGUE_LEADER.toString();
		boolean isSubLeagueLeaderRole = this.commonRoleService.checkUserIsExist(curUserId, role3);
		
		//负责人角色
		boolean isManagerRole = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
		
		//指导老师角色
		boolean isTeacherRole = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_TEACHER.toString());
	    
	    int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
//		Page page = this.associationService.pageQueryAssociationBaseInfo_(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		
		Page page = new Page();

		if(isHHUL||isSubLeagueLeaderRole||isHSLL){//校社联，院团委，校团委
			
			page = this.associationService.pageQueryAssociationBaseInfo_(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		}else if(isManagerRole){//负责人查看社团
			
			page = this.associationService.pageQueryAssociationBaseInfoByManager(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		}else if(isTeacherRole){//指导老师查看社团
			
			page = this.associationService.pageQueryAssociationBaseInfoByAdvisor(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		}
		
		
		List<AssociationBaseinfoModel> resultList = (List<AssociationBaseinfoModel>)page.getResult();
		List<AssociationBaseinfoModel> newResult = new ArrayList<AssociationBaseinfoModel>();
		for(AssociationBaseinfoModel param:resultList){
			String associationId =param.getId();
			//是否当前社团的负责人
			boolean isCurAssociationManager = 
					this.associationService.getAssociationMemberByUserId(associationId,curUserId);
			param.setIsCurAM(String.valueOf(isCurAssociationManager));
			//当前用户是否社团指导老师
			boolean isCurAssociationAdvisor = 
			this.associationService.isCurAssociationAdvisor(associationId,curUserId);
			param.setIsCurAA(String.valueOf(isCurAssociationAdvisor));
			newResult.add(param);
		}
		page.setResult(newResult);
		
		String curUserOrgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		BaseAcademyModel college=this.baseDataService.findAcademyById(curUserOrgId);
		model.addAttribute("college", college);
		model.addAttribute("isHCLL", isHSLL+"");
		model.addAttribute("isHHUL", isHHUL+"");
		model.addAttribute("abm", abm);
		model.addAttribute("page", page);
		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
		model.addAttribute("isNoList",dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
		return AssociationConstants.NAMESPACE_MAINTAIN+"/associationList";
   }
   
   /**
    * 获取社团信息列表
	 * @param model		页面数据加载器
	 * @param request		页面请求
    * @param abm				社团基础信息实体
	 * @return						指定视图
    */
   @RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/nsm/getAssociationLoadList"})
   public String getAssociationLoadList(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm){
	   String curUserId = this.sessionUtil.getCurrentUserId();
	   
		//校社联领导角色判断
		String  role1 = CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_UNION_LEADER.toString();
		boolean  isHHUL = this.commonRoleService.checkUserIsExist(curUserId, role1);
		
		//校团委
		String  role2 = CYLeagueUtil.CYL_ROLES.HKY_SCHOOL_LEAGUE_LEADER.toString();
		boolean isHSLL = this.commonRoleService.checkUserIsExist(curUserId, role2);
		
		//学院团委
		String  role3 = CYLeagueUtil.CYL_ROLES.HKY_COLLEGE_LEAGUE_LEADER.toString();
		boolean isSubLeagueLeaderRole = this.commonRoleService.checkUserIsExist(curUserId, role3);
		
		//负责人角色
		boolean isManagerRole = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
		
		//指导老师角色
		boolean isTeacherRole = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_TEACHER.toString());
	    
	    int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
//		Page page = this.associationService.pageQueryAssociationBaseInfo_(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		
		Page page = new Page();

		if(isHHUL||isSubLeagueLeaderRole||isHSLL){//校社联，院团委，校团委
			
			page = this.associationService.pageQueryAssociationBaseInfo_(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		}else if(isManagerRole){//负责人查看社团
			
			page = this.associationService.pageQueryAssociationBaseInfoByManager(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		}else if(isTeacherRole){//指导老师查看社团
			
			page = this.associationService.pageQueryAssociationBaseInfoByAdvisor(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		}
		
		
		List<AssociationBaseinfoModel> resultList = (List<AssociationBaseinfoModel>)page.getResult();
		List<AssociationBaseinfoModel> newResult = new ArrayList<AssociationBaseinfoModel>();
		for(AssociationBaseinfoModel param:resultList){
			String associationId =param.getId();
			//是否当前社团的负责人
			boolean isCurAssociationManager = 
					this.associationService.getAssociationMemberByUserId(associationId,curUserId);
			param.setIsCurAM(String.valueOf(isCurAssociationManager));
			//当前用户是否社团指导老师
			boolean isCurAssociationAdvisor = 
			this.associationService.isCurAssociationAdvisor(associationId,curUserId);
			param.setIsCurAA(String.valueOf(isCurAssociationAdvisor));
			newResult.add(param);
		}
		page.setResult(newResult);
		
		String curUserOrgId = ProjectSessionUtils.getCurrentTeacherOrgId(request);
		BaseAcademyModel college=this.baseDataService.findAcademyById(curUserOrgId);
		model.addAttribute("college", college);
		model.addAttribute("isHCLL", isHSLL+"");
		model.addAttribute("isHHUL", isHHUL+"");
		model.addAttribute("abm", abm);
		model.addAttribute("page", page);
		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
		model.addAttribute("isNoList",dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
	   return AssociationConstants.NAMESPACE_MAINTAIN+"/associationLoadList";
   }
   
   /**
    * 编辑社团信息
	 * @param model				页面数据加载器
	 * @param request				页面请求
    * @param associationId	社团主键
    * @return								指定视图
    */
   @RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/editAssociationInfo"})
   public String editAssociationInfo(ModelMap model,HttpServletRequest request,String associationId){
	   
	   AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
		//获取社团指导老师
		Page teacherPage = this.associationService.pageQueryAssociationAdvisor(associationId, 1, AssociationConstants.DEFALT_PAGE_SIZE);
		//获取社团负责人
		Page stuPage = this.associationService.pageQueryAssociationMember(abm,1, AssociationConstants.DEFALT_PAGE_SIZE);
		//获取社团指导老师列表
		List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
		//是否当前社团的负责人
		boolean isCurAssociationManager = 
				this.associationService.getAssociationMemberByUserId(associationId,this.sessionUtil.getCurrentUserId());
		
		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
		model.addAttribute("stuPage", stuPage);
	    model.addAttribute("hasStuData", this.isStuHasData(stuPage));
		model.addAttribute("teacherPage", teacherPage);
		model.addAttribute("aamList", aamList);
		model.addAttribute("aam_", new AssociationAdvisorModel());
		model.addAttribute("isCurManager",String.valueOf(isCurAssociationManager));
		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
	   model.addAttribute("abm",abm);
	   
	   return AssociationConstants.NAMESPACE_MAINTAIN+"/associationEdit";
   }
   
	/**
	 * 学生集合中是否有数据
	 * @param stuPage		分页信息
	 */
	private String isStuHasData(Page stuPage) {
		return String.valueOf(stuPage.getResult().size()>0);
	}
   
   /**
    * 保存社团信息
	 * @param model				页面数据加载器
	 * @param request				页面请求
    */
   @RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/saveAssociationInfo"})
   public String saveAssociationInfo(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm,String associationId){
	   AssociationBaseinfoModel newAbm = this.associationService.getAssociationInfo(associationId);
	   newAbm.setIsTopten(abm.getIsTopten());
	   newAbm.setHonorRating(abm.getHonorRating());
	   this.associationService.updateAssociationInfo(newAbm);
	   return "redirect:"+AssociationConstants.NAMESPACE_MAINTAIN+"/opt-query/getAssociationList.do";
   }
   
   /**
    * 查看社团信息
	 * @param model				页面数据加载器
	 * @param request				页面请求
    * @param associationId	社团主键
    * @return								指定视图
    */
   @RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/viewAssociationInfo"})
   public String viewAssociationInfo(ModelMap model,HttpServletRequest request,String associationId){
	   
	   AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
	   List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
	   model.addAttribute("advisorList", aamList);
	   model.addAttribute("abm",abm);
	   model.addAttribute("logicYesNo", dicUtil.getDicInfoList("Y&N"));
	   model.addAttribute("associationKind", dicUtil.getDicInfoList("ASSOCIATION_PROPERTY"));
	   model.addAttribute("openScope", dicUtil.getDicInfoList("ASSOCIATION_SCOPE"));
	   model.addAttribute("majorScope", dicUtil.getDicInfo("ASSOCIATION_SCOPE","MAJOR"));
	   return AssociationConstants.NAMESPACE_MAINTAIN+"/associationView";
   }
   
   /**
    * 获取可报名的社团列表
	 * @param model				页面数据加载器
	 * @param request				页面请求
    * @param abm						社团基础信息实体
    * @return								指定视图
    */
   @RequestMapping({AssociationConstants.NAMESPACE_REPORT+"/opt-query/getAssociationReportList"})
   public String getAssociationApplyList(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.associationService.pageQueryReportAssociationInfo(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		List<AssociationBaseinfoModel> newResultList = new ArrayList<AssociationBaseinfoModel>();
		List<AssociationBaseinfoModel> resultList = (List<AssociationBaseinfoModel>)page.getResult();
		StudentInfoModel stu = this.stuService.queryStudentById(this.sessionUtil.getCurrentUserId());
		if(stu != null) {
			for(AssociationBaseinfoModel param:resultList){
				if(param.getOpenScope() != null && param.getOpenScope().getCode().equals("COLLEGE") || 
						(param.getOpenScope().getCode().equals("MAJOR") && 
								param.getMajorIds().indexOf(stu.getMajor().getId()) > -1)) {    //社团面向全校学生或者与在选择的开放专业里
					boolean isCurAssociationMember = this.associationService.isCurAssociationMember(param.getId(),this.sessionUtil.getCurrentUserId());
					boolean isTempMember = this.associationService.isAssociationTemMember(param.getId(),this.sessionUtil.getCurrentUserId());
					param.setIsCurAssociationMember(String.valueOf(isCurAssociationMember).toLowerCase());
					param.setIsTempMember(String.valueOf(isTempMember).toLowerCase());
					AssociationMemberModel associationMember =associationService.getAssociationMember_(param.getId(),this.sessionUtil.getCurrentUserId());
					param.setMemberStatus(associationMember!=null?associationMember.getMemberStatus():null);
					param.setAssociationMemberModel(associationMember);
					newResultList.add(param);
				}
			}
		}
		page.setResult(newResultList);
		
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
		model.addAttribute("abm", abm);
		model.addAttribute("page", page);
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
	   return AssociationConstants.NAMESPACE_MAINTAIN+"/associationReportList";
   }
   
   /**
    * 获取可报名的社团列表【异步加载】
	 * @param model				页面数据加载器
	 * @param request				页面请求
    * @param abm						社团基础信息实体
    * @return								指定视图
    */
   @RequestMapping({AssociationConstants.NAMESPACE_REPORT+"/nsm/getAssociationReportLoadList"})
   public String getAssociationReportLoadList(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm){
	   int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
	   Page page = this.associationService.pageQueryReportAssociationInfo(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
	   List<AssociationBaseinfoModel> newResultList = new ArrayList<AssociationBaseinfoModel>();
	   List<AssociationBaseinfoModel> resultList = (List<AssociationBaseinfoModel>)page.getResult();
	   for(AssociationBaseinfoModel param:resultList){
		   boolean isCurAssociationMember = this.associationService.isCurAssociationMember(param.getId(),this.sessionUtil.getCurrentUserId());
		   boolean isTempMember = this.associationService.isAssociationTemMember(param.getId(),this.sessionUtil.getCurrentUserId());
		   param.setIsCurAssociationMember(String.valueOf(isCurAssociationMember).toLowerCase());
		   param.setIsTempMember(String.valueOf(isTempMember).toLowerCase());
		   AssociationMemberModel associationMember =associationService.getAssociationMember_(param.getId(),this.sessionUtil.getCurrentUserId());
		   param.setMemberStatus(associationMember!=null?associationMember.getMemberStatus():null);
		   newResultList.add(param);
	   }
	   page.setResult(newResultList);
	   
	   // 下拉列表 学院
	   List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
	   model.addAttribute("abm", abm);
	   model.addAttribute("page", page);
	   model.addAttribute("collegeList", collegeList);
	   model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
	   return AssociationConstants.NAMESPACE_MAINTAIN+"/associationReportLoadList";
   }
   
   /**
    *  社团报名
	 * @param model				页面数据加载器
	 * @param request				页面请求
    * @param associationId	社团主键
    */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-report/exeAssociationReport"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
   public String exeAssociationReport(ModelMap model,HttpServletRequest request,String associationId){
		try {
				this.associationService.createAssociationMember(associationId,this.sessionUtil.getCurrentUserId());
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			return "{\"flag\":\"error\"}";
		}
   }

	/**
	 * 社团星级评价
	 * @param model				页面数据加载器
	 * @param request				页面请求
     * @param abm					社团基础信息实体
	 * @param honorRating	星级评分
     * @return								指定视图
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/associationStarApprove"})
	public String associationStarApprove(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm,String honorRating){
		AssociationBaseinfoModel newAbm = this.associationService.getAssociationInfo(abm.getId()); 
		newAbm.setHonorRating(honorRating);
		return null;
	}
	
	/**
	 * 设置十佳社团
	 * @param model				页面数据加载器
	 * @param request				页面请求
     * @param abm					社团基础信息实体
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/setTopTenAssociation"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String setTopTenAssociation(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm){
		AssociationBaseinfoModel newAbm = this.associationService.getAssociationInfo(abm.getId()); 
		newAbm.setIsTopten(this.dicUtil.getDicInfo("Y&N", "Y"));
		this.associationService.updateAssociationInfo(newAbm);
		return "{\"flag\":\"success\"}";
	}
	
	/**
	 * 批量社团星级评价
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param ids						批量处理业务主键集合
	 * @param starGrade			星级评分
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/associationStarApproveMul"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String associationStarApproveMul(ModelMap model,HttpServletRequest request,String ids,String starGrade){
		String idArray [] = ids.split("_");
		for(String associationId:idArray){
			AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
			abm.setHonorRating(starGrade);
			this.associationService.updateAssociationInfo(abm);
		}
		return "{\"flag\":\"success\"}";
	}
	
	/**
	 * 批量设置十佳社团
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param ids						批量处理业务主键集合
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/setTopTenAssociationMul"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String setTopTenAssociationMul(ModelMap model,HttpServletRequest request,String ids){
		String idArray [] = ids.split("_");
		int counter = 0;
		for(String associationId:idArray){
			if(!this.associationService.isTopTen(associationId)){
				AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
				abm.setIsTopten(this.dicUtil.getDicInfo("Y&N", "Y"));
				this.associationService.updateAssociationInfo(abm);
				counter++;
			}
		}
		
		if(counter>0){
			return "{\"flag\":\"success\"}";
		}else{
			return "{\"flag\":\"nodata\"}";
		}
	}
	
	/**
	 * 废弃社团
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param abm					社团基础信息
     * @return								指定视图
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/opt-query/deprecatedAssociationInfo"})
	public String deprecatedAssociationInfo(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm){
		
		
		return null;
	}
	
	/**
	 * 社团成员列表
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param amm					社团成员实体
     * @return								指定视图
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/opt-query/getAssociationMemberList"})
	public String getAssociationMemberList(ModelMap model,HttpServletRequest request,AssociationMemberModel amm){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.associationService.pageQueryAssociationMember_(amm, pageNo, Page.DEFAULT_PAGE_SIZE);
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("approveStatusList", Constants.approveStatusList);
		model.addAttribute("genderList", Constants.genderList);
		model.addAttribute("applyApproveStatusList", Constants.applyApproveStatusList);
		model.addAttribute("amm", amm);
		model.addAttribute("page", page);
	   return AssociationConstants.NAMESPACE_MAINTAIN+"/associationMemberList";
	}
	
	/**
	 * 社团成员列表【异步加载】
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param amm					社团成员实体
     * @return								指定视图
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/nsm/asynLoadMemberList"})
	public String asynLoadMemberList(ModelMap model,HttpServletRequest request,AssociationMemberModel amm){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.associationService.pageQueryAssociationMember_(amm, pageNo, Page.DEFAULT_PAGE_SIZE);
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("approveStatusList", Constants.approveStatusList);
		model.addAttribute("genderList", Constants.genderList);
		model.addAttribute("applyApproveStatusList", Constants.applyApproveStatusList);
		model.addAttribute("amm", amm);
		model.addAttribute("page", page);
		return AssociationConstants.NAMESPACE_MAINTAIN+"/associationMemberLoadList";
	}
	
	/**
	 *  添加社团成员
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param associationId	社团主键
	 * @param memberIds		选定的成员id集合
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-add/addAssociationMember"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String addAssociationMember(ModelMap model,HttpServletRequest request,String associationId,String memberIds){
		String idArray [] = (DataUtil.isNotNull(memberIds))?memberIds.split(","):new String[]{};
		int counter = 0;
		for(String memberId:idArray){
			AssociationMemberModel associationMember = this.associationService.findAssociationMember(associationId, memberId);
			if(!this.associationService.isMemberExist(associationId,memberId)){
				AssociationMemberModel amm = this.formateMemberInfo(associationId, memberId);
				this.associationService.saveAssociationMember(amm);
				counter++;
			}else if(associationMember!=null && associationMember.getMemberStatus()!=null && !associationMember.getMemberStatus().getCode().equals("PASS")){
				//已存在的社团成员状态不为审核通过的（审核中或审核拒绝）
				associationMember.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
				associationMember.setJoinTime(new Date());
				this.associationService.updateAssociationMember(associationMember);
				counter++;
			}
		}
		this.associationService.synAssociationMemberNums(associationId, counter, CYLeagueUtil.OPERATOR_FLAG.PLUS.toString());
		
		if(idArray.length>0 && counter==0){
			return "{\"flag\":\"repeatdata\"}";
		}else if(idArray.length==0){
			return "{\"flag\":\"nodata\"}";
		}else{
			return "{\"flag\":\"success\"}";
		}
	}
	
	/**
	 * 删除社团成员
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param associationId	社团主键
	 * @param memberId		成员id
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-delete/deleteAssociationMember"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String deleteAssociationMember(ModelMap model,HttpServletRequest request,String associationId,String memberId){
		try {
			this.associationService.deleteAssociationMember(associationId,memberId);
			this.associationService.synAssociationMemberNums(associationId, 1, CYLeagueUtil.OPERATOR_FLAG.MINUS.toString());
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}
	
	/**
	 * 批量删除社团成员
	 * @param model				页面数据加载器
	 * @param request				页面请求
	 * @param associationId	社团主键
	 * @param memberIds		成员id集合
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-delete/deleteMemberMul"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String deleteMemberMul(ModelMap model,HttpServletRequest request,String associationId,String memberIds){
		try {
			String idArray [] = (DataUtil.isNotNull(memberIds))?memberIds.split(","):new String[]{};
			int counter = 0;
			for(String memberId:idArray){
				if(this.associationService.isMemberExist(associationId,memberId)){
					this.associationService.deleteAssociationMember(associationId,memberId);
					counter++;
				}
			}
			this.associationService.synAssociationMemberNums(associationId, counter, CYLeagueUtil.OPERATOR_FLAG.MINUS.toString());
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}
	
	/**
	 * 同意社团报名
	 * @param model						页面数据加载器
	 * @param request						页面请求
	 * @param associationId			社团主键
	 * @param memberIds				选定的社团成员集合
	 * @param approveStatus		审批状态
	 * @param approveSuggest	【审批意见】
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/doPass"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String doPass(ModelMap model,HttpServletRequest request,String associationId,
			String memberIds,String approveStatus,String approveSuggest){
		try {
			AssociationMemberModel amm = this.associationService.getAssociationMember_(associationId, memberIds);
			if(DataUtil.isNotNull(amm)){
				if(APPLY_APPROVE_STATUS.PASS.toString().equalsIgnoreCase(approveStatus)){
					amm.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
					this.associationService.synAssociationMemberNums(associationId, 1, CYLeagueUtil.OPERATOR_FLAG.PLUS.toString());
				}else{
					if(amm.getMemberStatus().getCode().equals("PASS")){
						this.associationService.synAssociationMemberNums(associationId, 1, CYLeagueUtil.OPERATOR_FLAG.MINUS.toString());
					}
					amm.setMemberStatus(CYLeagueUtil.APPROVE_REJECT);
					
				}
				this.associationService.updateAssociationMember(amm);
				this.saveMemberApproveHistory(amm, this.sessionUtil.getCurrentUserId(), approveSuggest);
			}
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}

	/**
	 *  强制注销社团【校社联】
	 * @param model						页面数据加载器
	 * @param request						页面请求
	 * @param associationId			社团主键
	 */
	@ResponseBody
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/associationCancel"},produces={"text/plain;charset=UTF-8"})
	public String associationCancel(ModelMap model,HttpServletRequest request,String associationId){
		try {
				AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
				if(DataUtil.isNotNull(abm)){
//					abm.setIsCancel(this.dicUtil.getDicInfo("Y&N", "Y"));
					abm.setIsForceCancel(this.dicUtil.getDicInfo("Y&N", "Y"));
					this.associationService.updateAssociationInfo(abm);
				}
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}
	
	/**
	 * 批量强制注销社团【校社联】
	 * @param model
	 * @param request
	 * @param associationIds
	 * @return
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/associationCancelMul"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String associationCancelMul(ModelMap model,HttpServletRequest request,String associationIds){
		try {
			String idArray [] = (DataUtil.isNotNull(associationIds))?associationIds.split(","):new String[]{};
			for(String associationId:idArray){
				AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
				if(DataUtil.isNotNull(abm)){
					//abm.setIsCancel(this.dicUtil.getDicInfo("Y&N", "Y"));
					abm.setIsForceCancel(this.dicUtil.getDicInfo("Y&N", "Y"));
					this.associationService.updateAssociationInfo(abm);
				}
			}
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}
	
	/**
	 * 社团强制注销确认【团委】
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/associationCancelConfirm"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String associationCancelConfirm(ModelMap model,HttpServletRequest request,String associationId){
		try {
				AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
				if(DataUtil.isNotNull(abm)){
					abm.setIsValid(this.dicUtil.getDicInfo("Y&N", "N"));
					abm.setIsCancel(this.dicUtil.getDicInfo("Y&N", "Y"));
					this.associationService.updateAssociationInfo(abm);
				}
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}
	
	/**
	 * 社团强制注销拒绝撤销【团委】
	 */
	@ResponseBody
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/associationCancelRefuse"},produces={"text/plain;charset=UTF-8"})
	public String associationCancelRefuse(ModelMap model,HttpServletRequest request,String associationId)
	{
		if(!StringUtils.isEmpty(associationId))
		{
			AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
			if(DataUtil.isNotNull(abm))
			{
				abm.setIsForceCancel(null);
				this.associationService.updateAssociationInfo(abm);
			}
		}
		return "success";
	}
	
	/**
	 * 批量社团强制注销确认【团委】
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/associationCancelConfirmMul"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String associationCancelConfirmMul(ModelMap model,HttpServletRequest request,String associationIds){
		try {
			String idArray [] = (DataUtil.isNotNull(associationIds))?associationIds.split(","):new String[]{};
			for(String associationId:idArray){
				AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
				if(DataUtil.isNotNull(abm)){
					abm.setIsValid(this.dicUtil.getDicInfo("Y&N", "N"));
					abm.setIsCancel(this.dicUtil.getDicInfo("Y&N", "Y"));
					this.associationService.updateAssociationInfo(abm);
				}
			}
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}
		
	/**
	 * 批量审批社员报名
	 * @param model						页面数据加载器
	 * @param request						页面请求
	 * @param associationId			社团主键
	 * @param memberIds				选定的社团成员集合
	 * @param approveStatus		审批状态
	 * @param approveSuggest	【审批意见】
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/doPassMul"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String doPassMul(ModelMap model,HttpServletRequest request,String associationId,
			String memberIds,String approveStatus,String approveSuggest){
		try {
			String idArray [] = (DataUtil.isNotNull(memberIds))?memberIds.split(","):new String[]{};
			int counter=0;
			for(String memberId:idArray){
				AssociationMemberModel amm = this.associationService.getAssociationMember_(associationId, memberId);
				if(DataUtil.isNotNull(amm)){
					if(APPLY_APPROVE_STATUS.PASS.toString().equalsIgnoreCase(approveStatus)){
						if(amm.getMemberStatus().getCode().equals("PASS")){
							counter--;
						}
						amm.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
					}else{
						if(amm.getMemberStatus().getCode().equals("REJECT")){
							counter--;
						}
						amm.setMemberStatus(CYLeagueUtil.APPROVE_REJECT);
					}
					this.associationService.updateAssociationMember(amm);
					counter++;
				}
				this.saveMemberApproveHistory(amm, this.sessionUtil.getCurrentUserId(), approveSuggest);
			}

			this.associationService.synAssociationMemberNums(associationId, counter, CYLeagueUtil.OPERATOR_FLAG.PLUS.toString());
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}
	
	/**
	 * 保存社员审批历史信息
	 * @param amm						 业务对象
	 * @param userId					 审批人
	 * @param approveSuggest 审批意见
	 */
	public void saveMemberApproveHistory(AssociationMemberModel amm,
			String userId,String approveSuggest){
		// 封装审核信息
		CommonApproveComments ac = new CommonApproveComments();
		// 审核结果
		if(DataUtil.isNotNull(amm.getMemberStatus())){
			ac.setApproveOpinion(amm.getMemberStatus().getName());
		}
		// 审核人
		ac.setApprover(this.userService.getUserById(userId));
		// 审核时间
		ac.setApproveTime(new Date());
		// 审核意见
		ac.setApproveComments(approveSuggest);
		// 业务主键
		ac.setObjectId(amm.getId());
		commonApproveService.saveApproveComments(ac);
	}
	
	/**
	 * 封装社团负责人初始值
	 * @param associationId	社团主键
	 * @param managerId		负责人id
	 * @return
	 */
	private AssociationMemberModel formateMemberInfo(String associationId,String memberId) {
		AssociationMemberModel amm = new AssociationMemberModel();
		AssociationBaseinfoModel associationPo = this.associationService.getAssociationInfo(associationId);
		if(DataUtil.isNotNull(associationId)){
			associationPo.setId(associationId);
		}
		//社团对象
		amm.setAssociationPo(associationPo);
		//学生对象
		amm.setMemberPo(this.stuService.queryStudentById(memberId));
		//是否负责人
		amm.setIsManager(this.dicUtil.getDicInfo("Y&N", "N"));
		//报名状态
		amm.setRegisterForm(this.dicUtil.getDicInfo("HKY_ACTIVITY_REGISTRA_FORM", "ACTIVITY_OFFLINE"));
		//新增社团成员默认【审核通过】
		amm.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
		//加入社团时间
		amm.setJoinTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
		//创建时间
		amm.setCreateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
		//修改时间
		amm.setUpdateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
		//删除状态
		amm.setDeleteStatus(this.dicUtil.getStatusNormal());
		return amm;
	}
	
	/**
	 * 社员服务
	 * @param model			页面数据加载器
	 * @param request			页面请求
	 * @param abm				社团基础实体
	 * @return							指定视图
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_SERVICE+"/opt-query/getMemberServiceList"})
	public String getMemberServiceList(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm){
		//设置查询条件：默认未注销状态
		if(abm.getIsCancel()==null||"".equals(abm.getIsCancel())){
			abm.setIsCancel(Constants.STATUS_NO);
		}
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.associationService.pageQueryAssociationBaseInfo_(abm,pageNo,Page.DEFAULT_PAGE_SIZE);
		String curUserId = this.sessionUtil.getCurrentUserId();
		List<AssociationBaseinfoModel> resultList = (List<AssociationBaseinfoModel>)page.getResult();
		List<AssociationBaseinfoModel> newResult = new ArrayList<AssociationBaseinfoModel>();
		for(AssociationBaseinfoModel param:resultList){
			String associationId =param.getId();
			//是否当前社团的负责人
			boolean isCurAssociationManager = 
					this.associationService.getAssociationMemberByUserId(associationId,curUserId);
			param.setIsCurAM(String.valueOf(isCurAssociationManager));
			
			//当前用户是否社团成员
			boolean isCurAssociationMember = 
			this.associationService.isCurAssociationMember(associationId, curUserId);
			param.setIsCurAssociationMember(String.valueOf(isCurAssociationMember));

			//当前用户是否社团指导老师
			boolean isCurAssociationAdvisor = 
			this.associationService.isCurAssociationAdvisor(associationId,this.sessionUtil.getCurrentUserId());
			param.setIsCurAA(String.valueOf(isCurAssociationAdvisor));
			if(isCurAssociationManager||isCurAssociationMember || isCurAssociationAdvisor){
				newResult.add(param);
			}
		}
		page.setResult(newResult);
		page.setTotalCount(newResult.size());
		
		model.addAttribute("abm", abm);
		model.addAttribute("page", page);
		model.addAttribute("isNoList",dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
		model.addAttribute("isTeacher", ProjectSessionUtils.checkIsTeacher(request));
		return AssociationConstants.NAMESPACE_SERVICE+"/associationInfoList";
	}

	
	/**
	 * 
	 * @Title: viewMemberHonorList
	 * @Description: 社员查看荣誉列表
	 * @param model
	 * @param request
	 * @param abm
	 * @return
	 * @throws
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_SERVICE+"/nsm/viewMemberHonor"})
	public String viewMemberHonor(ModelMap model,HttpServletRequest request,String id){
		AssociationMemberModel member = associationService.getAssociationMember_(id, sessionUtil.getCurrentUserId());
		if(null!=member && !StringUtils.isEmpty(member.getId()))
		{
			List<AssociationHonorModel> honorList = associationService.getMemberHonorList(id,member.getId());
			if(!CollectionUtils.isEmpty(honorList)){
				for(AssociationHonorModel honor : honorList)
					honor.setUploadFileList(fileUtil.getFileRefsByObjectId(honor.getId()));
			}
			model.addAttribute("honorList", honorList);
		}
		return AssociationConstants.NAMESPACE_SERVICE+"/serviceList";
	}
	/**
	 * 社团成员荣誉列表
	 * @param model					页面数据加载器
	 * @param request					页面请求
	 * @param amPo						社团成员实体
	 * @param associationId		社团主键
	 * @return									指定视图
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_SERVICE+"/opt-query/getMemberHonorList"})
	public String getMemberHonorList(ModelMap model,HttpServletRequest request,AssociationMemberModel amPo,AssociationHonorModel honor,String associationId){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		String curUserId = this.sessionUtil.getCurrentUserId();
		amPo = this.formateAssociationMember(associationId,amPo,curUserId);
		Page page = this.associationService.pageQueryAssociationHonor(amPo,honor,pageNo, Page.DEFAULT_PAGE_SIZE);
		List<AssociationHonorModel> resultList = (List<AssociationHonorModel>)page.getResult();
		List<AssociationHonorModel> newResult = new ArrayList<AssociationHonorModel>();
		
		//当前用户是否社团成员
		boolean isCurAssociationMember = 
		this.associationService.isCurAssociationMember(associationId, curUserId);

		//当前用户是否社团指导老师
		boolean isCurAssociationAdvisor = 
		this.associationService.isCurAssociationAdvisor(associationId,this.sessionUtil.getCurrentUserId());
		
		for(AssociationHonorModel ahm:resultList){
			String operateStatusCode = (ahm.getOperateStatus()!=null)?
										ahm.getOperateStatus().getCode():"";
			String submitCode = Constants.OPERATE_STATUS_SUBMIT.getCode();
			if(isCurAssociationMember || 
			  (isCurAssociationAdvisor && submitCode.equals(operateStatusCode))){
				newResult.add(ahm);
			}
		}
		page.setResult(newResult);
		page.setTotalCount(newResult.size());
		
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("page", page);
		model.addAttribute("honor", honor);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("honorTypeList",dicUtil.getDicInfoList("HONOR_TYPE"));
		model.addAttribute("operateStatusList",dicUtil.getDicInfoList("OPERATE_STATUS"));
		model.addAttribute("approveResultList",dicUtil.getDicInfoList("APPLY_APPROVE"));
		model.addAttribute("associationId", associationId);
		model.addAttribute("applyApproveStatusList", Constants.applyApproveStatusList);
		model.addAttribute("isCurAA", isCurAssociationAdvisor+"");
		return AssociationConstants.NAMESPACE_SERVICE+"/associationHonorList";
	}
	
	/**
	 * 
	 * @Title: viewMemberHonorList
	 * @Description:  教师查看学生社团荣誉
	 * @param model
	 * @param request
	 * @param amPo
	 * @param honor
	 * @param associationId
	 * @return
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping({AssociationConstants.NAMESPACE_SERVICE+"/opt-query/viewMemberHonorList"})
	public String viewMemberHonorList(ModelMap model,HttpServletRequest request,AssociationHonorModel honor,String associationId)
	{
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.associationService.pageQueryAssociationApprovedHonor(associationId,honor,pageNo, Page.DEFAULT_PAGE_SIZE);
		
        List<AssociationHonorModel> honorList =  (List<AssociationHonorModel>) page.getResult();
		if(!CollectionUtils.isEmpty(honorList))
		{
	        for(AssociationHonorModel honorPo : honorList)
				honorPo.setUploadFileList(fileUtil.getFileRefsByObjectId(honorPo.getId()));
		}
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("page", page);
		model.addAttribute("honor", honor);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("honorTypeList",dicUtil.getDicInfoList("HONOR_TYPE"));
		return AssociationConstants.NAMESPACE_SERVICE+"/associationHonorViewList";
	}
	
	/**
	 * 社团成员荣誉列表异步
	 * @param model					页面数据加载器
	 * @param request					页面请求
	 * @param amPo						社团成员对象
	 * @param associationId		社团主键
	 * @return									指定视图
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_SERVICE+"/nsm/loadMemberHonorList"})
	public String loadMemberHonorList(ModelMap model,HttpServletRequest request,AssociationMemberModel amPo,String associationId){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		String curUserId = this.sessionUtil.getCurrentUserId();
		amPo = this.formateAssociationMember(associationId,amPo,curUserId);
		Page page = this.associationService.pageQueryAssociationHonor(amPo,null, pageNo, Page.DEFAULT_PAGE_SIZE);
		List<AssociationHonorModel> resultList = (List<AssociationHonorModel>)page.getResult();
		List<AssociationHonorModel> newResult = new ArrayList<AssociationHonorModel>();
		
		//当前用户是否社团成员
		boolean isCurAssociationMember = 
				this.associationService.isCurAssociationMember(associationId, curUserId);
		
		//当前用户是否社团指导老师
		boolean isCurAssociationAdvisor = 
				this.associationService.isCurAssociationAdvisor(associationId,this.sessionUtil.getCurrentUserId());
		
		for(AssociationHonorModel ahm:resultList){
			String operateStatusCode = (ahm.getOperateStatus()!=null)?
										ahm.getOperateStatus().getCode():"";
			String submitCode = Constants.OPERATE_STATUS_SUBMIT.getCode();
			if(isCurAssociationMember || 
			  (isCurAssociationAdvisor && submitCode.equals(operateStatusCode))){
				newResult.add(ahm);
			}
		}
		page.setResult(newResult);
		page.setTotalCount(newResult.size());
		
		// 下拉列表 学院
		List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
		model.addAttribute("collegeList", collegeList);
		model.addAttribute("page", page);
		model.addAttribute("associationId", associationId);
		model.addAttribute("applyApproveStatusList", Constants.applyApproveStatusList);
		model.addAttribute("isCurAA", isCurAssociationAdvisor+"");
		return AssociationConstants.NAMESPACE_SERVICE+"/associationHonorLoadList";
	}
	
	/**
	 * 社团成员荣誉编辑页面
	 * @param model					页面数据加载器
	 * @param request					页面请求
	 * @return									指定视图
	 */
	@RequestMapping({"/association/service/opt-add/editMemberHonor","/association/service/opt-update/editMemberHonor"})
	public String editMemberHonor(ModelMap model,HttpServletRequest request){
		String id=request.getParameter("id");
		String associationId=request.getParameter("associationId");
		String userId = sessionUtil.getCurrentUserId();
		AssociationMemberModel associationMember =this.associationService.findAssociationMember(associationId, userId);
		
		if(id!=null){
			AssociationHonorModel honor=this.associationService.getAssociationHonorById(id);
			model.addAttribute("honor", honor);
			List<UploadFileRef> fileList=this.fileUtil.getFileRefsByObjectId(honor.getId());
			model.addAttribute("fileList", fileList);
		}

		model.addAttribute("associationId", associationId);
		model.addAttribute("associationMember", associationMember);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("honorTypeList",dicUtil.getDicInfoList("HONOR_TYPE"));
		
		return AssociationConstants.NAMESPACE_SERVICE+"/associationHonorEdit";
	}
	
	/**
	 * 保存荣誉
	 * @param model		页面数据加载器
	 * @param request		页面请求
	 * @param honor		社团荣誉
	 * @param fileId			荣誉附件集合
	 * @return						指定视图
	 */
	@RequestMapping(value={"/association/service/opt-save/saveHonor"})
  	public String saveHonor(ModelMap model,HttpServletRequest request,AssociationHonorModel honor,String[] fileId){
		String associationId=honor.getAssociationPo().getId();
		String memberId=honor.getMember().getId();
		String flags=request.getParameter("flags");
		if(Constants.OPERATE_STATUS.SUBMIT.toString().equals(flags)){
			
			honor.setOperateStatus(Constants.OPERATE_STATUS_SUBMIT);
			honor.setApproveResult(CYLeagueUtil.APPROVE_NOT_APPROVE);
		}else if(Constants.OPERATE_STATUS.SAVE.toString().equals(flags)){
			
			honor.setOperateStatus(Constants.OPERATE_STATUS_SAVE);
		}
		if(honor.getId()!=null && !"".equals(honor.getId())){
			this.associationService.updateHonor(honor,fileId);
		}else{
			this.associationService.saveHonor(honor,fileId);
		}
  		return "redirect:/association/service/opt-query/getMemberHonorList.do?memberId="+memberId+"&associationId="+associationId;
  	}
	
	/**
	 * 删除荣誉
	 * @param id			业务主键
	 */
	@ResponseBody
	@RequestMapping(value={"association/service/opt-del/delAssociationHonor"}, produces={"text/plain;charset=UTF-8"})
	public String delAssociationHonor(String id){
		AssociationHonorModel honor=this.associationService.getAssociationHonorById(id);
		this.associationService.delAssociationHonor(honor);
		return "success";
	}
	/**
  	 * 社员荣誉查看
  	 * @param model 页面数据加载器
  	 * @param request 页面请求
  	 * @return
  	 */
  	@RequestMapping(value={"/association/service/opt-view/honorView"})
	public String honorView(ModelMap model, HttpServletRequest request){
  		String id=request.getParameter("id");
  		String associationId=request.getParameter("associationId");
  		AssociationHonorModel honor=this.associationService.getAssociationHonorById(id);
		//志愿者荣誉附件
		List<UploadFileRef> fileList=this.fileUtil.getFileRefsByObjectId(honor.getId());
		model.addAttribute("fileList", fileList);
		model.addAttribute("honor", honor);
		model.addAttribute("associationId", associationId);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("honorTypeList",dicUtil.getDicInfoList("HONOR_TYPE"));
  		return AssociationConstants.NAMESPACE_SERVICE+"/associationHonorView";
  	}
  	/**
  	 * 社员荣誉审核编辑页面
  	 * @param model 页面数据加载器
  	 * @param request 页面请求
  	 * @return
  	 */
  	@RequestMapping(value={"/association/service/opt-approve/honorApproveEdit"})
	public String honorApproveEdit(ModelMap model, HttpServletRequest request){
  		String id=request.getParameter("id");
  		String associationId=request.getParameter("associationId");
  		AssociationHonorModel honor=this.associationService.getAssociationHonorById(id);
		//志愿者荣誉附件
		List<UploadFileRef> fileList=this.fileUtil.getFileRefsByObjectId(honor.getId());
		model.addAttribute("fileList", fileList);
		model.addAttribute("honor", honor);
		model.addAttribute("associationId", associationId);
		model.addAttribute("yearList",dicUtil.getDicInfoList("YEAR"));
		model.addAttribute("termList",dicUtil.getDicInfoList("TERM"));
		model.addAttribute("honorTypeList",dicUtil.getDicInfoList("HONOR_TYPE"));
  		return AssociationConstants.NAMESPACE_SERVICE+"/associationHonorApproveEdit";
  	}
	/**
	 * 封装社团成员服务查询列表
	 * @param associationId		社团主键
	 * @param amPo						社团成员对象
	 * @param curUserId			当前用户id
	 */
	private AssociationMemberModel formateAssociationMember(
			String associationId, AssociationMemberModel amPo,String curUserId) {
		AssociationBaseinfoModel associationPo = new AssociationBaseinfoModel();
		associationPo.setId(associationId);
		amPo.setAssociationPo(associationPo);
		boolean isACM = this.associationService.isAssociationConfirmMember(associationId,curUserId);
		if(isACM){
			StudentInfoModel stuPo = new StudentInfoModel();
			stuPo.setId(curUserId);
			amPo.setMemberPo(stuPo);
		}
		return amPo;
	}

	
	/**
	 * 社员荣誉审核保存
	 * @param model
	 * @param request
	 * @param response
	 * @param associationId
	 * @param id
	 * @param approveKey
	 * @param suggest
	 * @return
	 */
	@RequestMapping(value={AssociationConstants.NAMESPACE_SERVICE + "/opt-save/memberHonorApprove"} , produces={"text/plain;charset=UTF-8"})
	public String memberHonorApprove(ModelMap model,HttpServletRequest request,HttpServletResponse response, 
				   String associationId, String id, String approveKey,String suggest){
		
			AssociationHonorModel  ahm = this.associationService.getAssociationHonorById(id);
		    String memberId=ahm.getMember().getId();
			Dic approveStatus_ = this.dicUtil.getDicInfo("APPLY_APPROVE", approveKey);
			String approveStatusId = DataUtil.isNotNull(approveStatus_)?approveStatus_.getId():"";
			//this.associationService.updateAssociationMemberHonor(associationId, id, approveStatusId);
			ahm.setApproveResult(approveStatus_);
			this.associationService.updateObject(ahm);
			this.saveHonorApproveHistory(ahm,this.sessionUtil.getCurrentUserId(),suggest);
			
	  		return "redirect:/association/service/opt-query/getMemberHonorList.do?memberId="+memberId+"&associationId="+associationId;
		
    }
	
	/**
	 * 社员荣誉批量审核
	 * @param model					页面数据加载器
	 * @param request					页面请求
	 * @param response				请求响应
	 * @param associationId		社团主键
	 * @param ids							荣誉主键集合
	 * @param approveStatus	审核状态
	 * @param approveSuggest审核意见
	 */
	@ResponseBody
	@RequestMapping(value={AssociationConstants.NAMESPACE_SERVICE + "/opt-edit/memberHonorApproveMul"} , produces={"text/plain;charset=UTF-8"})
	public String memberHonorApproveMul(ModelMap model,HttpServletRequest request,HttpServletResponse response, 
			       String associationId, String ids, String approveStatus,String approveSuggest){
		try {
			String honorArray[] = (ids!=null)?ids.split(","):new String[]{};
			for(String honorId:honorArray){
				if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(honorId)){
					AssociationHonorModel  ahm = this.associationService.getAssociationHonorById(honorId);
					Dic approveStatus_ = this.dicUtil.getDicInfo("APPLY_APPROVE", approveStatus);
					String approveStatusId = DataUtil.isNotNull(approveStatus_)?approveStatus_.getId():"";
					this.associationService.updateAssociationMemberHonor(associationId, ids, approveStatusId);
					this.saveHonorApproveHistory(ahm,this.sessionUtil.getCurrentUserId(),approveSuggest);
				}
			}
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"flag\":\"error\"}";
		}
	}
	
	/**
	 * 社员荣誉审批历史信息
	 * @param amm						 业务对象
	 * @param userId					 审批人
	 * @param approveSuggest 审批意见
	 */
	public void saveHonorApproveHistory(AssociationHonorModel  ahm,
			String userId,String approveSuggest){
		// 封装审核信息
		CommonApproveComments ac = new CommonApproveComments();
		// 审核结果
		if(DataUtil.isNotNull(ahm.getApproveResult())){
			ac.setApproveOpinion(ahm.getApproveResult().getName());
		}
		// 审核人
		ac.setApprover(this.userService.getUserById(userId));
		// 审核时间
		ac.setApproveTime(new Date());
		// 审核意见
		ac.setApproveComments(approveSuggest);
		// 业务主键
		ac.setObjectId(ahm.getId());
		commonApproveService.saveApproveComments(ac);
	}
	
	/**
	 * 监管分析
	 * @param model			页面数据加载器
	 * @param request			页面请求
	 * @param abm				社团基本信息实体
	 * @return							指定视图
	 */
	@RequestMapping({AssociationConstants.NAMESPACE_STATISTIC+"/opt-query/associationSummaryInfo"})
	public String associationSummaryInfo(ModelMap model,HttpServletRequest request,AssociationBaseinfoModel abm){
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = this.associationService.pageQueryAssociationInfo(abm, pageNo, Page.DEFAULT_PAGE_SIZE);
		List<AssociationBaseinfoModel> newResult = new ArrayList<AssociationBaseinfoModel>();
		List<AssociationBaseinfoModel> resultList = (List<AssociationBaseinfoModel>)page.getResult();
		for(AssociationBaseinfoModel param:resultList){
			String associationId =param.getId();
			//关联获取指导老师
			String advisors = this.associationService.getAssociationAdvisorName(associationId);
			param.setAdvisors(advisors);
			//关联获取社团人数
			List<AssociationMemberModel> memberList = this.associationService.getAssociationMembers(associationId);
			param.setMemberNums(memberList.size());
			//是否当前社团的负责人
			boolean isCurAssociationManager = 
					this.associationService.getAssociationMemberByUserId(associationId,this.sessionUtil.getCurrentUserId());
			param.setIsCurAM(String.valueOf(isCurAssociationManager));
			//当前用户是否社团指导老师
			boolean isCurAssociationAdvisor = 
			this.associationService.isCurAssociationAdvisor(associationId,this.sessionUtil.getCurrentUserId());
			param.setIsCurAA(String.valueOf(isCurAssociationAdvisor));
			newResult.add(param);
		}
		page.setResult(newResult);
		// 下拉列表 学院
		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
		model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
	    model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
		model.addAttribute("page", page);
		model.addAttribute("isLogic", dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("abm", abm);
		return AssociationConstants.NAMESPACE_STATISTIC+"/associationSummaryList";
	}
	
	/**
	 * 导入页面
	 * @param model			页面数据加载器
	 * @param request			页面请求
	 * @return							指定视图
	 */
	@RequestMapping(value={"/association/maintain/opt-query/toImportPage.do"})
  	public String toImportPage(HttpServletRequest request,ModelMap model){
		String associationPoId=request.getParameter("associationPoId");
		model.addAttribute("associationPoId", associationPoId);
  		return AssociationConstants.NAMESPACE_MAINTAIN+"/importAssociationMember";
  	}
	
	/**
	 * 导入社员（保存）
	 * @param model			页面数据加载器
	 * @param request			页面请求
	 * @param session			当前会话	
	 * @param file					上传附件
	 * @param maxSize		上传附件最大值
	 * @param allowedExt	校验规则
	 * @param ampo				社团成员实体
	 * @return							指定视图
	 */
	@RequestMapping({"/association/maintain/opt-query/importAssociationMember"})
  	 public String importAssociationMember(ModelMap model,HttpServletRequest request, HttpSession session, @RequestParam("file") MultipartFile file, String maxSize, String allowedExt, AssociationMemberModel ampo){	
		
		String associationPoId=request.getParameter("associationPoId");
		model.addAttribute("associationPoId", associationPoId);
		List errorText = new ArrayList();
		String errorTemp = "";
		try {
		//构建文件验证对象
    	MultipartFileValidator validator = new MultipartFileValidator();
    	if(DataUtil.isNotNull(allowedExt)){
    		validator.setAllowedExtStr(allowedExt.toLowerCase());
    	}
    	//设置文件大小
    	if(DataUtil.isNotNull(maxSize)){
    		validator.setMaxSize(Long.valueOf(maxSize));//20M
    	}else{
    		validator.setMaxSize(1024*1024*20);//20M
    	}
		//调用验证框架自动验证数据
        String returnValue=validator.validate(file);
        if(!returnValue.equals("")){
			errorTemp = returnValue;       	
			errorText.add(errorTemp);
        	model.addAttribute("errorText",errorText.size()==0);
			model.addAttribute("importFlag", Boolean.valueOf(true));
			return AssociationConstants.NAMESPACE_MAINTAIN+"/importAssociationMember";
        }
        String tempFileId=fileUtil.saveSingleFile(true, file); 
        File tempFile=fileUtil.getTempRealFile(tempFileId);
		String filePath = tempFile.getAbsolutePath();
        session.setAttribute("filePath", filePath);
        String message="";
        	ImportUtil iu = new ImportUtil();
			// 将Excel数据映射成对象List
			List<AssociationMemberModel> list = iu.getDataList(tempFile.getAbsolutePath(), "importAssociationMember", null,AssociationMemberModel.class);
			List arrayList = this.associationService.compareData(list, associationPoId);//Excel与已有的重复的数据
			if((arrayList == null) || (arrayList.size() == 0)) {
				//1.无重复数据
				message=this.associationService.importAssociationMember(list,null, associationPoId);
				if (message != null && !"".equals(message)) {
					errorText.add(message);
				}
			}else{
				//有重复数据：显示页面
		    	session.setAttribute("arrayList", arrayList);
				List subList = null;
				if(arrayList.size() >= Page.DEFAULT_PAGE_SIZE) {
					subList = arrayList.subList(0, Page.DEFAULT_PAGE_SIZE);
				}else{
					subList = arrayList;
				}
				Page page = new Page();
				page.setPageSize(Page.DEFAULT_PAGE_SIZE);
				page.setResult(subList);
				page.setStart(0L);
				page.setTotalCount(arrayList.size());
				model.addAttribute("page", page);
		    }
		} catch (OfficeXmlFileException e) {
			e.printStackTrace();
			errorTemp = "OfficeXmlFileException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (ExcelException e) { 
        	e.printStackTrace();
			errorTemp = e.getMessage();
			errorText.add(errorTemp);
		} catch (InstantiationException e) {
			e.printStackTrace();
			errorTemp = "InstantiationException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (IOException e) {
			e.printStackTrace();
			errorTemp = "IOException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			errorTemp = "IllegalAccessException" + e.getMessage();
			errorText.add(errorTemp);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			errorText.add("模板不正确或者模板内数据异常，请检查后再导入。");
		} finally {
			model.addAttribute("importFlag", Boolean.valueOf(true));
			model.addAttribute("errorText", errorText.size()==0? null : errorText);
			return AssociationConstants.NAMESPACE_MAINTAIN+"/importAssociationMember";
		}
    }
	
	/** 
	 * 比对导入的数据
	 * @param model			页面数据加载器
	 * @param request			页面请求
	 * @param session			当前会话	
	 * @param pageNo			导入页配置
	 */
	@RequestMapping(value={"/association/maintain/opt-query/association"}, produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String member(ModelMap model, HttpServletRequest request, HttpSession session, @RequestParam(value="pageNo", 
		required=true) String pageNo) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List arrayList = (List)session.getAttribute("arrayList");
		List<Object[]> subList = null;
		int pageno = Integer.parseInt(pageNo);
		int length = arrayList.size();
		if(arrayList.size() >= Page.DEFAULT_PAGE_SIZE * pageno) {
			subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), Page.DEFAULT_PAGE_SIZE * pageno);
		}else{
			subList = arrayList.subList(Page.DEFAULT_PAGE_SIZE * (pageno - 1), length);
		}
		JSONArray array = new JSONArray();
	    JSONObject obj = null;
	    JSONObject json = new JSONObject();
	    for(Object[] infoArray : subList) {
	    	AssociationMemberModel m = (AssociationMemberModel) infoArray[0];
	    	AssociationMemberModel xls = (AssociationMemberModel) infoArray[1];
	    	obj.put("stuName", m.getMemberPo().getName());
	    	obj.put("stuNumber", m.getMemberPo().getStuNumber());
	    	obj.put("genderName", m.getMemberPo().getGenderDic().getName());

	    	obj.put("xlsStuName", m.getMemberPo().getName());
	    	obj.put("xlsStuNumber", m.getMemberPo().getStuNumber());
	    	obj.put("xlsGenderName", m.getMemberPo().getGenderDic().getName());
	    	array.add(obj);
	    }
	    json.put("result", array);
	    obj = new JSONObject();
	    obj.put("totalPageCount", Integer.valueOf(length % Page.DEFAULT_PAGE_SIZE == 0 ? 
	    		length / Page.DEFAULT_PAGE_SIZE : length / Page.DEFAULT_PAGE_SIZE + 1));
	    obj.put("previousPageNo", Integer.valueOf(pageno - 1));
	    obj.put("nextPageNo", Integer.valueOf(pageno + 1));
	    obj.put("currentPageNo", Integer.valueOf(pageno));
	    obj.put("pageSize", Integer.valueOf(Page.DEFAULT_PAGE_SIZE));
	    obj.put("totalCount", Integer.valueOf(length));
	    json.put("page", obj);
	    return json.toString();
	}
	
	/** 
	 * 导入更新社员）
	 * @param model					页面数据加载器
	 * @param session					当前会话	
	 * @param compareId			业务主键
	 * @param associationId		社团主键
	 * @return
	 */
	@SuppressWarnings("finally")
	@RequestMapping({"/association/maintain/opt-query/importData.do"})
	public String importData(ModelMap model, HttpSession session, @RequestParam("compareId") String compareId,String associationId) {
		List errorText = new ArrayList();
		String filePath = session.getAttribute("filePath").toString();
		List arrayList = (List)session.getAttribute("arrayList");
		String message="";
		try {
				String memberIdArray []=compareId.split(",");
				ImportUtil iu = new ImportUtil();
				List<AssociationMemberModel> infoList = iu.getDataList(filePath, "importAssociationMember", null,AssociationMemberModel.class);//Excel数据

//				if(memberIdArray.length== infoList.size()){
//					errorText.add(0,"没有可导入的数据，请重新导入。");
//			    }else{
			    	//-------有问题
					//2.2 有重复数据：根据选择 更新数据
					message=this.associationService.importAssociationMember(infoList, memberIdArray, associationId);
					if (message != null && !"".equals(message)) {
						errorText.add(0,message);
					}
//			}
		} catch (ExcelException e) {
			errorText.add(0, e.getMessage());
		    errorText = errorText.subList(0, errorText.size() > 20 ? 20 : errorText.size());
		    model.addAttribute("errorText", errorText.size()==0 ? null : errorText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (OfficeXmlFileException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			model.addAttribute("importFlag", Boolean.valueOf(true));
			model.addAttribute("errorText",errorText.size()==0 ? null : errorText);
			model.addAttribute("associationPoId", associationId);
			return AssociationConstants.NAMESPACE_MAINTAIN+"/importAssociationMember";
		}
	}

	/**
	 * 社团人员报名审核
	 * @param model						页面数据加载器
	 * @param request						页面请求
	 * @param associationId			社团id
	 * @param memberIds				审核成员id
	 * @param approveStatus		审核状态
	 * @param approveSuggest	审核意见
	 */
	@ResponseBody
	@RequestMapping(value={AssociationConstants.NAMESPACE_MAINTAIN+"/opt-edit/approveAssociationMember"},produces={"text/plain;charset=UTF-8"})
	public String approveAssociationMember(ModelMap model,HttpServletRequest request,String associationId,
			String memberIds,String approveStatus,String approveSuggest){
		try {
			AssociationMemberModel amm = this.associationService.getAssociationMember_(associationId, memberIds);
			if(DataUtil.isNotNull(amm)){
				if(CYLeagueUtil.APPROVE_PASS.getId().equals(approveStatus)){
					amm.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
					this.associationService.synAssociationMemberNums(associationId, 1, CYLeagueUtil.OPERATOR_FLAG.PLUS.toString());
				}else{
					if(amm.getMemberStatus().getCode().equals("PASS")){
						this.associationService.synAssociationMemberNums(associationId, 1, CYLeagueUtil.OPERATOR_FLAG.MINUS.toString());
					}
					amm.setMemberStatus(CYLeagueUtil.APPROVE_REJECT);
				}
				this.associationService.updateAssociationMember(amm);
				this.saveMemberApproveHistory(amm, this.sessionUtil.getCurrentUserId(), approveSuggest);
			}
			return "{\"flag\":\"success\"}";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "{\"flag\":\"error\"}";
		}
	}
	
}
