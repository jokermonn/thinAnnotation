### 原理

此插件只作用于打包过程，编码过程无感知、无影响。

混淆之后将会产生一个包含应用中所有 class 的 jar，通过 ASM 扫描所有的类、类的方法、类的字段等一切可能会出现注解的地方，扫描到开发者配置的注解则将其删除；而如果当前类是注解类且是开发者所配置的类的话，该注解类将会被删除。

### 配置

thinAnnotation 默认删除了 butterknife 包、 `android/support/annotation/` 包、`androidx/annotation/` 包的所有注解，原因是这些 CLASS 时期的类在运行时是无用的；thinAnnotation 默认删除**所有**的 SOURCE 时期注解，因为使用 SOURCE 时期注解的地方实际上会在 .java -> .class 过程中被擦除，所以所有的 SOURCE 注解类实际上都是无用类，例如 `android/support/design/widget/TabLayout$Mode` 类，所以开发者实际上只需要配置想要删除的 CLASS 和 RUNTIME 时期的注解类，配置方式如下——

在 app/build.gradle 中：

```
apply plugin: 'thinAnnotation'

thinAnnotation {
  // 是否开启插件
  enable true
  // 目标注解类的路径
  shrinkClass = ['com/joker/maindexkeep/annotations/Runtime', 'com/joker/maindexkeep/annotations/Type']
  // 目标包的路径
  shrinkPackage = ['com/joker/maindexkeep/shrink']
}
```

日志在：`app/build/outputs/thinAnnotation` 路径下。