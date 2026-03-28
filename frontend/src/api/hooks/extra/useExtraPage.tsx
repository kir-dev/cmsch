import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { joinPath } from '@/util/core-functions.util'
import { ApiPaths } from '@/util/paths'
import type { ExtraPageDto, ExtraPageView } from '@/util/views/extraPage.view'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useExtraPage = (slug: string) => {
  return useQuery<ExtraPageView, Error>({
    queryKey: [QueryKeys.EXTRA, slug],
    queryFn: async () => {
      const response = await axios.get<ExtraPageDto>(joinPath(ApiPaths.PAGE, slug))
      return response.data.page
    }
  })
}
