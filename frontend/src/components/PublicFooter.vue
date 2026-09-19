<template>
  <footer class="bg-panel border-t border-line mt-auto">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Museum info -->
        <div>
          <h3 class="font-serif text-xl text-textMain mb-4">济南市博物馆</h3>
          <p class="text-textSub text-sm leading-relaxed">
            济南市博物馆是济南市综合性博物馆，馆藏文物丰富，展示了济南深厚的历史文化底蕴。
          </p>
        </div>

        <!-- Contact info -->
        <div>
          <h4 class="font-medium text-textMain mb-4">联系我们</h4>
          <div class="space-y-2 text-sm text-textSub">
            <p>地址：济南市历下区经十路11899号</p>
            <p>咨询电话：0531-12345678</p>
            <p>开放时间：周二至周日 9:00-17:00</p>
            <p class="text-xs text-textSub/70">（16:00停止入馆，周一闭馆）</p>
          </div>
        </div>

        <!-- Quick links -->
        <div>
          <h4 class="font-medium text-textMain mb-4">快速链接</h4>
          <div class="space-y-2 text-sm">
            <router-link to="/reserve" class="block text-textSub hover:text-spring transition-colors">
              门票预约
            </router-link>
            <router-link to="/visit" class="block text-textSub hover:text-spring transition-colors">
              参观指南
            </router-link>
            <router-link to="/my-booking" class="block text-textSub hover:text-spring transition-colors">
              我的预约
            </router-link>
          </div>
        </div>
      </div>

      <!-- Bottom bar -->
      <div class="mt-8 pt-8 border-t border-line text-center text-xs text-textSub/70">
        <p>© 2026 济南市博物馆 版权所有</p>
        <p class="mt-1">本站为演示项目，内容虚构</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
</script>