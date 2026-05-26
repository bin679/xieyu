<template>
  <div class="login-page">
    <div class="bg-grid"></div>
    <div class="bg-glow bg-glow-1"></div>
    <div class="bg-glow bg-glow-2"></div>

    <div class="login-card" style="animation: card-entry .6s cubic-bezier(.22,1,.36,1) both;">
      <div class="card-header">
        <div class="card-icon" style="animation: icon-pop .5s .1s cubic-bezier(.34,1.56,.64,1) both;">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
            <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="8.5" cy="7" r="4"/>
            <line x1="20" y1="8" x2="20" y2="14"/>
            <line x1="23" y1="11" x2="17" y2="11"/>
          </svg>
        </div>
        <h1 class="card-title" style="animation: reveal-up .5s .2s cubic-bezier(.22,1,.36,1) both;">创建账号</h1>
        <p class="card-desc" style="animation: reveal-fade .5s .3s both;">注册后即可使用系统功能</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" size="large" class="login-form">
        <el-form-item prop="username" style="animation: reveal-up .45s .35s cubic-bezier(.22,1,.36,1) both;">
          <el-input v-model="form.username" placeholder="请输入用户名" class="custom-input">
            <template #prefix>
              <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password" style="animation: reveal-up .45s .40s cubic-bezier(.22,1,.36,1) both;">
          <el-input v-model="form.password" type="password" placeholder="请输入密码（至少6位）" show-password class="custom-input">
            <template #prefix>
              <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="realName" style="animation: reveal-up .45s .45s cubic-bezier(.22,1,.36,1) both;">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" class="custom-input">
            <template #prefix>
              <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="phone" style="animation: reveal-up .45s .50s cubic-bezier(.22,1,.36,1) both;">
          <el-input v-model="form.phone" placeholder="请输入手机号（选填）" class="custom-input" />
        </el-form-item>
        <el-form-item prop="email" style="animation: reveal-up .45s .55s cubic-bezier(.22,1,.36,1) both;">
          <el-input v-model="form.email" placeholder="请输入邮箱（选填）" class="custom-input" />
        </el-form-item>
        <el-form-item style="animation: reveal-up .45s .60s cubic-bezier(.22,1,.36,1) both;">
          <el-button type="primary" @click="doRegister" :loading="loading" class="btn-login-submit">
            注 册
          </el-button>
        </el-form-item>
        <div class="form-footer" style="animation: reveal-fade .45s .70s both;">
          <span>已有账号？</span>
          <el-button link type="primary" @click="$router.push('/login')">立即登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'

export default {
  name: 'Register',
  data() {
    return {
      loading: false,
      form: { username: '', password: '', realName: '', phone: '', email: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
      }
    }
  },
  methods: {
    doRegister() {
      this.$refs.formRef.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        try {
          await register(this.form)
          ElMessage.success('注册成功，请登录')
          this.$router.push('/login')
        } catch (e) {
          // error handled by interceptor
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #0f1724;
  position: relative;
  overflow: hidden;
}
.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,.02) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse 80% 50% at 50% 50%, black 40%, transparent 70%);
}
.bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: .12;
  pointer-events: none;
}
.bg-glow-1 {
  width: 500px; height: 500px;
  background: #c88d3d;
  top: -200px; right: -100px;
}
.bg-glow-2 {
  width: 400px; height: 400px;
  background: #5b8cbd;
  bottom: -150px; left: -100px;
}
.login-card {
  position: relative;
  width: 440px;
  padding: 36px 40px 32px;
  background: rgba(255,255,255,.97);
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,.3), 0 0 0 1px rgba(255,255,255,.06);
  backdrop-filter: blur(10px);
  z-index: 1;
}
.card-header { text-align: center; margin-bottom: 24px; }
.card-icon {
  width: 52px; height: 52px;
  margin: 0 auto 14px;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #111827 0%, #1f2937 100%);
  border-radius: 14px;
  color: #c88d3d;
  box-shadow: 0 8px 24px rgba(0,0,0,.15);
}
.card-title {
  font-family: var(--app-font-display);
  font-weight: 700;
  font-size: 1.35rem;
  color: #111827;
  letter-spacing: .03em;
  margin-bottom: 4px;
}
.card-desc { font-size: .85rem; color: #9ca3af; letter-spacing: .02em; }
.login-form { margin-top: 4px; }
.login-form :deep(.el-form-item) { margin-bottom: 14px; }
.custom-input :deep(.el-input__wrapper) {
  padding: 4px 14px;
  background: #f8f7f5;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e5e1da inset !important;
  transition: all .2s ease;
}
.custom-input :deep(.el-input__wrapper:hover) {
  background: #fff;
  box-shadow: 0 0 0 1px #d4cec5 inset !important;
}
.custom-input :deep(.el-input.is-focus .el-input__wrapper) {
  background: #fff;
  box-shadow: 0 0 0 2px rgba(200,141,61,.3) inset !important;
}
.custom-input :deep(.el-input__prefix) {
  margin-right: 8px;
  color: #b8b0a4;
}
.btn-login-submit {
  width: 100%;
  height: 46px;
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: .12em;
  border-radius: 8px;
  background: linear-gradient(135deg, #111827 0%, #1f2937 100%);
  border: none;
  transition: all .25s ease;
}
.btn-login-submit:hover {
  background: linear-gradient(135deg, #1a2434 0%, #2a3a4f 100%);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(0,0,0,.25);
}
.form-footer { text-align: center; font-size: .85rem; color: #9ca3af; }
.form-footer .el-button { font-weight: 500; }

@keyframes card-entry {
  from { opacity: 0; transform: scale(.96) translateY(12px); }
  to   { opacity: 1; transform: scale(1) translateY(0); }
}
@keyframes icon-pop {
  from { opacity: 0; transform: scale(.5); }
  to   { opacity: 1; transform: scale(1); }
}
@keyframes reveal-up {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}
@keyframes reveal-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}
</style>
