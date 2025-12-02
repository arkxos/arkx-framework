package io.arkx.data.lightning.query;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class DynamicGroupRule extends Rule {

    private String condition;

    @Override
    public String toSql() {
        List<String> sqls = getChildren().stream().map(Rule::toSql).toList();
        if (sqls.isEmpty()) {
            return "";
        }
        // 处理以下可能有空结构的问题
        /**
         *
         * String parse JS eval { "caseId":1, "pageSize":20, "pageNum":1,
         * "advancedQueryRules":{ "condition":"and", "ruleType":"DynamicGroupRule",
         * "children":[ { "ruleType":"DynamicFieldRule", "field":"customerName",
         * "operator":"like", "type":"string", "value":"福建省周滔捡科" }, { "condition":"and",
         * "ruleType":"DynamicGroupRule", "children":[ ] } ] } }
         */
        // 过滤空元素
        List<String> sqlList = sqls.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        String sql = Joiner.on(" " + condition + " ").join(sqlList);
        return "(" + sql + ")";
    }
}
