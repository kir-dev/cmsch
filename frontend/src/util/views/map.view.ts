export type MapDataItemView = {
  location: {
    latitude: number
    longitude: number
    accuracy: number
  }
  marker: {
    shape: MapMarkerShape
    color: string
  }
  displayName: string
  description: string | undefined
}

export enum MapMarkerShape {
  CIRCLE = 'circle',
  SQUARE = 'square',
  TRIANGLE = 'triangle'
}
