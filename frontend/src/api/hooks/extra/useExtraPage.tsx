import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { ExtraPageDto, ExtraPageView } from '../../../util/views/extraPage.view'
import { QueryKeys } from '../queryKeys'
import { joinPath } from '../../../util/core-functions.util'
import { ApiPaths } from '../../../util/paths'

export const useExtraPage = (slug: string) => {
  return useQuery<ExtraPageView, Error>({
    queryKey: [QueryKeys.EXTRA, slug],
    queryFn: async () => {
      const response = await axios.get<ExtraPageDto>(joinPath(ApiPaths.PAGE, slug))
      return response.data.page
    }
  })
}
