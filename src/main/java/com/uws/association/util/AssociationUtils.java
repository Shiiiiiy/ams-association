package com.uws.association.util;

import org.springframework.util.StringUtils;

import com.uws.association.service.IAssociationService;
import com.uws.core.util.SpringBeanLocator;
import com.uws.core.util.SystemPropertiesUtil;
import com.uws.sys.model.Dic;

/**
 * 
* @ClassName: AssociationUtils 
* @Description: 社团相关工具类 
* @author 联合永道
* @date 2016-1-8 上午10:14:09 
*
 */
public class AssociationUtils
{
	/**
	 * 
	 * @Title: generateAssociationCode
	 * @Description: 社团编号自动生成
	 * @param collegeId
	 * @param associationType
	 * @param isMajorRegister
	 * @return
	 * @throws
	 */
	public static String generateAssociationCode(String collegeId,Dic associationType,Dic isMajor)
	{
		StringBuffer code = new StringBuffer();
		if(StringUtils.hasText(collegeId) && null != associationType && StringUtils.hasText(associationType.getCode()))
		{
			//学院的代码
			code.append(getCollegeCode(collegeId));
			//是否专业
			if(isMajor!=null && StringUtils.hasText(isMajor.getId()) && isMajor.getCode().equals("COLLEGE"))
				code.append("00");
			else
				code.append("01");
			//类型编码
			code.append(associationTypeCodeMap(associationType.getCode()));
			//个数获取
			code.append(currentNumber(collegeId));
		}
		return code.toString();
	}
	
	/**
	 * 
	 * @Title: associationTypeCodeMap
	 * @Description: 类型对应的编码
	 * @param dicCode
	 * @return
	 * @throws
	 */
	private static String associationTypeCodeMap(String dicCode)
	{
		String propertyCodes = SystemPropertiesUtil.getSystemConfigProperties().getProperty("association.code.generator.type");
		String[] collegeCodes = propertyCodes.split("#");
		for(String str : collegeCodes)
		{
			if(str.startsWith(dicCode+","))
				return str.split(",")[1];
		}
		return "";
	}
	
	/**
	 * 
	 * @Title: getCollegeCode
	 * @Description: 学院对应的编码
	 * @param collegeId
	 * @return
	 * @throws
	 */
	private static String getCollegeCode(String collegeId)
	{
		String propertyCodes = SystemPropertiesUtil.getSystemConfigProperties().getProperty("association.code.generator.college");
		String[] collegeCodes = propertyCodes.split("#");
		for(String str : collegeCodes)
		{
			if(str.startsWith(collegeId+"-"))
				return str.split("-")[1];
		}
		return "";
	}
	
	/**
	 * 
	 * @Title: currentNumber
	 * @Description: 学院社团个数
	 * @param collegeId
	 * @return
	 * @throws
	 */
	public static String currentNumber(String collegeId)
	{
		IAssociationService associationService = (IAssociationService)SpringBeanLocator.getBean("com.uws.association.service.impl.AssociationServiceImpl");
		if(null!=associationService)
		{
			int totalCount = associationService.getAssociationTotalCountByCollege(collegeId);
			if(totalCount<9)
				return "0"+(totalCount+=1);
			else
				return (totalCount+=1)+"";
		}
		return "";
	}
	
}
