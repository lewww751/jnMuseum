<template>
  <PublicLayout>
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Progress steps -->
      <div class="flex items-center justify-between mb-8">
        <div class="flex items-center">
          <div
            v-for="(step, index) in steps"
            :key="index"
            class="flex items-center"
          >
            <div
              class="w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium"
              :class="currentStep >= index ? 'bg-spring text-ink' : 'bg-panel border border-line text-textSub'"
            >
              {{ index + 1 }}
            </div>
            <span
              v-if="index < steps.length - 1"
              class="w-16 h-0.5 mx-2"
              :class="currentStep > index ? 'bg-spring' : 'bg-line'"
            ></span>
          </div>
        </div>
      </div>

      <div class="text-center mb-8">
        <h1 class="font-serif text-2xl text-textMain">{{ steps[currentStep].title }}</h1>
        <p class="text-textSub text-sm mt-1">{{ steps[currentStep].description }}</p>
      </div>

      <!-- Step 1: Select date -->
      <div v-if="currentStep === 0" class="space-y-4">
        <div
          v-for="day in availability"
          :key="day.date"
          @click="selectDate(day)"
          :class="[
            'bg-panel border rounded-lg p-4 cursor-pointer transition-all',
            selectedDate?.date === day.date
              ? 'border-spring bg-spring/10'
              : day.isOpen
              ? 'border-line hover:border-spring'
              : 'border-line opacity-50 cursor-not-allowed'
          ]"
          :disabled="!day.isOpen"
        >
          <div class="flex items-center justify-between">
            <div>
              <div class="flex items-center space-x-3">
                <span class="font-medium text-textMain">{{ day.weekday }}</span>
                <span class="text-textSub text-sm">{{ formatDate(day.date) }}</span>
              </div>
              <p v-if="!day.isOpen" class="text-clay text-sm mt-1">{{ day.closeReason }}</p>
              <p v-else class="text-spring text-sm mt-1">
                可预约名额: {{ getTotalAvailable(day) }} 人
              </p>
            </div>
            <div class="text-2xl">{{ getEmojiForDay(day) }}</div>
          </div>
        </div>
      </div>

      <!-- Step 2: Select slot -->
      <div v-if="currentStep === 1" class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div
          v-for="slot in selectedDate?.slots || []"
          :key="slot.slot"
          @click="selectSlot(slot)"
          :class="[
            'bg-panel border rounded-lg p-6 cursor-pointer transition-all',
            selectedSlot?.slot === slot.slot
              ? 'border-spring bg-spring/10'
              : slot.bookable
              ? 'border-line hover:border-spring'
              : 'border-line opacity-50 cursor-not-allowed'
          ]"
          :disabled="!slot.bookable"
        >
          <div class="text-center">
            <div class="text-3xl mb-2">{{ slot.slot === 'AM' ? '🌅' : '🌆' }}</div>
            <h3 class="font-medium text-textMain mb-2">{{ slot.label }}</h3>
            <p class="text-textSub text-sm mb-1">剩余名额: {{ slot.remaining }} 人</p>
            <p v-if="!slot.bookable" class="text-clay text-sm">{{ slot.reason }}</p>
          </div>
        </div>
      </div>

      <!-- Step 3: Fill information -->
      <div v-if="currentStep === 2" class="bg-panel border border-line rounded-lg p-6">
        <div class="space-y-6">
          <!-- Phone number -->
          <div>
            <label class="block text-textMain text-sm font-medium mb-2">手机号码 *</label>
            <input
              v-model="bookingForm.phone"
              type="tel"
              maxlength="11"
              placeholder="请输入手机号码"
              class="w-full px-4 py-2 bg-panel2 border border-line rounded-lg text-textMain placeholder-textSub focus:outline-none focus:border-spring"
              :class="{ 'border-clay': phoneError }"
              @input="validatePhone"
            />
            <p v-if="phoneError" class="text-clay text-sm mt-1">{{ phoneError }}</p>
          </div>

          <!-- Primary guest -->
          <div class="border-t border-line pt-6">
            <h3 class="font-medium text-textMain mb-4">主预约人信息</h3>
            <div class="space-y-4">
              <div>
                <label class="block text-textMain text-sm font-medium mb-2">姓名 *</label>
                <input
                  v-model="bookingForm.guests[0].name"
                  type="text"
                  maxlength="20"
                  placeholder="请输入姓名"
                  class="w-full px-4 py-2 bg-panel2 border border-line rounded-lg text-textMain placeholder-textSub focus:outline-none focus:border-spring"
                />
              </div>
              <div>
                <label class="block text-textMain text-sm font-medium mb-2">身份证号 *</label>
                <input
                  v-model="bookingForm.guests[0].idCard"
                  type="text"
                  maxlength="18"
                  placeholder="请输入18位身份证号"
                  class="w-full px-4 py-2 bg-panel2 border border-line rounded-lg text-textMain placeholder-textSub focus:outline-none focus:border-spring"
                  :class="{ 'border-clay': idCardErrors[0] }"
                  @input="validateIdCard(0)"
                />
                <p v-if="idCardErrors[0]" class="text-clay text-sm mt-1">{{ idCardErrors[0] }}</p>
              </div>
            </div>
          </div>

          <!-- Companions -->
          <div class="border-t border-line pt-6">
            <div class="flex items-center justify-between mb-4">
              <h3 class="font-medium text-textMain">同行人信息（可选）</h3>
              <span class="text-textSub text-sm">{{ bookingForm.guests.length - 1 }}/2</span>
            </div>
            <div
              v-for="(guest, index) in bookingForm.guests.slice(1)"
              :key="index"
              class="space-y-4 mb-6"
            >
              <div class="flex items-center justify-between">
                <span class="text-textMain">同行人 {{ index + 1 }}</span>
                <button
                  @click="removeGuest(index + 1)"
                  class="text-clay hover:text-clay/80 text-sm"
                >
                  删除
                </button>
              </div>
              <div>
                <label class="block text-textMain text-sm font-medium mb-2">姓名 *</label>
                <input
                  v-model="guest.name"
                  type="text"
                  maxlength="20"
                  placeholder="请输入姓名"
                  class="w-full px-4 py-2 bg-panel2 border border-line rounded-lg text-textMain placeholder-textSub focus:outline-none focus:border-spring"
                />
              </div>
              <div>
                <label class="block text-textMain text-sm font-medium mb-2">身份证号 *</label>
                <input
                  v-model="guest.idCard"
                  type="text"
                  maxlength="18"
                  placeholder="请输入18位身份证号"
                  class="w-full px-4 py-2 bg-panel2 border border-line rounded-lg text-textMain placeholder-textSub focus:outline-none focus:border-spring"
                  :class="{ 'border-clay': idCardErrors[index + 1] }"
                  @input="validateIdCard(index + 1)"
                />
                <p v-if="idCardErrors[index + 1]" class="text-clay text-sm mt-1">{{ idCardErrors[index + 1] }}</p>
              </div>
            </div>

            <button
              v-if="bookingForm.guests.length < 3"
              @click="addGuest"
              class="w-full py-2 border border-dashed border-line text-textSub rounded-lg hover:border-spring hover:text-spring transition-colors"
            >
              + 添加同行人
            </button>
          </div>
        </div>
      </div>

      <!-- Step 4: Success -->
      <div v-if="currentStep === 3 && bookingResult" class="text-center">
        <div class="bg-panel border border-line rounded-lg p-8 mb-6">
          <div class="text-6xl mb-4">🎉</div>
          <h2 class="font-serif text-2xl text-textMain mb-4">预约成功！</h2>

          <div class="bg-panel2 rounded-lg p-6 mb-6">
            <div class="mb-4">
              <p class="text-textSub text-sm mb-1">预约码</p>
              <p class="text-4xl font-serif text-spring">{{ bookingResult.code }}</p>
            </div>
            <div class="flex justify-center">
              <canvas ref="qrCodeCanvas" class="border-4 border-white rounded"></canvas>
            </div>
          </div>

          <div class="text-left space-y-3">
            <div class="flex justify-between">
              <span class="text-textSub">参观日期</span>
              <span class="text-textMain">{{ formatDate(bookingResult.visitDate) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-textSub">参观时段</span>
              <span class="text-textMain">{{ bookingResult.slotLabel }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-textSub">预约人数</span>
              <span class="text-textMain">{{ bookingResult.guests.length }} 人</span>
            </div>
            <div class="flex justify-between">
              <span class="text-textSub">预约时间</span>
              <span class="text-textMain">{{ formatDateTime(bookingResult.createdAt) }}</span>
            </div>
          </div>
        </div>

        <div class="bg-clay/20 border border-clay rounded-lg p-4 mb-6">
          <p class="text-textMain text-sm">
            请保存预约码，参观时凭预约码和有效证件入馆。如需取消预约，请访问"我的预约"页面。
          </p>
        </div>

        <div class="flex space-x-4 justify-center">
          <router-link
            to="/my-booking"
            class="px-6 py-2 bg-spring text-ink rounded-lg font-medium hover:bg-springHover transition-colors"
          >
            查看我的预约
          </router-link>
          <button
            @click="resetForm"
            class="px-6 py-2 border border-line text-textMain rounded-lg hover:border-spring transition-colors"
          >
            再约一次
          </button>
        </div>
      </div>

      <!-- Navigation buttons -->
      <div v-if="currentStep < 3" class="flex justify-between mt-8">
        <button
          v-if="currentStep > 0"
          @click="previousStep"
          class="px-6 py-2 border border-line text-textMain rounded-lg hover:border-spring transition-colors"
        >
          上一步
        </button>
        <div v-else></div>
        <button
          v-if="currentStep < 2"
          @click="nextStep"
          :disabled="!canProceed"
          :class="[
            'px-6 py-2 rounded-lg font-medium transition-colors',
            canProceed
              ? 'bg-spring text-ink hover:bg-springHover'
              : 'bg-line text-textSub cursor-not-allowed'
          ]"
        >
          下一步
        </button>
        <button
          v-if="currentStep === 2"
          @click="submitBooking"
          :disabled="!canSubmit"
          :class="[
            'px-6 py-2 rounded-lg font-medium transition-colors',
            canSubmit
              ? 'bg-spring text-ink hover:bg-springHover'
              : 'bg-line text-textSub cursor-not-allowed'
          ]"
        >
          提交预约
        </button>
      </div>
    </div>
  </PublicLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { bookingApi } from '@/api/public'
import type { DayAvailability, SlotAvailability, BookingRequest, BookingResponse, BookingGuest, Slot } from '@/types'
import { validateIdCard as validateIdCardUtil, validatePhone as validatePhoneUtil } from '@/utils/validation'
import QRCode from 'qrcode'
import PublicLayout from '@/layouts/PublicLayout.vue'

const router = useRouter()

const steps = [
  { title: '选择日期', description: '请选择您希望参观的日期' },
  { title: '选择时段', description: '请选择上午或下午时段' },
  { title: '填写信息', description: '请填写预约人信息' },
  { title: '预约成功', description: '您的预约已成功提交' }
]

const currentStep = ref(0)
const availability = ref<DayAvailability[]>([])
const selectedDate = ref<DayAvailability | null>(null)
const selectedSlot = ref<SlotAvailability | null>(null)
const bookingResult = ref<BookingResponse | null>(null)
const qrCodeCanvas = ref<HTMLCanvasElement | null>(null)
const submitting = ref(false)

const bookingForm = ref<BookingRequest>({
  visitDate: '',
  slot: 'AM',
  phone: '',
  guests: [
    { type: 'PRIMARY', name: '', idCard: '' }
  ]
})

const phoneError = ref('')
const idCardErrors = ref<string[]>([''])

const canProceed = computed(() => {
  if (currentStep.value === 0) return !!selectedDate.value && selectedDate.value.isOpen
  if (currentStep.value === 1) return !!selectedSlot.value && selectedSlot.value.bookable
  return false
})

const canSubmit = computed(() => {
  return (
    !phoneError.value &&
    !idCardErrors.value.some(error => error) &&
    bookingForm.value.phone &&
    bookingForm.value.guests.every(guest => guest.name && guest.idCard)
  )
})

onMounted(async () => {
  try {
    availability.value = await bookingApi.getAvailability()
  } catch (error) {
    console.error('Failed to load availability:', error)
  }
})

const selectDate = (day: DayAvailability) => {
  if (!day.isOpen) return
  selectedDate.value = day
  bookingForm.value.visitDate = day.date
  selectedSlot.value = null
}

const selectSlot = (slot: SlotAvailability) => {
  if (!slot.bookable) return
  selectedSlot.value = slot
  bookingForm.value.slot = slot.slot
}

const nextStep = () => {
  if (currentStep.value < steps.length - 1) {
    currentStep.value++
  }
}

const previousStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const validatePhone = () => {
  const phone = bookingForm.value.phone
  if (!phone) {
    phoneError.value = ''
    return
  }
  if (!validatePhoneUtil(phone)) {
    phoneError.value = '请输入正确的手机号码'
  } else {
    phoneError.value = ''
  }
}

const validateIdCard = (index: number) => {
  const idCard = bookingForm.value.guests[index]?.idCard
  if (!idCard) {
    idCardErrors.value[index] = ''
    return
  }
  if (!validateIdCardUtil(idCard)) {
    idCardErrors.value[index] = '请输入正确的18位身份证号'
  } else {
    idCardErrors.value[index] = ''
  }
  // Check for duplicates
  checkDuplicateIdCards()
}

const checkDuplicateIdCards = () => {
  const idCards = bookingForm.value.guests.map(g => g.idCard.toUpperCase())
  const duplicates = idCards.filter((id, index) => idCards.indexOf(id) !== index)
  bookingForm.value.guests.forEach((guest, index) => {
    if (duplicates.includes(guest.idCard.toUpperCase()) && guest.idCard) {
      idCardErrors.value[index] = '身份证号不能重复'
    }
  })
}

const addGuest = () => {
  if (bookingForm.value.guests.length < 3) {
    bookingForm.value.guests.push({ type: 'COMPANION', name: '', idCard: '' })
    idCardErrors.value.push('')
  }
}

const removeGuest = (index: number) => {
  if (index > 0 && index < bookingForm.value.guests.length) {
    bookingForm.value.guests.splice(index, 1)
    idCardErrors.value.splice(index, 1)
    checkDuplicateIdCards()
  }
}

const submitBooking = async () => {
  if (!canSubmit.value || submitting.value) return

  submitting.value = true
  try {
    bookingResult.value = await bookingApi.create(bookingForm.value)
    currentStep.value = 3
    await nextTick()
    generateQRCode()
  } catch (error) {
    console.error('Failed to create booking:', error)
  } finally {
    submitting.value = false
  }
}

const generateQRCode = async () => {
  if (!qrCodeCanvas.value || !bookingResult.value) return
  try {
    await QRCode.toCanvas(qrCodeCanvas.value, bookingResult.value.code, {
      width: 200,
      margin: 2,
      color: {
        dark: '#161310',
        light: '#ffffff'
      }
    })
  } catch (error) {
    console.error('Failed to generate QR code:', error)
  }
}

const resetForm = () => {
  currentStep.value = 0
  selectedDate.value = null
  selectedSlot.value = null
  bookingResult.value = null
  bookingForm.value = {
    visitDate: '',
    slot: 'AM',
    phone: '',
    guests: [
      { type: 'PRIMARY', name: '', idCard: '' }
    ]
  }
  phoneError.value = ''
  idCardErrors.value = ['']
}

const formatDate = (dateStr: string): string => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const formatDateTime = (dateStr: string): string => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getTotalAvailable = (day: DayAvailability): number => {
  return day.slots.reduce((total, slot) => total + slot.remaining, 0)
}

const getEmojiForDay = (day: DayAvailability): string => {
  if (!day.isOpen) return '🚫'
  const totalAvailable = getTotalAvailable(day)
  if (totalAvailable > 200) return '🟢'
  if (totalAvailable > 50) return '🟡'
  return '🔴'
}
</script>