<template>
  <router-view />
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* ===== Design Tokens ===== */
:root {
  /* Primary palette — warm brass / copper */
  --app-accent:        #c88d3d;
  --app-accent-light:  #d9a85e;
  --app-accent-dark:   #a06e28;
  --app-accent-soft:   #faf3e8;

  /* Sidebar */
  --app-sidebar-bg:        #111827;
  --app-sidebar-hover:     #1a2436;
  --app-sidebar-text:      #9ca3af;
  --app-sidebar-active:    #c88d3d;

  /* Surfaces */
  --app-bg:            #f6f4f0;
  --app-card-bg:       #ffffff;
  --app-header-bg:     #ffffff;

  /* Text */
  --app-text-primary:   #1f2937;
  --app-text-secondary: #6b7280;
  --app-text-muted:     #9ca3af;

  /* Semantic */
  --app-success: #4b8b6b;
  --app-warning: #d4953a;
  --app-danger:  #c45d5d;
  --app-info:    #5b8cbd;

  /* Spacing */
  --app-radius-sm: 6px;
  --app-radius:    10px;
  --app-radius-lg: 14px;

  /* Shadow */
  --app-shadow-sm: 0 1px 3px rgba(0,0,0,.04), 0 1px 2px rgba(0,0,0,.06);
  --app-shadow:    0 1px 3px rgba(0,0,0,.06), 0 4px 12px rgba(0,0,0,.04);
  --app-shadow-lg: 0 4px 16px rgba(0,0,0,.06), 0 8px 32px rgba(0,0,0,.04);

  /* Typography */
  --app-font-display: 'IBM Plex Serif', 'Noto Serif SC', 'SimSun', serif;
  --app-font-body:    'Plus Jakarta Sans', 'Noto Sans SC', 'Microsoft YaHei', 'PingFang SC', sans-serif;

  /* Transition */
  --app-ease: cubic-bezier(.4,0,.2,1);
  --app-duration: 180ms;
}

/* ===== Element Plus Theme Override ===== */
:root {
  --el-color-primary:       var(--app-accent);
  --el-color-primary-light-3: var(--app-accent-light);
  --el-color-primary-light-5: #e4c896;
  --el-color-primary-light-7: #efe0c4;
  --el-color-primary-light-8: #f5ece0;
  --el-color-primary-light-9: #faf6f0;
  --el-color-primary-dark-2:  var(--app-accent-dark);
  --el-color-success: var(--app-success);
  --el-color-warning: var(--app-warning);
  --el-color-danger:  var(--app-danger);
  --el-color-info:    var(--app-info);

  --el-font-family: var(--app-font-body);
  --el-border-radius-base: var(--app-radius-sm);

  --el-bg-color-overlay: #ffffff;
  --el-border-color:     #e5e7eb;
  --el-border-color-light: #f0efed;
  --el-fill-color-light: #f9f8f6;
  --el-text-color-primary:   var(--app-text-primary);
  --el-text-color-regular:   var(--app-text-secondary);
}

/* ===== Reset & Base ===== */
*,
*::before,
*::after { margin: 0; padding: 0; box-sizing: border-box; }

html {
  font-size: 15px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  font-family: var(--app-font-body);
  color: var(--app-text-primary);
  background: var(--app-bg);
  line-height: 1.6;
  letter-spacing: .01em;
  position: relative;
}

body::before {
  content: '';
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 9999;
  opacity: .03;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='.85' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  background-repeat: repeat;
  background-size: 256px 256px;
}

/* ===== Page Transition ===== */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all .35s cubic-bezier(.22,1,.36,1);
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(16px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}

/* Staggered reveal for content sections */
@keyframes reveal-up {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}
@keyframes reveal-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}
@keyframes soft-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: .6; }
}

/* ===== Scrollbar ===== */
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #c4c0b8; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #a8a39b; }

/* ===== Global Element Plus Refinements ===== */

