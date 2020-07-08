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

生成的代码需要使用以下四个方法/函数和一个字段/变量
```
// 读取下一个词法类型
void next();
// 将下一个词法单元读取
Token push();
// 检查下一个词法类型
void check(TokenType needTokenType) throws Exceptions;
// 返回词法不匹配的错误
Exceptions need(TokenType has,TokenType... needs);

// 下一个词法类型
TokenType token;
```
其中
```
Exceptions,TokenType,Token
```
可以由用户定义
默认值是
```
空,int,String
```
其伪代码为
```
Queue<Token> queue;

void next() {
    readToken();
    token = readedTokenType;
    queue.add(readedToken);
}

Token push() {
    return queue.remove();
}

void check(TokenType needTokenType) throws Exceptions {
    if(token != needTokenType){
        throw need(token, needTokenType);
    }
}

Exceptions need(TokenType token,TokenType... needTokenTypes) {
    return new Exceptions("...");
}
```
