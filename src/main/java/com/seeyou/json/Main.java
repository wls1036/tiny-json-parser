package com.seeyou.json;

import com.alibaba.fastjson.JSON;

import java.io.InputStream;
import java.util.List;

/**
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2022/12/16 15:49
 * @history: 1.2022/12/16 created by jianfeng.zheng
 */
public class Main {

    public static void main(String[] args) throws Exception {
        InputStream fin = Main.class.getResourceAsStream("/example.json");
        byte[] buf = new byte[fin.available()];
        fin.read(buf);
        fin.close();
        String json = new String(buf, "utf-8");
        JSONParser parser = new JSONParser();
        List<Token> tokens = parser.tokenizer(json);
        tokens = parser.tokenClean(tokens);
        List<Ast> astItems = parser.generateAST();
        Ast ast = astItems.get(0);
        System.out.println(String.format("|%-12s|%-12s|%-15s|", "type", "valueType", "value"));
        System.out.println("-------------------------------------------");
        for (Token t : tokens) {
            System.out.println(String.format("|%-12s|%-12s|%-15s|",
                    t.getType(),
                    t.getValueType(),
                    t.getValue()));
        }
        System.out.println("-------------------------------------------");
        System.out.println(JSON.toJSONString(ast,true));
    }
}
