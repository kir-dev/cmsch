import { useToast } from '@chakra-ui/react'
import { Map, Marker } from 'pigeon-maps'
import { useEffect, useState } from 'react'
import { useGeolocated } from 'react-geolocated'
import { l } from '../../util/language'
import { MapDataItemView } from '../../util/views/map.view'
import { MapMarker } from './MapMarker'

interface MapContentProps {
  showUserLocation: boolean
  mapData: MapDataItemView[]
}

export function MapContent({ showUserLocation, mapData }: MapContentProps) {
  const toast = useToast()
  const [center, setCenter] = useState<[number, number]>([47.47303, 19.0531])

  const userLocation = useGeolocated({
    positionOptions: {
      enableHighAccuracy: false
    },
    userDecisionTimeout: 5000,
    suppressLocationOnMount: true,
    watchPosition: showUserLocation
  })

  useEffect(() => {
    if (showUserLocation) userLocation.getPosition()
  }, [showUserLocation])

  useEffect(() => {
    if (userLocation.coords) setCenter([userLocation.coords.latitude, userLocation.coords.longitude])
  }, [userLocation.coords])

  useEffect(() => {
    if (showUserLocation && userLocation.isGeolocationEnabled && !userLocation.isGeolocationAvailable) {
      toast({ title: l('location-sensor-denied'), status: 'error' })
    }
  }, [showUserLocation, userLocation.isGeolocationAvailable, userLocation.isGeolocationEnabled])

  return (
    <Map center={center} provider={StadiaMapProvider} height={300}>
      {userLocation.coords && (
        <Marker hover width={20} height={20} anchor={[userLocation.coords.latitude, userLocation.coords.longitude]}>
          <MapMarker color="blue.500" text="Te" />
        </Marker>
      )}
      {mapData.map((mapDataItem) => (
        <Marker
          hover
          key={mapDataItem.displayName}
          width={20}
          height={20}
          anchor={[mapDataItem.location.latitude, mapDataItem.location.longitude]}
        >
          <MapMarker color="brand.600" text={mapDataItem.displayName} />
        </Marker>
      ))}
    </Map>
  )
}

function StadiaMapProvider(x: number, y: number, z: number) {
  return `https://tiles.stadiamaps.com/tiles/alidade_smooth/${z}/${x}/${y}.png`
}
