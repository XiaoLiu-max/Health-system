<template>
  <!-- 1. 最外层：绿色背景（和 Home 一致） -->
  <div class="message-page">
    <!-- 2. 中间层：完整的白色容器（把所有内容包起来） -->
    <div class="message-container">
      <!-- 左侧边栏 -->
      <div class="sidebar-left">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索ID/用户名加好友"
            clearable
            @keyup.enter="handleSearchUser"
          >
            <template #append>
              <el-button @click="handleSearchUser">
                <svg style="width: 1em; height: 1em" viewBox="0 0 1024 1024" fill="currentColor">
                  <path
                    d="M909.6 854.8L669.1 614.3c51.6-62.8 82.8-143.6 82.8-234.3 0-209.1-169.1-378.2-378.2-378.2s-378.2 169.1-378.2 378.2 169.1 378.2 378.2 378.2c90.7 0 171.5-31.2 234.3-82.8l240.5 240.5c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3zM373.7 651.3c-152.5 0-276.2-123.7-276.2-276.2s123.7-276.2 276.2-276.2 276.2 123.7 276.2 276.2-123.7 276.2-276.2 276.2z"
                  ></path>
                </svg>
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="divider"></div>

        <div
          class="list-item system-item"
          :class="{ active: activeType === 'system' }"
          @click="activeType = 'system'"
        >
          🔔 系统通知
        </div>
        <div class="divider"></div>

        <div
          v-for="f in friendList"
          :key="f.friendId"
          class="list-item friend-item"
          :class="{ active: currentFriend?.friendId === f.friendId && activeType === 'chat' }"
          @click="selectFriend(f)"
        >
          <div class="name">
            {{ f.remark ? f.remark : f.username || '用户' + f.friendId }}
            <span v-if="f.status === 0" class="black-tag">已拉黑</span>
          </div>
          <div class="status">
            <span :class="f.onlineStatus === 1 ? 'online' : 'offline'">
              {{ f.onlineStatus === 1 ? '在线' : '离线' }}
            </span>

            <el-badge
              :value="f.unread ?? 0"
              v-if="(f.unread ?? 0) > 0"
              type="danger"
              style="margin-left: 6px; transform: scale(0.8)"
            />
          </div>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="content-right">
        <div v-if="activeType === 'system'" class="notice-list">
          <!-- 🔥 这里加了 is-warning 判断异常消息 -->
          <div
            v-for="item in systemMessages"
            :key="item.id"
            class="notice-item"
            :class="{
              'is-warning': item.type === 4,
              'apply-notice': item.type === 1,
            }"
            @click="handleNoticeClick(item)"
          >
            <div class="notice-title">
              {{ item.content }}
              <span v-if="item.type === 1" class="apply-tag">好友申请</span>
              <span v-if="item.type === 4" class="warning-tag">异常提醒</span>
              <span
                v-if="item.type === 1"
                class="status-tag"
                :class="getApplyStatusClass(item.fromUid)"
              >
                {{ getApplyStatusText(item.fromUid) }}
              </span>
            </div>
            <div class="notice-time">{{ formatTime(item.createTime) }}</div>
          </div>
        </div>

        <div v-if="activeType === 'chat' && currentFriend" class="chat-right">
          <div class="chat-header">
            <span>
              {{
                currentFriend.remark
                  ? currentFriend.remark
                  : currentFriend.username || '用户' + currentFriend.friendId
              }}
              <span v-if="currentFriend.status === 0" class="black-tag">已拉黑</span>
              <span v-if="blockType === 2" class="black-tag">被对方拉黑</span>
            </span>

            <el-dropdown trigger="click" @command="handleFriendMenu">
              <el-button type="text" style="font-size: 22px; padding: 0 10px">⋯</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="remark">修改备注</el-dropdown-item>
                  <el-dropdown-item v-if="blockType === 0 || blockType === 1" command="black">
                    {{ blockType === 1 ? '解除拉黑' : '拉黑' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除好友</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <div class="chat-body" ref="chatBodyRef">
            <div v-if="blockType === 1" class="blocked-tip">❌ 你已拉黑对方，无法发送消息</div>
            <div v-if="blockType === 2" class="blocked-tip">❌ 你已被对方拉黑，无法发送消息</div>
            <div
              v-for="msg in msgList"
              :key="msg.id"
              class="msg"
              :class="msg.fromUid === currentLoginUserId ? 'self' : 'other'"
              @mouseenter="hoverMsgId = msg.id"
              @mouseleave="hoverMsgId = null"
            >
              <div class="c">
                <span v-if="msg.isRecall === 1">【消息已撤回】</span>
                <span v-else>{{ msg.content }}</span>
              </div>
              <div class="t">{{ formatTime(msg.createTime) }}</div>
              <div
                v-if="
                  hoverMsgId === msg.id && msg.fromUid === currentLoginUserId && msg.isRecall !== 1
                "
                class="recall-btn"
                @click="handleRecall(msg.id)"
              >
                撤回
              </div>
            </div>
          </div>
          <div class="chat-input">
            <el-input
              v-model="inputMsg"
              type="textarea"
              :autosize="{ minRows: 1, maxRows: 4 }"
              placeholder="输入消息..."
              @keyup.enter="sendMsg"
              :disabled="blockType !== 0"
            />
            <el-button type="primary" @click="sendMsg" :disabled="blockType !== 0">发送</el-button>
          </div>
        </div>

        <div class="empty" v-if="activeType === 'chat' && !currentFriend">请选择好友开始聊天</div>
      </div>

      <el-dialog v-model="searchDialogVisible" width="360px" title="找到用户">
        <div class="search-user-card">
          <el-avatar :size="60">{{ searchUserInfo.username?.[0] || 'U' }}</el-avatar>
          <div class="user-info">
            <div class="info-row">
              <span class="label">用户ID：</span>
              <span>{{ searchUserInfo.id }}</span>
            </div>
            <div class="info-row">
              <span class="label">用户名：</span>
              <span>{{ searchUserInfo.username }}</span>
            </div>
          </div>
          <el-button type="primary" @click="sendFriendApply">发送好友申请</el-button>
        </div>
      </el-dialog>

      <el-dialog v-model="applyHandleDialogVisible" width="360px" title="处理好友申请">
        <div class="search-user-card">
          <el-avatar :size="60">{{ applyUserInfo.username?.[0] || 'U' }}</el-avatar>
          <div class="user-info">
            <div class="info-row">
              <span class="label">用户ID：</span>
              <span>{{ applyUserInfo.id }}</span>
            </div>
            <div class="info-row">
              <span class="label">用户名：</span>
              <span>{{ applyUserInfo.username }}</span>
            </div>
          </div>
        </div>

        <div style="margin: 10px 0; text-align: center">
          <span style="color: #666">当前状态：{{ applyStatusText }}</span>
        </div>

        <div class="apply-btns">
          <el-button type="success" @click="agreeApply" :disabled="currentApplyStatus !== 0"
            >同意</el-button
          >
          <el-button type="danger" @click="refuseApply" :disabled="currentApplyStatus !== 0"
            >拒绝</el-button
          >
        </div>
      </el-dialog>

      <el-dialog v-model="remarkDialogVisible" title="修改备注" width="350px">
        <el-input v-model="editRemarkText" placeholder="请输入备注"></el-input>
        <template #footer>
          <el-button @click="remarkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="doUpdateRemark">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()

interface Friend {
  friendId: number
  username?: string
  remark: string
  status: number
  onlineStatus: number
  lastTime?: string
  unread?: number
}

interface Message {
  id: string
  fromUid: number
  toUid: number
  content: string
  type: number
  isRead: number
  createTime: string
  userId?: number
  isRecall?: number
  recallTime?: string
}
const currentLoginUserId = ref<number>(0)
const activeType = ref<'system' | 'chat'>('system')
const friendList = ref<Friend[]>([])
const currentFriend = ref<Friend | null>(null)
const msgList = ref<Message[]>([])
const inputMsg = ref('')
const chatBodyRef = ref<HTMLElement | null>(null)
const systemMessages = ref<Message[]>([])

const blockType = ref<number>(0)

const searchKeyword = ref('')
const searchDialogVisible = ref(false)
const searchUserInfo = ref({ id: 0, username: '' })

const applyHandleDialogVisible = ref(false)
const applyUserInfo = ref({ id: 0, username: '' })
const currentApplyMsgId = ref<number>(0)
const currentApplyStatus = ref<number>(0)
const allAppliesCache = ref<any[]>([])

const remarkDialogVisible = ref(false)
const editRemarkText = ref('')

const hoverMsgId = ref<string | number | null>(null)

let onlineTimer: any = null

const getUnreadCount = async () => {
  try {
    const res = await request.get('/message/unread/count')
    if ((window as any).appUnread) {
      ;(window as any).appUnread.value = res.data || 0
    }
  } catch (err) {
    console.log('刷新未读数失败', err)
  }
}

const applyStatusText = computed(() => {
  if (currentApplyStatus.value === 0) return '待处理'
  if (currentApplyStatus.value === 1) return '已同意'
  if (currentApplyStatus.value === 2) return '已拒绝'
  return '未知'
})

const getApplyStatusText = (fromUid: number) => {
  const found = allAppliesCache.value.find((a) => a.fromUserId == fromUid)
  if (!found) return '无'
  if (found.status === 0) return '待处理'
  if (found.status === 1) return '已同意'
  if (found.status === 2) return '已拒绝'
  return '未知'
}

const getApplyStatusClass = (fromUid: number) => {
  const found = allAppliesCache.value.find((a) => a.fromUserId == fromUid)
  if (!found) return 'status-none'
  if (found.status === 0) return 'status-wait'
  if (found.status === 1) return 'status-success'
  if (found.status === 2) return 'status-danger'
  return 'status-none'
}

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }

  try {
    const tokenPayload = JSON.parse(atob(token.split('.')[1]))
    currentLoginUserId.value = Number(tokenPayload.sub)
  } catch (e) {
    ElMessage.error('登录异常，请重新登录')
    router.push('/login')
    return
  }

  await fetchFriendList()
  await fetchSystemMsg()
  await loadAllApplies()

  onlineTimer = setInterval(() => {
    fetchFriendList()
  }, 10000)
})

