<template>
  <div class="stat-grid">
    <div class="stat-card" v-for="(item, idx) in cards" :key="item.label" :style="{'--accent': item.color, animationDelay: (idx * .08) + 's'}">
      <div class="stat-icon">
        <svg viewBox="0 0 24 24" width="26" height="26" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
          <path v-if="item.key==='lab'" d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline v-if="item.key==='lab'" points="9 22 9 12 15 12 15 22"/>
          <rect v-if="item.key==='equip'" x="2" y="3" width="20" height="14" rx="2"/><line v-if="item.key==='equip'" x1="8" y1="21" x2="16" y2="21"/><line v-if="item.key==='equip'" x1="12" y1="17" x2="12" y2="21"/>
          <path v-if="item.key==='user'" d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle v-if="item.key==='user'" cx="9" cy="7" r="4"/><path v-if="item.key==='user'" d="M23 21v-2a4 4 0 0 0-3-3.87"/><path v-if="item.key==='user'" d="M16 3.13a4 4 0 0 1 0 7.75"/>
          <path v-if="item.key==='borrow'" d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/><rect v-if="item.key==='borrow'" x="8" y="2" width="8" height="4" rx="1"/>
        </svg>
      </div>
      <div class="stat-body">
        <span class="stat-value">{{ countDisplay[item.key] ?? item.value }}</span>
        <span class="stat-label">{{ item.label }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue'

export default {
  name: 'StatCards',
  props: {
    cards: { type: Array, required: true }
  },
  setup(props) {
    const countDisplay = ref({})

    const animate = (key, from, to) => {
      const start = performance.now()
      const duration = 800
      const tick = (now) => {
        const elapsed = now - start
        const progress = Math.min(elapsed / duration, 1)
        const eased = 1 - Math.pow(1 - progress, 3)
        countDisplay.value[key] = Math.round(from + (to - from) * eased)
        if (progress < 1) requestAnimationFrame(tick)
        else countDisplay.value[key] = to
      }
      requestAnimationFrame(tick)
    }

    watch(() => props.cards, (cards) => {
      cards.forEach(item => {
        const from = countDisplay.value[item.key] ?? 0
        animate(item.key, from, item.value)
      })
    }, { deep: true, immediate: true })

    return { countDisplay }
  }
}
</script>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 22px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--app-shadow);
  border: 1px solid #e8e5e0;
  transition: all .3s cubic-bezier(.4,0,.2,1);
  cursor: default;
  position: relative;
  overflow: hidden;
  animation: card-reveal .5s cubic-bezier(.22,1,.36,1) both;
}
.stat-card::after {
  content: '';
  position: absolute;
  top: 0; left: 0;
  width: 4px; height: 100%;
  background: var(--accent);
  border-radius: 4px 0 0 4px;
}
.stat-card::before {
  content: '';
  position: absolute;
  top: -20px; right: -20px;
  width: 80px; height: 80px;
  border-radius: 50%;
  background: color-mix(in srgb, var(--accent) 6%, transparent);
  transition: transform .4s cubic-bezier(.22,1,.36,1);
}
.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--app-shadow-lg);
}
.stat-card:hover::before {
  transform: scale(1.4);
}

.stat-icon {
  width: 50px; height: 50px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 12px;
  background: color-mix(in srgb, var(--accent) 12%, transparent);
  color: var(--accent);
  flex-shrink: 0;
  transition: transform .25s cubic-bezier(.34,1.56,.64,1);
}
.stat-card:hover .stat-icon {
  transform: scale(1.06);
}

.stat-body { display: flex; flex-direction: column; }
.stat-value {
  font-family: var(--app-font-display);
  font-size: 1.8rem;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
  font-variant-numeric: tabular-nums;
}
.stat-label { font-size: .82rem; color: #6b7280; margin-top: 2px; }

@keyframes card-reveal {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}

@media (max-width: 1000px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
