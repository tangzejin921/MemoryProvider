# 说明

有时需要在不同进程间使用一个变量，并且修改时要给出通知。 通过SettingsProvider可以满足， 
但是如果还需要重启后恢复默认值。这个程序就是用于这种情况。

## map暂存

```
dumpsys memory_provider -h
dumpsys memory_provider put test test
dumpsys memory_provider list
```

### SeLinux 报错处理

```
./packages/services/Car/car_product/sepolicy/public/service.te
添加如下
type memory_provider, service_manager_type;

./packages/services/Car/car_product/sepolicy/private/service_contexts
添加如下
memory_provider  u:object_r:memory_provider:s0

./packages/services/Car/car_product/sepolicy/private/system_server.te
添加如下
allow system_server memory_provider:service_manager { add find };

./packages/services/Car/car_product/sepolicy/private/system_app.te
添加如下
allow system_app memory_provider:service_manager { add find };
```

## sh调用

```
adb shell content call --uri content://memory_provider --method timeout --arg 10
adb shell content call --uri content://memory_provider --method cmd --arg ls
```

# TODO

sharedUserId="android.uid.system"并非 "android.uid.shell"shell是否能够执行