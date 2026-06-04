import { useToast } from '@/hooks/use-toast'
import { l } from '@/util/language'
import { type MapDataItemView, MapMarkerShape } from '@/util/views/map.view'
import { Map, Marker, ZoomControl } from 'pigeon-maps'
import { useEffect, useMemo } from 'react'
import { useGeolocated } from 'react-geolocated'
import { MapMarker } from './MapMarker'

interface MapContentProps {
  showUserLocation: boolean
  mapData: MapDataItemView[]
  className?: string
  height?: number
}

export function MapContent({ showUserLocation, mapData, className, height = 400 }: MapContentProps) {
  const { toast } = useToast()

  const { coords, getPosition, isGeolocationAvailable, isGeolocationEnabled } = useGeolocated({
    positionOptions: {
      enableHighAccuracy: false
    },
    userDecisionTimeout: 5000,
    suppressLocationOnMount: true,
    watchPosition: showUserLocation
  })

  const markersCenter = useMemo(() => {
    if (!mapData || mapData.length === 0) return null
    const latSum = mapData.reduce((acc, curr) => acc + curr.latitude, 0)
    const lonSum = mapData.reduce((acc, curr) => acc + curr.longitude, 0)
    return [latSum / mapData.length, lonSum / mapData.length] as [number, number]
  }, [mapData])

  const fallbackCenter: [number, number] = markersCenter ?? [47.47303, 19.0531]
  const center: [number, number] = showUserLocation && coords ? [coords.latitude, coords.longitude] : fallbackCenter

  useEffect(() => {
    if (showUserLocation) getPosition()
  }, [showUserLocation, getPosition])

  useEffect(() => {
    if (showUserLocation && isGeolocationEnabled && !isGeolocationAvailable) {
      toast({ title: l('location-sensor-denied'), variant: 'destructive' })
    }
  }, [showUserLocation, isGeolocationAvailable, isGeolocationEnabled, toast])

  return (
    <div className={className}>
      <Map center={center} provider={OsmProvider} height={height} dprs={[1, 2]}>
        <ZoomControl />
        {mapData.map((mapDataItem) => (
          <Marker hover key={mapDataItem.displayName} width={200} height={3} anchor={[mapDataItem.latitude, mapDataItem.longitude]}>
            <MapMarker color={mapDataItem.markerColor} text={mapDataItem.displayName} markerShape={mapDataItem.markerShape} />
          </Marker>
        ))}
        {showUserLocation && coords && (
          <Marker hover width={200} height={3} anchor={[coords.latitude, coords.longitude]}>
            <MapMarker color="lightblue" text="Te" markerShape={MapMarkerShape.PERSON} />
          </Marker>
        )}
      </Map>
    </div>
  )
}

function OsmProvider(x: number, y: number, z: number) {
  return `https://tile.openstreetmap.org/${z}/${x}/${y}.png`
}
