import { QueryKeys } from '@/api/hooks/queryKeys.ts'
import { ApiPaths } from '@/util/paths.ts'
import type { GalleryView } from '@/util/views/gallery.view.ts'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'

export const useHomeGallery = () => {
  return useQuery<GalleryView, Error>({
    queryKey: [QueryKeys.HOME_GALLERY],
    queryFn: async () => {
      const response = await axios.get<GalleryView>(ApiPaths.HOME_GALLERY)
      return response.data
    }
  })
}
