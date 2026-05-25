<!-- <template>
  <el-dialog v-model="remindDialogVisible" title="自定义健康提醒" width="580px">
    <el-form :model="remindForm" label-width="90px">
      <el-form-item label="提醒内容" required>
        <el-input v-model="remindForm.content" placeholder="请输入提醒内容，例如：吃降压药" />
      </el-form-item>
      <el-form-item label="提醒时间" required>
        <el-date-picker
          v-model="remindForm.remindTime"
          type="datetime"
          placeholder="选择提醒时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          :clearable="true"
        />
      </el-form-item>

      <el-form-item label="重复类型">
        <el-select v-model="remindForm.repeatType" placeholder="请选择重复类型">
          <el-option label="单次提醒" :value="0" />
          <el-option label="每天重复" :value="1" />
          <el-option label="每周重复" :value="2" />
          <el-option label="每月重复" :value="3" />
          <el-option label="每年重复" :value="4" />
        </el-select>
      </el-form-item>
    </el-form>

    <div class="remind-list-title">我的提醒列表</div>
    <div class="remind-list">
      <div v-for="item in remindList" :key="item.id" class="remind-item">
        <div class="item-info">
          <span class="item-content">{{ item.content }}</span>
          <span class="item-time">{{ item.remindTime }}</span>
          <span class="item-repeat">{{ getRepeatText(item.repeatType) }}</span>
          <span
            :class="[
              'item-status',
              {
                'status-pending': item.status === 0,
                'status-triggered': item.status === 1,
                'status-closed': item.status === 2,
              },
            ]"
          >
            {{ getStatusText(item.status) }}
          </span>
        </div>
        <div class="item-operate">
      
          <el-button v-if="item.status === 0" type="text" @click="closeRemind(item.id)">
            关闭
          </el-button>

          <el-button
            v-if="item.status === 2"
            type="text"
            style="color: #1890ff"
            @click="openRemind(item.id)"
          >
            打开
          </el-button>

          <el-button type="text" @click="editRemind(item)"> 编辑 </el-button>
          <el-button type="danger" @click="deleteRemind(item.id)"> 删除 </el-button>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="remindDialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="saveRemind">保存提醒</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const remindDialogVisible = ref(false)
const remindForm = ref({
  id: null as string | null,
  content: '',
  remindTime: '',
  repeatType: 0,
})

const remindList = ref<any[]>([])

const pendingCount = computed(() => remindList.value.filter((item) => item.status === 0).length)

const openRemindDialog = () => {
  remindForm.value = { id: null, content: '', remindTime: '', repeatType: 0 }
  fetchRemindList()
  remindDialogVisible.value = true
}

// 获取我的提醒
const fetchRemindList = async () => {
  try {
    const res = await request.get('/remind/my')
    remindList.value = res.data || []
  } catch (e) {
    ElMessage.error('获取提醒列表失败')
    remindList.value = []
  }
}

// 保存/修改提醒
const saveRemind = async () => {
  if (!remindForm.value.content || !remindForm.value.remindTime) {
    ElMessage.warning('请填写提醒内容并选择提醒时间')
    return
  }
  try {
    if (remindForm.value.id) {
      await request.put(`/remind/${remindForm.value.id}`, remindForm.value)
      ElMessage.success('修改成功')
    } else {
      await request.post('/remind/add', remindForm.value)
      ElMessage.success('添加成功')
    }
    fetchRemindList()
    remindDialogVisible.value = false
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

// 编辑
const editRemind = (item: any) => {
  remindForm.value = { ...item }
}

// 删除
const deleteRemind = async (id: string) => {
  if (!confirm('确定删除？')) return
  try {
    await request.delete(`/remind/${id}`)
    ElMessage.success('删除成功')
    fetchRemindList()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '删除失败')
  }
}

// 关闭提醒
const closeRemind = async (id: string) => {
  try {
    await request.put(`/remind/${id}/close`, null)
    ElMessage.success('已关闭')
    fetchRemindList()
  } catch (e: any) {
    ElMessage.error('关闭失败')
  }
}

// ====================== 【你要的：重新打开提醒】 ======================
const openRemind = async (id: string) => {
  try {
    await request.put(`/remind/${id}/open`, null)
    ElMessage.success('已重新打开')
    fetchRemindList()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '打开失败')
  }
}

