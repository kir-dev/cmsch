import { useMutation } from '@tanstack/react-query'
import axios from 'axios'
import { ApiPaths } from '../../../util/paths'
import { taskSubmissionStatus } from '../../../util/views/task.view'
import { QueryKeys } from '../queryKeys'

interface TaskSubmissionResponse {
  status: taskSubmissionStatus
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
