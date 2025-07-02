# 使用 xeus-cling 的 C/C++ Jupyter Notebook - Windows WSL 环境搭建

Jupyter notebook 通过 xeus-cling 项目支持 C/C++。xeus-cling 是基于 C++ 解释器 cling 和 Jupyter 协议原生实现 xeus 的 Jupyter 内核。

但这主要是为 Linux 和 OS X 平台构建的，不直接支持 Windows Jupyter notebook。

以下步骤帮助您通过 WSL 在 Windows 上搭建环境：

## 1. 在 WSL 上启用 Ubuntu
如果您之前没有在 Windows 上使用过 WSL，请按照[Microsoft 官方指南](https://docs.microsoft.com/en-us/windows/wsl/install-win10)的步骤操作。

从 Windows 商店安装适用于 Windows 的 Ubuntu。我使用的是 Ubuntu 18.04。

您也可以使用其他支持的 Linux 发行版，不过我还没有尝试过。

## 2. 安装 Miniconda
从 https://conda.io/miniconda.html 安装 miniconda。

请根据您的 32/64 位系统下载相应的 Linux 版本。

我为我的 64 位 Windows 下载了 Miniconda3-latest-Linux-x86_64.sh。

安装 miniconda：
[Miniconda 安装指南](InstallMiniconda.md)

## 3. Conda、Jupyter Notebook、Xeus-Cling 设置
初始化并更新 conda。初始化将在您的 bashrc 中添加必要的引导程序：

```
conda init
conda update conda
```

为 cpp 创建一个环境：

```
conda create -n cpp
```

激活环境：

```
conda activate cpp
```

安装 Jupyter notebook、xeus-cling 和所需的 cpp 扩展：

```
conda install notebook
conda install -c conda-forge xeus-cling
conda install -c conda-forge jupyter_contrib_nbextensions
conda install -c conda-forge jupyter_nbextensions_configurator
jupyter nbextensions_configurator enable --user

```

## 4. 编写 Notebooks
现在一切都设置好了，为您的 notebooks 选择一个目录：

我选择 ~/cpp-books：

```
cd ~/cpp-books
conda activate cpp
```

使用 --no-browser 选项运行 Jupyter notebook，因为我们在 WSL 中，路径不能直接在 Windows 中打开页面：

```
jupyter notebook --no-browser
```

## 5. 修复 Windows 浏览器打开问题
为 Jupyter 生成一个可编辑的默认配置：

```
jupyter notebook --generate-config
echo c.NotebookApp.use_redirect_file = False >> ~/.jupyter/jupyter_notebook_config.py
```

设置您想要的 Windows 浏览器变量，比如：

```
export BROWSER="/mnt/c/Program Files/Mozilla Firefox/firefox.exe" 
```

或者：

```
export BROWSER="/mnt/c/Program Files/Google/Chrome/Application/chrome.exe"
```

有了上面的配置和设置的 BROWSER 环境变量，就不需要添加 --no-browser 选项了。只需运行：

```
jupyter notebook
```

## 6. 创建 Windows 桌面快捷方式
在 WSL 中创建一个脚本文件来设置环境并运行 Notebook：

```
$ cat ~/note

#!/bin/bash                                                                                                             
cd ~/cpp-books
source ~/miniconda3/etc/profile.d/conda.sh 
conda activate cpp
export BROWSER="/mnt/c/Program Files/Mozilla Firefox/firefox.exe"                                                       
jupyter notebook
```

根据需要更改路径。

现在，从 Windows 桌面，右键单击并创建一个新的快捷方式。

将位置设置为：

```
C:\Windows\System32\wsl.exe bash -c "source /home/<user>/note"
```

现在您可以打开快捷方式，在 Windows 浏览器中查看您的 notebooks。

另外，在快捷方式属性中更改为"最小化启动"。
