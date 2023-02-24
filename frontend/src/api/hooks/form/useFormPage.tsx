import axios from 'axios'
import { useQuery } from 'react-query'
import { FormData } from '../../../util/views/form.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useFormPage = (slug: string, onError?: (err: any) => void) => {
  return useQuery<FormData, Error>(
    [QueryKeys.FORM, slug],
    async () => {
      const response = await axios.get<FormData>(joinPath(ApiPaths.FORM, slug))
      return response.data
    },
    { onError: onError }
  )
}
