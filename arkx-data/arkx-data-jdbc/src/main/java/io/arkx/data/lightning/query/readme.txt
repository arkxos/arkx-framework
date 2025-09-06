POST /api/ultimate-query
{
    "dataSource": "primary",
    "tableName": "employees",
    "fields": ["id", "name", "salary"],
    "rule": {
        "condition": "and",
        "ruleType": "DynamicGroupRule",
        "children": [
            {
                "ruleType": "DynamicFieldRule",
                "field": "department",
                "operator": "equals",
                "type": "string",
                "value": "IT"
            }
        ]
    },
    "sortFields": [
        {"field": "salary", "direction": "DESC"}
    ],
    "pageNum": 1,
    "pageSize": 10,
    "timeout": 5000,
    "formatOptions": {
        "pretty": true,
        "includeComments": true,
        "comment": "IT部门员工查询"
    }
}