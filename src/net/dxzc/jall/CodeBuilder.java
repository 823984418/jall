package net.dxzc.jall;

/**
 * 代码生成器.
 */
public interface CodeBuilder {

    /**
     * 针对某个语言生成代码.
     *
     * @param language 语法
     * @return 生成的代码
     */
    String build(Language language);

}
