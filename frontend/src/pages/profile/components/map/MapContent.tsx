import { useToast } from '@chakra-ui/react'
import { Map, Marker } from 'pigeon-maps'
import { useEffect, useState } from 'react'
import { useGeolocated } from 'react-geolocated'
import { useLocationQuery } from '../../../../api/hooks/location/useLocationQuery'
import { l } from '../../../../util/language'
import { MapMarker } from './MapMarker'

interface MapContentProps {
  showUserLocation: boolean
}

export function MapContent({ showUserLocation }: MapContentProps) {
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

  const locationQuery = useLocationQuery(() =>
    toast({
      title: l('location-query-failed'),
      status: 'error'
    })
  )

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
        <Marker
          onClick={(e) => console.log(e)}
          width={20}
          height={20}
          anchor={[userLocation.coords.latitude, userLocation.coords.longitude]}
        >
          <MapMarker color="blue.500" text="Te" />
        </Marker>
      )}
      {locationQuery.data?.map((location) => (
        <Marker key={location.userId} width={20} height={20} anchor={[location.latitude, location.longitude]}>
          <MapMarker color="brand.600" text={location.userName} />
        </Marker>
      ))}
    </Map>
  )
}

function StadiaMapProvider(x: number, y: number, z: number) {
  return `https://tiles.stadiamaps.com/tiles/alidade_smooth/${z}/${x}/${y}.png`
}