/* Table */
.el-table {
  --el-table-border-color: #eae7e2;
  --el-table-header-bg-color: #faf8f5;
  --el-table-row-hover-bg-color: #faf6f0;
  --el-table-current-row-bg-color: #faf6f0;
  border-radius: var(--app-radius);
  overflow: hidden;
  font-size: .92rem;
  animation: reveal-up .4s .15s cubic-bezier(.22,1,.36,1) both;
}
.el-table th.el-table__cell {
  font-weight: 600;
  font-size: .85rem;
  text-transform: uppercase;
  letter-spacing: .04em;
  color: var(--app-text-secondary);
  border-bottom: 2px solid #ebe7e0;
}
.el-table .el-table__cell { padding: 12px 0; }
.el-table__body tr {
  transition: background-color .2s ease;
}
.el-table__body tr:hover {
  background-color: #faf6f0 !important;
}

/* Card */
.el-card {
  border-radius: var(--app-radius-lg) !important;
  border: 1px solid #e8e5e0 !important;
  box-shadow: var(--app-shadow) !important;
  transition: box-shadow .3s ease, transform .3s ease;
}
.el-card:hover { box-shadow: var(--app-shadow-lg) !important; }
.el-card__header {
  border-bottom: 1px solid #f0edf8 !important;
  padding: 16px 22px !important;
  font-weight: 600;
  font-size: .95rem;
  letter-spacing: .02em;
}
.el-card__body { padding: 22px !important; }

/* Button */
.el-button {
  font-weight: 500;
  letter-spacing: .02em;
  transition: all .22s cubic-bezier(.4,0,.2,1);
}
.el-button--primary {
  --el-button-hover-bg-color: var(--app-accent-light);
  --el-button-active-bg-color: var(--app-accent-dark);
}
.el-button--primary:hover {
  box-shadow: 0 2px 8px rgba(200,141,61,.3);
  transform: translateY(-1px);
}

/* Input & Select */
.el-input__wrapper,
.el-select .el-input__wrapper {
  border-radius: var(--app-radius-sm);
  box-shadow: 0 0 0 1px #e0dcd6 inset !important;
  transition: all .22s cubic-bezier(.4,0,.2,1);
}
.el-input__wrapper:hover,
.el-select .el-input__wrapper:hover { box-shadow: 0 0 0 1px #c4beb6 inset !important; }
.el-input.is-focus .el-input__wrapper,
.el-select .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 2px rgba(200,141,61,.25) inset !important;
}

/* Dialog */
.el-dialog {
  border-radius: var(--app-radius-lg) !important;
  box-shadow: var(--app-shadow-lg), 0 0 0 1px rgba(0,0,0,.05) !important;
}
.el-dialog__header {
  padding: 22px 28px 0 !important;
  font-weight: 600;
  font-size: 1.05rem;
}
.el-dialog__body { padding: 20px 28px !important; }
.el-dialog__footer { padding: 8px 28px 22px !important; }

/* Pagination */
.el-pagination {
  --el-pagination-button-bg-color: #faf8f5;
}
.el-pagination .el-pager li:not(.is-disabled).is-active {
  background-color: var(--app-accent);
  border-radius: var(--app-radius-sm);
  box-shadow: 0 2px 6px rgba(200,141,61,.25);
}
.el-pagination .btn-prev,
.el-pagination .btn-next { border-radius: var(--app-radius-sm) !important; }

/* Tag */
.el-tag {
  border-radius: 20px;
  padding: 0 10px;
  font-weight: 500;
  letter-spacing: .02em;
}

/* Menu (sidebar) — overridden in Layout, but set base */
.el-menu { border-right: none !important; }

/* Alert */
.el-alert {
  border-radius: var(--app-radius);
}

/* Empty */
.el-empty__description {
  color: #b0aca5 !important;
}

/* Popconfirm */
.el-popconfirm__action .el-button--primary {
  --el-button-bg-color: var(--app-danger);
  --el-button-border-color: var(--app-danger);
}

/* Form label */
.el-form-item__label {
  color: var(--app-text-secondary) !important;
  font-weight: 500;
}
</style>
