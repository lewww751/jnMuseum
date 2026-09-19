<template>
  <PublicLayout>
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="font-serif text-3xl text-textMain mb-8">我的预约</h1>

      <!-- Query tabs -->
      <div class="flex space-x-4 mb-6">
        <button
          @click="queryType = 'code'"
          :class="[
            'px-6 py-2 rounded-lg transition-colors',
            queryType === 'code'
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          预约码查询
        </button>
        <button
          @click="queryType = 'phone'"
          :class="[
            'px-6 py-2 rounded-lg transition-colors',
            queryType === 'phone'
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          手机号查询
        </button>
      </div>

      <!-- Query form -->
      <div class="bg-panel border border-line rounded-lg p-6 mb-8">
        <div v-if="queryType === 'code'" class="space-y-4">
          <div>
            <label class="block text-textMain text-sm font-medium mb-2">预约码</label>
            <input
              v-model="queryForm.code"
              type="text"
              maxlength="8"
              placeholder="请输入8位预约码"
              class="w-full px-4 py-2 bg-panel2 border border-line rounded-lg text-textMain placeholder-textSub focus:outline-none focus:border-spring"
            />
          </div>
          <button
            @click="queryByCode"
            :disabled="!queryForm.code || querying"
            :class="[
              'w-full py-2 rounded-lg font-medium transition-colors',
              queryForm.code && !querying
                ? 'bg-spring text-ink hover:bg-springHover'
                : 'bg-line text-textSub cursor-not-allowed'
            ]"
          >
            {{ querying ? '查询中...' : '查询' }}
          </button>
        </div>

        <div v-else class="space-y-4">
          <div>
            <label class="block text-textMain text-sm font-medium mb-2">手机号码</label>
            <input
              v-model="queryForm.phone"
              type="tel"
              maxlength="11"
              placeholder="请输入手机号码"
              class="w-full px-4 py-2 bg-panel2 border border-line rounded-lg text-textMain placeholder-textSub focus:outline-none focus:border-spring"
            />
          </div>
          <div>
            <label class="block text-textMain text-sm font-medium mb-2">身份证号</label>
            <input
              v-model="queryForm.idCard"
              type="text"
              maxlength="18"
              placeholder="请输入入馆人身份证号"
              class="w-full px-4 py-2 bg-panel2 border border-line rounded-lg text-textMain placeholder-textSub focus:outline-none focus:border-spring"
            />
          </div>
          <button
            @click="queryByPhoneAndIdCard"
            :disabled="!queryForm.phone || !queryForm.idCard || querying"
            :class="[
              'w-full py-2 rounded-lg font-medium transition-colors',
              queryForm.phone && queryForm.idCard && !querying
                ? 'bg-spring text-ink hover:bg-springHover'
                : 'bg-line text-textSub cursor-not-allowed'
            ]"
          >
            {{ querying ? '查询中...' : '查询' }}
          </button>
        </div>
      </div>

      <!-- Results -->
      <div v-if="bookings.length > 0" class="space-y-4">
        <div
          v-for="booking in bookings"
          :key="booking.code"
          class="bg-panel border border-line rounded-lg p-6"
        >
          <div class="flex flex-wrap items-start justify-between gap-4 mb-4">
            <div>
              <div class="flex items-center space-x-3 mb-2">
                <span class="text-2xl font-serif text-spring">{{ booking.code }}</span>
                <span
                  class="px-2 py-1 text-xs rounded-full"
                  :class="getStatusClass(booking.status)"
                >
                  {{ booking.statusLabel }}
                </span>
              </div>
              <div class="space-y-1 text-sm text-textSub">
                <p>📅 {{ booking.weekday }} {{ formatDate(booking.visitDate) }}</p>
                <p>🕐 {{ booking.slotLabel }}</p>
                <p>👥 {{ booking.guests.length }} 人</p>
                <p>📝 预约时间: {{ formatDateTime(booking.createdAt) }}</p>
              </div>
            </div>
            <button
              v-if="booking.cancellable"
              @click="confirmCancel(booking)"
              class="px-4 py-2 border border-clay text-clay rounded-lg hover:bg-clay/10 transition-colors text-sm"
            >
              取消预约
            </button>
            <div v-else class="text-textSub text-sm">
              <p v-if="booking.status === 'CANCELLED'">已取消</p>
              <p v-else-if="booking.status === 'CHECKED_IN'">已入馆</p>
              <p v-else>取消截止: {{ formatDateTime(booking.cancelDeadline) }}</p>
            </div>
          </div>

          <!-- Guests info -->
          <div class="border-t border-line pt-4">
            <h4 class="text-textMain text-sm font-medium mb-2">入馆人信息</h4>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
              <div
                v-for="guest in booking.guests"
                :key="guest.idCardMasked"
                class="flex items-center space-x-2 text-sm"
              >
                <span class="text-textSub">{{ guest.type === 'PRIMARY' ? '主预约人' : '同行人' }}:</span>
                <span class="text-textMain">{{ guest.name }}</span>
                <span class="text-textSub">{{ guest.idCardMasked }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="hasQueried" class="text-center text-textSub py-12">
        未找到预约记录
      </div>
    </div>

    <!-- Cancel confirmation dialog -->
    <el-dialog
      v-model="showCancelDialog"
      title="取消预约"
      width="400px"
    >
      <div class="text-textMain">
        <p>确定要取消预约码 <span class="font-serif text-spring">{{ bookingToCancel?.code }}</span> 的预约吗？</p>
        <p class="text-textSub text-sm mt-2">取消后名额将立即释放，且无法恢复。</p>
      </div>
      <template #footer>
        <div class="flex justify-end space-x-3">
          <el-button @click="showCancelDialog = false">取消</el-button>
          <el-button
            type="primary"
            @click="cancelBooking"
            :loading="cancelling"
            class="!bg-clay !border-clay hover:!bg-clay/90"
          >
            确认取消
          </el-button>
        </div>
      </template>
    </el-dialog>
  </PublicLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { bookingApi } from '@/api/public'
import type { BookingView, BookingStatus } from '@/types'
import { validatePhone } from '@/utils/validation'
import PublicLayout from '@/layouts/PublicLayout.vue'

const queryType = ref<'code' | 'phone'>('code')
const queryForm = ref({
  code: '',
  phone: '',
  idCard: ''
})
const bookings = ref<BookingView[]>([])
const querying = ref(false)
const hasQueried = ref(false)
const showCancelDialog = ref(false)
const bookingToCancel = ref<BookingView | null>(null)
const cancelling = ref(false)

const queryByCode = async () => {
  if (!queryForm.value.code || querying.value) return

  querying.value = true
  hasQueried.value = false
  try {
    bookings.value = await bookingApi.lookupByCode(queryForm.value.code)
  } catch (error) {
    console.error('Failed to query booking:', error)
    bookings.value = []
  } finally {
    querying.value = false
    hasQueried.value = true
  }
}

const queryByPhoneAndIdCard = async () => {
  if (!queryForm.value.phone || !queryForm.value.idCard || querying.value) return

  if (!validatePhone(queryForm.value.phone)) {
    ElMessage.error('请输入正确的手机号码')
    return
  }

  querying.value = true
  hasQueried.value = false
  try {
    bookings.value = await bookingApi.lookupByPhoneAndIdCard(
      queryForm.value.phone,
      queryForm.value.idCard
    )
  } catch (error) {
    console.error('Failed to query booking:', error)
    bookings.value = []
  } finally {
    querying.value = false
    hasQueried.value = true
  }
}

const confirmCancel = (booking: BookingView) => {
  bookingToCancel.value = booking
  showCancelDialog.value = true
}

const cancelBooking = async () => {
  if (!bookingToCancel.value || cancelling.value) return

  cancelling.value = true
  try {
    const updated = await bookingApi.cancel({
      code: bookingToCancel.value.code,
      phone: queryForm.value.phone || bookingToCancel.value.guests[0].name // This should come from stored data
    })
    // Update the booking in the list
    const index = bookings.value.findIndex(b => b.code === updated.code)
    if (index !== -1) {
      bookings.value[index] = updated
    }
    ElMessage.success('预约已取消')
    showCancelDialog.value = false
  } catch (error) {
    console.error('Failed to cancel booking:', error)
  } finally {
    cancelling.value = false
  }
}

const getStatusClass = (status: BookingStatus): string => {
  const classes = {
    ACTIVE: 'bg-spring text-ink',
    CANCELLED: 'bg-line text-textSub',
    CHECKED_IN: 'bg-gold text-ink'
  }
  return classes[status]
}

const formatDate = (dateStr: string): string => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const formatDateTime = (dateStr: string): string => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}
</script>