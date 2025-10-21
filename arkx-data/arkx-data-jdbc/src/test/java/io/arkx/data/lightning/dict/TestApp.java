package io.arkx.data.lightning.dict;

import io.arkx.data.lightning.config.EntityAutoConfiguration;
import io.arkx.data.lightning.dict.dao.*;
import io.arkx.data.lightning.dict.entity.*;
import io.arkx.data.lightning.repository.support.SqlToyJdbcRepositoryFactoryBean;
import io.arkx.framework.commons.collection.tree.TreeNode;
import io.arkx.framework.commons.collection.tree.TreeUtil;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.commons.util.UuidUtil;
import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.configure.SqltoyAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 17:15
 * @since 1.0
 */
@Import({SqltoyAutoConfiguration.class, EntityAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = { "io.arkx", "org.sagacity.sqltoy" })
@EnableJdbcRepositories(
        repositoryFactoryBeanClass = SqlToyJdbcRepositoryFactoryBean.class,
        basePackages={"io.arkx.data.lightning.sample.repository"})
@SpringBootTest // 仅加载 JDBC 相关 Bean
@ActiveProfiles("test") // 使用测试配置（可选）
public class TestApp {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NewDictInfoDao newDictInfoDao;
    @Autowired
    private RawDictInfoDao rawDictInfoDao;
    @Autowired
    private AkSysDictionaryDao akSysDictionaryDao;
    @Autowired
    private AkSysDictionaryDataDao akSysDictionaryDataDao;
    @Autowired
    private DictMappingInfoDao dictMappingInfoDao;

    static long timestamp;

    static {
        LocalDateTime localDateTime = LocalDateTime.now();
        // 使用系统默认时区
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        timestamp = instant.toEpochMilli();
    }

    @Rollback(false)
    @Transactional
    @Test
    public void transDict() {
        List<ExistDictMappingInfo> existDictMappingInfoList = dictMappingInfoDao.findExistDictMappingInfo();
        List<DictMappingInfo> allDictMappingInfoList = dictMappingInfoDao.findAll();
        Map<Integer, Integer> new2rawIdMap = new HashMap<>();
        for (DictMappingInfo dictMappingInfo : allDictMappingInfoList) {
            new2rawIdMap.put(dictMappingInfo.getNewDictId(), dictMappingInfo.getRawDictId());
        }

        List<RawDictInfo> rawDictInfos = rawDictInfoDao.findAll();
        List<NewDictInfo> newDictInfos = newDictInfoDao.findAll();

        Map<Integer, ExistDictMappingInfo> existDictMappingInfoMap = new HashMap<>();
        for (ExistDictMappingInfo existDictMappingInfo : existDictMappingInfoList) {
            existDictMappingInfoMap.put(existDictMappingInfo.getNewDictId(), existDictMappingInfo);
        }

        NewDictInfo newRoot = new NewDictInfo();
        newRoot.setId(0);
        newRoot.setName("Root");
        newDictInfos.addFirst(newRoot);
        Treex<Integer, NewDictInfo> newTree = TreeUtil.buildTreexFromData(newDictInfos);

        RawDictInfo rawRoot = new RawDictInfo();
        rawRoot.setId(0);
        rawRoot.setName("Root");
        rawDictInfos.addFirst(rawRoot);
        Treex<Integer, RawDictInfo> rawTree = TreeUtil.buildTreexFromData(rawDictInfos);

        {

//            System.out.println(tree);
//            System.out.println(tree.getRoot());
            System.out.println(newTree.getRoot().getChildren().getFirst());
            TreeNode<Integer, NewDictInfo> root = newTree.getRoot().getChildren().getFirst();
            List<TreeNode<Integer, NewDictInfo>> dictList = root.getChildren();
            for (TreeNode<Integer, NewDictInfo> dictNode : dictList) {
                NewDictInfo fromDict = dictNode.getValue();

                AkSysDictionary dictionary = new AkSysDictionary();
                dictionary.setId(UuidUtil.base58Uuid());
                dictionary.setParentId("2");
                dictionary.setCode(fromDict.getCode());
                dictionary.setName(fromDict.getName());
                dictionary.setDescription("");
                dictionary.setBuiltin("0");

                LocalDateTime localDateTime = LocalDateTime.now();
                // 使用系统默认时区
                ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
                Instant instant = zonedDateTime.toInstant();
                long timestamp = instant.toEpochMilli();
                dictionary.setCreateTime(timestamp);
                dictionary.setUpdateTime(timestamp);
                akSysDictionaryDao.insert(dictionary);

                Map<Integer, String> rawIdMap = new HashMap<>();
                // 存在映射
                ExistDictMappingInfo existDictMappingInfo = existDictMappingInfoMap.get(fromDict.getId());
                if (existDictMappingInfoMap.get(fromDict.getId()) != null) {
                    Integer rawDictId = existDictMappingInfo.getRawId();
                    TreeNode<Integer, RawDictInfo> rawTreeNode = rawTree.findNodeById(rawDictId);
                    rawIdMap = saveRawDictInfo2DictData(rawTreeNode.getChildren(), "", dictionary, 1);
                }

                saveNewDictInfo2DictData(dictNode.getChildren(), "0", dictionary, 2, rawIdMap, new2rawIdMap);
            }
        }

        {
//            System.out.println(rawTree);
//            System.out.println(rawTree.getRoot().getChildren().getFirst());
            TreeNode<Integer, RawDictInfo> root = rawTree.getRoot().getChildren().getFirst();
            List<TreeNode<Integer, RawDictInfo>> dictList = root.getChildren();
            for (TreeNode<Integer, RawDictInfo> dictNode : dictList) {
                RawDictInfo fromDict = dictNode.getValue();

                AkSysDictionary dictionary = new AkSysDictionary();
                dictionary.setId(UuidUtil.base58Uuid());
                dictionary.setParentId("3");
                dictionary.setCode(fromDict.getOldValue());
                dictionary.setName(fromDict.getName());
                dictionary.setDescription("");
                dictionary.setBuiltin("0");

                dictionary.setCreateTime(timestamp);
                dictionary.setUpdateTime(timestamp);
                akSysDictionaryDao.insert(dictionary);

                saveRawDictInfo2DictData(dictNode.getChildren(), "0", dictionary, 1);
            }
        }
    }

    private void saveNewDictInfo2DictData(ArrayList<TreeNode<Integer, NewDictInfo>> children,String dictDataParentId,
                                          AkSysDictionary dictionary, int version, Map<Integer, String> rawIdMap,
                                          Map<Integer, Integer> new2rawIdMap) {
        if (children == null) {
            return;
        }

        for (TreeNode<Integer, NewDictInfo> node : children) {
            NewDictInfo newDictInfo = node.getValue();

            AkSysDictionaryData dictionaryData = new AkSysDictionaryData();
            dictionaryData.setId(UuidUtil.base58Uuid());
            dictionaryData.setDictId(dictionary.getId());
            dictionaryData.setDictName(dictionary.getName());
            dictionaryData.setDictDataParentId(dictDataParentId);
            dictionaryData.setDictKey(newDictInfo.getCode());
            dictionaryData.setDictValue(newDictInfo.getName());
            dictionaryData.setCreateTime(timestamp);
            dictionaryData.setUpdateTime(timestamp);
            dictionaryData.setOrdinal(newDictInfo.getSortValue());
            dictionaryData.setDictVersion(version);

            if (!rawIdMap.isEmpty()) {
                Integer rawId = new2rawIdMap.get(newDictInfo.getId());
                String rawNewId = rawIdMap.get(rawId);
                dictionaryData.setPreDictDataId(rawNewId);
            }

            System.out.println(dictionaryData);
            akSysDictionaryDataDao.insert(dictionaryData);

            saveNewDictInfo2DictData(node.getChildren(), dictionaryData.getId(), dictionary, version, rawIdMap, new2rawIdMap);
        }
    }

    private Map<Integer, String> saveRawDictInfo2DictData(ArrayList<TreeNode<Integer, RawDictInfo>> children, String dictDataParentId,
                                                          AkSysDictionary dictionary, int version) {
        Map<Integer, String> newIdMap = new HashMap<>();
        if (children == null) {
            return newIdMap;
        }

        children.sort((o1, o2) -> o2.getValue().getSortValue() - o1.getValue().getSortValue());

        int index = 1;
        for (TreeNode<Integer, RawDictInfo> node : children) {
            RawDictInfo raw = node.getValue();

            AkSysDictionaryData dictionaryData = new AkSysDictionaryData();
            dictionaryData.setId(UuidUtil.base58Uuid());
            dictionaryData.setDictId(dictionary.getId());
            dictionaryData.setDictName(dictionary.getName());
            dictionaryData.setDictDataParentId(dictDataParentId);
            dictionaryData.setDictKey(raw.getOldValue());
            dictionaryData.setDictValue(raw.getName());
            dictionaryData.setCreateTime(timestamp);
            dictionaryData.setUpdateTime(timestamp);
            dictionaryData.setOrdinal(index++);
            dictionaryData.setDictVersion(version);

            akSysDictionaryDataDao.insert(dictionaryData);

            newIdMap.put(raw.getId(), dictionaryData.getId());

            saveRawDictInfo2DictData(node.getChildren(), dictionaryData.getId(), dictionary, version);
        }
        return newIdMap;
    }

}
