---
jupytext:
  formats: md:markdown,ipynb/others/C+JupyterNotebookUsingWslSetup/InstallMiniconda.ipynb:notebook
  text_representation:
    extension: .md
    format_name: markdown
    format_version: '1.3'
    jupytext_version: 1.13.8
kernelspec:
  display_name: C++
  language: C++
  name: xcpp
language_info:
  codemirror_mode: text/x-c++src
  file_extension: .cpp
  mimetype: text/x-c++src
  name: c++
  version: '11'
---

[Miniconda 安装指南](https://www.anaconda.com/docs/getting-started/miniconda/install#linux)

运行以下四个命令来下载并安装最新的 Linux 安装程序（适用于您选择的芯片架构）。这些命令逐行解释如下：

在您的主目录中创建一个名为 "miniconda3" 的新目录。
下载适用于您选择的芯片架构的 Linux Miniconda 安装脚本，并将脚本保存为 miniconda3 目录中的 miniconda.sh。
使用 bash 在静默模式下运行 miniconda.sh 安装脚本。
安装完成后删除 miniconda.sh 安装脚本文件。

64-bit

AWS Graviton 2/ARM 64

IBM Z

```
mkdir -p ~/miniconda3
wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh -O ~/miniconda3/miniconda.sh
bash ~/miniconda3/miniconda.sh -b -u -p ~/miniconda3
rm ~/miniconda3/miniconda.sh
```

要下载旧版本，请访问 Miniconda 官方仓库

安装后，关闭并重新打开您的终端应用程序，或通过运行以下命令刷新终端：

```
source ~/miniconda3/bin/activate
```

然后，通过运行以下命令在所有可用的 shell 上初始化 conda：

```
conda init --all
```

如果您在安装后没有初始化 conda，即使已安装 conda，也可能会看到 "conda not found" 错误。有关可能的解决方案，请参阅 macOS/Linux 故障排除主题中的 "Conda: command not found"。
