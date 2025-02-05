import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { FormData } from '../../../util/views/form.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useFormPage = (slug: string) => {
  return useQuery<FormData, Error>({
    queryKey: [QueryKeys.FORM, slug],
    queryFn: async () => {
      const response = await axios.get<FormData>(joinPath(ApiPaths.FORM, slug))
      return response.data
    }
  })
}
