import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { useTokenRefresh } from '@/api/hooks/useTokenRefresh.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import { FormSubmitResult } from '@/util/views/form.view'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import axios from 'axios'

export const useFormSubmit = (slug: string) => {
  const client = useQueryClient()
  const tokenRefresh = useTokenRefresh()
  return useMutation<FormSubmitResult, Error, { formBody: Record<string, unknown>; edit?: boolean }>({
    mutationFn: async ({ formBody, edit }) => {
      const url = joinPath(ApiPaths.FORM, slug)
      const response = edit ? await axios.put<FormSubmitResult>(url, formBody) : await axios.post<FormSubmitResult>(url, formBody)
      return response.data
    },
    onSuccess: async (data: FormSubmitResult) =>
      Promise.allSettled([
        client.invalidateQueries({ queryKey: [QueryKeys.FORM, slug] }),
        data === FormSubmitResult.OK_RELOG_REQUIRED && tokenRefresh.mutateAsync()
      ]),
    onError: console.error
  })
}
