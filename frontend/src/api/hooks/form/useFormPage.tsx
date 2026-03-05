import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { FormData } from '@/util/views/form.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useFormPage = (slug: string) => {
  return useQuery<FormData, Error>({
    queryKey: [QueryKeys.FORM, slug],
    queryFn: async () => {
      const response = await axios.get<FormData>(joinPath(ApiPaths.FORM, slug))
      return response.data
    }
  })
}
