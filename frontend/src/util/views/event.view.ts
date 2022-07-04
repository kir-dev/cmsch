export type EventListView = {
  url: string
  title: string
  category: string
  timestampStart: number
  timestampEnd: number
  place: string
  previewDescription: string
  previewImageUrl: string
}

export type EventView = {
  url: string
  title: string
  category: string
  timestampStart: number
  timestampEnd: number
  place: string
  description: string
  extraButtonTitle: string
  extraButtonUrl: string
  fullImageUrl: string
  ogTitle: string
  ogImage: string
  ogDescription: string
}