onUnmounted(() => {
  if (onlineTimer) clearInterval(onlineTimer)
})

const fetchFriendList = async () => {
  try {
    const res = await request.get('/friend/list')
    let list = res.data || []

    list = list.map((f: any) => ({
      ...f,
      remark: f.remark?.trim() || null,
    }))

    for (let f of list) {
      try {
        const ur = await request.get('/user/search', { params: { keyword: f.friendId } })
        if (ur.data?.username) {
          f.username = ur.data.username
        }
      } catch (e) {}

      try {
        const unreadRes = await request.get('/message/unread/friend', {
          params: { friendId: f.friendId },
        })
        f.unread = unreadRes.data || 0
      } catch (e) {
        f.unread = 0
      }
    }

    friendList.value = list
  } catch (e) {
    console.error(e)
  }
}

const fetchSystemMsg = async () => {
  try {
    const res = await request.get('/message/list')
    const all = res.data || []
    systemMessages.value = all.filter((m: any) => m.type !== 2)
  } catch (e) {
    console.error('❌ 请求消息失败：', e)
  }
}

const loadAllApplies = async () => {
  try {
    const res = await request.get('/friendApply/getMyApplyList')
    allAppliesCache.value = res.data || []
  } catch (e) {}
}

const fetchMsgList = async (friendId: number) => {
  try {
    const res = await request.get('/message/list')
    const all = res.data || []
    msgList.value = all
      .filter(
        (item: any) =>
          item.type === 2 &&
          ((item.fromUid === currentLoginUserId.value && item.toUid === friendId) ||
            (item.fromUid === friendId && item.toUid === currentLoginUserId.value)),
      )
      .sort((a: any, b: any) => new Date(a.createTime).getTime() - new Date(b.createTime).getTime())
    await nextTick()
    if (chatBodyRef.value) chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
  } catch (e) {}
}

