<template>
  <el-container class="app-layout">
    <!-- Sidebar -->
    <el-aside width="232px" class="app-sidebar">
      <div class="sidebar-brand">
        <div class="brand-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
            <path d="M6 12v5c2 2 6 2 8 0v-5"/>
          </svg>
        </div>
        <div class="brand-text">
          <span class="brand-title">LabManager</span>
          <span class="brand-sub">高校实验室管理</span>
        </div>
      </div>

      <div class="sidebar-nav">
        <div class="nav-section-label">导航菜单</div>
        <el-menu
          :default-active="routePath"
          class="sidebar-menu"
          router
        >
          <el-menu-item index="/home">
            <el-icon><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg></el-icon>
            <span>首页概览</span>
          </el-menu-item>
          <el-menu-item index="/users" v-if="user.role === 'admin'">
            <el-icon><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/labs">
            <el-icon><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg></el-icon>
            <span>实验室管理</span>
          </el-menu-item>
          <el-menu-item index="/equipment">
            <el-icon><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg></el-icon>
            <span>设备管理</span>
          </el-menu-item>
          <el-menu-item index="/borrows">
            <el-icon><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/><rect x="8" y="2" width="8" height="4" rx="1"/></svg></el-icon>
            <span>借用管理</span>
          </el-menu-item>
          <el-menu-item index="/repairs">
            <el-icon><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg></el-icon>
            <span>维修管理</span>
          </el-menu-item>
          <el-menu-item index="/statistics">
            <el-icon><svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg></el-icon>
            <span>统计报表</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="sidebar-footer">
        <div class="sidebar-version">v1.0</div>
      </div>
    </el-aside>

    <!-- Main Area -->
    <el-container class="app-main">
      <el-header class="app-header">
        <div class="header-greeting">
          <span class="header-welcome">欢迎回来，</span>
          <span class="header-name">{{ user.realName }}</span>
        </div>
        <div class="header-actions">
          <span class="header-role">
            <span class="role-dot" :class="user.role"></span>
            {{ user.role === 'admin' ? '系统管理员' : '普通用户' }}
          </span>
          <el-button class="btn-logout" @click="logout" size="small">
            <el-icon><svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg></el-icon>
            退出
          </el-button>
        </div>
      </el-header>
      <el-main class="app-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'AppLayout',
  data() {
    return {
      user: JSON.parse(sessionStorage.getItem('user') || '{}')
    }
  },
  computed: {
    routePath() { return this.$route.path }
  },
  methods: {
    logout() {
      sessionStorage.removeItem('user')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
/* ===== Sidebar ===== */
.app-sidebar {
  background: #111827;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-right: 1px solid rgba(255,255,255,.06);
}

.sidebar-brand {
  padding: 20px 18px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255,255,255,.06);
}

.brand-icon {
  width: 40px; height: 40px;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #c88d3d 0%, #e0b860 100%);
  border-radius: 10px;
  color: #fff;
  flex-shrink: 0;
  transition: transform .25s cubic-bezier(.34,1.56,.64,1);
}
.brand-icon:hover {
  transform: scale(1.05) rotate(-3deg);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}
.brand-title {
  font-family: var(--app-font-display);
  font-weight: 700;
  font-size: 1.05rem;
  color: #f1f0ec;
  letter-spacing: .03em;
}
.brand-sub {
  font-size: .7rem;
  color: #88909a;
  letter-spacing: .04em;
}

/* Navigation */
.sidebar-nav { flex: 1; overflow-y: auto; padding: 12px 0; }

.nav-section-label {
  padding: 6px 22px 8px;
  font-size: .68rem;
  text-transform: uppercase;
  letter-spacing: .1em;
  color: #5a6270;
}

.sidebar-menu {
  background: transparent;
  border-right: none;
}
.sidebar-menu .el-menu-item {
  margin: 2px 10px;
  border-radius: 8px;
  height: 42px;
  line-height: 42px;
  font-size: .88rem;
  color: #9ca3af;
  transition: all .22s cubic-bezier(.4,0,.2,1);
  display: flex;
  align-items: center;
  gap: 10px;
  padding-left: 14px !important;
  position: relative;
}
.sidebar-menu .el-menu-item::before {
  content: '';
  position: absolute;
  left: -4px;
  top: 50%;
  transform: translateY(-50%) scale(0);
  width: 3px;
  height: 20px;
  border-radius: 3px;
  background: #e8b95b;
  transition: transform .25s cubic-bezier(.34,1.56,.64,1);
}
.sidebar-menu .el-menu-item:hover {
  background: #1a2334;
  color: #e2e3e6;
}
.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(200,141,61,.22) 0%, rgba(200,141,61,.08) 100%);
  color: #e8b95b;
  font-weight: 600;
}
.sidebar-menu .el-menu-item.is-active::before {
  transform: translateY(-50%) scale(1);
}
.sidebar-menu .el-menu-item:hover::before {
  transform: translateY(-50%) scale(.6);
}
.sidebar-menu .el-menu-item .el-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform .22s cubic-bezier(.34,1.56,.64,1);
}
.sidebar-menu .el-menu-item:hover .el-icon {
  transform: scale(1.08);
}

.sidebar-footer {
  padding: 12px 18px;
  border-top: 1px solid rgba(255,255,255,.06);
}
.sidebar-version {
  font-size: .72rem;
  color: #4a5362;
  letter-spacing: .04em;
}

/* ===== Header ===== */
.app-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  height: 58px;
  border-bottom: 1px solid #eae6e0;
  box-shadow: 0 1px 4px rgba(0,0,0,.03);
}

.header-greeting {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.header-welcome { font-size: .85rem; color: #9ca3af; }
.header-name {
  font-weight: 600;
  font-size: .95rem;
  color: #1f2937;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-role {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: .82rem;
  color: #6b7280;
}
.role-dot {
  width: 7px; height: 7px;
  border-radius: 50%;
  background: #9ca3af;
  transition: transform .3s cubic-bezier(.34,1.56,.64,1);
}
.role-dot.admin { background: #c88d3d; }
.role-dot:hover { transform: scale(1.5); }

.btn-logout {
  color: #9ca3af;
  border-color: #e5e7eb;
  font-weight: 500;
  transition: all .22s cubic-bezier(.4,0,.2,1);
}
.btn-logout:hover {
  color: #c45d5d;
  border-color: #c45d5d;
  background: #fef5f5;
}

/* ===== Content ===== */
.app-content {
  background: #f6f4f0;
  min-height: calc(100vh - 58px);
  padding: 24px;
}
</style>