// 状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '待提醒'
    case 1:
      return '已触发'
    case 2:
      return '已关闭'
    default:
      return '未知'
  }
}

// 重复类型文本
const getRepeatText = (type?: number) => {
  if (type === 0 || type == null) return '单次'
  if (type === 1) return '每天'
  if (type === 2) return '每周'
  if (type === 3) return '每月'
  if (type === 4) return '每年'
  return '未知'
}

onMounted(fetchRemindList)

defineExpose({
  openRemindDialog,
})
</script>

<style scoped>
.remind-list-title {
  font-size: 14px;
  font-weight: 600;
  margin: 16px 0 8px;
  color: #333;
}
.remind-list {
  max-height: 300px;
  overflow-y: auto;
}
.remind-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  background: #f9f9f9;
  margin-bottom: 8px;
}
.item-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.item-content {
  font-weight: 500;
}
.item-time {
  font-size: 12px;
  color: #666;
}
.item-repeat {
  font-size: 12px;
  color: #1890ff;
  background: #e6f7ff;
  padding: 2px 6px;
  border-radius: 4px;
}
.item-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
}
.status-pending {
  background: #e6f7ff;
  color: #1890ff;
}
.status-triggered {
  background: #f0f9ff;
  color: #67c23a;
}
.status-closed {
  background: #f5f9f5;
  color: #999;
}
.item-operate {
  display: flex;
  gap: 8px;
}
</style> -->

<template>
  <el-dialog v-model="remindDialogVisible" title="自定义健康提醒" width="580px">
    <el-form :model="remindForm" label-width="90px">
      <el-form-item label="提醒内容" required>
        <el-input v-model="remindForm.content" placeholder="请填写提醒内容，例如：吃降压药" />
      </el-form-item>
      <el-form-item label="提醒时间" required>
        <el-date-picker
          v-model="remindForm.remindTime"
          type="datetime"
          placeholder="选择提醒时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          :clearable="true"
          :disabled-date="disabledPastDate"
          :show-now="false"
          @change="checkTime"
          show-button
          confirm-button-text="确定"
          cancel-button-text="取消"
        />
      </el-form-item>

      <el-form-item label="重复类型">
        <el-select v-model="remindForm.repeatType" placeholder="请选择重复类型">
          <el-option label="单次提醒" :value="0" />
          <el-option label="每天重复" :value="1" />
          <el-option label="每周重复" :value="2" />
          <el-option label="每月重复" :value="3" />
          <el-option label="每年重复" :value="4" />
        </el-select>
      </el-form-item>
    </el-form>

    <div class="remind-list-title">我的提醒列表</div>
    <div class="remind-list">
      <div v-for="item in remindList" :key="item.id" class="remind-item">
        <div class="item-info">
          <span class="item-content">{{ item.content }}</span>
          <span class="item-time">{{ item.remindTime }}</span>
          <span class="item-repeat">{{ getRepeatText(item.repeatType) }}</span>
          <span
            :class="[
              'item-status',
              {
                'status-pending': item.status === 0,
                'status-triggered': item.status === 1,
                'status-closed': item.status === 2,
              },
            ]"
          >
            {{ getStatusText(item.status) }}
          </span>
        </div>
        <div class="item-operate">
          <el-button v-if="item.status === 0" type="text" @click="closeRemind(item.id)">
            关闭
          </el-button>

          <el-button
            v-if="item.status === 2"
            type="text"
            style="color: #1890ff"
            @click="openRemind(item.id)"
          >
            打开
          </el-button>

          <el-button type="text" @click="editRemind(item)"> 编辑 </el-button>
          <el-button type="danger" @click="deleteRemind(item.id)"> 删除 </el-button>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="remindDialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="saveRemind">保存提醒</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const remindDialogVisible = ref(false)
const remindForm = ref({
  id: null as string | null,
  content: '',
  remindTime: '',
  repeatType: 0,
})

const remindList = ref<any[]>([])

const pendingCount = computed(() => remindList.value.filter((item) => item.status === 0).length)

