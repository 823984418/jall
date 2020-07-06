package net.dxzc.jall;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void help() {
        System.out.println("Usage");
        System.out.println("path 进行处理并展示");
        System.out.println("path show 进行处理并输出表示文件");
        System.out.println("path java [class [head [exceptions [token type]]]] 进行处理并输出java代码");
        System.out.println("path c [token type] 进行处理并输出c代码");
        System.out.println("path js 进行处理并输出java代码");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            help();
            return;
        }
        String path = args[0];
        String code;
        try (InputStream input = new BufferedInputStream(new FileInputStream(path))) {
            byte[] data = new byte[input.available()];
            input.read(data);
            code = new String(data);
        } catch (IOException e) {
            System.out.println("打开文件失败\n");
            e.printStackTrace();
            return;
        }
        Language language = new Parser().parser(code);
        Compiler compiler = new Compiler();
        compiler.eliminateLeftRecursion(language);
        compiler.extractLeftCommonFactor(language);
        if (args.length == 1) {
            System.out.println(language.toLanguageString());
        } else {
            CodeBuilder codeBuilder;
            switch (args[1]) {
                case "show": {
                    System.out.println(language.toSource());
                    return;
                }
                case "java": {
                    JavaCodeBuilder builder = new JavaCodeBuilder();
                    if (args.length > 2) {
                        builder.packageName = args[2].substring(0, args[2].lastIndexOf('.'));
                        builder.className = args[2].substring(args[2].lastIndexOf('.') + 1);
                    }
                    if(args.length > 3){
                        builder.heads = args[3];
                    }
                    if(args.length > 4){
                        builder.exceptions = args[4];
                    }
                    if(args.length > 5){
                        builder.tokenType = args[5];
                    }
                    codeBuilder = builder;
                    break;
                }
                case "c": {
                    CCodeBuilder builder = new CCodeBuilder();
                    if(args.length > 2){
                        builder.tokenType = args[2];
                    }
                    codeBuilder = builder;
                    break;
                }
                case "js": {
                    JsCodeBuilder builder = new JsCodeBuilder();

                    codeBuilder = builder;
                    break;
                }
                default: {
                    System.out.println("错误的命令\n");
                    help();
                    return;
                }
            }
            System.out.println(codeBuilder.build(language));
        }

    }

}
