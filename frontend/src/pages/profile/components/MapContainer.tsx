import { Box, Checkbox, Divider, Heading, Text, useToast } from '@chakra-ui/react'
import Map from './openlayers/Map'
import { Layers, TileLayer } from './openlayers/Layers/'
import FullScreenControl from './openlayers/FullScreenControl'
import Stamen from 'ol/source/Stamen'
import { VectorLayer } from './openlayers/Layers'
import VectorSource from 'ol/source/Vector'
import { useEffect, useMemo, useState } from 'react'
import { useGeolocated } from 'react-geolocated'
import { useLocationQuery } from '../../../api/hooks/location/useLocationQuery'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { l } from '../../../util/language'
import { addMarkers } from '../util/addMarkers'

const layer = new Stamen({ layer: 'terrain' })

const MapContainer = () => {
  const [showUserLocation, setShowUserLocation] = useState<boolean>(false)
  const [watchStarted, setWatchStarted] = useState<boolean>(false)
  const toast = useToast()
  const profileConfig = useConfigContext()?.components.profile

  const { coords, isGeolocationAvailable, isGeolocationEnabled, timestamp, getPosition } = useGeolocated({
    positionOptions: {
      enableHighAccuracy: false
    },
    userDecisionTimeout: 10000,
    suppressLocationOnMount: true
  })

  const locationQuery = useLocationQuery(
    () =>
      toast({
        title: l('location-query-failed'),
        status: 'error'
      }),
    () => {
      if (watchStarted) {
        getPosition()
      }
    }
  )

  const leadersLocation = useMemo(() => {
    if (!locationQuery.data) return undefined
    return new VectorSource({ features: addMarkers(locationQuery.data) })
  }, [locationQuery.data])

  const userLocation = useMemo(() => {
    if (!coords) return undefined
    return new VectorSource({
      features: addMarkers([
        {
          id: -1,
          groupName: l('users-location-title'),
          alias: '(GPS szenzor alapján)',
          longitude: coords.longitude,
          latitude: coords.latitude,
          accuracy: coords.accuracy,
          timestamp: ((timestamp || 0) - new Date().getTimezoneOffset() * 60000) / 1000
        }
      ])
    })
  }, [coords, timestamp])

  useEffect(() => {
    if (showUserLocation && !watchStarted) {
      getPosition()
      setWatchStarted(true)
    }
  }, [showUserLocation, watchStarted])

  useEffect(() => {
    if (showUserLocation && (!isGeolocationAvailable || !isGeolocationEnabled)) {
      toast({ title: l('location-sensor-denied'), status: 'error' })
    }
  }, [showUserLocation, isGeolocationAvailable, isGeolocationEnabled])

  return (
    <Box>
      <Divider my={10} borderWidth={2} />
      {profileConfig && <Heading my={5}>{profileConfig.groupLeadersHeader} pozicíója</Heading>}
      <Checkbox ml={1} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        Saját pozícióm mutatása
      </Checkbox>
      <Map>
        <Layers>
          <TileLayer source={layer} />
          {leadersLocation && <VectorLayer source={leadersLocation} zIndex={3} />}
          {showUserLocation && userLocation && <VectorLayer source={userLocation} zIndex={2} />}
        </Layers>

        <FullScreenControl />
      </Map>
      <Text>Csak annak a helyzete látható, akinél a helymegosztás engedélyezve (használatban) van.</Text>
      <Text>A saját pozíciódat csak te látod, nem kerül megosztásra mással.</Text>
    </Box>
  )
}

export default MapContainer
