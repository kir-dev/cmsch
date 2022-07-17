import axios from 'axios'
import { useQuery } from 'react-query'
import { ExtraPageDto, ExtraPageView } from '../../util/views/extraPage.view'

export const useExtraPage = (slug: string, onError?: (err: any) => void) => {
  return useQuery<ExtraPageView, Error>(
    ['extra', slug],
    async () => {
      const response = await axios.get<ExtraPageDto>(`/api/page/${slug}`)
      return response.data.page
    },
    { onError: onError }
  )
}
