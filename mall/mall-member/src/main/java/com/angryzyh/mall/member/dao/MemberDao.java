package com.angryzyh.mall.member.dao;

import com.angryzyh.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:23:58
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
