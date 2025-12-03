package io.arkx.framework.cosyui.control.grid;

/**
 * 特性管理器
 *
 */
public class FeatureManager {

    private static FeatureManager instance = new FeatureManager();

    private AbstractGridFeature[] features;

    private FeatureManager() {
        // 注意：GridScroll必须放在最后面，因为它将table拆到各个div里面去了
        features = new AbstractGridFeature[]{new GridScript(), new GridAlternatingStyle(), new GridCheckbox(),
                new GridDownDropList(), new GridDrag(), new GridRowNo(), new GridSelector(), new GridPageBar(),
                new GridSort(), new GridTree(), new GridScroll()};
    }

    public static FeatureManager getInstance() {
        return instance;
    }

    public AbstractGridFeature[] getAll() {
        return features;
    }

}
