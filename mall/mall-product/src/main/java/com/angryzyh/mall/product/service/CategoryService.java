package com.angryzyh.mall.product.service;

import com.angryzyh.mall.product.controller.CategoryController;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.product.entity.CategoryEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //全查询商品种类,封装返回树形分层结构
    List<CategoryEntity> listWithTree();

    //批量删除-商品种类
    void removeMenusByIds(List<Long> asList);

    // 属性分组 级联选择器 回显三级分类信息
    Long[] findCatelogPath(Long catelogId);

    /**
     * category表自己更新的时候也要   更新其他冗余的表中 分类名
     * @param category 分类对象
     * 业务调用-->{@link CategoryController#update}
     */
    void updateByIdAndAllTable(CategoryEntity category);
}

