import org.ark.common.Person;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.rapidark.framework.commons.collection.Treex;
import com.rapidark.framework.data.fastjson.TreexObjectSerializer;

public class TreeSerializerTest {
    public static void main(String[] args) {
        Treex<Person> tree = new Treex<>();
        tree.getRoot().setData(new Person());
        tree.getRoot().addChild(new Person());
        tree.getRoot().addChild(new Person());

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
