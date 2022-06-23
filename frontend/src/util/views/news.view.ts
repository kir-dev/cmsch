export interface NewsPreviewDTO {
  title: string
  content: string
  imageUrl: string
  highlighted: boolean
  timestamp: number
}

export interface NewsView {
  warningMessage: string
  news: NewsPreviewDTO[]
}
