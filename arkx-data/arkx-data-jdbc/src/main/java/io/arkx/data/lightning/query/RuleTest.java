package io.arkx.data.lightning.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RuleTest {

    public static void main(String[] args) throws JsonProcessingException {
        String temp = """
                {
                    "condition": "and",
                    "ruleType":"DynamicGroupRule",
                    "children": [
                        {
                            "ruleType":"DynamicFieldRule",
                            "label": "EmployeeID",
                            "field": "employeeId",
                            "operator": "equals",
                            "type": "number",
                            "value": 1
                        },
                        {
                            "ruleType":"DynamicGroupRule",
                            "condition": "or",
                            "children": [
                                {
                                    "ruleType":"DynamicFieldRule",
                                    "label": "Employee ID",
                                    "field": "employeeId",
                                    "operator": "great_than_equals",
                                    "type": "number",
                                    "value": 2
                                },
                				{
                				   "ruleType":"DynamicFieldRule",
                                    "label": "Employee Name",
                                    "field": "employeeName",
                                    "operator": "like",
                                    "type": "number",
                                    "value": "2"
                                }
                            ]
                        }
                    ]
                }
                """;

        ObjectMapper mapper = new ObjectMapper();
        Rule result = mapper.readValue(temp, Rule.class);
        System.out.println(result.toSql());
    }
}
