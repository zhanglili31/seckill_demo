use seckill;
delete from t_order where id>1;
delete from t_seckill_order where id>1;
update t_seckill_goods set stock_count=100 where id>=1;