const getBlockType = async (friendId: number) => {
  try {
    const res = await request.get('/friend/blockType', { params: { friendId } })
    const data = res as any
    blockType.value = data.data
  } catch (e) {
    blockType.value = 0
  }
}

const sendMsg = async () => {
  if (!inputMsg.value || !currentFriend.value) return
  if (blockType.value !== 0) {
    ElMessage.error('已拉黑无法发送')
    return
  }
  try {
    await request.post('/message/test/send/chat', null, {
      params: { toUid: currentFriend.value.friendId, content: inputMsg.value },
    })
    inputMsg.value = ''
    setTimeout(() => fetchMsgList(currentFriend.value!.friendId), 300)
  } catch (e) {}
}

const selectFriend = async (f: Friend) => {
  activeType.value = 'chat'
  currentFriend.value = f
  await fetchMsgList(f.friendId)
  await getBlockType(f.friendId)

  try {
    await request.post(`/message/read/user/${f.friendId}`)
    f.unread = 0
    await getUnreadCount()
    await fetchFriendList()
  } catch (e) {}
}

const toggleBlack = async () => {
  if (!currentFriend.value) return
  const fid = currentFriend.value.friendId
  const willBlack = blockType.value === 0
  const url = willBlack ? '/friend/black/add' : '/friend/black/cancel'

  await request.post(url, null, { params: { friendId: fid } })
  ElMessage.success(willBlack ? '已拉黑' : '已解除拉黑')

  blockType.value = willBlack ? 1 : 0
  await fetchFriendList()
}

