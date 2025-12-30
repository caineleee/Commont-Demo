-- 参数列表

-- 优惠券ID
local VoucherID = ARGV[1];
-- 用户ID
local UserID = ARGV[2];
-- 订单ID
local OrderID = ARGV[3];

-- 数据 Key
-- 库存 key
local StockKey = "seckill:stock:" .. VoucherID
-- 订单 key, 存放的是所有购买了这优惠券的 UserID
local OrderKey = "seckill:order:" .. VoucherID


-- 脚本
-- 判断库存是否充足 (Lua get redis 数据拿到的是 String, 需要转换成数字 tonumber 才能进行比较)
if (tonumber(redis.call("get", StockKey)) <= 0) then
        -- 库存不足
        return 1;
end

-- 判断用户是否重复下单 使用 sisnumber 判断 userId 是否在 orderKey 中存在
if (redis.call("sismember", OrderKey, UserID) == 1) then
    -- 重复下单
    return 2;
end

-- Redis 扣减库存
redis.call("incrby", StockKey, -1)
-- Redis 下单(保存用户)
redis.call("sadd", OrderKey, UserID)

-- 向消息队列发送消息, orderId 为了适配java entity 类属性名, 所以这里使用 id
redis.call("xadd", "stream.order", "*", "voucherId", VoucherID, "userId", UserID, "id", OrderID)
return 0;




