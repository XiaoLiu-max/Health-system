<template>
  <div class="about-page">
    <div class="card">
      <h2>ℹ️ 关于我们 - 健康管理系统</h2>
      <div class="content">
        <h3>📌 系统功能介绍</h3>
        <ul>
          <li>数据录入：记录身高、体重、血压、体温、血糖等健康数据</li>
          <li>数据统计：自动分析健康趋势，生成周报、月报</li>
          <li>我的健康数据：按日期查看、修改、删除历史记录</li>
          <li>AI 健康助手：智能问答、健康建议</li>
          <li>个人中心：修改资料、密码、账号安全管理</li>
        </ul>

        <h3>📌 使用说明</h3>
        <ul>
          <li>登录后可在首页快速进入各功能模块</li>
          <li>健康数据每天可记录一次，自动计算 BMI</li>
          <li>修改密码、注销账号请在账号安全中操作</li>
          <li>退出登录后将清除本地登录状态</li>
        </ul>

        <h3>📌 版本信息</h3>
        <p>健康管理系统 v1.0</p>
        <p>专注于个人日常健康数据管理与分析</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about-page {
  padding: 20px 32px;
  background: #edf7f0; /* 浅绿色背景 */
  min-height: calc(100vh - 60px);
}
.card {
  background: #fff;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.content {
  line-height: 1.7;
  color: #333;
}
ul {
  padding-left: 20px;
  margin: 8px 0 16px;
}
li {
  margin-bottom: 6px;
}
h3 {
  margin-top: 24px;
  font-size: 16px;
}
</style>
