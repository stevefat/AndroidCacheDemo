# AndroidCacheDemo
Android 缓存实现

 
# 使用说明
1. 把这些文件拷入对应的工具包中
2. 在Application 中创建静态变量
```
public static CachManager cachManager;

public void onCreate(){
  cachManager= CachManager.getInstance(this);
}
```
3. 在需要使用的地方直接调用就可以了
```
//写入缓存
cachManager.writeCache("key_demo",str);
//读取缓存
cachManager.readCache("key_demo")

```
