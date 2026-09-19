<template>
  <div class="booking">
    <h1>门票预约</h1>
    <div class="booking-form">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="参观日期" prop="visitDate">
          <el-date-picker v-model="form.visitDate" type="date" placeholder="选择参观日期" />
        </el-form-item>
        
        <el-form-item label="参观时段" prop="slot">
          <el-radio-group v-model="form.slot">
            <el-radio label="AM">上午 (9:00-12:00)</el-radio>
            <el-radio label="PM">下午 (13:00-16:30)</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="入馆人信息">
          <div v-for="(guest, index) in form.guests" :key="index" class="guest-item">
            <el-form-item label="姓名" :prop="'guests.' + index + '.name'" :rules="rules.name">
              <el-input v-model="guest.name" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="证件号" :prop="'guests.' + index + '.idCard'" :rules="rules.idCard">
              <el-input v-model="guest.idCard" placeholder="请输入身份证号" />
            </el-form-item>
            <el-button @click="removeGuest(index)" type="danger" size="small">删除</el-button>
          </div>
          <el-button @click="addGuest" type="primary" size="small">添加同行人</el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交预约</el-button>
        </el-form-item>
      </el-form>
      
      <div v-if="booking" class="booking-result">
        <h3>预约成功！</h3>
        <p>预约码：{{ booking.code }}</p>
        <p>参观日期：{{ booking.visitDate }}</p>
        <p>参观时段：{{ booking.slot === 'AM' ? '上午 (9:00-12:00)' : '下午 (13:00-16:30)' }}</p>
        <p>预约时间：{{ booking.createdAt }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { createBooking } from '../api/booking';
import { ElMessage } from 'element-plus';

const formRef = ref();
const booking = ref<any>(null);

const form = reactive({
  visitDate: '',
  slot: 'AM',
  phone: '',
  guests: [
    {
      name: '',
      idCard: ''
    }
  ]
});

const rules = {
  visitDate: [
    { required: true, message: '请选择参观日期', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { validator: (rule: any, value: string, callback: any) => {
      if (!value) {
        callback();
        return;
      }
      if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/.test(value)) {
        callback(new Error('请输入正确的身份证号'));
      } else {
        callback();
      }
    }, trigger: 'blur' }
  ]
};

const addGuest = () => {
  if (form.guests.length >= 3) {
    ElMessage.warning('最多添加2名同行人');
    return;
  }
  form.guests.push({
    name: '',
    idCard: ''
  });
};

const removeGuest = (index: number) => {
  if (form.guests.length > 1) {
    form.guests.splice(index, 1);
  }
};

const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    const guests = form.guests.map(guest => ({
      name: guest.name,
      idCard: guest.idCard
    }));
    
    const response = await createBooking({
      phone: form.phone,
      visitDate: form.visitDate,
      slot: form.slot,
      guests
    });
    
    booking.value = response.data;
    ElMessage.success('预约成功！');
    
  } catch (error: any) {
    ElMessage.error(error.message || '预约失败，请重试');
  }
};
</script>

<style scoped>
.booking {
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
}

.booking-form {
  margin-top: 2rem;
}

.guest-item {
  margin-bottom: 1rem;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.booking-result {
  margin-top: 2rem;
  padding: 1rem;
  background-color: #f0f9eb;
  border-radius: 4px;
  border-left: 4px solid #67c23a;
}

.booking-result h3 {
  color: #67c23a;
  margin-bottom: 1rem;
}
</style>