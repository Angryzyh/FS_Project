package com.angryzyh.mall.product.service.impl;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;
import com.angryzyh.mall.product.dao.CategoryDao;
import com.angryzyh.mall.product.entity.CategoryEntity;
import com.angryzyh.mall.product.service.CategoryService;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    /**
     * @return 数据库查询全部数据, 在通过stream流筛选, 并封装好的三级菜单栏
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(null);
        return entities.stream()
                //筛选1级菜单栏
                .filter(x -> x.getParentCid() == 0)
                //把筛选出来的1级菜单栏&全部数据,传入到getChildMenus方法中,该方法可以获取1级菜单栏的所有子菜单栏
                .peek((x) -> x.setChildren(getChildMenus(x, entities)))
                .sorted((x1, x2) -> {
                    return (x1.getSort() == null ? 0 : x1.getSort()) - (x2.getSort() == null ? 0 : x2.getSort());
                })
                .collect(Collectors.toList());
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildMenus(CategoryEntity root, List<CategoryEntity> all) {
        //筛选全部数据
        return all.stream()
                //根据全部数据的parent_cid列的数据通过equals找到父级菜单栏,结果筛选出2级菜单栏
                .filter(x -> x.getParentCid().equals(root.getCatId()))
                //映射查询后的结果为2级菜单栏 ,递归调用
                .peek(x -> x.setChildren(getChildMenus(x, all)))
                //根据字段sort排序
                .sorted((x1, x2) -> {
                    return (x1.getSort() == null ? 0 : x1.getSort()) - (x2.getSort() == null ? 0 : x2.getSort());
                }).collect(Collectors.toList());
    }

    /**
     * @param asList 批量删除的 商品种类id
     */
    @Override
    public void removeMenusByIds(List<Long> asList) {
        // TODO 检查当前删除的菜单,是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * @param catelogId 根据分类的id ,找到他的三级分类路径
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> parentPath = findParentPath(catelogId, new ArrayList<>());
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[0]);
    }

    //根据 catelogId 找到他的父分类id
    public List<Long> findParentPath(Long catelogId,List<Long> temp) {
        CategoryEntity categoryEntity = this.getById(catelogId);
        temp.add(categoryEntity.getCatId());
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(),temp);
        }
        return temp;
    }


}