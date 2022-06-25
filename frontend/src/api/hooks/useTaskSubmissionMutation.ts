import axios from 'axios'
import { useMutation } from 'react-query'

interface TaskSubmissionResponse {
  status: string
}

export const useTaskSubmissionMutation = () => {
  return useMutation<TaskSubmissionResponse, Error, FormData>('taskSubmission', async (formData: FormData) => {
    const res = await axios.post(`/api/task/submit`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    return res.data
  })
}
