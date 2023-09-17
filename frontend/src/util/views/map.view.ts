import { FunctionComponent } from 'react'
import { FaBroadcastTower, FaCampground, FaCar, FaCrosshairs, FaHome, FaInfo, FaMarker, FaUser } from 'react-icons/fa'

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
  HOME = 'HOME',
  PERSON = 'PERSON'
}

export const MapMarkerIcons: Record<MapMarkerShape, FunctionComponent> = {
  [MapMarkerShape.CIRCLE]: () => null,
  [MapMarkerShape.SQUARE]: () => null,
  [MapMarkerShape.INFO]: FaInfo,
  [MapMarkerShape.CAR]: FaCar,
  [MapMarkerShape.CROSSHAIRS]: FaCrosshairs,
  [MapMarkerShape.CAMP]: FaCampground,
  [MapMarkerShape.TOWER]: FaBroadcastTower,
  [MapMarkerShape.MARKER]: FaMarker,
  [MapMarkerShape.HOME]: FaHome,
  [MapMarkerShape.PERSON]: FaUser
}
