<!-- src/components/GlobalFooter.vue -->
<template>
  <div class="global-footer">
    <div class="footer-content">
      © 2025 <a href="https://www.renkelin.vip" target="_blank" rel="noopener noreferrer">Idea Code</a>
    </div>
  </div>
</template>

<script setup lang="ts">
// GlobalFooter component
</script>

<style scoped>
.global-footer {
  text-align: center;
  padding: 24px 0;
  color: #666;
  font-size: 14px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.global-footer a {
  color: #1890ff;
  text-decoration: none;
  transition: color 0.3s;
}

.global-footer a:hover {
  color: #40a9ff;
}
</style>
