-- 性能分析
-- 找出最慢的SQL
SELECT full_sql, AVG(duration/1e6) as avg_ms, MAX(duration/1e6) as max_ms
FROM trace_nodes
WHERE type = 'SQL'
GROUP BY full_sql
ORDER BY max_ms DESC
LIMIT 10;

-- 找出最慢的方法
SELECT class_name, method_name,
       AVG(duration/1e6) as avg_ms, MAX(duration/1e6) as max_ms
FROM trace_nodes
WHERE type = 'METHOD'
GROUP BY class_name, method_name
ORDER BY max_ms DESC
LIMIT 10;


-- 故障诊断
-- 查找失败请求
SELECT * FROM trace_nodes
WHERE success = false
ORDER BY start_time DESC
LIMIT 100;

-- 跟踪请求完整链路
SELECT * FROM trace_nodes
WHERE request_id = 'd85f9d9c-7b4f-4b4a-8c0a-6e8c5f3a2e1c'
ORDER BY start_time;

-- 调用链分析
-- 查询层级结构
WITH RECURSIVE trace_tree AS (
  SELECT * FROM trace_nodes WHERE parent_id IS NULL AND request_id = '...'
  UNION ALL
  SELECT n.* FROM trace_nodes n
  JOIN trace_tree t ON n.parent_id = t.trace_id
)
SELECT * FROM trace_tree
ORDER BY start_time;