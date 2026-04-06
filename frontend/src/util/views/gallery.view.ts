export type GalleryView = {
  photos: GalleryItemView[]
}

export type GalleryItemView = {
  title: string
  description: string
  highlighted: boolean
  showOnHomePage: boolean
  url: string
  thumbnailUrl: string
}
