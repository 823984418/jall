package net.dxzc.jall;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException {
        String cn = null;
        boolean show = false;
        boolean visit = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                case "-help": {
                    System.out.println("-h[elp]  输出此帮助信息");
                    System.out.println("-s[how]  展示语法");
                    System.out.println("-v[isit] 展示处理后的语法");
                    return;
                }
                case "-s":
                case "-show": {
                    show = true;
                    break;
                }
                case "-v":
                case "-visit": {
                    visit = true;
                    break;
                }
                default: {
                    if (i == args.length - 1) {
                        cn = args[i];
                    }
                }
            }
        }
        if (cn != null) {
            Class<?> c = Class.forName(cn);
            Language language = Language.fromClass(c);
            if (show) {
                System.out.println(language.toLanguageString());
                return;
            }
            Compiler compiler = new Compiler();
            compiler.leftInit(language);
            compiler.afterInit(language);
            if (visit) {
                System.out.println(language.toLanguageString());
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("package ").append(c.getPackageName()).append(";\n\n");
            sb.append("public class ").append(c.getSimpleName()).append("Imp extends ").append(c.getSimpleName()).append(" {\n\n");
            sb.append(compiler.compile(language));
            sb.append("}\n");
            System.out.println(sb.toString());
        } else {
            System.out.println("需要类名");
        }
    }

}
