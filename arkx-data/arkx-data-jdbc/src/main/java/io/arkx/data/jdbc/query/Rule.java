package io.arkx.data.jdbc.query;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "ruleType")
@JsonSubTypes(value = {
    @JsonSubTypes.Type(value = DynamicFieldRule.class, name = "DynamicFieldRule"),
    @JsonSubTypes.Type(value = DynamicGroupRule.class, name = "DynamicGroupRule")
})
public abstract class Rule {

    private String ruleType;
    protected List<Rule> children;

    public List<Rule> getChildren() {
        if(children == null) {
            return new ArrayList<>();
        }
        return children;
    }

    public abstract String toSql();

    @Override
    public String toString() {
        String result = "";
        result += "["+ruleType+"]";
        for (Rule rule : getChildren()) {
            result += "\t" + rule.toString() + "\n";
        }

        return result;
    }
}
