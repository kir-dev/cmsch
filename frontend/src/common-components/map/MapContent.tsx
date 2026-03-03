import { useToast } from '@/hooks/use-toast'
import { l } from '@/util/language'
import { type MapDataItemView, MapMarkerShape } from '@/util/views/map.view'
import { Map, Marker, ZoomControl } from 'pigeon-maps'
import { useEffect, useState } from 'react'
import { useGeolocated } from 'react-geolocated'
import { MapMarker } from './MapMarker'

interface MapContentProps {
  showUserLocation: boolean
  mapData: MapDataItemView[]
}

export function MapContent({ showUserLocation, mapData }: MapContentProps) {
  const { toast } = useToast()
  const [center, setCenter] = useState<[number, number]>([47.47303, 19.0531])

  const { coords, getPosition, isGeolocationAvailable, isGeolocationEnabled } = useGeolocated({
    positionOptions: {
      enableHighAccuracy: false
    },
    userDecisionTimeout: 5000,
    suppressLocationOnMount: true,
    watchPosition: showUserLocation
  })

  useEffect(() => {
    if (showUserLocation) getPosition()
  }, [showUserLocation, getPosition])

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    if (coords) setCenter([coords.latitude, coords.longitude])
  }, [coords])

  useEffect(() => {
    if (showUserLocation && isGeolocationEnabled && !isGeolocationAvailable) {
      toast({ title: l('location-sensor-denied'), variant: 'destructive' })
    }
  }, [showUserLocation, isGeolocationAvailable, isGeolocationEnabled, toast])

  return (
    <Map center={center} provider={OsmProvider} height={400} dprs={[1, 2]}>
      <ZoomControl />
      {mapData.map((mapDataItem) => (
        <Marker hover key={mapDataItem.displayName} width={200} height={3} anchor={[mapDataItem.latitude, mapDataItem.longitude]}>
          <MapMarker color={mapDataItem.markerColor} text={mapDataItem.displayName} markerShape={mapDataItem.markerShape} />
        </Marker>
      ))}
      {coords && (
        <Marker hover width={200} height={3} anchor={[coords.latitude, coords.longitude]}>
          <MapMarker color="lightblue" text="Te" markerShape={MapMarkerShape.PERSON} />
        </Marker>
      )}
    </Map>
  )
}

function OsmProvider(x: number, y: number, z: number) {
  return `https://tile.openstreetmap.org/${z}/${x}/${y}.png`
}
