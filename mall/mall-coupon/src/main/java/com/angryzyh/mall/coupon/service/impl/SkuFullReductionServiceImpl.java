package com.angryzyh.mall.coupon.service.impl;

import com.angryzyh.common.to.MemberPrice;
import com.angryzyh.common.to.SpuReductionTo;
import com.angryzyh.mall.coupon.entity.MemberPriceEntity;
import com.angryzyh.mall.coupon.entity.SkuLadderEntity;
import com.angryzyh.mall.coupon.service.MemberPriceService;
import com.angryzyh.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.coupon.dao.SkuFullReductionDao;
import com.angryzyh.mall.coupon.entity.SkuFullReductionEntity;
import com.angryzyh.mall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    SkuFullReductionService skuFullReductionService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * mall-product模块商品新增远程调用
     *
     * @param spuReductionTo
     *   Long skuId
     *   //打折to
     *   int fullCount;
     *   BigDecimal discount;
     *   int countStatus;
     *   //满减to
     *   BigDecimal fullPrice;
     *   BigDecimal reducePrice;
     *   int priceStatus;
     *   // 会员价to
     *   List<MemberPrice> memberPrice;
     *      -->  Long id;      //会员等级id
     *           String name;  //会员等级名
     *           int price;    //sukId 产品对应的 会员价
     */
    @Override
    public void saveAllCouponFromSpu(SpuReductionTo spuReductionTo) {
        //6.4)、sku的打折优惠表: 远程调用 mall_sms->sms_sku_ladder
        /**
         *  SkuLadderEntity  操作数据po
         *  Long id;
         *  *Long skuId;  sku_id
         *  *Integer fullCount; 满几件
         * 	*BigDecimal discount; 打几折
         * 	BigDecimal price; 折后价
         * 	Integer addOther; 是否叠加其他优惠[0-不可叠加，1-可叠加]
         */
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(spuReductionTo, skuLadderEntity);
        skuLadderEntity.setAddOther(spuReductionTo.getCountStatus());
        if (skuLadderEntity.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }

        //6.5)、sku的满减又会表: 远程调用 mall_sms->sms_sku_full_reduction
        /**
         *  SkuFullReductionEntity 操作数据po
         *  Long id;
         *  Long skuId;  sku_id
         *  BigDecimal fullPrice;  满多少
         *  BigDecimal reducePrice; 减多少
         *  Integer addOther;  是否参与其他优惠
         */
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(spuReductionTo, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(spuReductionTo.getPriceStatus());
        if (skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            skuFullReductionService.save(skuFullReductionEntity);
        }

        //6.6)、sku内会员价格表: 远程调用 mall_sms->sms_member_price
        /**
                *  MemberPriceEntity 操作数据库po
                * 	 Long id;
         * 	 Long skuId;   sku_id
                * 	 Long memberLevelId; 会员等级id
                * 	 String memberLevelName; 会员等级名
                * 	 BigDecimal memberPrice; 会员对应价格
                * 	 Integer addOther; 可否叠加其他优惠[0-不可叠加优惠，1-可叠加]
         */
        List<MemberPrice> memberPrice = spuReductionTo.getMemberPrice();
        List<MemberPriceEntity> memberPriceEntityList = memberPrice.stream()
                .map(price -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(spuReductionTo.getSkuId());
                    memberPriceEntity.setMemberLevelId(price.getId());
                    memberPriceEntity.setMemberLevelName(price.getName());
                    memberPriceEntity.setMemberPrice(price.getPrice());
                    memberPriceEntity.setAddOther(1);
                    return memberPriceEntity;
                }).collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceEntityList);
    }
}