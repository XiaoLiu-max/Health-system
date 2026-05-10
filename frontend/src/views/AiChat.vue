<!-- <template>
  <div class="ai-chat-page">
    <div class="chat-header">
      <h2>AI 健康助手</h2>
      <p>有任何健康、养生、饮食问题都可以问我</p>
    </div>

    <div class="chat-body" ref="chatBody">
      <div class="message ai-message" v-if="messages.length === 0">
        <div class="avatar">AI</div>
        <div class="bubble">你好！我是你的 AI 健康助手，有什么可以帮你的？</div>
      </div>

      <div
        class="message"
        :class="[item.isSelf ? 'self-message' : 'ai-message']"
        v-for="(item, index) in messages"
        :key="index"
      >
        <div class="avatar" v-if="!item.isSelf">AI</div>
        <div class="bubble">{{ item.content }}</div>
      </div>


      <div class="loading" v-if="loading">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
      </div>
    </div>

    <div class="chat-footer">
      <input
        v-model="inputText"
        type="text"
        placeholder="请输入你的问题..."
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage" :disabled="loading">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'

const messages = ref([])
const inputText = ref('')
const chatBody = ref(null)
const loading = ref(false)

const sendMessage = async () => {
  if (!inputText.value.trim()) return

  const userMsg = inputText.value
  messages.value.push({ content: userMsg, isSelf: true })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await axios.post('/ai/chat', {
      question: userMsg,
    })
    messages.value.push({
      content: res.data.answer,
      isSelf: false,
    })
  } catch (err) {
    messages.value.push({
      content: 'AI 服务异常，请稍后重试',
      isSelf: false,
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}

onMounted(() => scrollToBottom())
</script>

<style scoped>
.ai-chat-page {
  width: 100%;
  height: 100vh;
  background: #f5f9f7;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 20px 24px;
  background: linear-gradient(to right, #56ab2f, #a8e063);
  color: #fff;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.chat-header h2 {
  margin: 0;
  font-size: 22px;
}

.chat-header p {
  margin: 4px 0 0;
  font-size: 14px;
  opacity: 0.95;
}

.chat-body {
  flex: 1;
  padding: 16px 20px;
  overflow-y: auto;
  background: url('@/assets/grain-rain-bg.png') center/cover no-repeat;
  background-attachment: fixed;
}

.message {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.self-message {
  justify-content: flex-end;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #4caf50;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-right: 8px;
  flex-shrink: 0;
}

.bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.5;
  word-break: break-all;
}

.ai-message .bubble {
  background: #ffffffee;
  color: #333;
  border-bottom-left-radius: 6px;
}

.self-message .bubble {
  background: #4caf50dd;
  color: #fff;
  border-bottom-right-radius: 6px;
}


.loading {
  display: flex;
  justify-content: center;
  gap: 6px;
  padding: 10px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #fff;
  animation: bounce 1s infinite;
}
.dot:nth-child(2) {
  animation-delay: 0.2s;
}
.dot:nth-child(3) {
  animation-delay: 0.4s;
}
@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-6px);
  }
}

.chat-footer {
  display: flex;
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #eee;
  gap: 10px;
}

.chat-footer input {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  border: 1px solid #ddd;
  padding: 0 18px;
  font-size: 15px;
  outline: none;
}

.chat-footer button {
  height: 44px;
  padding: 0 20px;
  border-radius: 22px;
  background: #4caf50;
  color: #fff;
  border: none;
  font-size: 15px;
  cursor: pointer;
}
button:disabled {
  background: #90caf9;
  cursor: not-allowed;
}
</style> -->

<template>
  <div class="ai-chat-page">
    <div class="chat-body" ref="chatBody">
      <div class="message ai-message" v-if="messages.length === 0">
        <div class="avatar">AI</div>
        <div class="bubble">你好！我是你的 AI 健康助手，有什么可以帮你的？</div>
      </div>

      <div
        class="message"
        :class="[item.isSelf ? 'self-message' : 'ai-message']"
        v-for="(item, index) in messages"
        :key="index"
      >
        <div class="avatar" v-if="!item.isSelf">AI</div>
        <div class="bubble">{{ item.content }}</div>
      </div>

      <div class="loading" v-if="loading">
        <div class="dot"></div>
        <div class="dot"></div>
        <div class="dot"></div>
      </div>
    </div>

    <div class="chat-footer">
      <input
        v-model="inputText"
        type="text"
        placeholder="你可以问我健康、养生、饮食、运动相关问题"
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage" :disabled="loading">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'

const messages = ref([])
const inputText = ref('')
const chatBody = ref(null)
const loading = ref(false)

const sendMessage = async () => {
  if (!inputText.value.trim()) return

  const userMsg = inputText.value
  messages.value.push({ content: userMsg, isSelf: true })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await axios.post('/ai/chat', {
      question: userMsg,
    })
    messages.value.push({
      content: res.data.answer,
      isSelf: false,
    })
  } catch (err) {
    messages.value.push({
      content: 'AI 服务异常，请稍后重试',
      isSelf: false,
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}

onMounted(() => scrollToBottom())
</script>

<style scoped>
.ai-chat-page {
  width: 100%;
  height: 88vh; /* 关键：控制整体高度，不溢出 */
  max-height: 88vh;
  background: #f5f9f7;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
}

/* .chat-body {
  flex: 1;
  padding: 16px 20px;
  overflow-y: auto;
  background: url('@/assets/ai-setting.png') center center / cover no-repeat;
  background-attachment: fixed;
} */

.chat-body {
  flex: 1;
  padding: 16px 20px;
  overflow-y: auto;
  background-image: url('@/assets/grain-rain-bg.png');
  background-size: cover; /* 保持比例，裁剪超出部分 */
  background-position: center;
  background-repeat: no-repeat;
}

.message {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.self-message {
  justify-content: flex-end;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #4caf50;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-right: 8px;
  flex-shrink: 0;
}

.bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.5;
  word-break: break-all;
}

.ai-message .bubble {
  background: #ffffffee;
  color: #333;
  border-bottom-left-radius: 6px;
}

.self-message .bubble {
  background: #4caf50dd;
  color: #fff;
  border-bottom-right-radius: 6px;
}

.loading {
  display: flex;
  justify-content: center;
  gap: 6px;
  padding: 10px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #fff;
  animation: bounce 1s infinite;
}
.dot:nth-child(2) {
  animation-delay: 0.2s;
}
.dot:nth-child(3) {
  animation-delay: 0.4s;
}
@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-6px);
  }
}

.chat-footer {
  display: flex;
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #eee;
  gap: 10px;
}

.chat-footer input {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  border: 1px solid #ddd;
  padding: 0 18px;
  font-size: 15px;
  outline: none;
}

.chat-footer button {
  height: 44px;
  padding: 0 20px;
  border-radius: 22px;
  background: #4caf50;
  color: #fff;
  border: none;
  font-size: 15px;
  cursor: pointer;
}
button:disabled {
  background: #90caf9;
  cursor: not-allowed;
}
</style>
