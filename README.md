# TWA 打包项目（blog-zvi-apk）

用 Google 官方 **TWA (Trusted Web Activity)** 方式把 `blog.zvi.onl` 打包成 Android APK。

- WebView 全屏加载线上博客（无浏览器地址栏）
- 依赖网站的 PWA manifest（`blog.zvi.onl/manifest.json`）
- GitHub Actions 自动构建，产物在 Actions 页面可下载

## 包名
`onl.zvi.blog.app`

## 构建
Push 到 GitHub 后 Actions 自动构建，下载 `app-release.apk` 安装到手机。
