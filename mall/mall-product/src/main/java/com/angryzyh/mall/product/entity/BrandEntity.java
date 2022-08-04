package com.angryzyh.mall.product.entity;

import com.angryzyh.common.validator.group.AddGroup;
import com.angryzyh.common.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

/**
@Valid	被注释的元素是一个对象，需要检查此对象的所有字段值
@Email	验证字段值是个邮箱
@Pattern	(regex=正则表达式) 被注解的元素必须符合给定的正则表达式

@NotNull	验证字段不为 null
@NotEmpty	验证字段不为空，常用于校验集合元素不为空
@NotBlank	验证字段不为空，常用于验证字符串不是空串

@Negative	校验必须是负数
@Positive	校验必须是正数
@NegativeOrZero	校验必须是负数或 0
@PositiveOrZero	校验必须是正数或 0
@Min(value)	被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@Max(value)	被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@Digits(integer=整数位数,fraction=小数位数) 验证字段整数位数和小数位数上限
@DecimalMax	与 @Max 类似，不同的是它限定值可以带小数，一般用于 double 和 Bigdecimal 类型
@DecimalMin	与 @Min 类似，…
@Range	验证数字类型字段值在最小值和最大值之间
@Size(max,min)	验证字段值的在 min 和 max （包含）指定区间之内，如字符长度、集合大小
@Length	验证字符串值长度在 min 和 max 区间内

@AssertFalse	验证布尔类型值是 false
@AssertTrue	验证布尔类型值是 true

@Future	验证日期类型字段值比当前时间晚
@Past	验证日期类型字段值比当前时间早
@PastOrPresent	验证日期类型字段值比当前时间早或者是当前日期
@FutureOrPresent	验证日期类型字段值比当前时间晚或者是当前日期
**/

	/**
	 * 品牌id
	 */
	@TableId
	@NotNull(message = "修改必须指定品牌id",groups = {UpdateGroup.class})
	@Null(message = "新增不能指定id",groups = {AddGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空", groups ={AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups = AddGroup.class, message = "logo图片不能为空")
	@URL(message = "logo必须是一个合法的url地址", groups ={AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@TableLogic(value ="1",delval = "0")
	@NotNull(groups ={AddGroup.class}, message = "显示状态异常为确认")
	@Range(min = 0,max =1, message="显示状态异常", groups ={AddGroup.class, UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@Pattern(regexp = "^[a-zA-Z]$" ,message = "检索首字母必须是一个字母",groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups ={AddGroup.class})
	@Min(value=0,message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
