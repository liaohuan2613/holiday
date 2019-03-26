package com.lhk;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleDupDemo {

    private static Logger logger = LoggerFactory.getLogger(ConsumerDemo.class);
    private static Gson gson = new Gson();
    private static final String REMOVAL_URL = "http://47.96.26.149:5002/api/deduplication";
    private static RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        String title = "Just a Test .....";
        String testContent = "网络直播屡屡闹出>人命自律不足致直播业乱象迭生\r\n\r\n2019年03月21日08:46:54来源：中国青年报任然\r\n\r\n2月9日是正月初五，生活在浙江绍兴柯桥区的四>川人郝小勇没有回老家，他不停地刷“快手”、约人一起拍段子，做着一夜暴富的“网红”梦。不幸的是，就在这一天，郝小勇因为在拍摄“跳河”短视>频时头部受伤，经抢救无效死亡。\\r\\n\\r\\n用“表演不规范，亲人两行泪”来形容这样一起“网红”悲剧，或许显得轻佻。然而，它残酷地揭示了直播>行业鱼龙混杂的另一面。过去，在谈到直播行业乱象时，其内容低俗、暴力的一面多被放大。其实，内容失范问题一体两面，从业者的权益和安全>问题同样需要得到正视。\\r\\n\\r\\n前不久，“工厂招工越来越难，年轻人宁愿送外卖也不愿进厂”的话题很热。而在不愿意进工厂的年轻人中，就有>一些选择了直播行业。直播已不再是小众的亚文化现象，而的确成了一部分人的就业选择。如某平台据称注册用户达7亿，而月活跃用户就达2亿，>即使这其中只有一小部分是职业“玩家”，也不是一个小群体。从目前直播行业的规模和社会的发展趋势看，直播的职业化发展方向已越来越清晰。\r\n\r\n问题在于，直播行业的职业规范和权益保障，仍显得非常孱弱，以至于该行业的热度和从业者人数已经无法让人忽视，却在整体上表现出显\n" +
                "著的“江湖”色彩。近几年，网络直播闹出人命的悲剧已发生多起。如就在上个月，有媒体报道，大连一男子连续3个月直播自己饮酒，最后不幸身亡\n" +
                "。这些行为虽然完全是自发的，但也与平台“流量为王”的利益分成机制有关。“搏命”式表演一再出现，足见主播职业规范仍存在着很大缺失。这也>是目前直播行业承担一定污名的重要原因。\r\n\r\n《网络表演经营活动管理办法》规定，网络表演不得含有表演方式恐怖、残忍、暴力、低俗，>摧残表演者身心健康的内容。但这更多是为了保障内容的“健康”。“网红”及大量一般性的职业主播，他们到底需要怎样的权益保护，目前并无明确>规范。除了闹出人命这类极端悲剧，像最近某直播平台宣布破产而引发主播讨薪，都说明作为一个职业化的行业，直播业尚未形成成熟的职业规范>、从业者权益保障机制。\\r\\n\\r\\n自律不足，一直被视为直播业乱象迭生的一个重要原因。但必须看到，自律意识的孕育，并不是无条件的。如果“游离态”的主播占了绝大多数，缺乏必要的职业归属感，谈自律和职业操守无疑过于奢侈。\r\n\r\n当然，这并不是说要把所有主播都按照传统企业\n" +
                "模式进行体制化的“收编”，但是否可以借鉴类似浙江横店群众演员公会这种组织化管理，对网红的职业规范和行为边界作出一定规范和引导？这既>有利于保障主播个人权益，也促进自律，让行业早日摆脱“鱼龙混杂”“不务正业”的污名。\r\n\r\n网络直播发展到“下半场”，从资本、企业主体，>再到一般从业者，都有更大的动力追求行业规范发展。目前，行业还有太多的灰色地带需要厘清，包括市场机制、利益分成模式、职业规范及权益>保障、自律建设，等等，都需要进一步完善、创新和探索。平台应该有针对性地提升合规发展的水平，监管层面也可以有更积极的引导措施。无论>如何，走向职业化的网络直播行业，不应该再让人“拼命”博出位。";
        String content = "测试测试\r\n不知道问题在哪，所以在这里稍微做了些许的测试";
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.putIfAbsent("id", "id - 1");
        requestMap.putIfAbsent("title", title);
        requestMap.putIfAbsent("content", testContent);
        requestMap.putIfAbsent("source", "财联社");
        requestMap.putIfAbsent("database", "8");
        System.out.println(content);
        Object object = requestEntity(requestMap, REMOVAL_URL, restTemplate);
        System.out.println(object);
    }

    public static Object requestEntity(Map<String, Object> requestMap, String requestApi, RestTemplate restTemplate) {
        requestMap.put("mktCd", "*");
        URI removalUrl = UriComponentsBuilder.fromUriString(requestApi).build().encode().toUri();
        Object obj = null;
        try {
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity(removalUrl, requestMap, Object.class);
            obj = responseEntity.getBody();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
