import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths'
import type { TaskSubmissionStatus } from '@/util/views/task.view.ts'
import { useMutation } from '@tanstack/react-query'
import axios from 'axios'

interface TaskSubmissionResponse {
  status: TaskSubmissionStatus
}

export const useTaskSubmissionMutation = () => {
  return useMutation<TaskSubmissionResponse, Error, FormData>({
    mutationKey: [QueryKeys.TASK_SUBMIT],
    mutationFn: async (formData: FormData) => {
      const res = await axios.post(ApiPaths.TASK_SUBMIT, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return res.data
    }
  })
}
