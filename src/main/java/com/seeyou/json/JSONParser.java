package com.seeyou.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2022/12/20 11:56
 * @history: 1.2022/12/20 created by jianfeng.zheng
 */
public class JSONParser {

    //currentIndex保存当前字符串扫描的位置，字符串是逐字符进行扫描
    private int currentIndex = 0;

    //清洗后的token
    private List<Token> jsonTokens;

    /**
     * 对json字符串进行分词
     *
     * @param json json字符串
     * @return token列表
     */
    public List<Token> tokenizer(String json) {
        // 保存分词结果
        List<Token> tokens = new ArrayList<>();
        while (currentIndex < json.length()) {
            char c = json.charAt(currentIndex);
            //对于空白符可以直接跳过,如果有更多的空白符只需添加新的判断即可
            if (c == ' ' || c == '\r' || c == '\n' || c == ',') {
                //字符只要处理过了必须要将当前位置移动到下一个
                ++currentIndex;
                continue;
            } else if (c == '{' || c == '}') {
                //对象
                tokens.add(new Token("object", c));
            } else if (c == '[' || c == ']') {
                //数组
                tokens.add(new Token("array", c));
            } else if (c == '"') {
                //字符串
                StringBuffer value = new StringBuffer();
                char cc = json.charAt(++currentIndex);
                // 这里以"作为字符串结束符的标志
                while (cc != '"') {
                    if (cc == '\\') {
                        cc = json.charAt(++currentIndex);
                    }
                    value.append(cc);
                    cc = json.charAt(++currentIndex);
                }
                tokens.add(new Token("string", value.toString()));
            } else if (c == ':') {
                // key-value中间的分隔符
                tokens.add(new Token("kvSymbol", "kvSymbol", c));
            } else if ((c >= '0' && c <= '9') || c == '-') {
                // 数字
                StringBuffer value = new StringBuffer();
                value.append(c);
                // 判断是不是浮点数
                boolean isFloat = false;
                //如果json是一位整型比如：1，那么这里不判断就会报错
                if (currentIndex + 1 < json.length()) {
                    char cc = json.charAt(++currentIndex);
                    // 判断包含浮点型，整型，科学技术法
                    while (cc == '.' || (cc >= '0' && cc <= '9') || cc == 'e' || cc == 'E' || cc == '+' || cc == '-') {
                        value.append(cc);
                        if (cc == '.') {
                            isFloat = true;
                        }
                        cc = json.charAt(++currentIndex);
                    }
                }
                if (isFloat) {
                    //浮点数
                    tokens.add(new Token("value", "float", Float.parseFloat(value.toString())));
                } else {
                    //整型
                    tokens.add(new Token("value", "long", Long.parseLong(value.toString())));
                }
            } else if ((c == 'n') && json.startsWith("null", currentIndex)) {
                tokens.add(new Token("value", "null", null));
                currentIndex += 3;
            } else if ((c == 't') &&
                    json.startsWith("true", currentIndex)) {
                tokens.add(new Token("value", "bool", true));
                currentIndex += 3;
            } else if ((c == 'f') &&
                    json.startsWith("false", currentIndex)) {
                tokens.add(new Token("value", "bool", false));
                //false是5个字符因此需要移动4位
                currentIndex += 4;
            }
            ++currentIndex;
        }
        //将当前位置重置
        currentIndex = 0;
        return tokens;
    }

    /**
     * token清洗
     * 区分key和value的token
     *
     * @param originTokens 原始token列表
     * @return 清洗过的token列表
     */
    public List<Token> tokenClean(List<Token> originTokens) {
        jsonTokens = new ArrayList<>();
        int tokenIndex = 0;
        while (tokenIndex < originTokens.size()) {
            Token token = originTokens.get(tokenIndex);
            if ("string".equals(token.getType())) {
                if (tokenIndex + 1 < originTokens.size() &&
                        "kvSymbol".equals(originTokens.get(tokenIndex + 1).getType())) {
                    token.setType("key");
                    jsonTokens.add(token);
                    Token valueToken = originTokens.get(tokenIndex + 2);
                    //object和array类型暂时不设置value类型
                    if (!"object".equals(valueToken.getType()) && !"array".equals(valueToken.getType())) {
                        valueToken.setType("value");
                    }
                    jsonTokens.add(valueToken);
                    //因为读取了kvSymbol和value两个token因此要+2
                    tokenIndex += 2;
                } else {
                    //如果没有kvSymbol直接当作value进行处理
                    token.setType("value");
                    jsonTokens.add(token);
                }
            } else {
                //清洗目前只处理string类型其他类型忽略
                jsonTokens.add(token);
            }
            ++tokenIndex;
        }
        return jsonTokens;
    }

    /**
     * 生成抽象语法树
     *
     * @return
     */
    public List<Ast> generateAST() {
        List<Ast> items = new ArrayList<>();
        //currentIndex作为当前读取token的位置
        //取出对象则currentIndex需要往前移动
        Token token = jsonTokens.get(currentIndex);
        if ("object".equals(token.getType()) && "{".equals(token.getValue())) {
            Ast item = new Ast();
            item.setType(token.getType());
            //如果json语法错误currentIndex有可能超过范围，这里先不考虑语法错误问题
            Token tt = jsonTokens.get(++currentIndex);
            while (!"object".equals(tt.getType()) || ("object".equals(tt.getType()) && !"}".equals(tt.getValue()))) {
                item.getItems().addAll(generateAST());
                tt = jsonTokens.get(currentIndex);
            }
            items.add(item);
            ++currentIndex;
        } else if ("array".equals(token.getType()) && "[".equals(token.getValue())) {
            Ast item = new Ast();
            item.setType(token.getType());
            Token tt = jsonTokens.get(++currentIndex);
            while (!"array".equals(tt.getType()) || ("array".equals(tt.getType()) && !"]".equals(tt.getValue()))) {
                item.getItems().addAll(generateAST());
                tt = jsonTokens.get(currentIndex);
            }
            items.add(item);
            ++currentIndex;
        } else if ("key".equals(token.getType())) {
            // key和value是成对出现，如果只有key没有value说明json语法错误，这里暂时不考虑语法错误
            Token value = jsonTokens.get(++currentIndex);
            if ("object".equals(value.getType()) || "array".equals(value.getType())) {
                //对象和数组需要递归获取语法树
                List<Ast> tts = generateAST();
                //如果是key-value结构必须设置key的名称
                tts.get(0).setName((String) token.getValue());
                return tts;
            } else if ("value".equals(value.getType())) {
                Ast item = new Ast();
                item.setValue(value.getValue());
                item.setType(value.getType());
                item.setName((String) token.getValue());
                items.add(item);
                ++currentIndex;
            }
        } else if ("value".equals(token.getType())) {
            //只有value没有key的情况，比如["1",2,true,null]
            Ast item = new Ast();
            item.setValue(token.getValue());
            item.setType(token.getType());
            items.add(item);
            ++currentIndex;
        }
        return items;
    }
}

