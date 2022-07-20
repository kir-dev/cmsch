import axios from 'axios'
import { useQuery } from 'react-query'

export const useFormPage = (slug: string, onError?: (err: any) => void) => {
  return useQuery<any, Error>(
    ['extra', slug],
    async () => {
      const response = await axios.get(`/api/form/${slug}`)
      console.log(response.data)
      return response.data
    },
    { onError: onError }
  )
}
