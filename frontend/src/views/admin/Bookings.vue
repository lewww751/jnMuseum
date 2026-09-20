<template>
  <div class="p-6">
    <h1 class="text-xl font-bold mb-4">预约管理</h1>

    <el-form inline class="mb-4">
      <el-form-item label="参观日期">
        <el-date-picker v-model="query.visitDate" type="date" value-format="YYYY-MM-DD" clearable />
      </el-form-item>
      <el-form-item label="时段">
        <el-select v-model="query.slot" clearable style="width: 110px">
          <el-option label="上午" value="AM" />
          <el-option label="下午" value="PM" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable style="width: 110px">
          <el-option label="有效" value="ACTIVE" />
          <el-option label="已取消" value="CANCELLED" />
          <el-option label="已入馆" value="CHECKED_IN" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" placeholder="预约码/姓名/证件号/手机号" clearable style="width: 220px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="records" v-loading="loading" border>
      <el-table-column prop="code" label="预约码" width="100" />
      <el-table-column label="参观日期" width="150">
        <template #default="{ row }">{{ row.visitDate }}（{{ row.weekday }}）</template>
      </el-table-column>
      <el-table-column prop="slotLabel" label="时段" width="130" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : row.status === 'CANCELLED' ? 'info' : 'warning'">
            {{ row.statusLabel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="入馆人" min-width="200">
        <template #default="{ row }">
          {{ row.guests.map((g: BookingGuestView) => `${g.name}(${g.idCardMasked})`).join('、') }}
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row)">详情</el-button>
          <el-button
            size="small"
            type="primary"
            :disabled="!canCheckIn(row)"
            @click="checkIn(row)"
          >
            核销
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="mt-4 justify-end"
      layout="total, prev, pager, next"
      :total="total"
      :page-size="query.size"
      :current-page="query.page"
      @current-change="onPageChange"
    />

    <el-drawer v-model="detailVisible" title="预约单详情" size="420px">
      <template v-if="detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="预约码">{{ detail.code }}</el-descriptions-item>
          <el-descriptions-item label="参观日期">{{ detail.visitDate }}（{{ detail.weekday }}）</el-descriptions-item>
          <el-descriptions-item label="时段">{{ detail.slotLabel }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detail.statusLabel }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detail.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="取消截止">{{ detail.cancelDeadline }}</el-descriptions-item>
        </el-descriptions>
        <h3 class="mt-4 mb-2 font-semibold">入馆人</h3>
        <el-table :data="detail.guests" size="small" border>
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="idCardMasked" label="证件号" />
          <el-table-column label="类型" width="80">
            <template #default="{ row }">{{ row.type === 'PRIMARY' ? '主预约人' : '同行人' }}</template>
          </el-table-column>
        </el-table>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminBookingApi } from '@/api/admin'
import type { BookingView, BookingGuestView } from '@/types'

const records = ref<BookingView[]>([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const detail = ref<BookingView | null>(null)

const query = reactive({
  visitDate: undefined as string | undefined,
  slot: undefined as 'AM' | 'PM' | undefined,
  status: undefined as 'ACTIVE' | 'CANCELLED' | 'CHECKED_IN' | undefined,
  keyword: undefined as string | undefined,
  page: 1,
  size: 10
})

const load = async () => {
  loading.value = true
  try {
    const result = await adminBookingApi.getList({ ...query })
    records.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.page = 1
  load()
}

const onPageChange = (page: number) => {
  query.page = page
  load()
}

const showDetail = (row: BookingView) => {
  detail.value = row
  detailVisible.value = true
}

const canCheckIn = (row: BookingView) =>
  row.status === 'ACTIVE' && row.visitDate === new Date().toISOString().slice(0, 10)

const checkIn = async (row: BookingView) => {
  await ElMessageBox.confirm(
    `确认核销预约码 ${row.code}？核销后整单标记为已入馆。`,
    '核销确认',
    { type: 'warning' }
  )
  await adminBookingApi.checkIn(row.code)
  ElMessage.success('已核销')
  await load()
}

onMounted(load)
</script>
