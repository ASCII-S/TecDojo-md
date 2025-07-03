# 📚 学习进度看板

```dataviewjs
// 请确保obsidian中安装了dataview插件，并且用obsidian打开本文件

// 获取所有页面
const allPages = dv.pages("");

// 计算7天前的时间
const today = new Date();
const sevenDaysAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);

// 获取项目主文件
const projects = allPages.filter(p => p.file.name === p.file.folder);

// 按状态分类项目
const activeProjects = projects.filter(p => p.file.mtime >= sevenDaysAgo);
const pausedProjects = projects.filter(p => p.file.mtime < sevenDaysAgo);

// 创建多列布局
dv.header(3, "📊 项目状态概览");

// 第一行：统计信息
const stats = [
    `总项目数: ${projects.length}`,
    `活跃项目: ${activeProjects.length}`,
    `暂停项目: ${pausedProjects.length}`,
    `活跃度: ${projects.length > 0 ? Math.round((activeProjects.length / projects.length) * 100) : 0}%`
];

dv.paragraph(`**📈 统计信息:** ${stats.join(' | ')}`);

// 第二行：活跃项目
if (activeProjects.length > 0) {
    dv.header(4, "🟢 活跃项目");
    dv.table(
        ["项目", "最近修改", "状态"],
        activeProjects.map(p => [
            p.file.link,
            p.file.mtime,
            "🟢 进行中"
        ])
    );
}

// 第三行：暂停项目
if (pausedProjects.length > 0) {
    dv.header(4, "🟠 暂停项目");
    dv.table(
        ["项目", "最近修改", "状态"],
        pausedProjects.map(p => [
            p.file.link,
            p.file.mtime,
            "🟠 暂停"
        ])
    );
}

// 第四行：最近修改的文件
const recentFiles = allPages.filter(p => p.file.mtime >= sevenDaysAgo);
if (recentFiles.length > 0) {
    dv.header(4, "📝 本周修改文件");
    dv.table(
        ["文件名", "修改时间", "所属项目"],
        recentFiles.map(p => [
            p.file.link,
            p.file.mtime,
            p.file.folder || "根目录"
        ])
    );
}
```

---

> [!tip] 提示
> 点击表格中的链接可以直接跳转到相应的索引页面。
> 使用上方的"最近阅读"可以快速回到之前的学习内容。