const deleteFriend = async () => {
  if (!currentFriend.value || !confirm('确定删除？')) return
  await request.post('/friend/delete', null, { params: { friendId: currentFriend.value.friendId } })
  friendList.value = friendList.value.filter((x) => x.friendId !== currentFriend.value!.friendId)
  currentFriend.value = null
  ElMessage.success('删除成功')
}

const formatTime = (time: string) => new Date(time).toLocaleString()

const handleSearchUser = async () => {
  if (!searchKeyword.value) return
  try {
    const res = (await request.get('/user/search', {
      params: { keyword: searchKeyword.value },
    })) as any
    if (res.code === 200) {
      searchUserInfo.value = res.data
      searchDialogVisible.value = true
    } else {
      ElMessage.warning(res.msg || '未找到该用户')
    }
  } catch (e: any) {
    console.error(e)
    ElMessage.error(e.response?.data?.msg || '搜索失败')
  }
}

const sendFriendApply = async () => {
  try {
    const res = (await request.post('/friendApply/send', {
      toUserId: searchUserInfo.value.id,
    })) as any
    if (res.code === 200) {
      ElMessage.success(res.msg || '申请已发送')
      searchDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '发送失败')
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '发送失败')
  }
}

const handleNoticeClick = async (item: Message) => {
  try {
    await request.post('/message/read', null, {
      params: { msgId: item.id },
    })
    await getUnreadCount()
    await fetchSystemMsg()
  } catch (e) {}

  if (item.type === 5) {
    router.push({
      path: '/friend-report',
      query: { userId: item.fromUid },
    })
    return
  }
  if (item.type !== 1) return
  try {
    const res = await request.get('/friendApply/getMyApplyList')
    const target = res.data?.[0]
    if (target) {
      currentApplyMsgId.value = target.applyId
      currentApplyStatus.value = target.status
    }
    const userRes = await request.get('/user/search', {
      params: { keyword: item.fromUid },
    })
    applyUserInfo.value = userRes.data
    applyHandleDialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取申请信息失败')
  }
}

const agreeApply = async () => {
  try {
    const res = (await request.post('/friendApply/handle', null, {
      params: { applyId: currentApplyMsgId.value, status: 1 },
    })) as any
    ElMessage.success(res.msg || '已同意')
    applyHandleDialogVisible.value = false
    await loadAllApplies()
    await fetchFriendList()
    await fetchSystemMsg()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '操作失败')
  }
}

const refuseApply = async () => {
  try {
    const res = (await request.post('/friendApply/handle', null, {
      params: { applyId: currentApplyMsgId.value, status: 2 },
    })) as any
    ElMessage.success(res.msg || '已拒绝')
    applyHandleDialogVisible.value = false
    await loadAllApplies()
    await fetchFriendList()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.msg || '操作失败')
  }
}

const handleFriendMenu = async (cmd: string) => {
  if (!currentFriend.value) return
  if (cmd === 'remark') {
    editRemarkText.value = currentFriend.value.remark || ''
    remarkDialogVisible.value = true
  }
  if (cmd === 'black') await toggleBlack()
  if (cmd === 'delete') await deleteFriend()
}

