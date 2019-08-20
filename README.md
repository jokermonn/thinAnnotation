相关文章链接：[MainDex 优化记](https://mp.weixin.qq.com/s?__biz=MzUyMDg2ODgwOQ==&mid=2247483679&idx=1&sn=4520ae38f703ee3b2303c02b47a80639&chksm=f9e287b9ce950eafa38deb9d8bf898a3abcaa0234adcc08ad017d8fd89bfc612a8d6e6a1cefe&token=1165484979&lang=zh_CN#rd)

### 原理

此插件只作用于打包过程，编码过程无感知、无影响。

混淆之后将会产生一个包含应用中所有 class 的 jar，通过 ASM 扫描所有的类、类的方法、类的字段等一切可能会出现注解的地方，扫描到开发者配置的注解则将其删除；而如果当前类是注解类且是开发者所配置的类的话，该注解类将会被删除。

### 配置

thinAnnotation 默认删除**所有**的 SOURCE 时期注解，因为使用 SOURCE 时期注解的地方实际上会在 .java -> .class 过程中被擦除，所以所有的 SOURCE 注解类实际上都是无用类，例如 `android/support/design/widget/TabLayout$Mode` 类；thinAnnotation 默认删除了 `butterknife/` 包、 `android/support/annotation/` 包、`androidx/annotation/` 包的所有注解，原因是这些 CLASS 时期的类在运行时是无用的（除非特殊要求，CLASS 文件实际上也是可以全部删除的）；开发者只需要配置想要删除的 CLASS 和 RUNTIME 时期的注解类，配置方式如下——

在 project/build.gradle 中：

```
buildscript {
  repositories {
    maven { url 'https://jitpack.io' }
  }
  dependencies {
    classpath 'com.github.jokermonn:thinAnnotation:0.0.2'
  }
}
```

在 app/build.gradle 中：

```
apply plugin: 'thinAnnotation'

thinAnnotation {
  enable true
  shrinkClass 'com/joker/maindexkeep/annotations/RuntimeAnn'
  // 删除 com/joker/maindexkeep/shrink/ 下所有注解
  shrinkPackage('com/joker/maindexkeep/shrink/', { true })
  // 删除 com/joker/maindexkeep/shrink2/ 下除 RUNTIME 之外的所有注解
  shrinkPackage 'com/joker/maindexkeep/shrink2/'
}
```

日志在：`app/build/outputs/thinAnnotation` 路径下。

## 示例

以 butterknife 为例：

使用前：butterknife 包注解类存在，`@BindView` 等注解存在于 .class 文件中

![](http://imglf6.nosdn0.126.net/img/UnlRcDgySWkxbnZUbjBCSXdnUFoza3RCR0R1TGY0M0x6dkxwQksxaFdJS3FJUHhmT2FCK21RPT0.png?imageView&thumbnail=2490y1632&type=png&quality=96&stripmeta=0)

使用后：butterknife 包注解类全部删除，所有使用该注解的地方也都会被清除注解

![](http://imglf5.nosdn0.126.net/img/UnlRcDgySWkxbnZUbjBCSXdnUFozanN2dzFqaU4xREZZalNtc2JrSGw0WXNQWEQ5NlpQNUlnPT0.png?imageView&thumbnail=2238y1484&type=png&quality=96&stripmeta=0)

## CHANGELOG

0.0.3：

- fix [issue#1](https://github.com/jokermonn/thinAnnotation/issues/1)
- 使用方式稍作改变
- 丰富 thinAnnotation log
- 使用 kotlin