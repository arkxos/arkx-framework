#!/usr/bin/env python3
import os
import re
import xml.etree.ElementTree as ET

def fix_pom_name(file_path):
    """为缺少name标签的pom.xml添加name标签"""
    try:
        # 读取文件内容
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 如果已经包含name标签，跳过
        if '<name>' in content:
            return False
        
        # 解析XML
        root = ET.fromstring(content)
        
        # 获取artifactId
        artifact_id = root.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
        if artifact_id is None:
            return False
        
        artifact_id_text = artifact_id.text
        
        # 生成name标签内容
        # 将arkx-data-db-product-mysql转换为ArkX Data DB Product MySQL
        name_parts = artifact_id_text.split('-')
        name_words = []
        for part in name_parts:
            if part == 'arkx':
                name_words.append('ArkX')
            elif part == 'data':
                name_words.append('Data')
            elif part == 'db':
                name_words.append('DB')
            elif part == 'product':
                name_words.append('Product')
            else:
                name_words.append(part.title())
        
        name_text = ' '.join(name_words)
        
        # 在artifactId后添加name标签
        artifact_id_element = root.find('.//{http://maven.apache.org/POM/4.0.0}artifactId')
        if artifact_id_element is not None:
            # 创建name元素
            name_element = ET.Element('name')
            name_element.text = name_text
            
            # 在artifactId后插入name元素
            index = list(root).index(artifact_id_element)
            root.insert(index + 1, name_element)
            
            # 写回文件
            ET.register_namespace('', 'http://maven.apache.org/POM/4.0.0')
            tree = ET.ElementTree(root)
            tree.write(file_path, encoding='utf-8', xml_declaration=True)
            
            print(f"Fixed {file_path}: added name '{name_text}'")
            return True
        
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

def main():
    # 需要修复的pom.xml文件列表
    pom_files = [
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-clickhouse/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-db2/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-dm/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-doris/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-elasticsearch/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-gbase/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-greenplum/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-highgo/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-hive/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-kingbase/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-mongodb/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-oceanbase/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-openguass/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-oscar/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-sqlite/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-sqlserver/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-starrocks/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-sybase/pom.xml",
        "arkx-data/arkx-data-db/arkx-data-db-product/arkx-data-db-product-tdengine/pom.xml",
    ]
    
    base_path = "e:/workspace/git/arkxos/arkxos-service/arkx-framework"
    fixed_count = 0
    
    for pom_file in pom_files:
        full_path = os.path.join(base_path, pom_file)
        if os.path.exists(full_path):
            if fix_pom_name(full_path):
                fixed_count += 1
        else:
            print(f"File not found: {full_path}")
    
    print(f"\nFixed {fixed_count} pom.xml files")

if __name__ == "__main__":
    main()