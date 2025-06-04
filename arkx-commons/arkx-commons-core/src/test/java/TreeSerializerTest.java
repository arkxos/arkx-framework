import org.ark.common.Person;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.data.fastjson.TreexObjectSerializer;

public class TreeSerializerTest {
    public static void main(String[] args) {
        Treex<String, Person> tree = new Treex<>();
        tree.getRoot().setValue(new Person());
        tree.getRoot().addChildByValue(new Person());
        tree.getRoot().addChildByValue(new Person());

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect);
        fastJsonConfig.getSerializeConfig().put(Treex.class, new TreexObjectSerializer());

        String jsonString = JSON.toJSONString(tree, fastJsonConfig.getSerializeConfig());
        System.out.println(jsonString);
    }
}
