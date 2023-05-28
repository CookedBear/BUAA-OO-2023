# 使用说明

- 将待测试`jar`文件放于根目录下，并依序命名为`test<i>.jar`
- 编辑`run_all.bat`，在循环内部追加`start cmd /c run_side2.bat <i>`
- 保存退出后，运行`run_all.bat`，会自动生成数据并启动新窗口运行所有待测`jar`程序
- 待多余的`cmd`窗口自动消失后按任意键获取上一次测试的log信息
  - 若出现错误提示信息，请打开根目录中的`errorOut<i>.txt`检查错误信息和该轮输入数据

- 再次按任意键开始下一轮测试

# 使用截图

<img src="https://cookedbear-2003-1307884465.cos.ap-beijing.myqcloud.com/NotePics/202303250139168.png" alt="image-20230325013916410" style="zoom:80%;" />

# 文件说明

- `run_1_nonstop.bat`：单jar测试，请将测试jar重命名为`test1.jar`
- `run_all.bat`：需手动操作的多线程运行，默认进行三个文件的测试
- `run_manual.bat`：手动添加测试样例进入`stdin.txt`并进行测试，默认进行三个文件的测试
- `run_side2.bat`：启动新窗口并追加信息至`log.txt`
- `test2-1-generate.jar`：生成随机时间戳的100条随机数据（数据合法）
- `test2-1.jar`：对带有参数指示的`txt`文件（输出）进行合法性检查，并输出相关信息统计（统计信息类别可详见于**使用截图**）
  - 错误信息会输出至`errorOut<i>.txt`