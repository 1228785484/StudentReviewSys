import request from "@/utils/request";

export const sendCode = async ({ code }) => {
  try {
    const response = await request({
      url: '/dify/workflow/run',
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      },
      data: { code }
    });

    // 直接从 response.data 获取 JSON 数据
    const data = response.data;

    if (data.event === 'workflow_finished' && data.data.outputs && data.data.outputs.result !== undefined) {
      return data.data.outputs.result.toString();  // 将 result 转换为字符串
    } else {
      console.warn('Unexpected response format:', data);
      return null;
    }

  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
};
