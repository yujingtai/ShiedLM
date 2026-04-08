package com.shieldlm.detection;

import java.util.LinkedHashSet;
import java.util.Set;

public class RiskSignalExtractor {

    public Set<RiskSignal> extract(NormalizedPrompt prompt) {
        Set<RiskSignal> signals = new LinkedHashSet<>();
        String compactText = prompt.compactText();

        if (containsAny(compactText,
                "忽略之前",
                "不要遵守前面的限制",
                "绕过限制",
                "无视规则",
                "跳过限制",
                "ignoreprevious")) {
            signals.add(RiskSignal.OVERRIDE_INTENT);
        }
        if (containsAny(compactText,
                "系统提示词",
                "systemprompt",
                "隐藏指令",
                "开发者消息",
                "developermessage")) {
            signals.add(RiskSignal.PROMPT_LEAK_INTENT);
        }
        if (containsAny(compactText,
                "apikey",
                "api密钥",
                "密钥",
                "内部配置",
                "token",
                "accesskey")) {
            signals.add(RiskSignal.SECRET_EXTRACTION_INTENT);
        }
        if (containsAny(compactText,
                "现在你是",
                "扮演",
                "切换身份",
                "作为审计员",
                "充当")) {
            signals.add(RiskSignal.ROLE_HIJACK_INTENT);
        }
        if (containsAny(compactText,
                "色情",
                "成人网站",
                "黄网站",
                "违法网站",
                "18+网站",
                "av网站",
                "成人视频",
                "成人影视",
                "儿童色情",
                "未成年人色情")) {
            signals.add(RiskSignal.UNSAFE_CONTENT_INTENT);
        }
        if (containsAny(compactText,
                "杀人",
                "灭口",
                "碎尸",
                "埋尸",
                "抛尸",
                "毁尸灭迹",
                "处理尸体",
                "肢解尸体",
                "下毒")) {
            signals.add(RiskSignal.VIOLENT_CRIME_INTENT);
        }
        if (containsAny(compactText,
                "冰毒",
                "海洛因",
                "制毒",
                "炼毒",
                "毒品配方",
                "贩毒",
                "k粉",
                "麻古")) {
            signals.add(RiskSignal.DRUG_CRIME_INTENT);
        }
        if (containsAny(compactText,
                "炸弹",
                "爆炸物",
                "土炸药",
                "雷管",
                "改枪",
                "枪支",
                "子弹",
                "手枪制作")) {
            signals.add(RiskSignal.WEAPON_EXPLOSIVE_INTENT);
        }
        if (containsAny(compactText,
                "入侵",
                "破解密码",
                "盗取密码",
                "撞库",
                "木马",
                "钓鱼网站",
                "勒索病毒",
                "ddos",
                "黑进")) {
            signals.add(RiskSignal.CYBER_CRIME_INTENT);
        }
        if (containsAny(compactText,
                "诈骗",
                "电信诈骗",
                "诈骗话术",
                "盗窃",
                "偷窃",
                "盗刷",
                "洗钱",
                "伪造证件")) {
            signals.add(RiskSignal.FRAUD_THEFT_INTENT);
        }

        return signals;
    }

    private boolean containsAny(String text, String... fragments) {
        for (String fragment : fragments) {
            if (text.contains(fragment)) {
                return true;
            }
        }
        return false;
    }
}
