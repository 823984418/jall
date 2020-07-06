jall是一个使用java写成的轻量级语法分析代码生成器

jall使用一个定制的语法描述语言作为输入，对输入的语法执行消左递归与提取左公因子
jall可以针对不同的目标语言生成对应的代码

你可以使用
```
java -jar jall.jar language.ll show
```
来展示经过处理后的语法
```
java -jar jall.jar language.ll java
```
来展示生成的对应java代码
