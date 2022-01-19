export interface EventsPreviewDTO {
  title: string
  category: string
  timestampStart: number
  timestampEnd: number
  place: string
  previewDescription: string
}

export interface EventsView {
  warningMessage: string
  allEvents: EventsPreviewDTO[]
}
