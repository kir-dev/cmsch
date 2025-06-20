import { ImageCarousel } from './ImageCarousel.tsx'
import { useHomeGallery } from '../../../api/hooks/gallery/useHomeGallery.ts'

export default function HomePageGalleryCarousel() {
  const homeGallery = useHomeGallery()

  if (!homeGallery.data) return null
  return <ImageCarousel images={homeGallery.data?.photos?.map((item) => item.url) ?? []} />
}
