import axios from 'axios'
import { useQuery } from 'react-query'
import { FormData } from '../../util/views/form.view'

export const useFormPage = (slug: string, onError?: (err: any) => void) => {
  return useQuery<FormData, Error>(
    ['formData', slug],
    async () => {
      const response = await axios.get<FormData>(`/api/form/${slug}`)
      return response.data
    },
    { onError: onError }
  )
}
