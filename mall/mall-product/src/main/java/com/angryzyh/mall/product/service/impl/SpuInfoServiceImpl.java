package com.angryzyh.mall.product.service.impl;

import com.angryzyh.common.to.SpuBoundsTo;
import com.angryzyh.common.to.SpuReductionTo;
import com.angryzyh.common.utils.R;
import com.angryzyh.mall.product.controller.SpuInfoController;
import com.angryzyh.mall.product.dao.ProductAttrValueDao;
import com.angryzyh.mall.product.dao.SpuImagesDao;
import com.angryzyh.mall.product.dao.SpuInfoDescDao;
import com.angryzyh.mall.product.entity.*;
import com.angryzyh.mall.product.feign.CouponFeignService;
import com.angryzyh.mall.product.service.*;
import com.angryzyh.mall.product.vo.spuvo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    AttrService attrService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    //远程调用
    @Autowired
    CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 保存新增商品信息
     * @param vo 恐怖的json串
     * 业务调用-->{@link SpuInfoController#save}
     */
    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);
        //2、保存Spu的描述海报图片 pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        List<String> decript = vo.getDecript();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);
        //3、保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        List<SpuImagesEntity> spuImagesEntityList = images.stream()
                .map(image -> {
                    SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                    spuImagesEntity.setImgUrl(image);
                    spuImagesEntity.setSpuId(spuInfoEntity.getId());
                    return spuImagesEntity;
                })
                .collect(Collectors.toList());
        spuImagesService.saveBatch(spuImagesEntityList);
        //4、保存spu的规格参数；pms_sku_sale_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream()
                .map(baseAttr -> {
                    ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                    productAttrValueEntity.setSpuId(spuInfoEntity.getId());
                    productAttrValueEntity.setAttrName(attrService.getById(baseAttr.getAttrId()).getAttrName());
                    productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
                    productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
                    productAttrValueEntity.setAttrId(baseAttr.getAttrId());
                    return productAttrValueEntity;
                })
                .collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntities);
        //5、保存spu的积分信息；远程调用mall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.save(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("新增商品,远程调用保存spu积分信息失败");
        }
        //6、保存当前spu对应的所有sku信息：
        List<Skus> skus = vo.getSkus();
        if (!skus.isEmpty()) {
            skus.forEach(sku -> {
                //6.1)、sku的基本信息；pms_sku_info
                String defaultImgUrl = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImgUrl = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                //String skuName      sku名称
                //BigDecimal price    价格
                //String skuTitle     sku标题
                //String skuSubtitle  sku副标题
                BeanUtils.copyProperties(sku, skuInfoEntity);
                /*private Long spuId;            spuId
                private String skuDesc         sku介绍描述
                private Long catalogId;        所属分类id
                private Long brandId;          品牌id
                private String skuDefaultImg;  默认图片
                private Long saleCount;        销量  */
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDesc(spuInfoEntity.getSpuDescription());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSkuDefaultImg(defaultImgUrl);
                skuInfoEntity.setSaleCount(0L);
                skuInfoService.save(skuInfoEntity);
                //6.2)、sku的图片信息；pms_sku_images
                List<SkuImagesEntity> imagesEntities = sku.getImages()
                    .stream()
                    .map(img -> {
                    /*  skuImagesEntity 属性如下
                    private Long id;
                    private Long skuId;
                    private String imgUrl;
                    private Integer imgSort;
                    private Integer defaultImg;*/
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                   /* Images vo
                    private String imgUrl;
                    private int defaultImg;*/
                    BeanUtils.copyProperties(img, skuImagesEntity);
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuImagesEntity;
                    }).filter(img->{
                        return StringUtils.isNotBlank(img.getImgUrl()); })
                    .collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);
                //6.3)、sku的销售属性信息：pms_sku_sale_attr_value
                 /* sku saleAttr vo
                private Long attrId;  //id
                private String attrName; // 属性名
                private String attrValue; // 属性值*/
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = sku.getAttr()
                    .stream()
                    .map(saleAttr -> {
                    /*SkuSaleAttrValueEntity实体类属性如下
                    private Long id;
                    private Long skuId;
                    *private Long attrId;  属性id
                    *private String attrName;销售属性名
                    *private String attrValue;销售属性值
                    private Integer attrSort;顺序*/
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(saleAttr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //6.4)、sku的打折优惠表: 远程调用 mall_sms->sms_sku_ladder
                //6.5)、sku的满减又会表；远程调用 mall_sms->sms_sku_full_reduction
                //6.6)、sku内会员价格表: 远程调用 mall_sms->sms_member_price
                SpuReductionTo spuReductionTo = new SpuReductionTo();
                BeanUtils.copyProperties(sku, spuReductionTo);
                spuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                if (spuReductionTo.getFullCount() > 0 || spuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
                    R r1 = couponFeignService.saveAllCouponFromSpu(spuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("新增商品,远程调用保存spu[打折优惠&满减优惠&会员价]信息失败");
                    }
                }
            });
        }
    }
}