const doUpdateRemark = async () => {
  if (!currentFriend.value) return
  await request.post('/friend/remark/update', null, {
    params: { friendId: currentFriend.value.friendId, remark: editRemarkText.value },
  })
  ElMessage.success('修改成功')
  remarkDialogVisible.value = false
  fetchFriendList()
}

const handleRecall = async (msgId: string) => {
  if (!confirm('确定撤回？')) return
  await request.post('/message/recall/ultimate')
  ElMessage.success('撤回成功')
  if (currentFriend.value) fetchMsgList(currentFriend.value.friendId)
}
</script>

<style scoped>
/* 最外层：铺满绿色背景 */
.message-page {
  background: #edf7f0;
  padding: 0;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 白色容器：100% 占满屏幕 */
.message-container {
  display: flex;
  height: 100vh;
  background: #fff;
  border-radius: 0;
  overflow: hidden;
  box-shadow: none;
}

/* 左侧边栏 */
.sidebar-left {
  width: 200px;
  background: #f9f9f9;
  border-right: 1px solid #eee;
  overflow-y: auto;
}
.search-box {
  padding: 12px;
}
.divider {
  height: 1px;
  background: #eee;
  margin: 4px 0;
}
.list-item {
  padding: 14px 16px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.list-item.active {
  background: #e6f7ff;
  color: #1890ff;
  font-weight: bold;
}
.system-item {
  font-weight: bold;
}
.status {
  font-size: 12px;
}
.online {
  color: #00b42a;
}
.offline {
  color: #999;
}

/* 右侧内容区 */
.content-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
}

/* 系统通知 - 默认白色 */
.notice-list {
  padding: 12px;
  flex: 1;
  overflow-y: auto;
  background: #fff;
}
.notice-item {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  background: #ffffff;
}
.notice-item:hover {
  background: #f5f5f5;
}

/* 🔥 异常提醒 → 只让 type=3 变红 */
.notice-item.is-warning {
  background: #fff1f0 !important;
  border-left: 4px solid #f56c6c !important;
}

/* 好友申请通知 */
.apply-notice {
  background: #fff7e6;
}
.apply-tag {
  margin-left: 8px;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #ff7d00;
  color: #fff;
}
.warning-tag {
  margin-left: 8px;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f56c6c;
  color: #fff;
}
.status-tag {
  margin-left: 8px;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  color: #fff;
}
.status-wait {
  background: #409eff;
}
.status-success {
  background: #67c23a;
}
.status-danger {
  background: #f56c6c;
}
.status-none {
  background: #909399;
}
.notice-title {
  font-weight: 500;
}
.notice-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* 聊天布局 */
.chat-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
}
.chat-header {
  padding: 14px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  background: #fff;
}
.chat-body {
  flex: 1;
  padding: 16px;
  background: #fafafa;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.chat-input {
  margin-top: auto;
  display: flex;
  gap: 10px;
  padding: 12px;
  border-top: 1px solid #eee;
  flex-shrink: 0;
  background: #fff;
}

/* 消息气泡 */
.msg {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 8px;
  position: relative;
}
.msg.self {
  margin-left: auto;
  background: #67c23a;
  color: #fff;
}
.msg.other {
  margin-right: auto;
  background: #fff;
  border: 1px solid #eee;
}
.c {
  margin-bottom: 4px;
}
.t {
  font-size: 11px;
  color: #ccc;
}

.empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  background: #fff;
}

.search-user-card {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.info-row {
  display: flex;
  align-items: center;
}
.label {
  width: 70px;
  color: #666;
}
.apply-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
}

.black-tag {
  margin-left: 6px;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f56c6c;
  color: #fff;
}
.blocked-tip {
  text-align: center;
  color: #f56c6c;
  padding: 10px;
  font-weight: bold;
  background: #fef0f0;
  border-radius: 8px;
}
.recall-btn {
  position: absolute;
  right: 8px;
  top: -22px;
  font-size: 12px;
  color: #999;
  cursor: pointer;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 4px;
}
.recall-btn:hover {
  color: #f56c6c;
}
</style>
