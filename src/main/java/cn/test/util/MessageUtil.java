package cn.test.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: HandSomeMaker
 * @date: 2020/2/13 4:43
 */
public class MessageUtil {

    public final static String TYPE = "type";
    public final static String DATA = "data";
    public final static String FROM_NAME = "fromName";
    public final static String TO_NAME = "toName";

    public final static String TYPE_MESSAGE = "message";
    public final static String TYPE_USER = "user";

    /**
     * 组装消息，然后返回一个JSON格式的消息数据
     */
    public static String getContext(String type, String fromName, String toName, String content) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(MessageUtil.TYPE, type);
        userMap.put(MessageUtil.DATA, content);
        userMap.put(MessageUtil.FROM_NAME, fromName);
        userMap.put(MessageUtil.TO_NAME, toName);

        String jsonMsg = JSON.toJSONString(userMap);
        return jsonMsg;
    }
}
