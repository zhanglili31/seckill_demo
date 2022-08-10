package com.example.seckilldemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.entity.TSeckillGoods;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 秒杀商品表 Mapper 接口
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Repository
public interface TSeckillGoodsMapper extends BaseMapper<TSeckillGoods> {
    //防止超卖
    @Update("UPDATE t_seckill_goods SET stock_count = stock_count-1 WHERE goods_id=#{goodsId} AND stock_count > 0")
    boolean reduceStack(long goodsId);

}
