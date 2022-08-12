package com.angryzyh.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.angryzyh.common.constant.ProductConstant;
import com.angryzyh.common.to.SkuHasStockTo;
import com.angryzyh.common.to.SpuBoundsTo;
import com.angryzyh.common.to.SpuReductionTo;
import com.angryzyh.common.to.es.SkuEsModel;
import com.angryzyh.common.utils.R;
import com.angryzyh.mall.product.controller.SpuInfoController;
import com.angryzyh.mall.product.entity.*;
import com.angryzyh.mall.product.feign.CouponFeignService;
import com.angryzyh.mall.product.feign.SearchFeignService;
import com.angryzyh.mall.product.feign.WareFeignService;
import com.angryzyh.mall.product.service.*;
import com.angryzyh.mall.product.vo.spuvo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;
    //远程调用
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<>()
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
                    }).filter(img-> StringUtils.isNotBlank(img.getImgUrl()))
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

    /**
     * 分页 模糊 匹配 查询
     * @param params {
     *               page: 1,//当前页码
     *               limit: 10,//每页记录数
     *               sidx: 'id',//排序字段
     *               order: 'asc/desc',//排序方式
     *               key: '华为',//检索关键字
     *               catelogId: 6,//三级分类id
     *               brandId: 1,//品牌id
     *               status: 0,//商品状态
     *               }
     * @return page
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String status = (String) params.get("status");
        LambdaQueryWrapper<SpuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(x -> {
                x.eq(SpuInfoEntity::getId, key)
                        .or().like(SpuInfoEntity::getSpuName, key);
            });
        }
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     * 商品上架 保存到es
     * @param spuId spuId
     * 需求: 根据spuId查到当前商品的可以被检索的 规格属性 放到es里面
     */
    @Override
    public void up(Long spuId) {
        // 1. 查询 获取 SkuInfoEntity
        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.list(new LambdaQueryWrapper<SkuInfoEntity>()
                .eq(spuId != null, SkuInfoEntity::getSpuId, spuId));
        // 2. 业务 hasStock(远程调用mall-ware) hotScore 先获取获取skuIds
        Map<Long, Boolean> hasStockMap = null;
        try {
            List<Long> skuIds = skuInfoEntityList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            R skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            Object data = skuHasStock.get("data");
            String s = JSON.toJSONString(data);
            List<SkuHasStockTo> skuHasStockTos = JSON.parseObject(s, new TypeReference<List<SkuHasStockTo>>(){});
            hasStockMap = skuHasStockTos.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 4. 查询attrs 因为都是 同一类型的spu 所以在最外层查询封装可以检索的规格属性
        // 查询 当前spuId 下的所有attrs
        List<ProductAttrValueEntity> attrForSpu = productAttrValueService.getAttrForSpu(spuId);
        // 查询到所有规格属性的attrId
        List<Long> attrIds = attrForSpu.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        // 查找出可以被检索的attrIds
        List<Long> attrIdsBySearch = attrService.listBySearchType(attrIds);
        // 过滤原 skuInfoEntityList 去除掉不在attrForSpu内的 attr
        List<SkuEsModel.Attrs> skuEsModelAttrs = attrForSpu.stream()
                .filter(attr -> attrIdsBySearch.contains(attr.getAttrId()))
                .map(attr -> {
                    SkuEsModel.Attrs attrEs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(attr, attrEs);
                    return attrEs;})
                .collect(Collectors.toList());
        // 接 1. 遍历所有 sku 把 SkuEsModel 所要的数据塞进去
        Map<Long, Boolean> finalHasStockMap = hasStockMap;
        List<SkuEsModel> skuEsModelList = skuInfoEntityList.stream()
            .map(sku -> {
                // 0. 创建es 封装对象
                SkuEsModel skuEsModel = new SkuEsModel();
                BeanUtils.copyProperties(sku, skuEsModel);
                /* private BigDecimal skuPrice; // 字段不匹配
                    private String skuImg;       // 字段不匹配*/
                skuEsModel.setSkuImg(sku.getSkuDefaultImg());
                skuEsModel.setSkuPrice(sku.getPrice());
                // 2. 业务 hasStock(远程调用mall-ware) hotScore
                if (finalHasStockMap != null) {
                    skuEsModel.setHasStock(finalHasStockMap.get(sku.getSkuId()));
                } else {
                    skuEsModel.setHasStock(true);
                }
                //  hotScore 热销指数
                skuEsModel.setHotScore(0L);
                // 3. 查询 获取 CategoryEntity & BrandEntity
                // From CategoryEntity
                //private String catalogName;
                CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
                skuEsModel.setCatalogName(categoryEntity.getName());
                // From BrandEntity
                //private String brandName;
                //private String brandImg;
                BrandEntity brandEntity = brandService.getById(sku.getBrandId());
                skuEsModel.setBrandName(brandEntity.getName());
                skuEsModel.setBrandImg(brandEntity.getLogo());
                // 4. 查询attrs 因为都是 同一类型的spu 所以在最外层查询封装可以检索的规格属性
                skuEsModel.setAttrs(skuEsModelAttrs);
                return skuEsModel;
            }).collect(Collectors.toList());
        //5. 远程调用search服务,把商品信息上传到 es服务器
        R r = searchFeignService.productStatusUp(skuEsModelList);
        if (r.getCode() == 0) {
            //6. 远程调用成功修改spu的spuStatus
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setPublishStatus(ProductConstant.ProductUpStatusEnum.UP_SPU.getCode());
            spuInfoEntity.setUpdateTime(new Date());
            spuInfoEntity.setId(spuId);
            baseMapper.updateById(spuInfoEntity);
        }else {
            // 接口被不同服务重复调用,接口等幂性
        }
    }
}