package com.angryzyh.mall.ware.service.impl;

import com.angryzyh.common.constant.WareConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.ware.dao.PurchaseDetailDao;
import com.angryzyh.mall.ware.entity.PurchaseDetailEntity;
import com.angryzyh.mall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    /**
     * 查询采购需求单
     * @param params{ key: '华为',//检索关键字
     *                status: 0,//状态
     *                wareId: 1,//仓库id }
     * @return page
     */
    @Override
    public PageUtils queryPageByKey(Map<String, Object> params) {
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String wareId = (String) params.get("wareId");
        LambdaQueryWrapper<PurchaseDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(qw -> qw.eq(PurchaseDetailEntity::getId, key)
            .or().like(PurchaseDetailEntity::getSkuNum, key)
            .or().eq(PurchaseDetailEntity::getSkuId, key));
        }
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(PurchaseDetailEntity::getStatus, status);
        }
        if (StringUtils.isNotBlank(wareId)) {
            queryWrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 根据采购单id 查询采购单关联的采购需求单ids
     *
     * @param id 采购单id
     * @return 采购需求单 List<PurchaseDetailEntity>
     */
    @Override
    public List<PurchaseDetailEntity> selectByPurchaseId(Long id) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<PurchaseDetailEntity>()
                .eq(id != null, PurchaseDetailEntity::getPurchaseId, id));
    }
}