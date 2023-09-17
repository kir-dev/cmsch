export type MapDataItemView = {
  accuracy: number
  altitude: number
  altitudeAccuracy: number
  description: string
  displayName: string
  heading: number
  latitude: number
  longitude: number
  markerColor: string
  markerShape: MapMarkerShape
  speed: number
  timestamp: number
}

export enum MapMarkerShape {
  CIRCLE = 'CIRCLE',
  SQUARE = 'SQUARE',
  INFO = 'INFO',
  CAR = 'CAR',
  CROSSHAIRS = 'CROSSHAIRS',
  CAMP = 'CAMP',
  TOWER = 'TOWER',
  MARKER = 'MARKER',
  HOME = 'HOME'
}