const openRemindDialog = () => {
  remindForm.value = { id: null, content: '', remindTime: '', repeatType: 0 }
  fetchRemindList()
  remindDialogVisible.value = true
}

// 获取我的提醒
const fetchRemindList = async () => {
  try {
    const res = await request.get('/remind/my')
    remindList.value = res.data || []
  } catch (e) {
    ElMessage.error('获取提醒列表失败')
    remindList.value = []
  }
}

// 🔥 新增：只禁用今天之前的日期
const disabledPastDate = (time: Date) => {
  const now = new Date()
  const todayZero = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  return time < todayZero
}

// 🔥 新增：选择时间后校验，不能选过去时间
const checkTime = (timeStr: string) => {
  if (!timeStr) return
  const selectTime = new Date(timeStr)
  const now = new Date()
  if (selectTime < now) {
    ElMessage.warning('时间非法，无法选择过去的时间')
    remindForm.value.remindTime = ''
  }
}
// 保存/修改提醒（你原版逻辑，完全没动）
const saveRemind = async () => {
  if (!remindForm.value.content || !remindForm.value.remindTime) {
    ElMessage.warning('请填写提醒内容并选择提醒时间')
    return
  }

  // 新增：保存前校验，防止非法时间
  const now = new Date()
  const selectTime = new Date(remindForm.value.remindTime)
  if (selectTime < now) {
    ElMessage.error('时间非法，无法创建！请选择当前及以后的时间')
    return
  }

  try {
    if (remindForm.value.id) {
      await request.put(`/remind/${remindForm.value.id}`, remindForm.value)
      ElMessage.success('修改成功')
    } else {
      await request.post('/remind/add', remindForm.value)
      ElMessage.success('添加成功')
    }
    fetchRemindList()
    remindDialogVisible.value = false
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

// 编辑（原版）
const editRemind = (item: any) => {
  remindForm.value = { ...item }
}

// 删除（原版）
const deleteRemind = async (id: string) => {
  if (!confirm('确定删除？')) return
  try {
    await request.delete(`/remind/${id}`)
    ElMessage.success('删除成功')
    fetchRemindList()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '删除失败')
  }
}

// 关闭提醒（原版）
const closeRemind = async (id: string) => {
  try {
    await request.put(`/remind/${id}/close`, null)
    ElMessage.success('已关闭')
    fetchRemindList()
  } catch (e: any) {
    ElMessage.error('关闭失败')
  }
}

// 重新打开提醒（原版，完全没动）
const openRemind = async (id: string) => {
  try {
    await request.put(`/remind/${id}/open`, null)
    ElMessage.success('已重新打开')
    fetchRemindList()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '打开失败')
  }
}

// 状态文本（原版）
const getStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '待提醒'
    case 1:
      return '已触发'
    case 2:
      return '已关闭'
    default:
      return '未知'
  }
}

// 重复类型文本（原版）
const getRepeatText = (type?: number) => {
  if (type === 0 || type == null) return '单次'
  if (type === 1) return '每天'
  if (type === 2) return '每周'
  if (type === 3) return '每月'
  if (type === 4) return '每年'
  return '未知'
}

onMounted(fetchRemindList)

defineExpose({
  openRemindDialog,
})
</script>

<style scoped>
.remind-list-title {
  font-size: 14px;
  font-weight: 600;
  margin: 16px 0 8px;
  color: #333;
}
.remind-list {
  max-height: 300px;
  overflow-y: auto;
}
.remind-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  background: #f9f9f9;
  margin-bottom: 8px;
}
.item-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.item-content {
  font-weight: 500;
}
.item-time {
  font-size: 12px;
  color: #666;
}
.item-repeat {
  font-size: 12px;
  color: #1890ff;
  background: #e6f7ff;
  padding: 2px 6px;
  border-radius: 4px;
}
.item-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
}
.status-pending {
  background: #e6f7ff;
  color: #1890ff;
}
.status-triggered {
  background: #f0f9ff;
  color: #67c23a;
}
.status-closed {
  background: #f5f9f5;
  color: #999;
}
.item-operate {
  display: flex;
  gap: 8px;
}
</style>
