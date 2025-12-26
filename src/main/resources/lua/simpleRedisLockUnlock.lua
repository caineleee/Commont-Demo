---
--- Created by lihongliang.
--- DateTime: 2025/12/26 17:48
---

-- 获取锁的 Value(线程标识)
local redisThreadId = redis.call("get", KEYS[1])
-- 比较线程标识与锁种的标识是否一致
if (redisThreadId == ARGV[1]) then
    -- 释放锁, 如果删除成功返回 1
    return redis.call("del", KEYS[1])
end
-- 锁未释放
return 0



